package com.ofoegbuvgmail.funbaking.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ofoegbuvgmail.funbaking.R;
import com.ofoegbuvgmail.funbaking.model.Recipe;
import com.ofoegbuvgmail.funbaking.model.Step;
import com.ofoegbuvgmail.funbaking.ui.activities.RecipeDetailActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeDetailAdapter extends RecyclerView.Adapter<RecipeDetailAdapter.StepViewHolder> {

    public final static String STEPS = "Step";
    private final OnStepClickListener mClickListener;
    private Context mContext;
    private Recipe mRecipe;

    public RecipeDetailAdapter(Context mContext, Recipe mRecipe, OnStepClickListener mClickListener) {
        this.mClickListener = mClickListener;
        this.mContext = mContext;
        this.mRecipe = mRecipe;
    }

    @Override
    public StepViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_step_item, parent, false);
        return new StepViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(StepViewHolder holder, int position) {

        Step step = mRecipe.getSteps().get(position);
        String stepId = step.getId().toString() + ".";
        holder.stepTitleId.setText(stepId);
        holder.stepTitleTextView.setText(step.getShortDescription());
    }

    @Override
    public int getItemCount() {
        if (mRecipe == null)
            return 0;
        return mRecipe.getSteps().size();
    }

    public interface OnStepClickListener {
        void onStepClick(int position);
    }

    class StepViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.step_title)
        TextView stepTitleTextView;
        @BindView(R.id.step_title_id)
        TextView stepTitleId;

        public StepViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mClickListener.onStepClick(getAdapterPosition());
        }
    }

}
