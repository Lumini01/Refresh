package com.example.refresh.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.example.refresh.Helper.DatabaseHelper;
import com.example.refresh.Model.Meal;
import com.example.refresh.Model.Food; // Ensure you have this import

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class MealsTable {

    public static final String TABLE_NAME = "meals";

    // Enum for table columns
    public enum Columns {
        MEAL_ID("meal_id"),
        DATE("date"),
        TIME("time"),
        TYPE("type"),
        NOTES("notes"),
        FOOD_IDS("food_ids"), // Stores food IDs as a comma-separated string
        SERVING_SIZES("serving_sizes"), // Stores serving sizes as a comma-separated string
        USER_ID("user_id");

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
                    Columns.TIME.getColumnName() + " TEXT, " +
                    Columns.TYPE.getColumnName() + " TEXT NOT NULL, " +
                    Columns.NOTES.getColumnName() + " TEXT, " +
                    Columns.FOOD_IDS.getColumnName() + " TEXT, " +
                    Columns.SERVING_SIZES.getColumnName() + " TEXT, " +
                    Columns.USER_ID.getColumnName() + " TEXT);" +

                    "FOREIGN KEY(" + Columns.FOOD_IDS.getColumnName() + ") " +
                    "REFERENCES " + FoodsTable.TABLE_NAME + " (" +
                    FoodsTable.Columns.FOOD_ID.getColumnName() + "));" + // Stores food IDs as CSV

                    "FOREIGN KEY(" + Columns.USER_ID.getColumnName() + ") " +
                    "REFERENCES " + UsersTable.TABLE_NAME + " (" +
                    UsersTable.Columns.ID.getColumnName() + "));"; // Stores user ID

    // Convert Meal to ContentValues
    public static ContentValues toContentValues(Meal meal) {
        ContentValues values = new ContentValues();

        LocalDate localDate = meal.getDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formattedDate = localDate.format(formatter);

        values.put(Columns.DATE.getColumnName(), formattedDate); // Store date as timestamp
        values.put(Columns.TIME.getColumnName(), meal.getTime().toString());
        values.put(Columns.TYPE.getColumnName(), meal.getType());
        values.put(Columns.NOTES.getColumnName(), meal.getNotes());

        // Convert food ID ArrayList to a comma-separated string
        values.put(Columns.FOOD_IDS.getColumnName(), foodIdsToString(meal.getFoodIDs()));
        values.put(Columns.SERVING_SIZES.getColumnName(), servingSizesToString(meal.getServingSizes()));
        values.put(Columns.USER_ID.getColumnName(), meal.getUserID());

        return values;
    }

    // Convert Cursor to Meal
    public static Meal fromCursor(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndexOrThrow(Columns.MEAL_ID.getColumnName()));
        String date = cursor.getString(cursor.getColumnIndexOrThrow(Columns.DATE.getColumnName()));
        String time = cursor.getString(cursor.getColumnIndexOrThrow(Columns.TIME.getColumnName()));
        String type = cursor.getString(cursor.getColumnIndexOrThrow(Columns.TYPE.getColumnName()));
        String notes = cursor.getString(cursor.getColumnIndexOrThrow(Columns.NOTES.getColumnName()));
        String foodIdsString = cursor.getString(cursor.getColumnIndexOrThrow(Columns.FOOD_IDS.getColumnName()));
        String servingSizesString = cursor.getString(cursor.getColumnIndexOrThrow(Columns.SERVING_SIZES.getColumnName()));

        int userID = cursor.getInt(cursor.getColumnIndexOrThrow(Columns.USER_ID.getColumnName()));


        // Convert comma-separated string back to an ArrayList of integers
        ArrayList<Integer> foodIds = stringToFoodIds(foodIdsString);
        ArrayList<Integer> servingSizes = stringToServingSizes(servingSizesString);

        return new Meal(id, date, time, type, notes, foodIds, servingSizes, userID);
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

    private static String servingSizesToString(ArrayList<Integer> servingSizes) {
        if (servingSizes == null || servingSizes.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (int id : servingSizes) {
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

    private static ArrayList<Integer> stringToServingSizes(String servingSizesString) {
        ArrayList<Integer> foodIds = new ArrayList<>();
        if (servingSizesString == null || servingSizesString.isEmpty()) return foodIds;
        String[] split = servingSizesString.split(",");
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

    public static ArrayList<Meal> getMealsInRange(Context context, LocalDate startDate, LocalDate endDate) {
        ArrayList<Meal> mealsInRange = new ArrayList<>();

        // Build the selection query.
        // This converts the stored date ("DD/MM/YYYY") into ISO format ("YYYY-MM-DD") on the fly.
        String selection = "(substr(date, 7, 4) || '-' || substr(date, 4, 2) || '-' || substr(date, 1, 2)) >= ? " +
                "AND (substr(date, 7, 4) || '-' || substr(date, 4, 2) || '-' || substr(date, 1, 2)) <= ?";
        // Use ISO-formatted week boundaries as selection arguments.
        String[] selectionArgs = { startDate.toString(), endDate.toString() };

        // Query the "meals" table. Change null to specify columns if needed.
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        mealsInRange = dbHelper.getRecords(DatabaseHelper.Tables.MEALS, selection, selectionArgs);

        return mealsInRange != null ? mealsInRange : new ArrayList<>();
    }

    public static int getWaterIntakeForDay(Context context, LocalDate day) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        String selection = "type = 'waterIntake' AND (substr(date, 7, 4) || '-' || substr(date, 4, 2) || '-' || substr(date, 1, 2)) = ?";

        ArrayList<Meal> waterIntakeMeals = dbHelper.getRecords(DatabaseHelper.Tables.MEALS, selection, DatabaseHelper.toStringArray(day.toString()));

        if (waterIntakeMeals == null || waterIntakeMeals.isEmpty()) return 0;
        Meal waterIntakeMeal = waterIntakeMeals.get(0);

        return waterIntakeMeal != null ? waterIntakeMeal.getServingSizes().get(0) : 0;
    }

    public static Meal getMealByID(Context context, int mealID) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        String selection = "meal_id = ?";
        return dbHelper.getRecord(DatabaseHelper.Tables.MEALS, Columns.MEAL_ID, DatabaseHelper.toStringArray(mealID));
    }

}
