package com.example.refresh.Model;

import com.example.refresh.MyApplication;

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

    // Constructor without food ids, meal id, and user id.
    public Meal(LocalDate date, LocalTime time, String type, String notes) {
        id = -1;
        this.date = date;
        this.time = time;
        this.type = (type != null) ? type : "Meal";
        this.notes = (notes != null) ? notes : "";
        this.foodIDs = new ArrayList<>();
        userID = MyApplication.getInstance().getLoggedUserID();
    }

    // Constructor without meal id, and user id.
    public Meal(LocalDate date, LocalTime time, String type, String notes, ArrayList<Integer> foodIDs) {
        id = -1;
        this.date = date;
        this.time = time;
        this.type = (type != null) ? type : "Meal";
        this.notes = (notes != null) ? notes : "";
        this.foodIDs = (foodIDs != null) ? foodIDs : new ArrayList<>();
        userID = MyApplication.getInstance().getLoggedUserID();
    }

    // Constructor without meal id, and user id. receives date and time as strings.
    public Meal(String stringDate, String stringTime, String type, String notes, ArrayList<Integer> foodIDs) {
        id = -1;
        setDate(stringDate);
        setTime(stringTime);
        this.type = (type != null) ? type : "Meal";
        this.notes = (notes != null) ? notes : "";
        this.foodIDs = (foodIDs != null) ? foodIDs : new ArrayList<>();
        userID = MyApplication.getInstance().getLoggedUserID();
    }


    // Constructor with food ids, meal id, and user id.
    public Meal(int id, LocalDate date, LocalTime time, String type, String notes, ArrayList<Integer> foodIDs, int userID) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.type = (type != null) ? type : "Meal";
        this.notes = (notes != null) ? notes : "";
        this.foodIDs = (foodIDs != null) ? foodIDs : new ArrayList<>();
        this.userID = userID;
    }

    // Constructor with food ids, meal id, and user id. Receives date and time as strings.
    public Meal(int id, String stringDate, String stringTime, String type, String notes, ArrayList<Integer> foodIDs, int userID) {
        this.id = id;
        setDate(stringDate);
        setTime(stringTime);
        this.type = (type != null) ? type : "Meal";
        this.notes = (notes != null) ? notes : "";
        this.foodIDs = (foodIDs != null) ? foodIDs : new ArrayList<>();
        this.userID = userID;
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

    public int getUserID() { return userID; }
    public void setUserID(int userID) { this.userID = userID; }
    public void setUserIDToActiveUser() { this.userID = MyApplication.getInstance().getLoggedUserID(); }

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

    public static String determineMealType(LocalTime time) {
        if (time.isAfter(LocalTime.of(5, 0)) && time.isBefore(LocalTime.of(12, 0))) {
            return "Breakfast";
        }
        if (time.isAfter(LocalTime.of(12, 0)) && time.isBefore(LocalTime.of(17, 0))) {
            return "Lunch";
        }
        if (time.isAfter(LocalTime.of(17, 0)) && time.isBefore(LocalTime.of(23, 59))) {
            return "Dinner";
        }

        return "Snack";
    }
}
