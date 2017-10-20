package com.example.alit.bakingappnano.recipeProvider;

import android.content.ContentUris;
import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

/**
 * Created by AliT on 10/7/17.
 */

@ContentProvider(authority = RecipesProvider.AUTHORITY, database = RecipesDatabase.class)
public final class RecipesProvider {

    public static final String SCHEME = "content://";

    public static final String AUTHORITY = "com.example.alit.bakingappnano";

    public static final Uri BASE_CONTENT_URI = Uri.parse(SCHEME + AUTHORITY);

    @TableEndpoint(table = RecipesDatabase.RECIPES)
    public static class Recipes {

        @ContentUri(
                path = RecipesDatabase.RECIPES,
                type = "vnd.android.cursor.dir/list",
                defaultSort = RecipesTable._ID + " ASC")
        public static final Uri RECIPES = BASE_CONTENT_URI.buildUpon().appendPath(RecipesDatabase.RECIPES).build();

        @InexactContentUri(
                path = RecipesDatabase.RECIPES + "/#",
                name = "RECIPE_WITH_ID",
                type = "vnd.android.cursor.item/list",
                whereColumn = RecipesTable._ID,
                pathSegment = 1)
        public static Uri recipeWithId(long id) {
            return ContentUris.withAppendedId(RECIPES, id);
        }

    }

    @TableEndpoint(table = RecipesDatabase.INGREDIENTS)
    public static class Ingredients {

        @ContentUri(
                path = RecipesDatabase.INGREDIENTS,
                type = "vnd.android.cursor.dir/list",
                defaultSort = IngredientsTable._ID + " ASC")
        public static final Uri INGREDIENTS = BASE_CONTENT_URI.buildUpon().appendPath(RecipesDatabase.INGREDIENTS).build();

        @InexactContentUri(
                path = RecipesDatabase.INGREDIENTS + "/#",
                name = "INGREDIENT_WITH_ID",
                type = "vnd.android.cursor.item/list",
                whereColumn = IngredientsTable._ID,
                pathSegment = 1)
        public static Uri ingredientWithId(long id) {
            return ContentUris.withAppendedId(INGREDIENTS, id);
        }

    }

    @TableEndpoint(table = RecipesDatabase.STEPS)
    public static class Steps {

        @ContentUri(
                path = RecipesDatabase.STEPS,
                type = "vnd.android.cursor.dir/list",
                defaultSort = StepsTable._ID + " ASC")
        public static final Uri STEPS = BASE_CONTENT_URI.buildUpon().appendPath(RecipesDatabase.STEPS).build();

        @InexactContentUri(
                path = RecipesDatabase.STEPS + "/#",
                name = "STEPS_WITH_ID",
                type = "vnd.android.cursor.item/list",
                whereColumn = StepsTable._ID,
                pathSegment = 1)
        public static Uri stepsWithId(long id) {
            return ContentUris.withAppendedId(STEPS, id);
        }

    }

}
