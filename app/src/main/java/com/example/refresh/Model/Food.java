package com.example.refresh.Model;

public class Food {
    private int id;
    private String name;
    private String category;
    private String label; // Additional label, can be empty
    private int servingSize = 100; // in grams
    private int calories; // per 100 grams
    private int carbs; // per 100 grams
    private int protein; // per 100 grams
    private int fat; // per 100 grams

    public Food(int id, String name, String category, String label, int servingSize, int calories, int carbs, int protein, int fat) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.label = (label != null) ? label : ""; // Ensure label is never null
        this.servingSize = servingSize;
        this.calories = calories;
        this.carbs = carbs;
        this.protein = protein;
        this.fat = fat;
    }

    public Food(int id, String name, String category, String label, int calories, int carbs, int protein, int fat) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.label = (label != null) ? label : ""; // Ensure label is never null
        this.calories = calories;
        this.carbs = carbs;
        this.protein = protein;
        this.fat = fat;
    }


    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = (label != null) ? label : ""; }

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

    @Override
    public String toString() {
        return "Food{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", label='" + label + '\'' +
                ", servingSize=" + servingSize +
                ", calories=" + calories +
                ", carbs=" + carbs +
                ", protein=" + protein +
                ", fat=" + fat +
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
}
