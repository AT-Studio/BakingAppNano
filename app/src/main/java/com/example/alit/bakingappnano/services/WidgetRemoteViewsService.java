package com.example.alit.bakingappnano.services;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.alit.bakingappnano.IngredientsWidget;
import com.example.alit.bakingappnano.R;
import com.example.alit.bakingappnano.myDatastructures.Ingredient;
import com.example.alit.bakingappnano.recipeProvider.IngredientsTable;
import com.example.alit.bakingappnano.recipeProvider.RecipesProvider;
import com.example.alit.bakingappnano.recipeProvider.RecipesTable;

import java.util.ArrayList;

import timber.log.Timber;

/**
 * Created by AliT on 10/16/17.
 */

public class WidgetRemoteViewsService extends RemoteViewsService {

    public static ArrayList<WidgetRemoteViewsFactory> viewsFactories = new ArrayList<>();

    public static void setItemClicked(int position, Context context) {

        Timber.d("broadcast test setItemClicked: " + position);

        for (WidgetRemoteViewsFactory factory : viewsFactories) {

            Timber.d("broadcast iterating over factories");
            factory.itemClicked(position);

        }

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, IngredientsWidget.class));
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.ingredientsListView);

    }

    BroadcastReceiver broadcastReceiver;
    IntentFilter intentFilter;

    public static void updateRecipeId(long id) {

        for (WidgetRemoteViewsFactory factory : viewsFactories) {
            factory.setRecipeId(id);
        }

    }

    @Override
    public void onCreate() {
        super.onCreate();
        Timber.d("broadcast service onCreate");
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle bundle = intent.getExtras();
                Timber.d("received broadcast service " + bundle.getInt(IngredientsWidget.EXTRA_ITEM_POSITION));
            }
        };
        intentFilter = new IntentFilter(IngredientsWidget.ACTION_ITEM_CLICKED);
//        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        Timber.d("broadcast service ondestroy");
//        unregisterReceiver(broadcastReceiver);
    }

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Timber.d("called onGetViewFactory");
        WidgetRemoteViewsFactory factory = new WidgetRemoteViewsFactory(this.getApplicationContext(), intent.getLongExtra(RecipesTable._ID, -1));
        viewsFactories.add(factory);
        return factory;
    }

    class WidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

        long recipeId;

        ArrayList<Ingredient> ingredients;
        ArrayList<Boolean> isChecked;
        ArrayList<RemoteViews> remoteViewses;

        Context context;

        boolean calledOnCreate;

        BroadcastReceiver broadcastReceiver;
        IntentFilter intentFilter;

        boolean dontResetLists;

        public WidgetRemoteViewsFactory(Context context, long recipeId) {
            this.context = context;
            this.recipeId = recipeId;
            ingredients = new ArrayList<>();
            isChecked = new ArrayList<>();
            remoteViewses = new ArrayList<>();
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Bundle bundle = intent.getExtras();
                    Timber.d("received broadcast factory" + bundle.getInt(IngredientsWidget.EXTRA_ITEM_POSITION));
                }
            };
            intentFilter = new IntentFilter(IngredientsWidget.ACTION_ITEM_CLICKED);
//            registerReceiver(broadcastReceiver, intentFilter);
        }

        public void setRecipeId(long recipeId) {
            this.recipeId = recipeId;
        }

        @Override
        public void onCreate() {

            Timber.d("called oncreate");

//            if (IngredientsWidget.recipeID == -1) return;

            calledOnCreate = true;

//            ingredients.clear();

            ContentResolver contentResolver = getContentResolver();

            Cursor data = contentResolver.query(
                    RecipesProvider.Ingredients.INGREDIENTS,
                    null,
                    IngredientsTable.RECIPE_ID + "=?",
                    new String[]{Long.toString(recipeId)},
                    null);

            while (data.moveToNext()) {

                String ingredient = data.getString(data.getColumnIndex(IngredientsTable.INGREDIENT));
                int quantity = data.getInt(data.getColumnIndex(IngredientsTable.QUANTITY));
                String measure = data.getString(data.getColumnIndex(IngredientsTable.MEASURE));

                ingredients.add(new Ingredient(quantity, measure, ingredient));

                isChecked.add(false);

            }


        }

        @Override
        public void onDataSetChanged() {
            Timber.d("called datasetChanged");

            if (calledOnCreate) {
                calledOnCreate = false;
                return;
            }

            if (dontResetLists) {
                dontResetLists = false;
                return;
            }

            Thread thread = new Thread() {
                public void run() {
                    ingredients.clear();
                    isChecked.clear();

                    ContentResolver contentResolver = getContentResolver();

                    Cursor data = contentResolver.query(
                            RecipesProvider.Ingredients.INGREDIENTS,
                            null,
                            IngredientsTable.RECIPE_ID + "=?",
                            new String[]{Long.toString(recipeId)},
                            null);

                    while (data.moveToNext()) {

                        String ingredient = data.getString(data.getColumnIndex(IngredientsTable.INGREDIENT));
                        int quantity = data.getInt(data.getColumnIndex(IngredientsTable.QUANTITY));
                        String measure = data.getString(data.getColumnIndex(IngredientsTable.MEASURE));

                        ingredients.add(new Ingredient(quantity, measure, ingredient));

                        isChecked.add(false);

                    }
                }
            };
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onDestroy() {
            Timber.d("destroy broadcast factory");
//            unregisterReceiver(broadcastReceiver);
        }

        @Override
        public int getCount() {
            return ingredients.size();
        }

        @Override
        public RemoteViews getViewAt(int i) {

            Ingredient ingredient = ingredients.get(i);

            RemoteViews view = new RemoteViews(context.getPackageName(), R.layout.widget_ingredient_item_layout);

            view.setTextViewText(R.id.nameText, ingredient.ingredient);
            view.setTextViewText(R.id.quantityText, Integer.toString(ingredient.quanitiy));
            view.setTextViewText(R.id.measureText, ingredient.measure);

            boolean checked = isChecked.get(i);

            if (checked) {
                Timber.d("broadcast item: " + i + " was checked");
                view.setViewVisibility(R.id.checkmark, View.VISIBLE);
            } else {
                Timber.d("broadcast item: " + i + " was unchecked");
                view.setViewVisibility(R.id.checkmark, View.INVISIBLE);
            }

            Bundle bundle = new Bundle();
            bundle.putInt(IngredientsWidget.EXTRA_ITEM_POSITION, i);
            Intent intent = new Intent();
            intent.putExtras(bundle);

            view.setOnClickFillInIntent(R.id.widgetItemLayout, intent);

//            if (!dontResetLists) {
//                remoteViewses.add(view);
//            }

            return view;
        }

        public void itemClicked(int position) {

//            RemoteViews view = remoteViewses.get(position);

            boolean checked = isChecked.get(position);

//            if (checked) {
//                Timber.d("broadcast item: " + position + " was checked");
//                view.setViewVisibility(R.id.checkmark, View.INVISIBLE);
//            } else {
//                Timber.d("broadcast item: " + position + " was unchecked");
//                view.setViewVisibility(R.id.checkmark, View.VISIBLE);
//            }

            isChecked.remove(position);
            isChecked.add(position, !checked);

            dontResetLists = true;

        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

    }

    private Intent getFillIntent(RemoteViews view, int checkMarkId, ArrayList<Boolean> isChecked, int position) {

        boolean checked = isChecked.get(position);

        Timber.d("triggered getFillIntent");

        if (checked) {
            Timber.d("unchecking");
            view.setViewVisibility(checkMarkId, View.INVISIBLE);
        } else {
            Timber.d("checking");
            view.setViewVisibility(checkMarkId, View.VISIBLE);
        }

        isChecked.remove(position);
        isChecked.add(position, !checked);

//        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//
//            }
//        };
//        IntentFilter intentFilter = new IntentFilter();
//        registerReceiver(broadcastReceiver, intentFilter);

        return null;
    }
}
