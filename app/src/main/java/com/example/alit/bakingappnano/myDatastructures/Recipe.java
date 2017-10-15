package com.example.alit.bakingappnano.myDatastructures;

import java.util.ArrayList;

/**
 * Created by AliT on 10/7/17.
 */

public class Recipe {

    public RecipeDescription recipeDescription;
    public ArrayList<Ingredient> ingredients;
    public ArrayList<Step> steps;

    public Recipe(RecipeDescription recipeDescription, ArrayList<Ingredient> ingredients, ArrayList<Step> steps) {

        this.recipeDescription = recipeDescription;
        this.ingredients = ingredients;
        this.steps = steps;

    }

}
