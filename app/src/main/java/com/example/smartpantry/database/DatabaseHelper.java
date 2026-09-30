package com.example.smartpantry.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.smartpantry.model.Ingredient;
import com.example.smartpantry.model.Recipe;
import com.example.smartpantry.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.List;

/**
  Handles all SQLite access for the app.
  Two areas of data:
    1) pantry_items      - the ingredients the user currently has (editable by the user)
    2) recipes / recipe_ingredients - the fixed recipe book (seeded once, read-only in the app)
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "smart_pantry.db";
    private static final int DATABASE_VERSION = 1;

    // Pantry table
    public static final String TABLE_PANTRY = "pantry_items";
    public static final String COL_PANTRY_ID = "id";
    public static final String COL_PANTRY_NAME = "name";
    public static final String COL_PANTRY_QUANTITY = "quantity";
    public static final String COL_PANTRY_UNIT = "unit";
    public static final String COL_PANTRY_EXPIRY = "expiry_date";

    // Recipes table
    public static final String TABLE_RECIPES = "recipes";
    public static final String COL_RECIPE_ID = "id";
    public static final String COL_RECIPE_NAME = "name";
    public static final String COL_RECIPE_STEPS = "steps";

    // Recipe ingredients table (each row = one ingredient a recipe needs)
    public static final String TABLE_RECIPE_INGREDIENTS = "recipe_ingredients";
    public static final String COL_RI_ID = "id";
    public static final String COL_RI_RECIPE_ID = "recipe_id";
    public static final String COL_RI_NAME = "ingredient_name";
    public static final String COL_RI_QUANTITY = "quantity";
    public static final String COL_RI_UNIT = "unit";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_PANTRY + " (" +
                COL_PANTRY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_PANTRY_NAME + " TEXT NOT NULL, " +
                COL_PANTRY_QUANTITY + " REAL NOT NULL, " +
                COL_PANTRY_UNIT + " TEXT NOT NULL, " +
                COL_PANTRY_EXPIRY + " TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_RECIPES + " (" +
                COL_RECIPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_RECIPE_NAME + " TEXT NOT NULL, " +
                COL_RECIPE_STEPS + " TEXT NOT NULL)");

        db.execSQL("CREATE TABLE " + TABLE_RECIPE_INGREDIENTS + " (" +
                COL_RI_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_RI_RECIPE_ID + " INTEGER NOT NULL, " +
                COL_RI_NAME + " TEXT NOT NULL, " +
                COL_RI_QUANTITY + " REAL NOT NULL, " +
                COL_RI_UNIT + " TEXT NOT NULL, " +
                "FOREIGN KEY(" + COL_RI_RECIPE_ID + ") REFERENCES " + TABLE_RECIPES + "(" + COL_RECIPE_ID + "))");

        seedRecipes(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPE_INGREDIENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PANTRY);
        onCreate(db);
    }


    // PANTRY CRUD


    public long addIngredient(Ingredient ingredient) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_PANTRY_NAME, ingredient.getName());
        values.put(COL_PANTRY_QUANTITY, ingredient.getQuantity());
        values.put(COL_PANTRY_UNIT, ingredient.getUnit());
        values.put(COL_PANTRY_EXPIRY, ingredient.getExpiryDate());
        long newId = db.insert(TABLE_PANTRY, null, values);
        db.close();
        return newId;
    }

    public int updateIngredient(Ingredient ingredient) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_PANTRY_NAME, ingredient.getName());
        values.put(COL_PANTRY_QUANTITY, ingredient.getQuantity());
        values.put(COL_PANTRY_UNIT, ingredient.getUnit());
        values.put(COL_PANTRY_EXPIRY, ingredient.getExpiryDate());
        int rowsAffected = db.update(TABLE_PANTRY, values,
                COL_PANTRY_ID + " = ?",
                new String[]{String.valueOf(ingredient.getId())});
        db.close();
        return rowsAffected;
    }

    public void deleteIngredient(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PANTRY, COL_PANTRY_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public List<Ingredient> getAllIngredients() {
        List<Ingredient> ingredients = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PANTRY, null, null, null, null, null,
                COL_PANTRY_NAME + " ASC");

        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(COL_PANTRY_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COL_PANTRY_NAME));
                double quantity = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_PANTRY_QUANTITY));
                String unit = cursor.getString(cursor.getColumnIndexOrThrow(COL_PANTRY_UNIT));
                String expiry = cursor.getString(cursor.getColumnIndexOrThrow(COL_PANTRY_EXPIRY));
                ingredients.add(new Ingredient(id, name, quantity, unit, expiry));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return ingredients;
    }

    public Ingredient getIngredientById(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PANTRY, null, COL_PANTRY_ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null);

        Ingredient ingredient = null;
        if (cursor.moveToFirst()) {
            long ingId = cursor.getLong(cursor.getColumnIndexOrThrow(COL_PANTRY_ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(COL_PANTRY_NAME));
            double quantity = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_PANTRY_QUANTITY));
            String unit = cursor.getString(cursor.getColumnIndexOrThrow(COL_PANTRY_UNIT));
            String expiry = cursor.getString(cursor.getColumnIndexOrThrow(COL_PANTRY_EXPIRY));
            ingredient = new Ingredient(ingId, name, quantity, unit, expiry);
        }
        cursor.close();
        db.close();
        return ingredient;
    }


    // RECIPES (read-only from the app's point of view)


    public List<Recipe> getAllRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_RECIPES, null, null, null, null, null,
                COL_RECIPE_NAME + " ASC");

        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(COL_RECIPE_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COL_RECIPE_NAME));
                String steps = cursor.getString(cursor.getColumnIndexOrThrow(COL_RECIPE_STEPS));
                Recipe recipe = new Recipe(id, name, steps);
                loadIngredientsForRecipe(db, recipe);
                recipes.add(recipe);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return recipes;
    }

    public Recipe getRecipeById(long recipeId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_RECIPES, null, COL_RECIPE_ID + " = ?",
                new String[]{String.valueOf(recipeId)}, null, null, null);

        Recipe recipe = null;
        if (cursor.moveToFirst()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(COL_RECIPE_ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(COL_RECIPE_NAME));
            String steps = cursor.getString(cursor.getColumnIndexOrThrow(COL_RECIPE_STEPS));
            recipe = new Recipe(id, name, steps);
            loadIngredientsForRecipe(db, recipe);
        }
        cursor.close();
        db.close();
        return recipe;
    }

    private void loadIngredientsForRecipe(SQLiteDatabase db, Recipe recipe) {
        Cursor cursor = db.query(TABLE_RECIPE_INGREDIENTS, null,
                COL_RI_RECIPE_ID + " = ?",
                new String[]{String.valueOf(recipe.getId())}, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COL_RI_NAME));
                double quantity = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_RI_QUANTITY));
                String unit = cursor.getString(cursor.getColumnIndexOrThrow(COL_RI_UNIT));
                recipe.addRequiredIngredient(new RecipeIngredient(name, quantity, unit));
            } while (cursor.moveToNext());
        }
        cursor.close();
    }


    // SEED DATA - runs once, the first time the database is created


    private void seedRecipes(SQLiteDatabase db) {
        addRecipe(db, "Tomato Egg Stir Fry",
                "1. Beat the eggs. 2. Fry tomatoes until soft. 3. Add eggs and scramble together. 4. Season and serve.",
                new Object[][]{
                        {"egg", 3.0, "pcs"},
                        {"tomato", 2.0, "pcs"},
                        {"salt", 1.0, "tsp"}
                });

        addRecipe(db, "Simple Fried Rice",
                "1. Heat oil. 2. Add cooked rice. 3. Stir in egg and vegetables. 4. Add soy sauce and stir fry 5 minutes.",
                new Object[][]{
                        {"rice", 300.0, "g"},
                        {"egg", 2.0, "pcs"},
                        {"onion", 1.0, "pcs"},
                        {"soy sauce", 2.0, "tbsp"}
                });

        addRecipe(db, "Cheese Omelette",
                "1. Beat eggs with milk. 2. Pour into hot pan. 3. Add cheese. 4. Fold and serve.",
                new Object[][]{
                        {"egg", 2.0, "pcs"},
                        {"cheese", 50.0, "g"},
                        {"milk", 30.0, "ml"}
                });

        addRecipe(db, "Peanut Butter Toast",
                "1. Toast the bread. 2. Spread peanut butter on top. 3. Serve.",
                new Object[][]{
                        {"bread", 2.0, "pcs"},
                        {"peanut butter", 30.0, "g"}
                });

        addRecipe(db, "Banana Pancake",
                "1. Mash the banana. 2. Mix with flour and egg into a batter. 3. Fry spoonfuls until golden.",
                new Object[][]{
                        {"banana", 1.0, "pcs"},
                        {"flour", 100.0, "g"},
                        {"egg", 1.0, "pcs"}
                });

        addRecipe(db, "Chicken and Rice",
                "1. Season chicken and pan-fry until cooked. 2. Boil rice. 3. Serve chicken over rice.",
                new Object[][]{
                        {"chicken", 300.0, "g"},
                        {"rice", 200.0, "g"},
                        {"salt", 1.0, "tsp"}
                });

        addRecipe(db, "Vegetable Soup",
                "1. Chop all vegetables. 2. Simmer in water with stock for 20 minutes. 3. Season and serve.",
                new Object[][]{
                        {"carrot", 2.0, "pcs"},
                        {"potato", 2.0, "pcs"},
                        {"onion", 1.0, "pcs"},
                        {"stock cube", 1.0, "pcs"}
                });

        addRecipe(db, "Grilled Cheese Sandwich",
                "1. Butter the outside of the bread. 2. Place cheese between slices. 3. Grill until golden on both sides.",
                new Object[][]{
                        {"bread", 2.0, "pcs"},
                        {"cheese", 60.0, "g"},
                        {"butter", 10.0, "g"}
                });

        addRecipe(db, "Potato Hash",
                "1. Dice and boil potatoes until soft. 2. Fry in a pan with onion until golden. 3. Season and serve.",
                new Object[][]{
                        {"potato", 3.0, "pcs"},
                        {"onion", 1.0, "pcs"},
                        {"salt", 1.0, "tsp"}
                });

        addRecipe(db, "Milk and Cereal",
                "1. Pour cereal into a bowl. 2. Add cold milk. 3. Serve immediately.",
                new Object[][]{
                        {"cereal", 60.0, "g"},
                        {"milk", 200.0, "ml"}
                });

        addRecipe(db, "Tuna Sandwich",
                "1. Mix tuna with mayonnaise. 2. Spread onto bread. 3. Add a second slice on top and serve.",
                new Object[][]{
                        {"bread", 2.0, "pcs"},
                        {"tuna", 100.0, "g"},
                        {"mayonnaise", 20.0, "g"}
                });

        addRecipe(db, "Carrot and Potato Mash",
                "1. Boil carrot and potato until soft. 2. Drain and mash together. 3. Add butter and season.",
                new Object[][]{
                        {"carrot", 2.0, "pcs"},
                        {"potato", 2.0, "pcs"},
                        {"butter", 15.0, "g"}
                });

        addRecipe(db, "Simple Pasta Butter",
                "1. Boil pasta until soft. 2. Drain and toss with butter. 3. Season with salt and serve.",
                new Object[][]{
                        {"pasta", 150.0, "g"},
                        {"butter", 20.0, "g"},
                        {"salt", 1.0, "tsp"}
                });

        addRecipe(db, "Onion Egg Scramble",
                "1. Fry chopped onion until soft. 2. Add beaten eggs. 3. Scramble together and season.",
                new Object[][]{
                        {"onion", 1.0, "pcs"},
                        {"egg", 3.0, "pcs"},
                        {"salt", 1.0, "tsp"}
                });

        addRecipe(db, "Chicken Soup",
                "1. Simmer chicken pieces in water with a stock cube. 2. Add chopped carrot and onion. 3. Cook 25 minutes and serve.",
                new Object[][]{
                        {"chicken", 200.0, "g"},
                        {"carrot", 1.0, "pcs"},
                        {"onion", 1.0, "pcs"},
                        {"stock cube", 1.0, "pcs"}
                });

        addRecipe(db, "Banana Milkshake",
                "1. Add banana and milk to a blender. 2. Blend until smooth. 3. Pour and serve chilled.",
                new Object[][]{
                        {"banana", 1.0, "pcs"},
                        {"milk", 250.0, "ml"}
                });

        addRecipe(db, "Cheesy Rice Bowl",
                "1. Cook rice. 2. Stir in grated cheese while rice is still hot. 3. Season and serve.",
                new Object[][]{
                        {"rice", 200.0, "g"},
                        {"cheese", 40.0, "g"}
                });

        addRecipe(db, "Tomato Pasta",
                "1. Boil pasta. 2. Fry chopped tomato and onion into a sauce. 3. Mix with drained pasta and serve.",
                new Object[][]{
                        {"pasta", 150.0, "g"},
                        {"tomato", 3.0, "pcs"},
                        {"onion", 1.0, "pcs"}
                });
    }

    /**
     Helper used only inside seedRecipes() to insert one recipe and its
     ingredient rows together. ingredientRows is an array of
     {name, quantity, unit} triples.
     */
    private void addRecipe(SQLiteDatabase db, String name, String steps, Object[][] ingredientRows) {
        ContentValues recipeValues = new ContentValues();
        recipeValues.put(COL_RECIPE_NAME, name);
        recipeValues.put(COL_RECIPE_STEPS, steps);
        long recipeId = db.insert(TABLE_RECIPES, null, recipeValues);

        for (int i = 0; i < ingredientRows.length; i++) {
            Object[] row = ingredientRows[i];
            ContentValues riValues = new ContentValues();
            riValues.put(COL_RI_RECIPE_ID, recipeId);
            riValues.put(COL_RI_NAME, (String) row[0]);
            riValues.put(COL_RI_QUANTITY, (Double) row[1]);
            riValues.put(COL_RI_UNIT, (String) row[2]);
            db.insert(TABLE_RECIPE_INGREDIENTS, null, riValues);
        }
    }
}
