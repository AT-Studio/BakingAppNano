package com.example.alit.bakingappnano.myDatastructures;

/**
 * Created by AliT on 10/9/17.
 */

public class RecipeDescription {

    public long ID;
    public String name;
    public int servings;
    public String imagePath;

    public RecipeDescription(long ID, String name, int servings, String imagePath) {

        this.ID = ID;
        this.name = name;
        this.servings = servings;
        this.imagePath = imagePath;

    }

}
