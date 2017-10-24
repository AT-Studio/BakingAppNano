package com.example.alit.bakingappnano.dagger.application;

import com.example.alit.bakingappnano.adapters.RecipeRecyclerViewAdapter;
import com.example.alit.bakingappnano.adapters.StepRecyclerViewAdapter;
import com.example.alit.bakingappnano.dagger.activity.BakingApplicationComponent;

import dagger.Component;

/**
 * Created by AliT on 10/23/17.
 */

@BakingActivityScope
@Component (modules = PicassoModule.class, dependencies = BakingApplicationComponent.class)
public interface BakingActivityComponent {

    void inject(RecipeRecyclerViewAdapter recipeRecyclerViewAdapter);

    void inject(StepRecyclerViewAdapter stepRecyclerViewAdapter);

}
