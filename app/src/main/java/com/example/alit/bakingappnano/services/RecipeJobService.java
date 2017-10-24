package com.example.alit.bakingappnano.services;

import android.content.Intent;

import com.example.alit.bakingappnano.BakingApplication;
import com.example.alit.bakingappnano.dagger.service.BakingServiceComponent;
import com.example.alit.bakingappnano.dagger.service.DaggerBakingServiceComponent;
import com.example.alit.bakingappnano.myDatastructures.Recipe;
import com.example.alit.bakingappnano.utils.BakingRecipeService;
import com.example.alit.bakingappnano.utils.DbUtils;
import com.example.alit.bakingappnano.utils.ServiceUtils;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by AliT on 10/9/17.
 */

public class RecipeJobService extends JobService {

    @Inject
    BakingRecipeService service;

    @Override
    public void onCreate() {
        super.onCreate();
        BakingServiceComponent component = DaggerBakingServiceComponent.builder()
                .bakingApplicationComponent(((BakingApplication) getApplication()).getApplicationComponent())
                .build();
        component.inject(this);
    }

    @Override
    public boolean onStartJob(JobParameters job) {

        Call<List<Recipe>> call = service.getRecipes();

        call.enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {

                Intent intent = new Intent();
                intent.setAction(ServiceUtils.ACTION_RECIPES_FETCHED);
                RecipeJobService.this.sendBroadcast(intent);

                DbUtils.insertReponseIntoDb(RecipeJobService.this, response.body());

            }

            @Override
            public void onFailure(Call<List<Recipe>> call, Throwable t) {

            }
        });

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {

        return true;
    }
}
