package com.example.smartpantry.activity;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.smartpantry.R;
import com.example.smartpantry.database.DatabaseHelper;
import com.example.smartpantry.model.Recipe;
import com.example.smartpantry.model.RecipeIngredient;

import java.util.List;

/**
 Shows the full ingredient list and preparation steps for one recipe.
 */
public class RecipeDetailActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        databaseHelper = new DatabaseHelper(this);

        long recipeId = getIntent().getLongExtra(SuggestedRecipesActivity.EXTRA_RECIPE_ID, -1);
        Recipe recipe = databaseHelper.getRecipeById(recipeId);

        TextView textTitle = findViewById(R.id.textRecipeTitle);
        TextView textIngredients = findViewById(R.id.textIngredientsList);
        TextView textSteps = findViewById(R.id.textSteps);

        if (recipe != null) {
            getSupportActionBar().setTitle(recipe.getName());
            textTitle.setText(recipe.getName());
            textSteps.setText(recipe.getSteps());

            // Build the ingredient list text with a simple loop
            // (avoids StringBuilder streams / joining shortcuts for readability).
            String ingredientsText = "";
            List<RecipeIngredient> required = recipe.getRequiredIngredients();
            for (int i = 0; i < required.size(); i++) {
                RecipeIngredient item = required.get(i);
                ingredientsText = ingredientsText + "- " + item.getQuantity() + " " + item.getUnit()
                        + " " + item.getName() + "\n";
            }
            textIngredients.setText(ingredientsText);
        } else {
            textTitle.setText("Recipe not found");
        }
    }
}
