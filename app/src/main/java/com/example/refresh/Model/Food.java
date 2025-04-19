package com.example.refresh.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Food Model Class which represents a food item
public class Food implements Serializable {
    // Attributes
    private int id;
    private String name;
    private String description;
    private String category;
    private ArrayList<String> labels; // Additional label, can be empty
    private int servingSize = 100; // in grams
    private int calories; // per 100 grams
    private int carbs; // per 100 grams
    private int protein; // per 100 grams
    private int fat; // per 100 grams
    private String notes;

    // Constructors, getters and setters
    public Food(int id, String name, String description, String category, ArrayList<String> labels, int servingSize, int calories, int carbs, int protein, int fat, String notes) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.labels = (labels != null) ? labels : new ArrayList<>(List.of("")); // Ensure label is never null
        this.servingSize = servingSize;
        this.calories = calories;
        this.carbs = carbs;
        this.protein = protein;
        this.fat = fat;
        this.notes = (notes != null) ? notes : "";
    }

    public Food() {}

    public Food(int id, String name, String description, String category, ArrayList<String> labels, int calories, int carbs, int protein, int fat, String notes) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.labels = (labels != null) ? labels : new ArrayList<>(List.of("")); // Ensure label is never null.calories = calories;
        this.calories = calories;
        this.carbs = carbs;
        this.protein = protein;
        this.fat = fat;
        this.notes = (notes != null) ? notes : "";
    }

    public Food(int id, String name, String description, String category, String labels, int servingSize, int calories, int carbs, int protein, int fat, String notes) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.labels = (labels != null) ? new ArrayList<>(Arrays.asList(labels.split(","))) : new ArrayList<>(List.of("")); // Ensure label is never null
        this.servingSize = servingSize;
        this.calories = calories;
        this.carbs = carbs;
        this.protein = protein;
        this.fat = fat;
        this.notes = (notes != null) ? notes : "";
    }


    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = (description != null) ? description : "";
    }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public ArrayList<String> getLabels() { return labels; }
    public void setLabels(ArrayList<String> labels) { this.labels = (labels != null) ? labels : new ArrayList<>(List.of("")); }

    public int getServingSize() { return servingSize; }
    public void setServingSize(int servingSize) { this.servingSize = servingSize; }

    public int getCalories() { return calories; }
    public void setCalories(int calories) { this.calories = calories; }

    public int getCarbs() { return carbs; }
    public void setCarbs(int carbs) { this.carbs = carbs; }

    public int getProtein() { return protein; }
    public void setProtein(int protein) { this.protein = protein; }

    public int getFat() { return fat; }
    public void setFat(int fat) { this.fat = fat; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = (notes != null) ? notes : ""; }

    @Override
    public String toString() {
        return "Food{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", category='" + category + '\'' +
                ", label='" + labels + '\'' +
                ", servingSize=" + servingSize +
                ", calories=" + calories +
                ", carbs=" + carbs +
                ", protein=" + protein +
                ", fat=" + fat +
                ", notes=" + notes +
                '}';
    }

    // Method to calculate actual calories based on serving size
    public int getActualCalories() {
        return (calories * servingSize) / 100;
    }

    // Method to calculate actual carbs based on serving size
    public int getActualCarbs() {
        return (carbs * servingSize) / 100;
    }

    // Method to calculate actual protein based on serving size
    public int getActualProtein() {
        return (protein * servingSize) / 100;
    }

    // Method to calculate actual fat based on serving size
    public int getActualFat() {
        return (fat * servingSize) / 100;
    }

    public boolean isLiquid() {
        return (category == "Beverages" || labels.contains("oil"));
    }
}
