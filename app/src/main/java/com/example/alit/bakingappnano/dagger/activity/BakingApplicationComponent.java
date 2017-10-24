package com.example.alit.bakingappnano.dagger.activity;

import dagger.Component;
import okhttp3.OkHttpClient;

/**
 * Created by AliT on 10/23/17.
 */

@BakingApplicationScope
@Component(modules = {NetworkModule.class})
public interface BakingApplicationComponent {

    OkHttpClient getOkHttpClient();

}