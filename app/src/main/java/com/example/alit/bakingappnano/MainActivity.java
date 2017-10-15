package com.example.alit.bakingappnano;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.alit.bakingappnano.myDatastructures.RecipeDescription;
import com.example.alit.bakingappnano.recipeProvider.RecipesProvider;
import com.example.alit.bakingappnano.recipeProvider.RecipesTable;
import com.example.alit.bakingappnano.services.RecipeService;
import com.example.alit.bakingappnano.utils.ServiceUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, RecipeRecyclerViewAdapter.RecipeItemClickListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    public static final int RECIPE_LOADER = 1;

    @BindView(R.id.recipeRecyclerView) RecyclerView recipeRecyclerView;
    RecipeRecyclerViewAdapter adapter;
    GridLayoutManager layoutManager;

    StaggeredGridLayoutManager staggeredGridLayoutManager;

    @BindView(R.id.progressbar) ProgressBar progressbar;

    @BindView(R.id.errorTextView) TextView errorTextView;

    @BindView(R.id.swipeRefresh) SwipeRefreshLayout swipeRefresh;

    RecipesFetchedReceiver recipesFetchedReceiver;
    IntentFilter recipesFetchedIntentFilter;

    ArrayList<RecipeDescription> recipeDescriptions;

    private final String TAG = "TESTSTUFF";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        layoutManager = new GridLayoutManager(this, getNumColumn());
//        recipeRecyclerView.setLayoutManager(layoutManager);

        staggeredGridLayoutManager = new StaggeredGridLayoutManager(getNumColumn(), StaggeredGridLayoutManager.VERTICAL);

        recipeRecyclerView.setLayoutManager(staggeredGridLayoutManager);

        recipesFetchedIntentFilter = new IntentFilter(ServiceUtils.ACTION_RECIPES_FETCHED);
        recipesFetchedReceiver = new RecipesFetchedReceiver();

        Intent intent = new Intent(this, RecipeService.class);
        startService(intent);

        LoaderManager loaderManager = getSupportLoaderManager();

        loaderManager.restartLoader(RECIPE_LOADER, null, this);

        ServiceUtils.scheduleRecipeJob(this);

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

        int orientation = getScreenOrientation();

        switch (orientation) {
            case Surface.ROTATION_0:
                return 1;
            case Surface.ROTATION_90:
                return 2;
            case Surface.ROTATION_180:
                return 1;
            default:
                return 1;
        }

    }

    @Override
    public void recipeClicked(int position) {

        Timber.d("called recipeClicked");

        RecipeDescription recipe = recipeDescriptions.get(position);

        Bundle bundle = new Bundle();
        bundle.putLong(RecipesTable._ID, recipe.ID);
        bundle.putString(RecipesTable.NAME, recipe.name);
        bundle.putInt(RecipesTable.SERVINGS, recipe.servings);
        bundle.putString(RecipesTable.IMAGE_PATH, recipe.imagePath);

        Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "creating loader");
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

        Log.d(TAG, "load finished");

        if (recipeDescriptions == null) recipeDescriptions = new ArrayList<>();
        else recipeDescriptions.clear();

        while (data.moveToNext()) {

            Log.d(TAG, "iterating over cursor");

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
        }
        else adapter.notifyDataSetChanged();

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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
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
