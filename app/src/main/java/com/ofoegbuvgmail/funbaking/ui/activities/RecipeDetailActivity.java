package com.ofoegbuvgmail.funbaking.ui.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.ofoegbuvgmail.funbaking.R;
import com.ofoegbuvgmail.funbaking.adapter.RecipeDetailAdapter;
import com.ofoegbuvgmail.funbaking.model.Ingredient;
import com.ofoegbuvgmail.funbaking.model.Recipe;
import com.ofoegbuvgmail.funbaking.ui.fragments.StepDetailFragment;

import java.util.ArrayList;

import static com.ofoegbuvgmail.funbaking.adapter.RecipeAdapter.RECIPE;
import static com.ofoegbuvgmail.funbaking.adapter.RecipeDetailAdapter.STEPS;
import static com.ofoegbuvgmail.funbaking.data.RecipeContract.RECIPE_CONTENT_URI;
import static com.ofoegbuvgmail.funbaking.data.RecipeContract.RecipeEntry;

public class RecipeDetailActivity extends AppCompatActivity implements RecipeDetailAdapter.OnStepClickListener {

    public final static String POSITION = "Position";
    public final static String PANES = "Panes";
    private final static String TAG = RecipeDetailActivity.class.getName();
    private boolean mTwoPane;
    private Recipe mRecipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        mRecipe = getIntent().getParcelableExtra(RECIPE);
        getSupportActionBar().setTitle(mRecipe.getName());
        mTwoPane = findViewById(R.id.step_detail_linear_layout) != null;
    }

    @Override
    public void onStepClick(int position) {
        Bundle bundle = new Bundle();

        if (mTwoPane) {
            StepDetailFragment fragment = new StepDetailFragment();
            bundle.putInt(POSITION, position);
            bundle.putBoolean(PANES, mTwoPane);
            bundle.putParcelableArrayList(STEPS, mRecipe.getSteps());
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.step_detail_container, fragment)
                    .commit();

        } else {
            bundle.putInt(POSITION, position);
            bundle.putBoolean(PANES, mTwoPane);
            bundle.putParcelableArrayList(STEPS, mRecipe.getSteps());
            bundle.putParcelable(RECIPE, mRecipe);
            Intent intent = new Intent(RecipeDetailActivity.this, StepDetailActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_recipe_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_favorite:
                if (isFavorite()) {
                    removeRecipeFromFavorites();
                    item.setIcon(R.drawable.ic_favorite_normal);
                    Toast.makeText(this, String.format(getString(R.string.favorite_removed_message), mRecipe.getName()), Toast.LENGTH_LONG).show();
                } else {
                    addRecipeToFavorites();
                    item.setIcon(R.drawable.ic_favorite_added);
                    Toast.makeText(this, String.format(getString(R.string.favorite_added_message), mRecipe.getName()), Toast.LENGTH_LONG).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem menuItem = menu.findItem(R.id.action_favorite);
        if (isFavorite()) {
            menuItem.setIcon(R.drawable.ic_favorite_added);
        } else {
            menuItem.setIcon(R.drawable.ic_favorite_normal);
        }
        return true;
    }

    private boolean isFavorite() {
        String[] projection = {RecipeEntry.COLUMN_RECIPE_ID};
        String selection = RecipeEntry.COLUMN_RECIPE_ID + " = " + mRecipe.getId();
        Cursor cursor = getContentResolver().query(RECIPE_CONTENT_URI,
                projection,
                selection,
                null,
                null,
                null);

        return (cursor != null ? cursor.getCount() : 0) > 0;
    }

    synchronized private void removeRecipeFromFavorites() {
        getContentResolver().delete(RECIPE_CONTENT_URI, null, null);
    }

    synchronized private void addRecipeToFavorites() {
        getContentResolver().delete(RECIPE_CONTENT_URI, null, null);
        getIngredient(mRecipe.getIngredients());
    }

    private void getIngredient(ArrayList<Ingredient> ingredients) {

        for (Ingredient ingredient : ingredients) {
            ContentValues values = new ContentValues();
            values.put(RecipeEntry.COLUMN_RECIPE_ID, mRecipe.getId());
            values.put(RecipeEntry.COLUMN_RECIPE_NAME, mRecipe.getName());
            values.put(RecipeEntry.COLUMN_INGREDIENT_NAME, ingredient.getIngredient());
            values.put(RecipeEntry.COLUMN_INGREDIENT_MEASURE, ingredient.getMeasure());
            values.put(RecipeEntry.COLUMN_INGREDIENT_QUANTITY, ingredient.getQuantity());
            getContentResolver().insert(RECIPE_CONTENT_URI, values);
        }
    }
}
