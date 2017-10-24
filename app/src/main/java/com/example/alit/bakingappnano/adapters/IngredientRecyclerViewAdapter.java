package com.example.alit.bakingappnano.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.alit.bakingappnano.R;
import com.example.alit.bakingappnano.myDatastructures.Ingredient;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by AliT on 10/10/17.
 */

public class IngredientRecyclerViewAdapter extends RecyclerView.Adapter<IngredientRecyclerViewAdapter.IngredientViewHolder> {

    private ArrayList<Ingredient> ingredients;

    public IngredientRecyclerViewAdapter(ArrayList<Ingredient> ingredients) {

        this.ingredients = ingredients;

    }

    @Override
    public IngredientViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.ingredient_adapter_item_layout, parent, false);

        return new IngredientViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(IngredientViewHolder holder, int position) {

        Ingredient ingredient = ingredients.get(position);

        String quantity;

        if (ingredient.quantity == (int) ingredient.quantity) quantity = Integer.toString((int) ingredient.quantity);
        else quantity = Float.toString(ingredient.quantity);

        holder.nameText.setText(ingredient.ingredient);
        holder.quantityText.setText(quantity);
        holder.measureText.setText(ingredient.measure);

    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    public void update(ArrayList<Ingredient> ingredients) {
        this.ingredients = ingredients;
        notifyDataSetChanged();
    }

    public class IngredientViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.nameText)
        TextView nameText;
        @BindView(R.id.quantityText)
        TextView quantityText;
        @BindView(R.id.measureText)
        TextView measureText;

        public IngredientViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

        }

    }
}
