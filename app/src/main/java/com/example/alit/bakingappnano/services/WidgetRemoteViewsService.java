package com.example.alit.bakingappnano.services;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.alit.bakingappnano.R;
import com.example.alit.bakingappnano.myDatastructures.Ingredient;
import com.example.alit.bakingappnano.recipeProvider.IngredientsTable;
import com.example.alit.bakingappnano.recipeProvider.RecipesProvider;
import com.example.alit.bakingappnano.recipeProvider.RecipesTable;
import com.example.alit.bakingappnano.widgets.IngredientsWidget;

import java.util.ArrayList;

/**
 * Created by AliT on 10/16/17.
 */

public class WidgetRemoteViewsService extends RemoteViewsService {

    public static ArrayList<WidgetRemoteViewsFactory> viewsFactories = new ArrayList<>();

    public static void setItemClicked(int position, Context context) {

        for (WidgetRemoteViewsFactory factory : viewsFactories) {

            factory.itemClicked(position);

        }

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, IngredientsWidget.class));
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.ingredientsListView);

    }

    public static void updateRecipeId(long id) {

        for (WidgetRemoteViewsFactory factory : viewsFactories) {
            factory.setRecipeId(id);
        }

    }

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        WidgetRemoteViewsFactory factory = new WidgetRemoteViewsFactory(this.getApplicationContext(), intent.getLongExtra(RecipesTable._ID, -1));
        viewsFactories.add(factory);
        return factory;
    }

    class WidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

        private long recipeId;

        private ArrayList<Ingredient> ingredients;
        private ArrayList<Boolean> isChecked;

        private Context context;

        private boolean calledOnCreate;

        private boolean dontResetLists;

        public WidgetRemoteViewsFactory(Context context, long recipeId) {
            this.context = context;
            this.recipeId = recipeId;
            ingredients = new ArrayList<>();
            isChecked = new ArrayList<>();
        }

        public void setRecipeId(long recipeId) {
            this.recipeId = recipeId;
        }

        @Override
        public void onCreate() {

            calledOnCreate = true;

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

            data.close();

        }

        @Override
        public void onDataSetChanged() {

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
        public int getCount() {
            return ingredients.size();
        }

        @Override
        public RemoteViews getViewAt(int i) {

            Ingredient ingredient = ingredients.get(i);

            RemoteViews view = new RemoteViews(context.getPackageName(), R.layout.widget_ingredient_item_layout);

            view.setTextViewText(R.id.nameText, ingredient.ingredient);
            view.setTextViewText(R.id.quantityText, Float.toString(ingredient.quantity));
            view.setTextViewText(R.id.measureText, ingredient.measure);

            boolean checked = isChecked.get(i);

            if (checked) {
                view.setViewVisibility(R.id.checkmark, View.VISIBLE);
            } else {
                view.setViewVisibility(R.id.checkmark, View.INVISIBLE);
            }

            Bundle bundle = new Bundle();
            bundle.putInt(IngredientsWidget.EXTRA_ITEM_POSITION, i);
            Intent intent = new Intent();
            intent.putExtras(bundle);

            view.setOnClickFillInIntent(R.id.widgetItemLayout, intent);

            return view;
        }

        public void itemClicked(int position) {

            boolean checked = isChecked.get(position);

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

        @Override
        public void onDestroy() {

        }
    }
}
