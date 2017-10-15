package com.example.alit.bakingappnano;

import android.app.Application;

import timber.log.Timber;

/**
 * Created by AliT on 10/10/17.
 */

public class BakingApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();

        Timber.plant(new Timber.DebugTree());
    }
}
