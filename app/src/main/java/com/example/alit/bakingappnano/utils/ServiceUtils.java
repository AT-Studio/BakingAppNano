package com.example.alit.bakingappnano.utils;

import android.content.Context;

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

    private static final int SYNC_INTERVAL_WINDOW = (int) (TimeUnit.MINUTES.toSeconds(30));

    public static final String ACTION_RECIPES_FETCHED = "recipesFetched";

    private static final String RECIPE_JOB_TAG = "recipeJobTag";

    private static boolean isScheduled;

    private ServiceUtils() {
    }

    synchronized public static void scheduleRecipeJob(Context context, int interval) {

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
                        interval,
                        interval + SYNC_INTERVAL_WINDOW
                ))
                .setReplaceCurrent(true)
                .build();

        dispatcher.schedule(recipeJob);

        isScheduled = true;

    }

    public static void restartJob(Context context, int interval) {

        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        dispatcher.cancel(RECIPE_JOB_TAG);
        isScheduled = false;

        scheduleRecipeJob(context, interval);

    }

}
