package com.example.refresh.Model;

import android.content.Context;

import com.example.refresh.Database.FoodsTable;
import com.example.refresh.MyApplication;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Locale;

// Meal Model Class which represents a meal
public class Meal implements Serializable {
        private int id; // Unique identifier for the meal
    private LocalDate date;
    private LocalTime time; // 15-minute accuracy
    private String type; // breakfast, lunch, dinner, snack, etc.
    private final ArrayList<Integer> foodIDs; // List of food IDs (not Food objects)
    private ArrayList<Integer> servingSizes; // List of food quantities in grams
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
        this.servingSizes = new ArrayList<>();
        userID = MyApplication.getInstance().getLoggedUserID();
    }

    // Constructor without meal id, and user id.
    public Meal(LocalDate date, LocalTime time, String type, String notes, ArrayList<Integer> foodIDs, ArrayList<Integer> servingSizes) {
        id = -1;
        this.date = date;
        this.time = time;
        this.type = (type != null) ? type : "Meal";
        this.notes = (notes != null) ? notes : "";
        this.foodIDs = (foodIDs != null) ? foodIDs : new ArrayList<>();
        this.servingSizes = (servingSizes != null) ? servingSizes : new ArrayList<>();
        userID = MyApplication.getInstance().getLoggedUserID();
    }

    // Constructor without meal id, and user id. receives date and time as strings.
    public Meal(String stringDate, String stringTime, String type, String notes, ArrayList<Integer> foodIDs, ArrayList<Integer> servingSizes) {
        id = -1;
        setDate(stringDate);
        setTime(stringTime);
        this.type = (type != null) ? type : "Meal";
        this.notes = (notes != null) ? notes : "";
        this.foodIDs = (foodIDs != null) ? foodIDs : new ArrayList<>();
        this.servingSizes = (servingSizes != null) ? servingSizes : new ArrayList<>();
        userID = MyApplication.getInstance().getLoggedUserID();
    }


    // Constructor with food ids, meal id, and user id.
    public Meal(int id, LocalDate date, LocalTime time, String type, String notes, ArrayList<Integer> foodIDs, ArrayList<Integer> servingSizes, int userID) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.type = (type != null) ? type : "Meal";
        this.notes = (notes != null) ? notes : "";
        this.foodIDs = (foodIDs != null) ? foodIDs : new ArrayList<>();
        this.servingSizes = (servingSizes != null) ? servingSizes : new ArrayList<>();
        this.userID = userID;
    }

    // Constructor with food ids, meal id, and user id. Receives date and time as strings.
    public Meal(int id, String stringDate, String stringTime, String type, String notes, ArrayList<Integer> foodIDs, ArrayList<Integer> servingSizes, int userID) {
        this.id = id;
        setDate(stringDate);
        setTime(stringTime);
        this.type = (type != null) ? type : "Meal";
        this.notes = (notes != null) ? notes : "";
        this.foodIDs = (foodIDs != null) ? foodIDs : new ArrayList<>();
        this.servingSizes = (servingSizes != null) ? servingSizes : new ArrayList<>();
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


    /* ===================================================================
       1) FOOD IDs METHODS
       =================================================================== */

    // --------------------------------------------------
    // GET
    // --------------------------------------------------
    /** Returns the entire list of food IDs. */
    public ArrayList<Integer> getFoodIDs() {
        return foodIDs;
    }

    /** Returns the food ID at the given index. */
    public int getFoodID(int index) {
        return foodIDs.get(index);
    }

    // --------------------------------------------------
    // SET
    // --------------------------------------------------
    /**
     * Sets the food IDs and serving sizes to the provided lists.
     * Both lists MUST be of equal length.
     */
    public void setFoodIDs(ArrayList<Integer> newFoodIDs, ArrayList<Integer> newServingSizes) {
        if (newFoodIDs.size() != newServingSizes.size()) {
            throw new IllegalArgumentException("foodIDs and servingSizes must have the same length.");
        }

        this.foodIDs.clear();
        this.servingSizes.clear();

        for (int i = 0; i < newFoodIDs.size(); i++) {
            this.foodIDs.add(newFoodIDs.get(i));
            this.servingSizes.add(newServingSizes.get(i));
        }
    }

    /**
     * Sets the food IDs to the given list and assigns a default serving size (100) for each.
     */
    public void setFoodIDs(ArrayList<Integer> newFoodIDs) {
        this.foodIDs.clear();
        this.servingSizes.clear();

        for (Integer id : newFoodIDs) {
            this.foodIDs.add(id);
            this.servingSizes.add(100); // default serving size
        }
    }

    // --------------------------------------------------
    // ADD
    // --------------------------------------------------
    /**
     * Adds a Food object. Extracts its ID and servingSize, then inserts both in sync.
     */
    public void addFood(Food food) {
        addFoodByID(food.getId(), food.getServingSize());
    }

    /**
     * Adds a food with an explicit ID and serving size.
     */
    public void addFoodByID(int foodID, int servingSize) {
        this.foodIDs.add(foodID);
        this.servingSizes.add(servingSize);
    }

    /**
     * Adds a food with an explicit ID and a default serving size of 100.
     */
    public void addFoodByID(int foodID) {
        this.foodIDs.add(foodID);
        this.servingSizes.add(100);
    }

    // --------------------------------------------------
    // REMOVE
    // --------------------------------------------------
    /**
     * Removes a single Food object from the internal lists (by its ID).
     */
    public void removeFood(Food food) {
        removeFoodByID(food.getId());
    }

    /**
     * Removes a food by ID (removes the *first* occurrence).
     */
    public void removeFoodByID(int foodID) {
        int index = this.foodIDs.indexOf(foodID);
        if (index != -1) {
            removeFoodByIndex(index);
        }
    }

    /**
     * Removes a food by its index.
     */
    public void removeFoodByIndex(int index) {
        if (index < 0 || index >= this.foodIDs.size()) {
            throw new IndexOutOfBoundsException("Invalid index: " + index);
        }
        // Remove both entries to stay in sync
        this.foodIDs.remove(index);
        this.servingSizes.remove(index);
    }
    // --------------------------------------------------
    // EDIT
    // --------------------------------------------------

    /**
     * Edits a single Food object. Replaces oldFood (found by oldFood's ID) with newFood’s ID & servingSize.
     */
    public void editFood(Food oldFood, Food newFood) {
        int oldID = oldFood.getId();
        int newID = newFood.getId();
        int newSize = newFood.getServingSize();
        editFoodByID(oldID, newID, newSize);
    }

    /**
     * Edits a food item by replacing the old ID with a new ID and updating serving size.
     */
    public void editFoodByID(int oldFoodID, int newFoodID, int newServingSize) {
        int index = this.foodIDs.indexOf(oldFoodID);
        if (index != -1) {
            editFoodByIndex(index, newFoodID, newServingSize);
        }
    }

    /**
     * Edits a food item at a given index (if valid).
     */
    public void editFoodByIndex(int index, int newFoodID, int newServingSize) {
        if (index < 0 || index >= this.foodIDs.size()) {
            throw new IndexOutOfBoundsException("Invalid index: " + index);
        }
        this.foodIDs.set(index, newFoodID);
        this.servingSizes.set(index, newServingSize);
    }

    /* ===================================================================
       2) SERVING SIZES METHODS
       =================================================================== */
    // --------------------------------------------------
    // GET
    // --------------------------------------------------

    /** Returns the entire list of serving sizes. */
    public ArrayList<Integer> getServingSizes() {
        return servingSizes;
    }

    /** Returns the serving size at the given index. */
    public int getServingSize(int index) {
        return servingSizes.get(index);
    }

    /**
     * Returns the serving size for the given foodID (first occurrence).
     * Throws an exception if not found.
     */
    public int getServingSizeByFoodID(int foodID) {
        int index = this.foodIDs.indexOf(foodID);
        if (index == -1) {
            throw new IllegalArgumentException("Food ID not found: " + foodID);
        }
        return servingSizes.get(index);
    }
    // --------------------------------------------------
    // SET
    // --------------------------------------------------

    /**
     * Sets the servingSizes list. Must match the size of foodIDs.
     */
    public void setServingSizes(ArrayList<Integer> newServingSizes) {
        if (newServingSizes.size() != this.foodIDs.size()) {
            throw new IllegalArgumentException("Size mismatch: must match the number of foodIDs.");
        }
        this.servingSizes = new ArrayList<>(newServingSizes);
    }
    // --------------------------------------------------
    // ADD
    // --------------------------------------------------

    /**
     * Adding a serving size alone **would break synchronization** if there's
     * no corresponding food ID. So we disallow it.
     */
    public void addServingSize(int servingSize) {
        throw new UnsupportedOperationException(
                "Cannot add serving size alone without a corresponding food ID."
        );
    }

    /**
     * Adds a default serving size of 100 alone, also disallowed by default.
     */
    public void addServingSize() {
        throw new UnsupportedOperationException(
                "Cannot add serving size alone without a corresponding food ID."
        );
    }
    // --------------------------------------------------
    // REMOVE
    // --------------------------------------------------

    /**
     * Removes a serving size at the given index (and its corresponding ID).
     */
    public void removeServingSizeByIndex(int index) {
        removeFoodByIndex(index);
    }
    // --------------------------------------------------
    // EDIT
    // --------------------------------------------------

    /**
     * Edits the serving size at a given index. Food ID remains unchanged.
     */
    public void editServingSize(int index, int newServingSize) {
        if (index < 0 || index >= this.servingSizes.size()) {
            throw new IndexOutOfBoundsException("Invalid index: " + index);
        }
        this.servingSizes.set(index, newServingSize);
    }

    /* ===================================================================
       3) METHODS FOR MANAGING "MEAL FOODS" (Food Objects)
          - Converts between (foodID, servingSize) and actual Food objects.
       =================================================================== */

    /**
     * Converts all entries in foodIDs + servingSizes into a list of Food objects.
     * We only know the ID and servingSize. Other Food fields are unknown,
     * so we can create a Food object with minimal data, or fetch from a DB, etc.
     */
    public ArrayList<Food> getMealFoods(Context context) {
        ArrayList<Food> mealFoods = new ArrayList<>();
        for (int i = 0; i < foodIDs.size(); i++) {
            int currentID = foodIDs.get(i);
            int currentSize = servingSizes.get(i);

            Food food = FoodsTable.getFoodByID(context, currentID);
            if (food != null)
                food.setServingSize(currentSize);

            mealFoods.add(food);
        }
        return mealFoods;
    }

    /**
     * Replaces the internal lists with the IDs/servingSizes extracted from the given Foods.
     * All other Food fields are lost unless you persist them elsewhere.
     */
    public void setMealFoods(ArrayList<Food> mealFoods) {
        this.foodIDs.clear();
        this.servingSizes.clear();

        for (Food food : mealFoods) {
            this.foodIDs.add(food.getId());
            this.servingSizes.add(food.getServingSize());
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
                ", foodQuantities=" + servingSizes +
                ", userID=" + userID +
                ", notes='" + notes + '\'' +
                '}';
    }

    public static String determineDayTime(LocalTime time) {
        if (time.isAfter(LocalTime.of(5, 0)) && time.isBefore(LocalTime.of(12, 0))) {
            return "Morning";
        }
        if (time.isAfter(LocalTime.of(12, 0)) && time.isBefore(LocalTime.of(17, 0))) {
            return "AfterNoon";
        }
        if (time.isAfter(LocalTime.of(17, 0)) && time.isBefore(LocalTime.of(21, 30))) {
            return "Evening";
        }

        return "Night";
    }

    public static String determineMealType(LocalTime time) {
        String timeOfDay = Meal.determineDayTime(time);
        if (timeOfDay.equals("Morning")) {
            return "Breakfast";
        }
        if (timeOfDay.equals("AfterNoon")) {
            return "Lunch";
        }
        if (timeOfDay.equals("Evening")) {
            return "Dinner";
        }

        return "Snack";
    }

    public static boolean determineIfWaterIntake(Meal meal) {
        return meal.foodIDs.size() == 1 && meal.foodIDs.get(0) == 144 && meal.time == null;
    }

    public static String getDayOfWeek(LocalDate date) {
        return date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());
    }

    public String getMealTitle() {
        return getDayOfWeek(date) + " " + determineDayTime(time) + " - " + type;
    }

    public static String getMealLogTitle(LocalDate date, LocalTime time) {
        return getDayOfWeek(date) + " " + determineDayTime(time);
    }


    public String getMealDescription(Context context) {
        return getStringDate() + " | " + getStringTime() + "  ✦  " + getCalories(context);
    }

    public static String getCurrentDateParsed() {
        LocalDate date = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return date.format(formatter);
    }

    public int getCalories(Context context) {
        int calories = 0;
        for (Food food : getMealFoods(context)) {
            calories += food.getActualCalories();
        }
        return calories;
    }

    public int getCarbs(Context context) {
        int carbs = 0;
        for (Food food : getMealFoods(context)) {
            carbs += food.getActualCarbs();
        }
        return carbs;
    }

    public int getProtein(Context context) {
        int protein = 0;
        for (Food food : getMealFoods(context)) {
            protein += food.getActualProtein();
        }
        return protein;
    }

    public int getFat(Context context) {
        int fat = 0;
        for (Food food : getMealFoods(context)) {
            fat += food.getActualFat();
        }
        return fat;
    }
}
