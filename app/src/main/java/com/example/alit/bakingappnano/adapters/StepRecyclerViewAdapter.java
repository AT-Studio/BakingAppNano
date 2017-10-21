package com.example.alit.bakingappnano.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.alit.bakingappnano.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by AliT on 10/10/17.
 */

public class StepRecyclerViewAdapter extends RecyclerView.Adapter<StepRecyclerViewAdapter.StepViewHolder> {

    private ArrayList<String> steps;
    private ArrayList<String> thumbnails;

    private StepItemClickListener stepItemClickListener;

    private Context context;

    public StepRecyclerViewAdapter(ArrayList<String> steps, ArrayList<String> thumbnails,
                                   StepItemClickListener stepItemClickListener, Context context) {

        this.steps = steps;
        this.thumbnails = thumbnails;
        this.stepItemClickListener = stepItemClickListener;
        this.context = context;

    }

    @Override
    public StepViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(context).inflate(R.layout.step_adapter_item_layout, parent, false);

        return new StepViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final StepViewHolder holder, int position) {

        int stepNum = position + 1;

        String thumbnail = thumbnails.get(position);

        if (!TextUtils.isEmpty(thumbnail)) {
            Picasso.with(context).load(thumbnail)
                    .into(holder.stepThumbnail, new Callback() {
                        @Override
                        public void onSuccess() {
                            holder.thumbnailError.setVisibility(View.GONE);
                            holder.stepThumbnail.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError() {
                            holder.stepThumbnail.setVisibility(View.GONE);
                            holder.thumbnailError.setVisibility(View.VISIBLE);
                        }
                    });
        } else {
            holder.thumbnailError.setVisibility(View.VISIBLE);
        }

        holder.stepNumber.setText("Step " + stepNum);
        holder.shortDesc.setText(steps.get(position));

    }

    @Override
    public int getItemCount() {
        return steps.size();
    }

    public void update(ArrayList<String> steps, ArrayList<String> thumbnails) {
        this.steps = steps;
        this.thumbnails = thumbnails;
        notifyDataSetChanged();
    }

    public interface StepItemClickListener {

        void stepItemClicked(int position);

    }

    public class StepViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.stepNumber)
        TextView stepNumber;

        @BindView(R.id.stepThumbnail)
        ImageView stepThumbnail;

        @BindView(R.id.thumbnailError)
        TextView thumbnailError;

        @BindView(R.id.shortDesc)
        TextView shortDesc;

        public StepViewHolder(View itemView) {

            super(itemView);

            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    stepItemClickListener.stepItemClicked(getAdapterPosition());
                }
            });

        }
    }
}
