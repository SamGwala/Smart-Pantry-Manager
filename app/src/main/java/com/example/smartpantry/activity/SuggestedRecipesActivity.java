package com.example.smartpantry.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantry.R;
import com.example.smartpantry.adapter.RecipeAdapter;
import com.example.smartpantry.database.DatabaseHelper;
import com.example.smartpantry.database.RecipeMatcher;
import com.example.smartpantry.model.Ingredient;
import com.example.smartpantry.model.Recipe;

import java.util.List;

/**
  Runs the strict-matching rule (RecipeMatcher) against the current pantry
  and shows two lists:
 1) Recipes the user can make right now (100% of ingredients present)
 2) "Almost there" recipes missing exactly one ingredient (bonus feature)
 */
public class SuggestedRecipesActivity extends AppCompatActivity {

    public static final String EXTRA_RECIPE_ID = "extra_recipe_id";

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        databaseHelper = new DatabaseHelper(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        runMatchingAndDisplay();
    }

    private void runMatchingAndDisplay() {
        List<Ingredient> pantry = databaseHelper.getAllIngredients();
        List<Recipe> allRecipes = databaseHelper.getAllRecipes();

        List<Recipe> fullMatches = RecipeMatcher.findFullMatches(allRecipes, pantry);
        List<Recipe> almostMatches = RecipeMatcher.findAlmostMatches(allRecipes, pantry);

        TextView textEmpty = findViewById(R.id.textEmptyRecipes);
        RecyclerView recyclerSuggested = findViewById(R.id.recyclerSuggested);
        RecyclerView recyclerAlmost = findViewById(R.id.recyclerAlmost);

        if (fullMatches.isEmpty()) {
            textEmpty.setVisibility(TextView.VISIBLE);
            recyclerSuggested.setVisibility(RecyclerView.GONE);
        } else {
            textEmpty.setVisibility(TextView.GONE);
            recyclerSuggested.setVisibility(RecyclerView.VISIBLE);
        }

        recyclerSuggested.setLayoutManager(new LinearLayoutManager(this));
        recyclerSuggested.setAdapter(new RecipeAdapter(fullMatches, recipe -> openRecipeDetail(recipe)));

        recyclerAlmost.setLayoutManager(new LinearLayoutManager(this));
        recyclerAlmost.setAdapter(new RecipeAdapter(almostMatches, recipe -> openRecipeDetail(recipe), true));
    }

    private void openRecipeDetail(Recipe recipe) {
        Intent intent = new Intent(SuggestedRecipesActivity.this, RecipeDetailActivity.class);
        intent.putExtra(EXTRA_RECIPE_ID, recipe.getId());
        startActivity(intent);
    }
}
