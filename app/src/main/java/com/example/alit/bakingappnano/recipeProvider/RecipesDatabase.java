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

    public static final int VERSION = 2;

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

    }

}
