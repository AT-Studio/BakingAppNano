package com.example.alit.bakingappnano.utils;

import com.example.alit.bakingappnano.myDatastructures.Recipe;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by AliT on 10/21/17.
 */

public interface BakingRecipeService {

    @GET("/topher/2017/May/59121517_baking/baking.json")
    Call<List<Recipe>> getRecipes();

}
