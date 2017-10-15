package com.example.alit.bakingappnano;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by AliT on 10/10/17.
 */

public class StepRecyclerViewAdapter extends RecyclerView.Adapter<StepRecyclerViewAdapter.StepViewHolder> {

    ArrayList<String> steps;

    StepItemClickListener stepItemClickListener;

    public StepRecyclerViewAdapter(ArrayList<String> steps, StepItemClickListener stepItemClickListener) {

        this.steps = steps;
        this.stepItemClickListener = stepItemClickListener;

    }

    @Override
    public StepViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.step_adapter_item_layout, parent, false);

        return new StepViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(StepViewHolder holder, int position) {

        int stepNum = position + 1;

        holder.stepNumber.setText("Step " + stepNum);
        holder.shortDesc.setText(steps.get(position));

    }

    @Override
    public int getItemCount() {
        return steps.size();
    }

    public void update(ArrayList<String> steps) {
        this.steps = steps;
        notifyDataSetChanged();
    }

    public interface StepItemClickListener {

        void stepItemClicked(int position);

    }

    public class StepViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.stepNumber) TextView stepNumber;

        @BindView(R.id.shortDesc) TextView shortDesc;

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
