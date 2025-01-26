package com.example.refresh.Database.Tables;

import android.content.ContentValues;
import android.database.Cursor;

import com.example.refresh.Database.DatabaseHelper;
import com.example.refresh.Model.Meal;
import com.example.refresh.Model.Food; // Ensure you have this import

import java.util.ArrayList;
import java.util.Date;

public class MealsTable {

    public static final String TABLE_NAME = "meals";

    // Enum for table columns
    public enum Columns {
        MEAL_ID("meal_id"),
        DATE("date"),
        TIME("time"),
        TYPE("type"),
        NOTES("notes"),
        FOOD_IDS("food_ids"); // Stores food IDs as a comma-separated string

        private final String columnName;

        Columns(String columnName) {
            this.columnName = columnName;
        }

        public String getColumnName() {
            return columnName;
        }
    }

    // Create table query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    Columns.MEAL_ID.getColumnName() + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    Columns.DATE.getColumnName() + " TEXT NOT NULL, " +
                    Columns.TIME.getColumnName() + " TEXT NOT NULL, " +
                    Columns.TYPE.getColumnName() + " TEXT NOT NULL, " +
                    Columns.NOTES.getColumnName() + " TEXT, " +
                    Columns.FOOD_IDS.getColumnName() + " TEXT);" +
                    "FOREIGN KEY(" + Columns.FOOD_IDS.getColumnName() + ") " +
                    "REFERENCES " + FoodsTable.TABLE_NAME + " (" +
                    FoodsTable.Columns.FOOD_ID.getColumnName() + "));"; // Stores food IDs as CSV

    // Convert Meal to ContentValues
    public static ContentValues toContentValues(Meal meal) {
        ContentValues values = new ContentValues();
        values.put(Columns.MEAL_ID.getColumnName(), meal.getId());
        values.put(Columns.DATE.getColumnName(), meal.getDate().getTime()); // Store date as timestamp
        values.put(Columns.TIME.getColumnName(), meal.getTime());
        values.put(Columns.TYPE.getColumnName(), meal.getType());
        values.put(Columns.NOTES.getColumnName(), meal.getNotes());

        // Convert food ID ArrayList to a comma-separated string
        values.put(Columns.FOOD_IDS.getColumnName(), foodIdsToString(meal.getFoodIDs()));

        return values;
    }

    // Convert Cursor to Meal
    public static Meal fromCursor(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndexOrThrow(Columns.MEAL_ID.getColumnName()));
        long dateMillis = cursor.getLong(cursor.getColumnIndexOrThrow(Columns.DATE.getColumnName()));
        String time = cursor.getString(cursor.getColumnIndexOrThrow(Columns.TIME.getColumnName()));
        String type = cursor.getString(cursor.getColumnIndexOrThrow(Columns.TYPE.getColumnName()));
        String notes = cursor.getString(cursor.getColumnIndexOrThrow(Columns.NOTES.getColumnName()));
        String foodIdsString = cursor.getString(cursor.getColumnIndexOrThrow(Columns.FOOD_IDS.getColumnName()));

        // Convert comma-separated string back to an ArrayList of integers
        ArrayList<Integer> foodIds = stringToFoodIds(foodIdsString);

        return new Meal(id, new Date(dateMillis), time, type, notes, foodIds);
    }

    // Converts an ArrayList of food IDs to a comma-separated string
    private static String foodIdsToString(ArrayList<Integer> foodIds) {
        if (foodIds == null || foodIds.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (int id : foodIds) {
            sb.append(id).append(",");
        }
        return sb.deleteCharAt(sb.length() - 1).toString(); // Remove last comma
    }

    // Converts a comma-separated string back to an ArrayList of integers
    private static ArrayList<Integer> stringToFoodIds(String foodIdsString) {
        ArrayList<Integer> foodIds = new ArrayList<>();
        if (foodIdsString == null || foodIdsString.isEmpty()) return foodIds;
        String[] split = foodIdsString.split(",");
        for (String s : split) {
            try {
                foodIds.add(Integer.parseInt(s.trim()));
            } catch (NumberFormatException e) {
                // Handle invalid number format if necessary
                e.printStackTrace();
            }
        }
        return foodIds;
    }

    /**
     * Calculates the total calories for the given meal.
     *
     * @param meal      The meal for which to calculate total calories.
     * @param dbHelper  The database helper to access Food data.
     * @return The total calories.
     */
    public int getTotalCalories(Meal meal, DatabaseHelper dbHelper) {
        int total = 0;
        for (int foodID : meal.getFoodIDs()) {
            Food food = FoodsTable.getFoodByID(dbHelper.getContext(), foodID);
            if (food != null) {
                total += food.getActualCalories();
            }
        }
        return total;
    }

    /**
     * Calculates the total carbohydrates for the given meal.
     *
     * @param meal      The meal for which to calculate total carbs.
     * @param dbHelper  The database helper to access Food data.
     * @return The total carbohydrates.
     */
    public int getTotalCarbs(Meal meal, DatabaseHelper dbHelper) {
        int total = 0;
        for (int foodID : meal.getFoodIDs()) {
            Food food = FoodsTable.getFoodByID(dbHelper.getContext(), foodID);
            if (food != null) {
                total += food.getActualCarbs();
            }
        }
        return total;
    }

    /**
     * Calculates the total protein for the given meal.
     *
     * @param meal      The meal for which to calculate total protein.
     * @param dbHelper  The database helper to access Food data.
     * @return The total protein.
     */
    public int getTotalProtein(Meal meal, DatabaseHelper dbHelper) {
        int total = 0;
        for (int foodID : meal.getFoodIDs()) {
            Food food = FoodsTable.getFoodByID(dbHelper.getContext(), foodID);
            if (food != null) {
                total += food.getActualProtein();
            }
        }
        return total;
    }

    /**
     * Calculates the total fat for the given meal.
     *
     * @param meal      The meal for which to calculate total fat.
     * @param dbHelper  The database helper to access Food data.
     * @return The total fat.
     */
    public int getTotalFat(Meal meal, DatabaseHelper dbHelper) {
        int total = 0;
        for (int foodID : meal.getFoodIDs()) {
            Food food = FoodsTable.getFoodByID(dbHelper.getContext(), foodID);
            if (food != null) {
                total += food.getActualFat();
            }
        }
        return total;
    }
}
