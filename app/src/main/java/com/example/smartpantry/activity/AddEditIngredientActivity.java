package com.example.smartpantry.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.smartpantry.R;
import com.example.smartpantry.database.DatabaseHelper;
import com.example.smartpantry.model.Ingredient;

/**
 Handles both "Add new ingredient" and "Edit existing ingredient
 */
public class AddEditIngredientActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private EditText editName, editQuantity, editUnit, editExpiry;
    private Button btnSave, btnDelete;

    private long ingredientId = -1; // -1 means "adding a new ingredient"
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_ingredient);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        databaseHelper = new DatabaseHelper(this);

        editName = findViewById(R.id.editName);
        editQuantity = findViewById(R.id.editQuantity);
        editUnit = findViewById(R.id.editUnit);
        editExpiry = findViewById(R.id.editExpiry);
        btnSave = findViewById(R.id.btnSave);
        btnDelete = findViewById(R.id.btnDelete);

        // Check if we were opened to edit an existing ingredient.
        if (getIntent().hasExtra(PantryListActivity.EXTRA_INGREDIENT_ID)) {
            ingredientId = getIntent().getLongExtra(PantryListActivity.EXTRA_INGREDIENT_ID, -1);
            isEditMode = true;
        }

        if (isEditMode) {
            getSupportActionBar().setTitle("Edit Ingredient");
            btnDelete.setVisibility(Button.VISIBLE);
            loadIngredientIntoForm();
        } else {
            getSupportActionBar().setTitle("Add Ingredient");
            btnDelete.setVisibility(Button.GONE);
        }

        btnSave.setOnClickListener(v -> saveIngredient());
        btnDelete.setOnClickListener(v -> deleteIngredient());
    }

    private void loadIngredientIntoForm() {
        Ingredient ingredient = databaseHelper.getIngredientById(ingredientId);
        if (ingredient != null) {
            editName.setText(ingredient.getName());
            editQuantity.setText(String.valueOf(ingredient.getQuantity()));
            editUnit.setText(ingredient.getUnit());
            editExpiry.setText(ingredient.getExpiryDate());
        }
    }

    /**
     Validates the form, then either inserts a new row (Create) or
     updates the existing row (Update)
     */
    private void saveIngredient() {
        String name = editName.getText().toString().trim();
        String quantityText = editQuantity.getText().toString().trim();
        String unit = editUnit.getText().toString().trim();
        String expiry = editExpiry.getText().toString().trim();

        // --- Input validation ---
        if (name.isEmpty()) {
            editName.setError(getString(R.string.error_name_required));
            return;
        }

        if (unit.isEmpty()) {
            editUnit.setError(getString(R.string.error_unit_required));
            return;
        }

        double quantity;
        if (quantityText.isEmpty()) {
            editQuantity.setError(getString(R.string.error_quantity_required));
            return;
        }
        try {
            quantity = Double.parseDouble(quantityText);
        } catch (NumberFormatException e) {
            editQuantity.setError(getString(R.string.error_quantity_required));
            return;
        }
        if (quantity <= 0) {
            editQuantity.setError(getString(R.string.error_quantity_required));
            return;
        }

        // --- Save to database ---
        Ingredient ingredient = new Ingredient(ingredientId, name, quantity, unit, expiry);

        if (isEditMode) {
            databaseHelper.updateIngredient(ingredient);
            Toast.makeText(this, "Ingredient updated", Toast.LENGTH_SHORT).show();
        } else {
            databaseHelper.addIngredient(ingredient);
            Toast.makeText(this, "Ingredient added", Toast.LENGTH_SHORT).show();
        }

        finish(); // return to the pantry list, which reloads in onResume()
    }

    private void deleteIngredient() {
        if (ingredientId != -1) {
            databaseHelper.deleteIngredient(ingredientId);
            Toast.makeText(this, "Ingredient deleted", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
