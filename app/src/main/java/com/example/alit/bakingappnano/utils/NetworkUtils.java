package com.example.alit.bakingappnano.utils;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by AliT on 10/7/17.
 */

public class NetworkUtils {

    public final static String urlString = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";

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

    private NetworkUtils() {
    }

    public static URL getURL() {

        URL url = null;
        try {
            url = new URL(urlString);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return url;
    }

//    public static ArrayList<Recipe> getRecipesFromJSON(String JSONString) {
//
//        ArrayList<Recipe> recipes = new ArrayList<>();
//
//        try {
//            JSONArray recipesJSON = new JSONArray(JSONString);
//
//            for (int i = 0; i < recipesJSON.length(); i++) {
//
//                JSONObject recipeJSON = (JSONObject) recipesJSON.get(i);
//
//                int id = recipeJSON.getInt(RECIPE_ID);
//                String name = recipeJSON.getString(RECIPE_NAME);
//                int servings = recipeJSON.getInt(RECIPE_SERVINGS);
//                String imagePath = recipeJSON.getString(RECIPE_IMAGE);
//
//                JSONArray ingredientsJSON = recipeJSON.getJSONArray(ING_JSON);
//                JSONArray stepsJSON = recipeJSON.getJSONArray(STEPS_JSON);
//
//                ArrayList<Ingredient> ingredients = new ArrayList<>();
//                ArrayList<Step> steps = new ArrayList<>();
//
//                for (int j = 0; j < ingredientsJSON.length(); j++) {
//
//                    JSONObject ingredientJSON = (JSONObject) ingredientsJSON.get(j);
//
//                    int quantity = ingredientJSON.getInt(ING_QUANTITY);
//                    String measure = ingredientJSON.getString(ING_MEASURE);
//                    String ingredient = ingredientJSON.getString(ING_INGREDIENT);
//
//                    ingredients.add(new Ingredient(quantity, measure, ingredient));
//
//                }
//
//                for (int k = 0; k < stepsJSON.length(); k++) {
//
//                    JSONObject stepJSON = (JSONObject) stepsJSON.get(k);
//
//                    String shortDesc = stepJSON.getString(STEP_SHORT_DESC);
//                    String longDesc = stepJSON.getString(STEP_LONG_DESC);
//                    String videoPath = stepJSON.getString(STEP_VIDEO_URL);
//                    String thumbnailPath = stepJSON.getString(STEP_THUMBNAIL);
//
//                    steps.add(new Step(shortDesc, longDesc, videoPath, thumbnailPath));
//
//                }
//
//                recipes.add(new Recipe(new RecipeDescription(0, name, servings, imagePath), ingredients, steps));
//
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return recipes;
//
//    }

    public static String getRecipes() {

        URL url = getURL();

        StringBuilder stringBuilder = new StringBuilder();

        HttpURLConnection connection = null;

        try {
            connection = (HttpURLConnection) url.openConnection();
            Scanner scanner = new Scanner(connection.getInputStream());
            scanner.useDelimiter("\\A");
            while (scanner.hasNext()) {
                String next = scanner.next();
                stringBuilder.append(next);
            }
            scanner.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) connection.disconnect();
        }

        return stringBuilder.toString();

    }

}
