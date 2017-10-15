package com.example.alit.bakingappnano.services;

import android.os.AsyncTask;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

/**
 * Created by AliT on 10/9/17.
 */

public class RecipeJobService extends JobService {

    AsyncTask backgroundTask;

    @Override
    public boolean onStartJob(JobParameters job) {

        backgroundTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {

                FetchRecipesTask.execute(RecipeJobService.this);

                return null;
            }
        };

        backgroundTask.execute();

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {

        if (backgroundTask != null) backgroundTask.cancel(true);

        return true;
    }
}
