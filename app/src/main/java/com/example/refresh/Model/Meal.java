package com.example.refresh.Model;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

// Meal Model Class which represents a meal
public class Meal {
    private int id; // Unique identifier for the meal
    private LocalDate date;
    private LocalTime time; // 15-minute accuracy
    private String type; // breakfast, lunch, dinner, snack, etc.
    private ArrayList<Integer> foodIDs; // List of food IDs (not Food objects)
    private int userID;
    private String notes; // Can be empty

    // Constructor without food IDs
    public Meal(LocalDate date, LocalTime time, String type, String notes) {
        id = -1;
        this.date = date;
        this.time = time;
        this.type = (type != null) ? type : "Meal";
        this.notes = (notes != null) ? notes : "";
        this.foodIDs = new ArrayList<>();

        // TODO: Set userID, in all constructors, also add set and get methods.
        // TODO: FIx meal table, add user ID as parameter to constructors who need it.
    }

    public Meal(LocalDate date, LocalTime time, String type, String notes, ArrayList<Integer> foodIDs) {
        id = -1;
        this.date = date;
        this.time = time;
        this.type = (type != null) ? type : "Meal";
        this.notes = (notes != null) ? notes : "";
        this.foodIDs = (foodIDs != null) ? foodIDs : new ArrayList<>();
    }

    public Meal(String stringDate, String stringTime, String type, String notes, ArrayList<Integer> foodIDs) {
        id = -1;
        setDate(stringDate);
        setTime(stringTime);
        this.type = (type != null) ? type : "Meal";
        this.notes = (notes != null) ? notes : "";
        this.foodIDs = (foodIDs != null) ? foodIDs : new ArrayList<>();
    }


    // Constructor with food IDs
    public Meal(int id, LocalDate date, LocalTime time, String type, String notes, ArrayList<Integer> foodIDs) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.type = (type != null) ? type : "Meal";
        this.notes = (notes != null) ? notes : "";
        this.foodIDs = (foodIDs != null) ? foodIDs : new ArrayList<>();
    }

    public Meal(int id, String stringDate, String stringTime, String type, String notes, ArrayList<Integer> foodIDs) {
        this.id = id;
        setDate(stringDate);
        setTime(stringTime);
        this.type = (type != null) ? type : "Meal";
        this.notes = (notes != null) ? notes : "";
        this.foodIDs = (foodIDs != null) ? foodIDs : new ArrayList<>();
    }

    // Getters and Setters

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }
    public LocalDate getDate() { return date; }

    public String getStringDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return date.format(formatter);
    }
    public void setDate(LocalDate date) { this.date = date; }

    private void setDate(String stringDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        date = LocalDate.parse(stringDate, formatter);
    }

    public LocalTime getTime() { return time; }

    public String getStringTime() {
        return time.toString();
    }

    public void setTime(LocalTime time) { this.time = time; }

    private void setTime(String stringTime) {
        time = LocalTime.parse(stringTime);
    }

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
                ", date=" + getStringDate() +
                ", time='" + getStringTime() + '\'' +
                ", type='" + type + '\'' +
                ", foodIDs=" + foodIDs +
                ", notes='" + notes + '\'' +
                '}';
    }
}
