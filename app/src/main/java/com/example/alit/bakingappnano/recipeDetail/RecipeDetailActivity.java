package com.example.alit.bakingappnano.recipeDetail;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.alit.bakingappnano.R;
import com.example.alit.bakingappnano.myDatastructures.Step;
import com.example.alit.bakingappnano.recipeProvider.RecipesProvider;
import com.example.alit.bakingappnano.recipeProvider.RecipesTable;
import com.example.alit.bakingappnano.recipeProvider.StepsTable;
import com.example.alit.bakingappnano.widgets.IngredientsWidget;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeDetailActivity extends AppCompatActivity implements MasterDetailFragment.MasterDetailClickListener,
        StepDetailFragment.StepDetailClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final int STEPS_LOADER = 3;

    public static final String STEP_NUM = "stepNum";
    public static final String IS_LAST_STEP = "numSteps";

    private static final String STEP_FRAGMENT = "stepFragment";

    public static final String EXTRA_IS_TABLET = "isTablet";

    @BindView(R.id.setWidgetFAB)
    FloatingActionButton setWidgetFAB;

    @BindView(R.id.stepContainer)
    FrameLayout stepContainer;

    private boolean twoPane;
    private boolean isTablet;

    private long recipeID;
    private String recipeName;

    private ArrayList<Step> steps;

//    private int lastStepPos;
//
//    private boolean showLastStep;

//    private int containerID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_recipe_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        containerID = R.id.stepContainer;

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        ButterKnife.bind(this);

        Intent intent = getIntent();

        Bundle bundle = intent.getExtras();

//        if (findViewById(R.id.mainLayout) != null) twoPane = true;
        Resources resources = getResources();
        twoPane = resources.getBoolean(R.bool.isTwoPane);
        isTablet = resources.getBoolean(R.bool.isTablet);
//        if (findViewById(R.id.checkTablet) != null) isTablet = true;

        if (savedInstanceState != null) {
            recipeID = savedInstanceState.getLong(RecipesTable._ID);
            recipeName = savedInstanceState.getString(RecipesTable.NAME);
//            String lastStep = savedInstanceState.getString(STEP_NUM);
//            if (lastStep != null) {
//                lastStepPos = Integer.parseInt(lastStep);
//                showLastStep = true;
//            }
        } else if (bundle != null) {
            recipeID = bundle.getLong(RecipesTable._ID);
            recipeName = bundle.getString(RecipesTable.NAME);
        } else finish();

        actionBar.setTitle(recipeName);

        LoaderManager loaderManager = getSupportLoaderManager();
        loaderManager.restartLoader(STEPS_LOADER, null, this);

        setWidgetFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = RecipeDetailActivity.this;
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, IngredientsWidget.class));
                IngredientsWidget.updateIngredientsWidget(context, appWidgetManager, recipeName, recipeID, appWidgetIds);
                Toast.makeText(context, "Ingredients saved to widget", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void masterDetailClicked(int position) {

        if (steps == null) {
            return;
        }

//        lastStepPos = position;

        Step step = steps.get(position);

        Bundle bundle = new Bundle();
        bundle.putInt(STEP_NUM, position + 1);
        bundle.putBoolean(IS_LAST_STEP, position == steps.size() - 1);
        bundle.putString(StepsTable.SHORT_DESC, step.shortDescription);
        bundle.putString(StepsTable.LONG_DESC, step.description);
        bundle.putString(StepsTable.VIDEO_PATH, step.videoURL);
        bundle.putString(StepsTable.THUMBNAIL_PATH, step.thumbnailURL);
        bundle.putBoolean(EXTRA_IS_TABLET, isTablet);

        StepDetailFragment fragment = new StepDetailFragment();
        fragment.setArguments(bundle);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.stepContainer, fragment, STEP_FRAGMENT);
        fragmentTransaction.commit();

    }

    @Override
    public void stepDetailClicked(boolean moveUp, int position) {

        if (moveUp) position++;
        else position--;

//        lastStepPos = position;

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (position >= 0) {

            Step step = steps.get(position);

            Bundle bundle = new Bundle();
            bundle.putInt(STEP_NUM, position + 1);
            bundle.putBoolean(IS_LAST_STEP, position == steps.size() - 1);
            bundle.putString(StepsTable.SHORT_DESC, step.shortDescription);
            bundle.putString(StepsTable.LONG_DESC, step.description);
            bundle.putString(StepsTable.VIDEO_PATH, step.videoURL);
            bundle.putString(StepsTable.THUMBNAIL_PATH, step.thumbnailURL);
            bundle.putBoolean(EXTRA_IS_TABLET, isTablet);

            StepDetailFragment fragment = new StepDetailFragment();
            fragment.setArguments(bundle);

            if (moveUp)
                fragmentTransaction.setCustomAnimations(R.anim.frag_slide_in_right, R.anim.frag_slide_out_left);
            else
                fragmentTransaction.setCustomAnimations(R.anim.frag_slide_in_left, R.anim.frag_slide_out_right);
            fragmentTransaction.replace(R.id.stepContainer, fragment, STEP_FRAGMENT);
        } else
            fragmentTransaction.remove(getSupportFragmentManager().findFragmentByTag(STEP_FRAGMENT));
        fragmentTransaction.commit();

    }

    @Override
    public long getRecipeId() {
        return recipeID;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                this,
                RecipesProvider.Steps.STEPS,
                null,
                StepsTable.RECIPE_ID + "=?",
                new String[]{Long.toString(recipeID)},
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (steps == null) steps = new ArrayList<>();
        else steps.clear();

        while (data.moveToNext()) {

            String shortDesc = data.getString(data.getColumnIndex(StepsTable.SHORT_DESC));
            String longDesc = data.getString(data.getColumnIndex(StepsTable.LONG_DESC));
            String videoPath = data.getString(data.getColumnIndex(StepsTable.VIDEO_PATH));
            String thumbnailPath = data.getString(data.getColumnIndex(StepsTable.THUMBNAIL_PATH));

            steps.add(new Step(shortDesc, longDesc, videoPath, thumbnailPath));

        }

//        if (showLastStep) {
//            showLastStep = false;
//            stepContainer.post(new Runnable() {
//                @Override
//                public void run() {
//                    Step step = steps.get(lastStepPos);
//
//                    Bundle bundle = new Bundle();
//                    bundle.putInt(STEP_NUM, lastStepPos + 1);
//                    bundle.putBoolean(IS_LAST_STEP, lastStepPos == steps.size() - 1);
//                    bundle.putString(StepsTable.SHORT_DESC, step.shortDesc);
//                    bundle.putString(StepsTable.LONG_DESC, step.longDesc);
//                    bundle.putString(StepsTable.VIDEO_PATH, step.videoPath);
//                    bundle.putString(StepsTable.THUMBNAIL_PATH, step.thumbnailPath);
//                    bundle.putBoolean(EXTRA_IS_TABLET, isTablet);
//
//                    StepDetailFragment fragment = new StepDetailFragment();
//                    fragment.setArguments(bundle);
//
//                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//                    fragmentTransaction.replace(R.id.stepContainer, fragment, STEP_FRAGMENT);
//                    fragmentTransaction.commit();
//                }
//            });
//        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        Fragment fragment = getSupportFragmentManager().findFragmentByTag(STEP_FRAGMENT);
//        if (fragment != null) {
//            outState.putString(STEP_NUM, Integer.toString(lastStepPos));
//        }
        outState.putLong(RecipesTable._ID, recipeID);
        outState.putString(RecipesTable.NAME, recipeName);
    }


    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(STEP_FRAGMENT);
        if (fragment != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.remove(fragment);
            transaction.commit();
        } else super.onBackPressed();
    }
}
