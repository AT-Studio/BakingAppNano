package com.example.alit.bakingappnano;

import android.app.Application;

import com.example.alit.bakingappnano.dagger.activity.BakingApplicationComponent;
import com.example.alit.bakingappnano.dagger.activity.DaggerBakingApplicationComponent;

import timber.log.Timber;

/**
 * Created by AliT on 10/10/17.
 */

public class BakingApplication extends Application {

    BakingApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        Timber.plant(new Timber.DebugTree());

        applicationComponent = DaggerBakingApplicationComponent.builder().build();
    }

    public BakingApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }
}
