package com.ofoegbuvgmail.funbaking.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ofoegbuvgmail.funbaking.R;
import com.ofoegbuvgmail.funbaking.ui.activities.RecipeDetailActivity;
import com.ofoegbuvgmail.funbaking.model.Recipe;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    public static final String RECIPE = "recipe";
    private Context mContext;
    private ArrayList<Recipe> mRecipes;


    public RecipeAdapter(Context mContext, ArrayList<Recipe> mRecipes) {
        this.mContext = mContext;
        this.mRecipes = mRecipes;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View recipeView = layoutInflater.inflate(R.layout.recipe_item, parent, false);

        return new ViewHolder(recipeView);
    }

    @Override
    public void onBindViewHolder(RecipeAdapter.ViewHolder holder, int position) {

        Recipe recipe = mRecipes.get(position);
        holder.recipeTextView.setText(recipe.getName());
        holder.servingsTexView.append("Servings " + recipe.getServings());

        ImageView imageView = holder.recipeImageView;

        switch (recipe.getId()) {
            case 1:
                imageView.setImageResource(R.drawable.nutella_pie);
                break;
            case 2:
                imageView.setImageResource(R.drawable.brownies);
                break;
            case 3:
                imageView.setImageResource(R.drawable.yellow_cake);
                break;
            case 4:
                imageView.setImageResource(R.drawable.cheese_cake);
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemCount() {

        if (mRecipes == null) {
            return 0;
        }
        return mRecipes.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.recipe_image_view)
        ImageView recipeImageView;
        @BindView(R.id.recipe_text_view)
        TextView recipeTextView;
        @BindView(R.id.servings_text_view)
        TextView servingsTexView;

        public ViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mContext, RecipeDetailActivity.class);
            intent.putExtra(RECIPE, mRecipes.get(getAdapterPosition()));
            mContext.startActivity(intent);
        }
    }
}

