package com.example.alit.bakingappnano.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.alit.bakingappnano.R;
import com.example.alit.bakingappnano.about.AboutActivity;
import com.example.alit.bakingappnano.adapters.RecipeRecyclerViewAdapter;
import com.example.alit.bakingappnano.myDatastructures.RecipeDescription;
import com.example.alit.bakingappnano.recipeDetail.RecipeDetailActivity;
import com.example.alit.bakingappnano.recipeProvider.RecipesProvider;
import com.example.alit.bakingappnano.recipeProvider.RecipesTable;
import com.example.alit.bakingappnano.services.RecipeService;
import com.example.alit.bakingappnano.settings.SettingsActivity;
import com.example.alit.bakingappnano.utils.ServiceUtils;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, RecipeRecyclerViewAdapter.RecipeItemClickListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    public static final int RECIPE_LOADER = 1;

    @BindView(R.id.recipeRecyclerView)
    RecyclerView recipeRecyclerView;
    private RecipeRecyclerViewAdapter adapter;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;

    @BindView(R.id.progressbar)
    ProgressBar progressbar;

    @BindView(R.id.errorTextView)
    TextView errorTextView;

    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefresh;

    private RecipesFetchedReceiver recipesFetchedReceiver;
    private IntentFilter recipesFetchedIntentFilter;

    public ArrayList<RecipeDescription> recipeDescriptions;

    private boolean isTablet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(getResources().getString(R.string.mainActivity_title));

        ButterKnife.bind(this);

        if (findViewById(R.id.mainLayout) != null) isTablet = true;

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        staggeredGridLayoutManager = new StaggeredGridLayoutManager(getNumColumn(), StaggeredGridLayoutManager.VERTICAL);

        recipeRecyclerView.setLayoutManager(staggeredGridLayoutManager);

        recipesFetchedIntentFilter = new IntentFilter(ServiceUtils.ACTION_RECIPES_FETCHED);
        recipesFetchedReceiver = new RecipesFetchedReceiver();

        Intent intent = new Intent(this, RecipeService.class);
        startService(intent);

        LoaderManager loaderManager = getSupportLoaderManager();

        loaderManager.restartLoader(RECIPE_LOADER, null, this);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        int syncPref = Integer.parseInt(sharedPreferences.getString(getResources().getString(R.string.settings_sync_key), getResources().getString(R.string.settings_sync_24_hours_value)));

        ServiceUtils.scheduleRecipeJob(this, (int) TimeUnit.HOURS.toSeconds(syncPref));

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                Intent intent = new Intent(MainActivity.this, RecipeService.class);
                startService(intent);

            }
        });

        Handler loading = new Handler();
        loading.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (recipeRecyclerView.getVisibility() == View.INVISIBLE) {
                    progressbar.setVisibility(View.INVISIBLE);
                    errorTextView.setVisibility(View.VISIBLE);
                }
            }
        }, 10000);

    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(recipesFetchedReceiver, recipesFetchedIntentFilter);
    }

    public int getNumColumn() {

        if (isTablet) {
            int orientation = getScreenOrientation();
            switch (orientation) {
                case Surface.ROTATION_0:
                    return 2;
                case Surface.ROTATION_90:
                    return 3;
                case Surface.ROTATION_180:
                    return 2;
                case Surface.ROTATION_270:
                    return 3;
                default:
                    return 2;
            }
        } else {
            int orientation = getScreenOrientation();
            switch (orientation) {
                case Surface.ROTATION_0:
                    return 1;
                case Surface.ROTATION_90:
                    return 2;
                case Surface.ROTATION_180:
                    return 1;
                case Surface.ROTATION_270:
                    return 2;
                default:
                    return 1;
            }
        }

    }

    @Override
    public void recipeClicked(int position) {

        RecipeDescription recipe = recipeDescriptions.get(position);

        Bundle bundle = new Bundle();
        bundle.putLong(RecipesTable._ID, recipe.ID);
        bundle.putString(RecipesTable.NAME, recipe.name);

        Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                this,
                RecipesProvider.Recipes.RECIPES,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (recipeDescriptions == null) recipeDescriptions = new ArrayList<>();
        else recipeDescriptions.clear();

        while (data.moveToNext()) {

            long id = data.getLong(data.getColumnIndex(RecipesTable._ID));
            String name = data.getString(data.getColumnIndex(RecipesTable.NAME));
            int servings = data.getInt(data.getColumnIndex(RecipesTable.SERVINGS));
            String imagePath = data.getString(data.getColumnIndex(RecipesTable.IMAGE_PATH));

            recipeDescriptions.add(new RecipeDescription(id, name, servings, imagePath));

        }

        if (recipeDescriptions.size() > 0 && recipeRecyclerView.getVisibility() == View.INVISIBLE) {
            progressbar.setVisibility(View.INVISIBLE);
            errorTextView.setVisibility(View.INVISIBLE);
            recipeRecyclerView.setVisibility(View.VISIBLE);
        }

        if (adapter == null) {
            adapter = new RecipeRecyclerViewAdapter(recipeDescriptions, this, this, getNumColumn());
            recipeRecyclerView.setAdapter(adapter);
        } else adapter.notifyDataSetChanged();

        data.close();

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_settings) {

            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_about) {

            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);

        }
        return true;
    }

    public int getScreenOrientation() {
        return ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
    }

    public class RecipesFetchedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (swipeRefresh.isRefreshing()) swipeRefresh.setRefreshing(false);

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(recipesFetchedReceiver);
    }
}
