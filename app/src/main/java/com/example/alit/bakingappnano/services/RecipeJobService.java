package com.example.alit.bakingappnano.services;

import android.content.Intent;
import android.os.AsyncTask;

import com.example.alit.bakingappnano.myDatastructures.Recipe;
import com.example.alit.bakingappnano.utils.BakingRecipeService;
import com.example.alit.bakingappnano.utils.DbUtils;
import com.example.alit.bakingappnano.utils.ServiceUtils;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by AliT on 10/9/17.
 */

public class RecipeJobService extends JobService {

    private AsyncTask backgroundTask;

    @Override
    public boolean onStartJob(JobParameters job) {

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("https://d17h27t6h515a5.cloudfront.net")
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        BakingRecipeService service = retrofit.create(BakingRecipeService.class);
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

//        backgroundTask = new AsyncTask() {
//            @Override
//            protected Object doInBackground(Object[] objects) {
//
//                FetchRecipesTask.execute(RecipeJobService.this);
//
//                return null;
//            }
//        };
//
//        backgroundTask.execute();

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {

        if (backgroundTask != null) backgroundTask.cancel(true);

        return true;
    }
}
