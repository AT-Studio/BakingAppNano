package com.example.alit.bakingappnano;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.alit.bakingappnano.myDatastructures.Step;
import com.example.alit.bakingappnano.recipeProvider.RecipesProvider;
import com.example.alit.bakingappnano.recipeProvider.RecipesTable;
import com.example.alit.bakingappnano.recipeProvider.StepsTable;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class RecipeDetailActivity extends AppCompatActivity implements MasterDetailFragment.MasterDetailClickListener,
        StepDetailFragment.StepDetailClickListener, LoaderManager.LoaderCallbacks<Cursor> {

//    private static final int INGREDIENTS_LOADER = 1;
    private static final int STEPS_LOADER = 3;

    public static final String STEP_NUM = "stepNum";
    public static final String IS_LAST_STEP = "numSteps";

    private static final String STEP_FRAGMENT = "stepFragment";

    @BindView(R.id.mainLayout) FrameLayout mainLayout;

    @BindView(R.id.setWidgetFAB) FloatingActionButton setWidgetFAB;

//    @BindView(R.id.stepDetailViewPager) ViewPager stepDetailViewPager;
//    StepDetailPagerAdapter pagerAdapter;

    private long recipeID;
    private String recipeName;
    private int recipeServings;
    private String recipeImagePath;

    public int numPages;

    ArrayList<Step> steps;

    public final String TAG = "lifecycle";

    public int lastStepPos;

    public boolean showLastStep;

    int containerID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "Called onCreate");

        setContentView(R.layout.activity_recipe_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        containerID = R.id.stepContainer;

        Log.d(TAG, "stepContainer ID: " + containerID);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        ButterKnife.bind(this);

        Intent intent = getIntent();

        Bundle bundle = intent.getExtras();

        if (savedInstanceState != null) {
            Log.d(TAG, "onSavedInstaceState not null");
            recipeID = savedInstanceState.getLong(RecipesTable._ID);
            recipeName = savedInstanceState.getString(RecipesTable.NAME);
            recipeServings = savedInstanceState.getInt(RecipesTable.SERVINGS);
            recipeImagePath = bundle.getString(RecipesTable.IMAGE_PATH);
            String lastStep = savedInstanceState.getString(STEP_NUM);
            if (lastStep != null) {
                lastStepPos = Integer.parseInt(lastStep);
                Log.d(TAG, "last step wasnt null: " + lastStepPos);
                showLastStep = true;
//                masterDetailClicked(lastStepPos);
            }
        } else if (bundle != null) {
            Log.d(TAG, "bundle not null");
            recipeID = bundle.getLong(RecipesTable._ID);
            Timber.d("recipeID " + recipeID);
            recipeName = bundle.getString(RecipesTable.NAME);
            Timber.d("recipeName " + recipeName);
            recipeServings = bundle.getInt(RecipesTable.SERVINGS);
            recipeImagePath = bundle.getString(RecipesTable.IMAGE_PATH);
        } else finish();

        actionBar.setTitle(recipeName);

        Timber.d("finished onCreate");

        LoaderManager loaderManager = getSupportLoaderManager();
//        loaderManager.restartLoader(INGREDIENTS_LOADER, null, this);
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

//        stepDetailViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//            }
//
//            @Override
//            public void onPageSelected(int position) {
////                Timber.d("Page selected: " + position);
////                pagerAdapter.getFragmentAtPosition(position).initializeExoPlayer();
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
////                if (state == ViewPager.SCROLL_STATE_DRAGGING) {
////                    Timber.d("dragging");
////                    pagerAdapter.getFragmentAtPosition(stepDetailViewPager.getCurrentItem()).simpleExoPlayerView.setVisibility(View.GONE);
////                }
////                else if (state == ViewPager.SCROLL_STATE_IDLE) {
////                    Timber.d("idling" + stepDetailViewPager.getCurrentItem());
////                    pagerAdapter.getFragmentAtPosition(stepDetailViewPager.getCurrentItem()).setAllowVideoLoading();
////                }
////                else if (state == ViewPager.SCROLL_STATE_SETTLING) Timber.d("settling");
//
//            }
//        });
    }

    @Override
    public void masterDetailClicked(int position) {

//        if (stepDetailViewPager.getAdapter() == null) return;
//
//        stepDetailViewPager.setCurrentItem(step, false);
//        stepDetailViewPager.setVisibility(View.VISIBLE);

        if (steps == null) {
            Log.d(TAG, "steps was null");
            return;
        }

        lastStepPos = position;

        Step step = steps.get(position);

        Bundle bundle = new Bundle();
        bundle.putInt(STEP_NUM, position + 1);
        bundle.putBoolean(IS_LAST_STEP, position == steps.size() - 1);
        bundle.putString(StepsTable.SHORT_DESC, step.shortDesc);
        bundle.putString(StepsTable.LONG_DESC, step.longDesc);
        bundle.putString(StepsTable.VIDEO_PATH, step.videoPath);
        bundle.putString(StepsTable.THUMBNAIL_PATH, step.thumbnailPath);

        StepDetailFragment fragment = new StepDetailFragment();
        fragment.setArguments(bundle);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.stepContainer, fragment, STEP_FRAGMENT);
        fragmentTransaction.commit();

//        if (moveUp) fragmentTransaction.setCustomAnimations(R.anim.frag_slide_in_right, R.anim.frag_slide_out_left);
//        else fragmentTransaction.setCustomAnimations(R.anim.frag_slide_in_left, R.anim.frag_slide_out_right);
//        fragmentTransaction.replace(R.id.mainLayout, fragment, "test");

    }

    @Override
    public void stepDetailClicked(boolean moveUp, int position) {

//        int position = stepDetailViewPager.getCurrentItem();

//        int position = 0;

        if (moveUp) position++;
        else position--;

        lastStepPos = position;

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (position >= 0) {

            Step step = steps.get(position);

            Bundle bundle = new Bundle();
            bundle.putInt(STEP_NUM, position + 1);
            bundle.putBoolean(IS_LAST_STEP, position == steps.size() - 1);
            bundle.putString(StepsTable.SHORT_DESC, step.shortDesc);
            bundle.putString(StepsTable.LONG_DESC, step.longDesc);
            bundle.putString(StepsTable.VIDEO_PATH, step.videoPath);
            bundle.putString(StepsTable.THUMBNAIL_PATH, step.thumbnailPath);

            StepDetailFragment fragment = new StepDetailFragment();
            fragment.setArguments(bundle);

            if (moveUp) fragmentTransaction.setCustomAnimations(R.anim.frag_slide_in_right, R.anim.frag_slide_out_left);
            else fragmentTransaction.setCustomAnimations(R.anim.frag_slide_in_left, R.anim.frag_slide_out_right);
            fragmentTransaction.replace(R.id.stepContainer, fragment, STEP_FRAGMENT);
        }
        else fragmentTransaction.remove(getSupportFragmentManager().findFragmentByTag(STEP_FRAGMENT));
//        fragmentTransaction.add(R.id.mainLayout, fragment, "test");
        fragmentTransaction.commit();

//        if (moveUp) {
//            stepDetailViewPager.setCurrentItem(position + 1);
//        }
//        else if (position > 0) {
//            stepDetailViewPager.setCurrentItem(position - 1);
//        }
//        else {
//            stepDetailViewPager.setVisibility(View.GONE);
//        }

    }

    @Override
    public long getRecipeId() {
        return recipeID;
    }

    public class StepDetailPagerAdapter extends FragmentStatePagerAdapter {

//        String[] pageTitles = {"Trailers", "Reviews"};

        ArrayList<StepDetailFragment> fragments;

        public StepDetailPagerAdapter(FragmentManager fm) {
            super(fm);
            fragments = new ArrayList<>();
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public Fragment getItem(int position) {
//            if (position == 0) {
//                //return new MovieTrailersFragment();
//            } else {
//                //return new MovieReviewsFragment();
//            }

            Step step = steps.get(position);

            Bundle bundle = new Bundle();
            bundle.putInt(STEP_NUM, position + 1);
            bundle.putBoolean(IS_LAST_STEP, position == getCount() - 1);
            bundle.putString(StepsTable.SHORT_DESC, step.shortDesc);
            bundle.putString(StepsTable.LONG_DESC, step.longDesc);
            bundle.putString(StepsTable.VIDEO_PATH, step.videoPath);
            bundle.putString(StepsTable.THUMBNAIL_PATH, step.thumbnailPath);

            StepDetailFragment fragment = new StepDetailFragment();
            fragment.setArguments(bundle);

            return fragment;

        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            StepDetailFragment fragment = (StepDetailFragment) super.instantiateItem(container, position);
            if (position < fragments.size()) fragments.remove(position);
            fragments.add(position, fragment);
            return super.instantiateItem(container, position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
//            if (position < fragments.size()) fragments.remove(position);
            super.destroyItem(container, position, object);
        }

        @Override
        public int getCount() {
            return steps.size();
        }

        public StepDetailFragment getFragmentAtPosition(int position) {
            return fragments.get(position);
        }

//        @Override
//        public CharSequence getPageTitle(int position) {
////            return pageTitles[position];
//            return null;
//        }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Timber.d("called on create loader");
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

        Timber.d("load finished recipe detail");

        if (steps == null) steps = new ArrayList<>();
        else steps.clear();

        while (data.moveToNext()) {

            String shortDesc = data.getString(data.getColumnIndex(StepsTable.SHORT_DESC));
            String longDesc = data.getString(data.getColumnIndex(StepsTable.LONG_DESC));
            String videoPath = data.getString(data.getColumnIndex(StepsTable.VIDEO_PATH));
            String thumbnailPath = data.getString(data.getColumnIndex(StepsTable.THUMBNAIL_PATH));

            steps.add(new Step(shortDesc, longDesc, videoPath, thumbnailPath));

        }

        data.close();

        Log.d(TAG, "stepContainer ID: " + containerID);

//        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.stepContainer);
//        frameLayout.setVisibility(View.GONE);

        if (showLastStep) {
            showLastStep = false;
            mainLayout.post(new Runnable() {
                @Override
                public void run() {
                    Step step = steps.get(lastStepPos);

                    Bundle bundle = new Bundle();
                    bundle.putInt(STEP_NUM, lastStepPos + 1);
                    bundle.putBoolean(IS_LAST_STEP, lastStepPos == steps.size() - 1);
                    bundle.putString(StepsTable.SHORT_DESC, step.shortDesc);
                    bundle.putString(StepsTable.LONG_DESC, step.longDesc);
                    bundle.putString(StepsTable.VIDEO_PATH, step.videoPath);
                    bundle.putString(StepsTable.THUMBNAIL_PATH, step.thumbnailPath);

                    StepDetailFragment fragment = new StepDetailFragment();
                    fragment.setArguments(bundle);

                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.stepContainer, fragment, STEP_FRAGMENT);
                    fragmentTransaction.commit();
                }
            });
//            Handler handler = new Handler();
//            handler.post(new Runnable() {
//                @Override
//                public void run() {
//                }
//            });
        }

//        Handler handler = new Handler();
//        handler.post(new Runnable() {
//            @Override
//            public void run() {
//
//                int position = 0;
//
//                Step step = steps.get(position);
//
//                Bundle bundle = new Bundle();
//                bundle.putInt(STEP_NUM, position + 1);
//                bundle.putBoolean(IS_LAST_STEP, position == steps.size() - 1);
//                bundle.putString(StepsTable.SHORT_DESC, step.shortDesc);
//                bundle.putString(StepsTable.LONG_DESC, step.longDesc);
//                bundle.putString(StepsTable.VIDEO_PATH, step.videoPath);
//                bundle.putString(StepsTable.THUMBNAIL_PATH, step.thumbnailPath);
//
//                StepDetailFragment fragment = new StepDetailFragment();
//                fragment.setArguments(bundle);
//
//                FragmentTransaction fragmentTransaction = getSupportonger().beginTransaction();
//                fragmentTransaction.add(R.id.mainLayout, fragment, "test");
//                fragmentTransaction.commit();
//
//            }
//        });

//        pagerAdapter = new StepDetailPagerAdapter(getSupportFragmentManager());
//        stepDetailViewPager.setAdapter(pagerAdapter);

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

//    @Override
//    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
//
//
////        Fragment fragment = getSupportFragmentManager().findFragmentByTag(STEP_FRAGMENT);
////        if (fragment != null) {
////            Log.d(TAG, "step fragment not null");
////            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
////            transaction.remove(fragment);
////            transaction.commit();
////        }
//        super.onSaveInstanceState(outState, outPersistentState);
//        Log.d(TAG, "onSavedInstaceState");
//        Fragment fragment = getSupportFragmentManager().findFragmentByTag(STEP_FRAGMENT);
//        if (fragment != null) outState.putString(STEP_NUM, Integer.toString(lastStepPos));
//        outState.putLong(RecipesTable._ID, recipeID);
//        outState.putString(RecipesTable.NAME, recipeName);
//        outState.putInt(RecipesTable.SERVINGS, recipeServings);
//        outState.putString(RecipesTable.IMAGE_PATH, recipeImagePath);
//    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSavedInstaceState");
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(STEP_FRAGMENT);
        if (fragment != null) {
            Log.d(TAG, "saving last step");
            outState.putString(STEP_NUM, Integer.toString(lastStepPos));
        }
        outState.putLong(RecipesTable._ID, recipeID);
        outState.putString(RecipesTable.NAME, recipeName);
        outState.putInt(RecipesTable.SERVINGS, recipeServings);
        outState.putString(RecipesTable.IMAGE_PATH, recipeImagePath);
    }

    //    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
// //       if (movieQueryUrl != null) outState.putString(MOVIE_QUERY_URL_EXTRA, movieQueryUrl);
//    }


    @Override
    public void onBackPressed() {
//        if (stepDetailViewPager.getVisibility() == View.VISIBLE) {
//            stepDetailViewPager.setVisibility(View.GONE);
//        }
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(STEP_FRAGMENT);
        if (fragment != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.remove(fragment);
            transaction.commit();
        }
        else super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "calling ondestroy");
//
//        Fragment fragment = getSupportFragmentManager().findFragmentByTag(STEP_FRAGMENT);
//        if (fragment != null) {
//            Log.d(TAG, "step fragment not null");
//            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//            transaction.remove(fragment);
//            transaction.commit();
//        }
//
        super.onDestroy();

//        mainLayout.removeViewAt(0);
    }
}
