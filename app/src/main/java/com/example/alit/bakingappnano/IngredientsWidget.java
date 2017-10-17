package com.example.alit.bakingappnano;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.example.alit.bakingappnano.recipeProvider.RecipesTable;
import com.example.alit.bakingappnano.services.WidgetRemoteViewsService;

import timber.log.Timber;

/**
 * Implementation of App Widget functionality.
 */
public class IngredientsWidget extends AppWidgetProvider {

    public static int randomNumber;

    public static final String ACTION_ITEM_CLICKED = "itemClicked";
    public static final String ACTION_ITEM_CLICKED_SERVICE = "itemClickedService";
    public static final String EXTRA_ITEM_POSITION = "itemPosition";

//    public static String recipe;
//    public static long recipeID;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, String recipe, long recipeId) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.ingredients_widget);

        Intent detailIntent = new Intent(context, RecipeDetailActivity.class);

        Bundle bundle = new Bundle();
        bundle.putLong(RecipesTable._ID, recipeId);
        bundle.putString(RecipesTable.NAME, recipe);

        Timber.d("recipeId: " + recipeId);
        Timber.d("recipe: " + recipe);

        detailIntent.putExtras(bundle);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, detailIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        views.setOnClickPendingIntent(R.id.widgetMainLayout, pendingIntent);

        views.setTextViewText(R.id.appwidget_text, recipe);

        Intent intent = new Intent(context, WidgetRemoteViewsService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
//        intent.putExtra("random", randomNumber);
//        randomNumber++;
        intent.putExtra(RecipesTable._ID, recipeId);

        views.setRemoteAdapter(R.id.ingredientsListView, intent);

        Intent testIntent = new Intent(context, IngredientsWidget.class);
        testIntent.setAction(ACTION_ITEM_CLICKED);
        PendingIntent pendingIntent1 = PendingIntent.getBroadcast(context, 0, testIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.ingredientsListView, pendingIntent1);

//        IngredientArrayAdapter arrayAdapter = new IngredientArrayAdapter(context, R.layout.ingredient_adapter_item_layout, ingredients);

        // Instruct the widget manager to update the widget
        WidgetRemoteViewsService.updateRecipeId(recipeId);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.ingredientsListView);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
//        AppWidgetManager mgr = AppWidgetManager.getInstance(context);
        Timber.d("received broadcast onreceive");
        if (intent.getAction().equals(ACTION_ITEM_CLICKED)) {
//            int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
//                    AppWidgetManager.INVALID_APPWIDGET_ID);
            int viewIndex = intent.getIntExtra(EXTRA_ITEM_POSITION, 0);
            Bundle bundle = new Bundle();
            bundle.putInt(EXTRA_ITEM_POSITION, intent.getIntExtra(EXTRA_ITEM_POSITION, 0));
            Intent intent1 = new Intent();
            intent1.setAction(ACTION_ITEM_CLICKED);
            intent1.putExtras(bundle);
//            context.sendBroadcast(intent1);
            WidgetRemoteViewsService.setItemClicked(viewIndex, context);
//            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            Toast.makeText(context, "Touched view " + viewIndex, Toast.LENGTH_LONG).show();
        }
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
//        for (int appWidgetId : appWidgetIds) {
//            updateAppWidget(context, appWidgetManager, appWidgetId);
//        }
    }

    public static void updateIngredientsWidget(Context context, AppWidgetManager appWidgetManager,
                                               String recipeName, long recipeId, int[] appWidgetIds) {
//        recipe = recipeName;
//        recipeID = recipeId;
        for (int appWidgetId : appWidgetIds) {
            Timber.d("appWidgetId: " + appWidgetId);
            updateAppWidget(context, appWidgetManager, appWidgetId, recipeName, recipeId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

