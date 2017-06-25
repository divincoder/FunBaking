package com.ofoegbuvgmail.funbaking.ui.activities;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ofoegbuvgmail.funbaking.R;
import com.ofoegbuvgmail.funbaking.adapter.RecipeAdapter;
import com.ofoegbuvgmail.funbaking.model.Recipe;
import com.ofoegbuvgmail.funbaking.rest.ApiUtil;
import com.ofoegbuvgmail.funbaking.rest.BakingApiInterface;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ofoegbuvgmail.funbaking.adapter.RecipeAdapter.RECIPE;

public class MainActivity extends AppCompatActivity {

    private RecipeAdapter mAdapter;
    @BindView(R.id.recipe_rv)
    RecyclerView mRecyclerView;
    @BindView(R.id.progressBar)
    ProgressBar mLoadingIndicator;
    @BindView(R.id.refresh_fab)
    FloatingActionButton fab;
    private BakingApiInterface mBakingApiInterface;
    private ArrayList<Recipe> mRecipesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if (savedInstanceState != null) {
            mRecipesList = savedInstanceState.getParcelableArrayList(RECIPE);
            displayData();
        } else {
            loadRecipes();
        }

    }

    private void loadRecipes() {
        mBakingApiInterface = ApiUtil.getApiInterface();
        mBakingApiInterface.getRecipe().enqueue(new Callback<ArrayList<Recipe>>() {
            @Override
            public void onResponse(Call<ArrayList<Recipe>> call, Response<ArrayList<Recipe>> response) {
                if (response.isSuccessful()) {
                    mLoadingIndicator.setVisibility(View.INVISIBLE);
                    //List<Recipe> recipes = response.body();
                    mRecipesList = response.body();
                    displayData();
                    //mAdapter.updateRecipes(recipes);

                } else {
                    mLoadingIndicator.setVisibility(View.INVISIBLE);
                    Toast.makeText(MainActivity.this, "Check Internet Connectivity", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Recipe>> call, Throwable t) {

                showErrorMessage();
                t.printStackTrace();
            }
        });
    }

    public void showErrorMessage() {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        Toast.makeText(this, "Error loading Recipes", Toast.LENGTH_SHORT).show();
    }

    public void displayData() {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        RecipeAdapter recipeAdapter = new RecipeAdapter(MainActivity.this, mRecipesList);
        mRecyclerView.setAdapter(recipeAdapter);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(RECIPE, mRecipesList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public void refreshRecipes(View view) {
        mLoadingIndicator.setVisibility(View.VISIBLE);
        loadRecipes();
    }
}
