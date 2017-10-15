package com.example.alit.bakingappnano.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by AliT on 10/9/17.
 */

public class RecipeService extends IntentService {

    private final String TAG = "TESTSTUFF";

    public RecipeService() {
        super("RecipeService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        Log.d(TAG, "service started");

        FetchRecipesTask.execute(this);

    }

}
