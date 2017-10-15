package com.example.alit.bakingappnano;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.alit.bakingappnano.myDatastructures.Ingredient;
import com.example.alit.bakingappnano.recipeProvider.IngredientsTable;
import com.example.alit.bakingappnano.recipeProvider.RecipesProvider;
import com.example.alit.bakingappnano.recipeProvider.StepsTable;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;

/**
 * Created by AliT on 10/10/17.
 */

public class MasterDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        StepRecyclerViewAdapter.StepItemClickListener {

    private static final int INGREDIENTS_LOADER = 1;
    private static final int STEPS_DESC_LOADER = 2;

    @BindView(R.id.ingredientsRecyclerView) RecyclerView ingredientsRecyclerView;
    LinearLayoutManager ingredientsLayoutManager;
    IngredientRecyclerViewAdapter ingredientsAdapter;

    @BindView(R.id.stepsRecyclerView) RecyclerView stepsRecyclerView;
    LinearLayoutManager stepsLayoutManager;
    StepRecyclerViewAdapter stepsAdapter;

    Context context;

    MasterDetailClickListener clickListener;

    long recipeID;

    Unbinder unbinder;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Timber.d("called onAttach");

        this.context = context;

        try {
            clickListener = (MasterDetailClickListener) context;
        }
        catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement MasterDetailClickListener");
        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        setRetainInstance(true);

//        this.ingredientsLayoutManager = new LinearLayoutManager(context);
        this.ingredientsLayoutManager = new LinearLayoutManager(context) {

            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        this.stepsLayoutManager = new LinearLayoutManager(context) {

            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.master_detail_fragment_layout, container, false);

        unbinder = ButterKnife.bind(this, rootView);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Timber.d("Called onActivityCreated");

        recipeID = clickListener.getRecipeId();

        Timber.d("RecipeID: " + recipeID);

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
                new String[]{StepsTable.SHORT_DESC},
                StepsTable.RECIPE_ID + "=?",
                new String[]{Long.toString(recipeID)},
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

//        Timber.d("load finished");

        if (loader.getId() == INGREDIENTS_LOADER) {

            ArrayList<Ingredient> ingredients = new ArrayList<>();

            while (data.moveToNext()) {

//                Timber.d("moving through data");

                String ingredient = data.getString(data.getColumnIndex(IngredientsTable.INGREDIENT));
                int quantity = data.getInt(data.getColumnIndex(IngredientsTable.QUANTITY));
                String measure = data.getString(data.getColumnIndex(IngredientsTable.MEASURE));

//                Timber.d(ingredient);

                ingredients.add(new Ingredient(quantity, measure, ingredient));

            }

            if (ingredientsAdapter == null) {
                ingredientsAdapter = new IngredientRecyclerViewAdapter(ingredients);
                ingredientsRecyclerView.setLayoutManager(ingredientsLayoutManager);
                ingredientsRecyclerView.setAdapter(ingredientsAdapter);
            }
            else ingredientsAdapter.update(ingredients);

        }
        else {

            ArrayList<String> steps = new ArrayList<>();

            while (data.moveToNext()) {

                String shortDesc = data.getString(data.getColumnIndex(StepsTable.SHORT_DESC));
//                String longDesc = data.getString(data.getColumnIndex(StepsTable.LONG_DESC));
//                String videoPath = data.getString(data.getColumnIndex(StepsTable.VIDEO_PATH));
//                String thumbnailPath = data.getString(data.getColumnIndex(StepsTable.THUMBNAIL_PATH));

//                steps.add(new Step(shortDesc, longDesc, videoPath, thumbnailPath));

//                Timber.d(shortDesc);

                steps.add(shortDesc);

            }

            if (stepsAdapter == null) {
                stepsAdapter = new StepRecyclerViewAdapter(steps, this);
                stepsRecyclerView.setLayoutManager(stepsLayoutManager);
                stepsRecyclerView.setAdapter(stepsAdapter);
            }
            else stepsAdapter.update(steps);

        }

        data.close();

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
