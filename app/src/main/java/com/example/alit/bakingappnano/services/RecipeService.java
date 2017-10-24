package com.example.alit.bakingappnano.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.example.alit.bakingappnano.BakingApplication;
import com.example.alit.bakingappnano.dagger.service.BakingServiceComponent;
import com.example.alit.bakingappnano.dagger.service.DaggerBakingServiceComponent;
import com.example.alit.bakingappnano.myDatastructures.Recipe;
import com.example.alit.bakingappnano.utils.BakingRecipeService;
import com.example.alit.bakingappnano.utils.DbUtils;
import com.example.alit.bakingappnano.utils.ServiceUtils;

import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by AliT on 10/9/17.
 */

public class RecipeService extends IntentService {

    @Inject
    BakingRecipeService service;

    public RecipeService() {
        super("RecipeService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        BakingServiceComponent component = DaggerBakingServiceComponent.builder()
                .bakingApplicationComponent(((BakingApplication) getApplication()).getApplicationComponent())
                .build();
        component.inject(this);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        Call<List<Recipe>> call = service.getRecipes();

        call.enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {

                Intent intent = new Intent();
                intent.setAction(ServiceUtils.ACTION_RECIPES_FETCHED);
                RecipeService.this.sendBroadcast(intent);

                DbUtils.insertReponseIntoDb(RecipeService.this, response.body());

            }

            @Override
            public void onFailure(Call<List<Recipe>> call, Throwable t) {

            }
        });

    }

}
