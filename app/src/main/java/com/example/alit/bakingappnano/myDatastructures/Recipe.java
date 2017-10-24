package com.example.alit.bakingappnano.myDatastructures;

import java.util.ArrayList;

/**
 * Created by AliT on 10/7/17.
 */

public class Recipe {

    public long id;
    public String name;
    public ArrayList<Ingredient> ingredients;
    public ArrayList<Step> steps;
    public int servings;
    public String image;

    public Recipe(long id, String name, ArrayList<Ingredient> ingredients, ArrayList<Step> steps, int servings, String image) {

        this.id = id;
        this.name = name;
        this.ingredients = ingredients;
        this.steps = steps;
        this.servings = servings;
        this.image = image;
    }

}
