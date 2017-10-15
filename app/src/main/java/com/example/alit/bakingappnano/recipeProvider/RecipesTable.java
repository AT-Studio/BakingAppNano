package com.example.alit.bakingappnano.recipeProvider;

import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

import static net.simonvt.schematic.annotation.DataType.Type.INTEGER;
import static net.simonvt.schematic.annotation.DataType.Type.TEXT;

/**
 * Created by AliT on 10/7/17.
 */

public interface RecipesTable {

    @DataType(INTEGER) @PrimaryKey String _ID = "_id";

    @DataType(TEXT) @NotNull String NAME = "name";

    @DataType(INTEGER) @NotNull String SERVINGS = "servings";

    @DataType(TEXT) String IMAGE_PATH = "image";

}
