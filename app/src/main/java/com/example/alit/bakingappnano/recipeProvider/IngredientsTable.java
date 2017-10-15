package com.example.alit.bakingappnano.recipeProvider;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;
import net.simonvt.schematic.annotation.References;

import static net.simonvt.schematic.annotation.DataType.Type.INTEGER;
import static net.simonvt.schematic.annotation.DataType.Type.TEXT;

/**
 * Created by AliT on 10/7/17.
 */

public interface IngredientsTable {

    @DataType(INTEGER) @PrimaryKey @AutoIncrement String _ID = "_id";

    @DataType(INTEGER) @References(table = RecipesDatabase.RECIPES, column = RecipesTable._ID) @NotNull String RECIPE_ID = "recipeId";

    @DataType(INTEGER) @NotNull String QUANTITY = "quantity";

    @DataType(TEXT) @NotNull String MEASURE = "measure";

    @DataType(TEXT) @NotNull String INGREDIENT = "ingredient";

}
