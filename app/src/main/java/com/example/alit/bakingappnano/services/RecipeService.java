package com.example.alit.bakingappnano.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

/**
 * Created by AliT on 10/9/17.
 */

public class RecipeService extends IntentService {

    public RecipeService() {
        super("RecipeService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        FetchRecipesTask.execute(this);

    }

}
