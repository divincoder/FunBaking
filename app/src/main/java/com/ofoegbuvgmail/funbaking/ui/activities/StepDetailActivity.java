package com.ofoegbuvgmail.funbaking.ui.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ofoegbuvgmail.funbaking.R;
import com.ofoegbuvgmail.funbaking.model.Recipe;
import com.ofoegbuvgmail.funbaking.model.Step;
import com.ofoegbuvgmail.funbaking.ui.fragments.StepDetailFragment;

import java.util.ArrayList;

import static com.ofoegbuvgmail.funbaking.adapter.RecipeAdapter.RECIPE;
import static com.ofoegbuvgmail.funbaking.adapter.RecipeDetailAdapter.STEPS;
import static com.ofoegbuvgmail.funbaking.ui.activities.RecipeDetailActivity.PANES;
import static com.ofoegbuvgmail.funbaking.ui.activities.RecipeDetailActivity.POSITION;

public class StepDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_detail);

        if (savedInstanceState == null) {
            Recipe recipe = getIntent().getParcelableExtra(RECIPE);
            ArrayList<Step> steps = getIntent().getParcelableArrayListExtra(STEPS);
            int position = getIntent().getIntExtra(POSITION, 0);
            getSupportActionBar().setTitle(recipe.getName());
            boolean mTwoPane = getIntent().getBooleanExtra(PANES, false);
            StepDetailFragment fragment = new StepDetailFragment();
            Bundle bundle = new Bundle();
            bundle.putBoolean(PANES, mTwoPane);
            bundle.putParcelableArrayList(STEPS, steps);
            bundle.putInt(POSITION, position);
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_step_detail_container, fragment)
                    .commit();
        }

    }


}
