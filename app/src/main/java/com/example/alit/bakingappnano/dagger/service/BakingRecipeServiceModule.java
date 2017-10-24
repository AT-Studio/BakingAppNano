package com.example.alit.bakingappnano.dagger.service;

import com.example.alit.bakingappnano.utils.BakingRecipeService;
import com.google.gson.Gson;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by AliT on 10/23/17.
 */

@Module
public class BakingRecipeServiceModule {

    @Provides
    @BakingServiceScope
    public BakingRecipeService bakingRecipeService(Retrofit retrofit) {
        return retrofit.create(BakingRecipeService.class);
    }

    @Provides
    @BakingServiceScope
    public Retrofit retrofit(OkHttpClient okHttpClient, Gson gson) {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("https://d17h27t6h515a5.cloudfront.net")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson));
        return builder.build();
    }

    @Provides
    @BakingServiceScope
    public Gson gson() {
        return new Gson();
    }

}
