package com.example.smartpantry.model;

/**
 * One ingredient line that a recipe requires (e.g. "flour, 200, g").
 */
public class RecipeIngredient {

    private String name;
    private double quantity;
    private String unit;

    public RecipeIngredient(String name, double quantity, String unit) {
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
    }

    public String getName() {
        return name;
    }

    public double getQuantity() {
        return quantity;
    }

    public String getUnit() {
        return unit;
    }
}
