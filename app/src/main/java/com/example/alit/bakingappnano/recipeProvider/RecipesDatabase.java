package com.example.alit.bakingappnano.recipeProvider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.OnUpgrade;
import net.simonvt.schematic.annotation.Table;

/**
 * Created by AliT on 10/7/17.
 */

@Database(version = RecipesDatabase.VERSION)
public final class RecipesDatabase {

    public static final int VERSION = 5;

    @Table(RecipesTable.class)
    public static final String RECIPES = "recipes";

    @Table(IngredientsTable.class)
    public static final String INGREDIENTS = "ingredients";

    @Table(StepsTable.class)
    public static final String STEPS = "steps";

    public RecipesDatabase() {

    }

    @OnUpgrade
    public static void onUpgrade(Context context, SQLiteDatabase db, int oldVersion,
                                 int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + RECIPES);
        db.execSQL("DROP TABLE IF EXISTS " + INGREDIENTS);
        db.execSQL("DROP TABLE IF EXISTS " + STEPS);

        final String RECIPES = "CREATE TABLE recipes ("
                + RecipesTable._ID + " INTEGER PRIMARY KEY,"
                + RecipesTable.NAME + " TEXT NOT NULL,"
                + RecipesTable.SERVINGS + " INTEGER NOT NULL,"
                + RecipesTable.IMAGE_PATH + " TEXT)";

        final String INGREDIENTS = "CREATE TABLE ingredients ("
                + IngredientsTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + IngredientsTable.RECIPE_ID + " INTEGER NOT NULL REFERENCES recipes(_id),"
                + IngredientsTable.QUANTITY + " REAL NOT NULL,"
                + IngredientsTable.MEASURE + " TEXT NOT NULL,"
                + IngredientsTable.INGREDIENT + " TEXT NOT NULL)";

        final String STEPS = "CREATE TABLE steps ("
                + StepsTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + StepsTable.RECIPE_ID + " INTEGER NOT NULL REFERENCES recipes(_id),"
                + StepsTable.SHORT_DESC + " TEXT NOT NULL,"
                + StepsTable.LONG_DESC + " TEXT NOT NULL,"
                + StepsTable.VIDEO_PATH + " TEXT,"
                + StepsTable.THUMBNAIL_PATH + " TEXT)";

        db.execSQL(RECIPES);
        db.execSQL(INGREDIENTS);
        db.execSQL(STEPS);

    }

}
