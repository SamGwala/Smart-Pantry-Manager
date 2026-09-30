package com.example.smartpantry.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantry.R;
import com.example.smartpantry.adapter.PantryAdapter;
import com.example.smartpantry.database.DatabaseHelper;
import com.example.smartpantry.model.Ingredient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

/**
 Launcher screen: shows every ingredient currently in the pantry.

 */
public class PantryListActivity extends AppCompatActivity {

    public static final String EXTRA_INGREDIENT_ID = "extra_ingredient_id";

    private DatabaseHelper databaseHelper;
    private RecyclerView recyclerPantry;
    private TextView textEmptyPantry;
    private PantryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        databaseHelper = new DatabaseHelper(this);
        recyclerPantry = findViewById(R.id.recyclerPantry);
        textEmptyPantry = findViewById(R.id.textEmptyPantry);
        recyclerPantry.setLayoutManager(new LinearLayoutManager(this));

        FloatingActionButton fab = findViewById(R.id.fabAddIngredient);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(PantryListActivity.this, AddEditIngredientActivity.class);
            startActivity(intent);
        });

        setupBottomNav();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload every time we come back to this screen, in case an
        // ingredient was added, edited or deleted on the other screen.
        loadPantryItems();
    }

    private void loadPantryItems() {
        List<Ingredient> ingredients = databaseHelper.getAllIngredients();

        if (ingredients.isEmpty()) {
            textEmptyPantry.setVisibility(TextView.VISIBLE);
            recyclerPantry.setVisibility(RecyclerView.GONE);
        } else {
            textEmptyPantry.setVisibility(TextView.GONE);
            recyclerPantry.setVisibility(RecyclerView.VISIBLE);
        }

        if (adapter == null) {
            adapter = new PantryAdapter(ingredients, ingredient -> {
                Intent intent = new Intent(PantryListActivity.this, AddEditIngredientActivity.class);
                intent.putExtra(EXTRA_INGREDIENT_ID, ingredient.getId());
                startActivity(intent);
            });
            recyclerPantry.setAdapter(adapter);
        } else {
            adapter.updateData(ingredients);
        }
    }

    private void setupBottomNav() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_pantry);
        bottomNav.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_pantry) {
                    return true; // already here
                } else if (id == R.id.nav_recipes) {
                    startActivity(new Intent(PantryListActivity.this, SuggestedRecipesActivity.class));
                    return true;
                } else if (id == R.id.nav_settings) {
                    startActivity(new Intent(PantryListActivity.this, SettingsActivity.class));
                    return true;
                }
                return false;
            }
        });
    }
}
