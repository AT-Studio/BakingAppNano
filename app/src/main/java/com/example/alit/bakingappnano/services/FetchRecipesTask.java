package com.example.alit.bakingappnano.services;

import android.content.Context;
import android.content.Intent;

import com.example.alit.bakingappnano.utils.NetworkUtils;
import com.example.alit.bakingappnano.utils.ServiceUtils;

/**
 * Created by AliT on 10/9/17.
 */

public class FetchRecipesTask {

    private FetchRecipesTask() {
    }

    public static boolean DbFilled;

    public static void execute(Context context) {

        String reponse = NetworkUtils.getRecipes();

        Intent intent = new Intent();
        intent.setAction(ServiceUtils.ACTION_RECIPES_FETCHED);
        context.sendBroadcast(intent);

//        DbFilled = DbUtils.insertReponseIntoDb(context, reponse);

    }

}
