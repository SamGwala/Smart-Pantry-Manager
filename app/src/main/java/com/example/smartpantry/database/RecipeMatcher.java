package com.example.smartpantry.database;

import com.example.smartpantry.model.Ingredient;
import com.example.smartpantry.model.Recipe;
import com.example.smartpantry.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.List;

/**
 Contains the strict-matching rule described in the assignment brief:
 a recipe is only "suggested" if EVERY ingredient it needs is present
 in the pantry in at least the required quantity.
 */
public class RecipeMatcher {

    /**
     Returns only the recipes where 100% of required ingredients are
     present in the pantry (in sufficient quantity).
     */
    public static List<Recipe> findFullMatches(List<Recipe> allRecipes, List<Ingredient> pantry) {
        List<Recipe> matches = new ArrayList<>();

        for (int i = 0; i < allRecipes.size(); i++) {
            Recipe recipe = allRecipes.get(i);
            int missing = countMissingIngredients(recipe, pantry);
            recipe.setMissingCount(missing);
            if (missing == 0) {
                matches.add(recipe);
            }
        }
        return matches;
    }

    /**
     * exactly one ingredient - shown separately from the strict list.
     */
    public static List<Recipe> findAlmostMatches(List<Recipe> allRecipes, List<Ingredient> pantry) {
        List<Recipe> almost = new ArrayList<>();

        for (int i = 0; i < allRecipes.size(); i++) {
            Recipe recipe = allRecipes.get(i);
            int missing = countMissingIngredients(recipe, pantry);
            recipe.setMissingCount(missing);
            if (missing == 1) {
                almost.add(recipe);
            }
        }
        return almost;
    }

    /**
     Counts how many of the recipe's required ingredients are NOT
     satisfied by the pantry. Zero means every ingredient is covered.
     */
    private static int countMissingIngredients(Recipe recipe, List<Ingredient> pantry) {
        int missingCount = 0;
        List<RecipeIngredient> required = recipe.getRequiredIngredients();

        for (int i = 0; i < required.size(); i++) {
            RecipeIngredient need = required.get(i);
            boolean found = pantryHasEnough(pantry, need);
            if (!found) {
                missingCount = missingCount + 1;
            }
        }
        return missingCount;
    }

    /**
     Checks whether the pantry contains this specific required ingredient
     in at least the needed quantity. Ingredient names are normalised
     first (lower case, trimmed, simple plural stripped) so "Tomato" in
     the recipe still matches "tomatoes" typed by the user.
     */
    private static boolean pantryHasEnough(List<Ingredient> pantry, RecipeIngredient need) {
        String neededName = normalizeName(need.getName());

        for (int i = 0; i < pantry.size(); i++) {
            Ingredient owned = pantry.get(i);
            String ownedName = normalizeName(owned.getName());

            if (ownedName.equals(neededName)) {
                // Same ingredient  now check quantity.
                // If the units match, compare the numbers directly.
                // If the units are different (e.g. "g" vs "pcs") we can't
                // safely convert without a full unit-conversion table, so
                // we treat having the item at all as satisfying the recipe.

                if (owned.getUnit() != null && need.getUnit() != null
                        && owned.getUnit().trim().equalsIgnoreCase(need.getUnit().trim())) {
                    if (owned.getQuantity() >= need.getQuantity()) {
                        return true;
                    } else {
                        return false; // same ingredient, same unit, but not enough of it
                    }
                } else {
                    return true; // same ingredient, unit differs count it as present
                }
            }
        }
        return false; // ingredient not found in the pantry at all
    }

    /**
     * Normalises an ingredient name for comparison:
     trims whitespace
     converts to lower case
     strips a single trailing "es" or "s" so "tomatoes"/"tomato"
     and "onions"/"onion" are treated as the same ingredient.
     */
    public static String normalizeName(String rawName) {
        if (rawName == null) {
            return "";
        }
        String name = rawName.trim().toLowerCase();

        if (name.endsWith("es") && name.length() > 4) {
            name = name.substring(0, name.length() - 2);
        } else if (name.endsWith("s") && name.length() > 3) {
            name = name.substring(0, name.length() - 1);
        }
        return name;
    }
}
