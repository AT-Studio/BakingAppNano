package com.example.alit.bakingappnano.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.example.alit.bakingappnano.myDatastructures.Ingredient;
import com.example.alit.bakingappnano.myDatastructures.Recipe;
import com.example.alit.bakingappnano.myDatastructures.Step;
import com.example.alit.bakingappnano.recipeProvider.IngredientsTable;
import com.example.alit.bakingappnano.recipeProvider.RecipesProvider;
import com.example.alit.bakingappnano.recipeProvider.RecipesTable;
import com.example.alit.bakingappnano.recipeProvider.StepsTable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AliT on 10/9/17.
 */

public class DbUtils {

    private DbUtils() {
    }

    public static void insertReponseIntoDb(Context context, List<Recipe> recipes) {

        ContentResolver contentResolver = context.getContentResolver();

        Cursor recipesInDb = contentResolver.query(
                RecipesProvider.Recipes.RECIPES,
                new String[]{RecipesTable._ID},
                null,
                null,
                null
        );

        ArrayList<Long> recipeIds = new ArrayList<>();

        while (recipesInDb.moveToNext()) {
            recipeIds.add(recipesInDb.getLong(recipesInDb.getColumnIndex(RecipesTable._ID)));
        }

        ArrayList<ContentValues> ingredientValues = new ArrayList<>();
        ArrayList<ContentValues> stepvalues = new ArrayList<>();

        for (Recipe recipe : recipes) {

            if (recipeIds.contains(recipe.id)) continue;

            ContentValues recipeValue = new ContentValues();

            recipeValue.put(RecipesTable._ID, recipe.id);
            recipeValue.put(RecipesTable.NAME, recipe.name);
            recipeValue.put(RecipesTable.SERVINGS, recipe.servings);
            recipeValue.put(RecipesTable.IMAGE_PATH, recipe.image);

            Uri uri = contentResolver.insert(RecipesProvider.Recipes.RECIPES, recipeValue);

            long key = Long.parseLong(uri.getLastPathSegment());

            for (Ingredient ingredient : recipe.ingredients) {

                ContentValues ingValue = new ContentValues();

                ingValue.put(IngredientsTable.RECIPE_ID, key);
                ingValue.put(IngredientsTable.QUANTITY, ingredient.quantity);
                ingValue.put(IngredientsTable.MEASURE, ingredient.measure);
                ingValue.put(IngredientsTable.INGREDIENT, ingredient.ingredient);

                ingredientValues.add(ingValue);

            }

            for (Step step : recipe.steps) {

                ContentValues stepvalue = new ContentValues();

                stepvalue.put(StepsTable.RECIPE_ID, key);
                stepvalue.put(StepsTable.SHORT_DESC, step.shortDescription);
                stepvalue.put(StepsTable.LONG_DESC, step.description);
                stepvalue.put(StepsTable.VIDEO_PATH, step.videoURL);
                stepvalue.put(StepsTable.THUMBNAIL_PATH, step.thumbnailURL);

                stepvalues.add(stepvalue);

            }

        }

        ContentValues[] ingredientsArray = new ContentValues[ingredientValues.size()];
        ingredientsArray = ingredientValues.toArray(ingredientsArray);

        ContentValues[] stepsArray = new ContentValues[stepvalues.size()];
        stepsArray = stepvalues.toArray(stepsArray);

        contentResolver.bulkInsert(RecipesProvider.Ingredients.INGREDIENTS, ingredientsArray);
        contentResolver.bulkInsert(RecipesProvider.Steps.STEPS, stepsArray);

    }

}
