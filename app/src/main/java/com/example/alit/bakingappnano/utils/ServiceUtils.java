package com.example.alit.bakingappnano.utils;

import android.content.Context;
import android.util.Log;

import com.example.alit.bakingappnano.services.RecipeJobService;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import java.util.concurrent.TimeUnit;

/**
 * Created by AliT on 10/9/17.
 */

public class ServiceUtils {

    private static final int SYNC_INTERVAL_MINUTES = 60;
    private static final int SYNC_INTERVAL_SECONDS = (int) (TimeUnit.MINUTES.toSeconds(SYNC_INTERVAL_MINUTES));

    public static final String ACTION_RECIPES_FETCHED = "recipesFetched";

    private static final String RECIPE_JOB_TAG = "recipeJobTag";

    private static final String TAG = "TESTSTUFF";

    public static boolean isScheduled;

    private ServiceUtils() {}

    synchronized public static void scheduleRecipeJob(Context context) {

        Log.d(TAG, "scheduleRecipeJob called");

        if (isScheduled) return;

        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        Job recipeJob = dispatcher.newJobBuilder()
                .setService(RecipeJobService.class)
                .setTag(RECIPE_JOB_TAG)
                .setConstraints(Constraint.DEVICE_CHARGING)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(
                        SYNC_INTERVAL_SECONDS,
                        SYNC_INTERVAL_SECONDS + SYNC_INTERVAL_SECONDS
                ))
                .setReplaceCurrent(true)
                .build();

        dispatcher.schedule(recipeJob);

        isScheduled = true;

    }

}
