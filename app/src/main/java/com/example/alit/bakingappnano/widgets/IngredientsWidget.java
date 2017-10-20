package com.example.alit.bakingappnano.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.RemoteViews;

import com.example.alit.bakingappnano.R;
import com.example.alit.bakingappnano.recipeDetail.RecipeDetailActivity;
import com.example.alit.bakingappnano.recipeProvider.RecipesTable;
import com.example.alit.bakingappnano.services.WidgetRemoteViewsService;

/**
 * Implementation of App Widget functionality.
 */
public class IngredientsWidget extends AppWidgetProvider {

    public static final String ACTION_ITEM_CLICKED = "itemClicked";
    public static final String EXTRA_ITEM_POSITION = "itemPosition";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, String recipe, long recipeId) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.ingredients_widget);

        Intent detailIntent = new Intent(context, RecipeDetailActivity.class);

        Bundle bundle = new Bundle();
        bundle.putLong(RecipesTable._ID, recipeId);
        bundle.putString(RecipesTable.NAME, recipe);

        detailIntent.putExtras(bundle);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, detailIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        views.setOnClickPendingIntent(R.id.widgetMainLayout, pendingIntent);

        views.setTextViewText(R.id.appwidget_text, recipe);

        Intent intent = new Intent(context, WidgetRemoteViewsService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        intent.putExtra(RecipesTable._ID, recipeId);

        views.setRemoteAdapter(R.id.ingredientsListView, intent);

        Intent testIntent = new Intent(context, IngredientsWidget.class);
        testIntent.setAction(ACTION_ITEM_CLICKED);
        PendingIntent pendingIntent1 = PendingIntent.getBroadcast(context, 0, testIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.ingredientsListView, pendingIntent1);

        WidgetRemoteViewsService.updateRecipeId(recipeId);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.ingredientsListView);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION_ITEM_CLICKED)) {
            int viewIndex = intent.getIntExtra(EXTRA_ITEM_POSITION, 0);
            WidgetRemoteViewsService.setItemClicked(viewIndex, context);
        }
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
    }

    public static void updateIngredientsWidget(Context context, AppWidgetManager appWidgetManager,
                                               String recipeName, long recipeId, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, recipeName, recipeId);
        }
    }

    @Override
    public void onEnabled(Context context) {
    }

    @Override
    public void onDisabled(Context context) {
    }
}

