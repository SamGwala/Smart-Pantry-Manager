package com.example.smartpantry.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A recipe: a name, its preparation steps, and the list of ingredients
 * it needs. The ingredient list is filled in by DatabaseHelper when the
 * recipe is loaded from the recipe_ingredients table.
 */
public class Recipe {

    private long id;
    private String name;
    private String steps;
    private List<RecipeIngredient> requiredIngredients;

    // How many required ingredients are missing from the pantry.
    // Only used by the Suggested Recipes screen - not stored in the DB.
    private int missingCount;

    public Recipe(long id, String name, String steps) {
        this.id = id;
        this.name = name;
        this.steps = steps;
        this.requiredIngredients = new ArrayList<>();
        this.missingCount = 0;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSteps() {
        return steps;
    }

    public List<RecipeIngredient> getRequiredIngredients() {
        return requiredIngredients;
    }

    public void addRequiredIngredient(RecipeIngredient ingredient) {
        requiredIngredients.add(ingredient);
    }

    public int getMissingCount() {
        return missingCount;
    }

    public void setMissingCount(int missingCount) {
        this.missingCount = missingCount;
    }
}
