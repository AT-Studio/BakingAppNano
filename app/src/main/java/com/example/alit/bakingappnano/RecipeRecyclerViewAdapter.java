package com.example.alit.bakingappnano;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.alit.bakingappnano.myDatastructures.RecipeDescription;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Created by AliT on 10/5/17.
 */

public class RecipeRecyclerViewAdapter extends RecyclerView.Adapter<RecipeRecyclerViewAdapter.RecipeViewHolder> {

    RecipeItemClickListener listener;
    ArrayList<RecipeDescription> descriptions;
    Context context;

    DisplayMetrics displayMetrics;

    int numColumns;

    private final String TAG = "TESTSTUFF";

    public RecipeRecyclerViewAdapter(ArrayList<RecipeDescription> descriptions, RecipeItemClickListener listener, Context context,
                                     int numColumns) {

        this.descriptions = descriptions;
        this.listener = listener;
        this.context = context;
        this.numColumns = numColumns;

        this.displayMetrics = context.getResources().getDisplayMetrics();

    }

    @Override
    public RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(context).inflate(R.layout.recipe_adapter_item_layout, parent, false);

        return new RecipeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecipeViewHolder holder, int position) {

        RecipeDescription description = descriptions.get(position);

        String imagePath = description.imagePath;

        if (getItemCount() - position <= numColumns) {
            FrameLayout.MarginLayoutParams params = (FrameLayout.MarginLayoutParams) holder.recipeCard.getLayoutParams();
            params.bottomMargin = getPixels(20);
            holder.recipeCard.setLayoutParams(params);
        } else {
            FrameLayout.MarginLayoutParams params = (FrameLayout.MarginLayoutParams) holder.recipeCard.getLayoutParams();
            params.bottomMargin = 0;
            holder.recipeCard.setLayoutParams(params);
        }

//        for (int i = 1; i < numColumns + 1; i++) {
//
//            Timber.d("going through last columns");
//
//            if (position == getItemCount() - i) {
//                FrameLayout.MarginLayoutParams params = (FrameLayout.MarginLayoutParams) holder.recipeCard.getLayoutParams();
//                params.bottomMargin = getPixels(20);
//                holder.recipeCard.setLayoutParams(params);
//            }
//
//        }

        if (position == 0) {
            holder.noImageText.setVisibility(View.GONE);
            holder.recipeImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.cupcake));
        }
        else if (position == 1) {
            holder.noImageText.setVisibility(View.GONE);
            holder.recipeImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.brownie));
        }
        else if (imagePath != null && !imagePath.isEmpty()) {
            Picasso.with(context).load(description.imagePath).into(holder.recipeImage);
            holder.noImageText.setVisibility(View.GONE);
            holder.recipeImage.setVisibility(View.VISIBLE);
        }
        else {
            holder.recipeImage.setVisibility(View.GONE);
            holder.noImageText.setVisibility(View.VISIBLE);
        }

        holder.recipeName.setText(description.name);

        holder.servingNum.setText(Integer.toString(description.servings));

    }

//    public void updateDescriptions(ArrayList<RecipeDescription> descriptions) {
//
//        this.descriptions = descriptions;
//        notifyDataSetChanged();
//
//    }

    @Override
    public int getItemCount() {
        return descriptions.size();
    }

    public interface RecipeItemClickListener {

        void recipeClicked(int position);

    }

    public class RecipeViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.recipeCard) CardView recipeCard;
        @BindView(R.id.recipeImage) ImageView recipeImage;
        @BindView(R.id.recipeName) TextView recipeName;
        @BindView(R.id.servingNum) TextView servingNum;
        @BindView(R.id.noImageText) TextView noImageText;

        public RecipeViewHolder(View itemView) {

            super(itemView);

            ButterKnife.bind(this, itemView);

            recipeCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Timber.d("clicked recyclerItem");
                    listener.recipeClicked(getAdapterPosition());
                }
            });

        }
    }

    public int getPixels(int dp) {

        return (int) (dp * displayMetrics.density);

    }
}
