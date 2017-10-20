package com.example.alit.bakingappnano.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.example.alit.bakingappnano.recipeProvider.IngredientsTable;
import com.example.alit.bakingappnano.recipeProvider.RecipesProvider;
import com.example.alit.bakingappnano.recipeProvider.RecipesTable;
import com.example.alit.bakingappnano.recipeProvider.StepsTable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by AliT on 10/9/17.
 */

public class DbUtils {

    public final static String ING_JSON = "ingredients";
    public final static String STEPS_JSON = "steps";

    public final static String RECIPE_ID = "id";
    public final static String RECIPE_NAME = "name";
    public final static String RECIPE_SERVINGS = "servings";
    public final static String RECIPE_IMAGE = "image";

    public final static String ING_QUANTITY = "quantity";
    public final static String ING_MEASURE = "measure";
    public final static String ING_INGREDIENT = "ingredient";

    public final static String STEP_SHORT_DESC = "shortDescription";
    public final static String STEP_LONG_DESC = "description";
    public final static String STEP_VIDEO_URL = "videoURL";
    public final static String STEP_THUMBNAIL = "thumbnailURL";

    private DbUtils() {
    }

    public static boolean insertReponseIntoDb(Context context, String JSONString) {

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

        recipesInDb.close();

        ArrayList<ContentValues> ingredientValues = new ArrayList<>();
        ArrayList<ContentValues> stepvalues = new ArrayList<>();

        try {
            JSONArray recipesJSON = new JSONArray(JSONString);

            for (int i = 0; i < recipesJSON.length(); i++) {

                JSONObject recipeJSON = (JSONObject) recipesJSON.get(i);

                ContentValues recipeValue = new ContentValues();

                long id = recipeJSON.getLong(RECIPE_ID);

                if (recipeIds.contains(id)) continue;

                String name = recipeJSON.getString(RECIPE_NAME);
                int servings = recipeJSON.getInt(RECIPE_SERVINGS);
                String imagePath = recipeJSON.getString(RECIPE_IMAGE);

                recipeValue.put(RecipesTable._ID, id);
                recipeValue.put(RecipesTable.NAME, name);
                recipeValue.put(RecipesTable.SERVINGS, servings);
                recipeValue.put(RecipesTable.IMAGE_PATH, imagePath);

                Uri uri = contentResolver.insert(RecipesProvider.Recipes.RECIPES, recipeValue);

                long key = Long.parseLong(uri.getLastPathSegment());

                JSONArray ingredientsJSON = recipeJSON.getJSONArray(ING_JSON);
                JSONArray stepsJSON = recipeJSON.getJSONArray(STEPS_JSON);

                for (int j = 0; j < ingredientsJSON.length(); j++) {

                    JSONObject ingredientJSON = (JSONObject) ingredientsJSON.get(j);

                    ContentValues ingValue = new ContentValues();

                    int quantity = ingredientJSON.getInt(ING_QUANTITY);
                    String measure = ingredientJSON.getString(ING_MEASURE);
                    String ingredient = ingredientJSON.getString(ING_INGREDIENT);

                    ingValue.put(IngredientsTable.RECIPE_ID, key);
                    ingValue.put(IngredientsTable.QUANTITY, quantity);
                    ingValue.put(IngredientsTable.MEASURE, measure);
                    ingValue.put(IngredientsTable.INGREDIENT, ingredient);

                    ingredientValues.add(ingValue);

                }

                for (int k = 0; k < stepsJSON.length(); k++) {

                    JSONObject stepJSON = (JSONObject) stepsJSON.get(k);

                    ContentValues stepvalue = new ContentValues();

                    String shortDesc = stepJSON.getString(STEP_SHORT_DESC);
                    String longDesc = stepJSON.getString(STEP_LONG_DESC);
                    String videoPath = stepJSON.getString(STEP_VIDEO_URL);
                    String thumbnailPath = stepJSON.getString(STEP_THUMBNAIL);

                    stepvalue.put(StepsTable.RECIPE_ID, key);
                    stepvalue.put(StepsTable.SHORT_DESC, shortDesc);
                    stepvalue.put(StepsTable.LONG_DESC, longDesc);
                    stepvalue.put(StepsTable.VIDEO_PATH, videoPath);
                    stepvalue.put(StepsTable.THUMBNAIL_PATH, thumbnailPath);

                    stepvalues.add(stepvalue);

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ContentValues[] ingredientsArray = new ContentValues[ingredientValues.size()];
        ingredientsArray = ingredientValues.toArray(ingredientsArray);

        ContentValues[] stepsArray = new ContentValues[stepvalues.size()];
        stepsArray = stepvalues.toArray(stepsArray);

        contentResolver.bulkInsert(RecipesProvider.Ingredients.INGREDIENTS, ingredientsArray);
        contentResolver.bulkInsert(RecipesProvider.Steps.STEPS, stepsArray);

        return true;

    }

}
