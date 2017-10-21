package com.example.alit.bakingappnano.recipeDetail;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.example.alit.bakingappnano.R;
import com.example.alit.bakingappnano.adapters.IngredientRecyclerViewAdapter;
import com.example.alit.bakingappnano.adapters.StepRecyclerViewAdapter;
import com.example.alit.bakingappnano.myDatastructures.Ingredient;
import com.example.alit.bakingappnano.recipeProvider.IngredientsTable;
import com.example.alit.bakingappnano.recipeProvider.RecipesProvider;
import com.example.alit.bakingappnano.recipeProvider.StepsTable;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by AliT on 10/10/17.
 */

public class MasterDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        StepRecyclerViewAdapter.StepItemClickListener {

    private static final int INGREDIENTS_LOADER = 1;
    private static final int STEPS_DESC_LOADER = 2;

    private static final String SCROLL_VIEW_STATE = "scrollState";

    @BindView(R.id.nestedScrollView)
    NestedScrollView nestedScrollView;

    @BindView(R.id.masterDetailWrapper)
    ConstraintLayout masterDetailWrapper;

    @BindView(R.id.ingredientsRecyclerView)
    RecyclerView ingredientsRecyclerView;
    private LinearLayoutManager ingredientsLayoutManager;
    private IngredientRecyclerViewAdapter ingredientsAdapter;

    @BindView(R.id.stepsRecyclerView)
    RecyclerView stepsRecyclerView;
    private LinearLayoutManager stepsLayoutManager;
    public StepRecyclerViewAdapter stepsAdapter;

    private Context context;

    private MasterDetailClickListener clickListener;

    private long recipeID;

    private Unbinder unbinder;

    boolean ingredientsLoaded;
    boolean stepsLoaded;
    int[] scrollState;
    boolean hasSavedScrollState;

    public MasterDetailFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.context = context;

        try {
            clickListener = (MasterDetailClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement MasterDetailClickListener");
        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ingredientsLayoutManager = new LinearLayoutManager(context);
        stepsLayoutManager = new LinearLayoutManager(context);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.master_detail_fragment_layout, container, false);

        unbinder = ButterKnife.bind(this, rootView);

        ingredientsRecyclerView.setNestedScrollingEnabled(false);
        stepsRecyclerView.setNestedScrollingEnabled(false);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            final int[] scrollState = savedInstanceState.getIntArray(SCROLL_VIEW_STATE);
            if (scrollState != null) {
                this.scrollState = scrollState;
//                hasSavedScrollState = true;

                masterDetailWrapper.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {

//                        Timber.d("triggered globallayoutlistener: " + masterDetailWrapper.getHeight());

                        if (stepsLoaded && ingredientsLoaded) {
                            masterDetailWrapper.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                            Timber.d("restoring scroll state: " + scrollState[1]);
//                            Timber.d("scrollView height: " + nestedScrollView.getHeight());
//                            Timber.d("wrapper height: " + masterDetailWrapper.getHeight());
//                                hasSavedScrollState = false;
                            nestedScrollView.scrollTo(scrollState[0], scrollState[1]);
                        }

                    }
                });
            }
//            Timber.d("scrollstate found x: " + scrollState[0]);
//            Timber.d("scrollstate found y: " + scrollState[1]);
//            nestedScrollView.post(new Runnable() {
//                @Override
//                public void run() {
////                    nestedScrollView.scrollTo(scrollState[0], scrollState[1]);
//                    nestedScrollView.setScrollY(scrollState[0]);
//                    nestedScrollView.setScrollY(scrollState[1]);
//                }
//            });
        }

        recipeID = clickListener.getRecipeId();

        LoaderManager loaderManager = ((AppCompatActivity) clickListener).getSupportLoaderManager();

        loaderManager.restartLoader(INGREDIENTS_LOADER, null, this);
        loaderManager.restartLoader(STEPS_DESC_LOADER, null, this);
    }

    @Override
    public void stepItemClicked(int position) {

        clickListener.masterDetailClicked(position);

    }

    public interface MasterDetailClickListener {

        void masterDetailClicked(int step);

        long getRecipeId();

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == INGREDIENTS_LOADER) return new CursorLoader(
                context,
                RecipesProvider.Ingredients.INGREDIENTS,
                null,
                IngredientsTable.RECIPE_ID + "=?",
                new String[]{Long.toString(recipeID)},
                null);
        else return new CursorLoader(
                context,
                RecipesProvider.Steps.STEPS,
                new String[]{StepsTable.SHORT_DESC, StepsTable.THUMBNAIL_PATH},
                StepsTable.RECIPE_ID + "=?",
                new String[]{Long.toString(recipeID)},
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (loader.getId() == INGREDIENTS_LOADER) {

            ArrayList<Ingredient> ingredients = new ArrayList<>();

            while (data.moveToNext()) {

                String ingredient = data.getString(data.getColumnIndex(IngredientsTable.INGREDIENT));
                int quantity = data.getInt(data.getColumnIndex(IngredientsTable.QUANTITY));
                String measure = data.getString(data.getColumnIndex(IngredientsTable.MEASURE));

                ingredients.add(new Ingredient(quantity, measure, ingredient));

            }

            if (ingredientsAdapter == null) {
                ingredientsAdapter = new IngredientRecyclerViewAdapter(ingredients);
                ingredientsRecyclerView.setLayoutManager(ingredientsLayoutManager);
                ingredientsRecyclerView.setAdapter(ingredientsAdapter);
            } else ingredientsAdapter.update(ingredients);

            ingredientsLoaded = true;

        } else {

            ArrayList<String> steps = new ArrayList<>();
            ArrayList<String> thumbnails = new ArrayList<>();

            while (data.moveToNext()) {

                String shortDesc = data.getString(data.getColumnIndex(StepsTable.SHORT_DESC));
                String thumbnail = data.getString(data.getColumnIndex(StepsTable.THUMBNAIL_PATH));

                steps.add(shortDesc);
                thumbnails.add(thumbnail);

            }

            if (stepsAdapter == null) {
                stepsAdapter = new StepRecyclerViewAdapter(steps, thumbnails, this, context);
                stepsRecyclerView.setLayoutManager(stepsLayoutManager);
                stepsRecyclerView.setAdapter(stepsAdapter);
            } else stepsAdapter.update(steps, thumbnails);

            stepsLoaded = true;

        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        Timber.d("scrollstate x: " + nestedScrollView.getScrollX());
//        Timber.d("scrollstate y: " + nestedScrollView.getScrollY());
        outState.putIntArray(SCROLL_VIEW_STATE, new int[] {nestedScrollView.getScrollX(), nestedScrollView.getScrollY()});
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }
}
