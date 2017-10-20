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

public interface StepsTable {

    @DataType(INTEGER)
    @PrimaryKey
    @AutoIncrement
    String _ID = "_id";

    @DataType(INTEGER)
    @References(table = RecipesDatabase.RECIPES, column = RecipesTable._ID)
    @NotNull
    String RECIPE_ID = "recipeId";

    @DataType(TEXT)
    @NotNull
    String SHORT_DESC = "shortDesc";

    @DataType(TEXT)
    @NotNull
    String LONG_DESC = "longDesc";

    @DataType(TEXT)
    String VIDEO_PATH = "videoPath";

    @DataType(TEXT)
    String THUMBNAIL_PATH = "thumbnailPath";

}
