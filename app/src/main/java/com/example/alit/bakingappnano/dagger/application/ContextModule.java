package com.example.alit.bakingappnano.dagger.application;

import android.content.Context;

import dagger.Module;
import dagger.Provides;

/**
 * Created by AliT on 10/23/17.
 */

@Module
public class ContextModule {

    public Context context;

    public  ContextModule(Context context) {
        this.context = context;
    }

    @Provides
    @BakingActivityScope
    public Context context() {
        return context;
    }

}
