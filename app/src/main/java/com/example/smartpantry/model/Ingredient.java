package com.example.smartpantry.model;

/**
 * Simple data model for one pantry ingredient.
 * Kept as plain fields with getters/setters (no Lombok, no records)
 * so it stays easy to read for a beginner-level submission.
 */
public class Ingredient {

    private long id;
    private String name;
    private double quantity;
    private String unit;
    private String expiryDate; // stored as "YYYY-MM-DD" or empty string if not set

    public Ingredient() {
        // empty constructor used before we know the values yet
    }

    public Ingredient(long id, String name, double quantity, String unit, String expiryDate) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.expiryDate = expiryDate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }
}
