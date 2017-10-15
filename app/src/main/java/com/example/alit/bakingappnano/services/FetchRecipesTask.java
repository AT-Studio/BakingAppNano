package com.example.alit.bakingappnano.services;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import com.example.alit.bakingappnano.recipeProvider.IngredientsTable;
import com.example.alit.bakingappnano.recipeProvider.RecipesProvider;
import com.example.alit.bakingappnano.recipeProvider.RecipesTable;
import com.example.alit.bakingappnano.recipeProvider.StepsTable;
import com.example.alit.bakingappnano.utils.DbUtils;
import com.example.alit.bakingappnano.utils.NetworkUtils;
import com.example.alit.bakingappnano.utils.ServiceUtils;

/**
 * Created by AliT on 10/9/17.
 */

public class FetchRecipesTask {

    private static final String TAG = "TESTSTUFF";

    private FetchRecipesTask() {}

    public static boolean DbFilled;

    public static void execute(Context context) {

        Log.d(TAG, "executing fetch recipes task");

        String reponse = NetworkUtils.getRecipes();

        Intent intent = new Intent();
        intent.setAction(ServiceUtils.ACTION_RECIPES_FETCHED);
        context.sendBroadcast(intent);

//        Log.d(TAG, "response: " + reponse);

        DbFilled = DbUtils.insertReponseIntoDb(context, reponse);

        Log.d(TAG, "done executing");

//        queryAll(context);

    }

    public static void queryAll(Context context) {

        ContentResolver contentResolver = context.getContentResolver();

        Cursor recipeCursor = contentResolver.query(RecipesProvider.Recipes.RECIPES, null, null, null, null);

        Cursor ingredientCursor = contentResolver.query(RecipesProvider.Ingredients.INGREDIENTS, null, null, null, null);

        Cursor stepCursor = contentResolver.query(RecipesProvider.Steps.STEPS, null, null, null, null);

        StringBuilder recipes = new StringBuilder();

        recipes.append("Recipes: ");

        StringBuilder ingredients = new StringBuilder();

        ingredients.append("Ingredients: ");

        StringBuilder steps = new StringBuilder();

        steps.append("Steps: ");



        while (recipeCursor.moveToNext()) {

            recipes.append(" -" + recipeCursor.getString(recipeCursor.getColumnIndex(RecipesTable.NAME)));

        }

        while (ingredientCursor.moveToNext()) {

            ingredients.append(" -" + ingredientCursor.getString(ingredientCursor.getColumnIndex(IngredientsTable.INGREDIENT)));

        }

        while (stepCursor.moveToNext()) {

            steps.append(" -" + stepCursor.getString(stepCursor.getColumnIndex(StepsTable.SHORT_DESC)));

        }

        Log.d(TAG, recipes.toString());
        Log.d(TAG, ingredients.toString());
        Log.d(TAG, steps.toString());

//        recipeCursor.moveToPosition(2);
//
//        long key = recipeCursor.getLong(recipeCursor.getColumnIndex(RecipesTable._ID));
//        String recipe = recipeCursor.getString(recipeCursor.getColumnIndex(RecipesTable.NAME));

//        querySpecific(key, recipe);

    }

}
