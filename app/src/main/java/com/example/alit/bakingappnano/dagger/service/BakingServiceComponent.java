package com.example.alit.bakingappnano.dagger.service;

import com.example.alit.bakingappnano.dagger.activity.BakingApplicationComponent;
import com.example.alit.bakingappnano.services.RecipeJobService;
import com.example.alit.bakingappnano.services.RecipeService;

import dagger.Component;

/**
 * Created by AliT on 10/23/17.
 */

@BakingServiceScope
@Component (modules = BakingRecipeServiceModule.class, dependencies = BakingApplicationComponent.class)
public interface BakingServiceComponent {

    void inject(RecipeService recipeService);

    void inject(RecipeJobService recipeJobService);

}
