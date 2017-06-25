package com.ofoegbuvgmail.funbaking.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ofoegbuvgmail.funbaking.R;
import com.ofoegbuvgmail.funbaking.adapter.RecipeDetailAdapter;
import com.ofoegbuvgmail.funbaking.adapter.RecipeDetailAdapter.OnStepClickListener;
import com.ofoegbuvgmail.funbaking.model.Ingredient;
import com.ofoegbuvgmail.funbaking.model.Recipe;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.ofoegbuvgmail.funbaking.adapter.RecipeAdapter.RECIPE;


public class RecipeDetailsFragment extends Fragment {

    @BindView(R.id.recipe_steps_recycler)
    RecyclerView mStepsRecyclerView;
    @BindView(R.id.ingredient_recycler_view)
    RecyclerView mIngredientRecyclerView;
    private Recipe mRecipe;
    private OnStepClickListener mStepClickListener;
    private Unbinder unbinder;

    public RecipeDetailsFragment() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mStepClickListener = (OnStepClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    " Must implement OnStepClickListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mRecipe = savedInstanceState.getParcelable(RECIPE);
        }
        final View rootView = inflater.inflate(R.layout.fragment_recipe_details, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        mRecipe = getActivity().getIntent().getParcelableExtra(RECIPE);
        IngredientAdapter ingredientAdapter = new IngredientAdapter(getContext(), mRecipe.getIngredients());
        RecipeDetailAdapter mAdapter = new RecipeDetailAdapter(getContext(), mRecipe, mStepClickListener);
        mIngredientRecyclerView.setAdapter(ingredientAdapter);
        mStepsRecyclerView.setAdapter(mAdapter);

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(RECIPE, mRecipe);
    }


    class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder> {

        private ArrayList<Ingredient> mIngredients;
        private Context mContext;

        public IngredientAdapter(Context mContext, ArrayList<Ingredient> mIngredients) {
            this.mIngredients = mIngredients;
            this.mContext = mContext;
        }

        @Override
        public IngredientViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.ingredient_item, parent, false);
            return new IngredientViewHolder(rootView);
        }

        @Override
        public void onBindViewHolder(IngredientViewHolder holder, int position) {
            Ingredient ingredient = mIngredients.get(position);
            String ingredientName = ingredient.getIngredient();
            Double ingredientQuantity = ingredient.getQuantity();
            String ingredientMeasure = ingredient.getMeasure();

            holder.ingredientTextView.setText(ingredientName + " (" + ingredientQuantity + " " + ingredientMeasure + ")");

        }

        @Override
        public int getItemCount() {
            if (mIngredients == null) {
                return 0;
            }
            return mIngredients.size();
        }

        class IngredientViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.ingredient_item_text)
            TextView ingredientTextView;

            private IngredientViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }
}
