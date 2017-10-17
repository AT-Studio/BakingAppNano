package com.example.alit.bakingappnano;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.alit.bakingappnano.myDatastructures.Ingredient;

import java.util.ArrayList;

/**
 * Created by AliT on 10/16/17.
 */

public class IngredientArrayAdapter extends ArrayAdapter<Ingredient> {

    private final Context context;
    private final int layoutResourceId;
    private final ArrayList<Ingredient> ingredients;

    public IngredientArrayAdapter(Context context, int layoutResourceId, ArrayList<Ingredient> ingredients) {

        super(context, layoutResourceId, ingredients);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.ingredients = ingredients;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Ingredient ingredient = ingredients.get(position);

        TextView nameText = (TextView) convertView.findViewById(R.id.nameText);
        TextView quantityText = (TextView) convertView.findViewById(R.id.quantityText);
        TextView measureText = (TextView) convertView.findViewById(R.id.measureText);

        nameText.setText(ingredient.ingredient);
        quantityText.setText(Integer.toString(ingredient.quanitiy));
        measureText.setText(ingredient.measure);

        return convertView;
    }

    @Override
    public int getCount() {
        return ingredients.size();
    }
}
