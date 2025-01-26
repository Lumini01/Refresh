package com.example.refresh.Model;

import java.util.ArrayList;
import java.util.Date;

// Meal Model Class which represents a meal
public class Meal {
    private int id; // Unique identifier for the meal
    private Date date;
    private String time; // 15-minute accuracy
    private String type; // breakfast, lunch, dinner, snack, etc.
    private ArrayList<Integer> foodIDs; // List of food IDs (not Food objects)
    private String notes; // Can be empty

    // Constructor without food IDs
    public Meal(int id, Date date, String time, String type, String notes) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.type = (type != null) ? type : "Meal";
        this.notes = (notes != null) ? notes : "";
        this.foodIDs = new ArrayList<>();
    }

    // Constructor with food IDs
    public Meal(int id, Date date, String time, String type, String notes, ArrayList<Integer> foodIDs) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.type = (type != null) ? type : "Meal";
        this.notes = (notes != null) ? notes : "";
        this.foodIDs = (foodIDs != null) ? foodIDs : new ArrayList<>();
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public ArrayList<Integer> getFoodIDs() { return foodIDs; }
    public void addFood(int foodID) { this.foodIDs.add(foodID); }
    public void removeFood(int foodID) { this.foodIDs.remove((Integer) foodID); }
    public void editFood(int oldFoodID, int newFoodID) {
        int index = this.foodIDs.indexOf(oldFoodID);
        if (index != -1) {
            this.foodIDs.set(index, newFoodID);
        }
    }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = (notes != null) ? notes : ""; }

    @Override
    public String toString() {
        return "Meal{" +
                "id=" + id +
                ", date=" + date +
                ", time='" + time + '\'' +
                ", type='" + type + '\'' +
                ", foodIDs=" + foodIDs +
                ", notes='" + notes + '\'' +
                '}';
    }
}
