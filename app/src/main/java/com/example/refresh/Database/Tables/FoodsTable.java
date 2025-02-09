package com.example.refresh.Database.Tables;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.refresh.Database.DatabaseHelper;
import com.example.refresh.Model.Food;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

public class FoodsTable {

    public static final String TABLE_NAME = "foods";

    // Enum for table columns
    public enum Columns {
        FOOD_ID("food_id"),
        NAME("name"),
        DESCRIPTION("description"),
        CATEGORY("category"),
        LABEL("label"),
        SERVING_SIZE("serving_size"),
        CALORIES("calories"),
        CARBS("carbs"),
        PROTEIN("protein"),
        FAT("fat"),
        NOTES("notes");

        private final String columnName;

        Columns(String columnName) {
            this.columnName = columnName;
        }

        public String getColumnName() {
            return columnName;
        }
    }
    public static final String JSON_FILE_NAME = "foods.json";

    // Create table query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    Columns.FOOD_ID.getColumnName() + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    Columns.NAME.getColumnName() + " TEXT NOT NULL, " +
                    Columns.DESCRIPTION.getColumnName() + " TEXT NOT NULL, " +
                    Columns.CATEGORY.getColumnName() + " TEXT NOT NULL, " +
                    Columns.LABEL.getColumnName() + " TEXT, " +
                    Columns.SERVING_SIZE.getColumnName() + " INTEGER NOT NULL, " +
                    Columns.CALORIES.getColumnName() + " INTEGER NOT NULL, " +
                    Columns.CARBS.getColumnName() + " INTEGER NOT NULL, " +
                    Columns.PROTEIN.getColumnName() + " INTEGER NOT NULL, " +
                    Columns.FAT.getColumnName() + " INTEGER NOT NULL, " +
                    Columns.NOTES.getColumnName() + " TEXT NOT NULL);";

    // Convert Food to ContentValues
    public static ContentValues toContentValues(Food food) {
        ContentValues values = new ContentValues();
        values.put(Columns.FOOD_ID.getColumnName(), food.getId());
        values.put(Columns.NAME.getColumnName(), food.getName());
        values.put(Columns.DESCRIPTION.getColumnName(), food.getDescription());
        values.put(Columns.CATEGORY.getColumnName(), food.getCategory());
        values.put(Columns.LABEL.getColumnName(), String.join(",", food.getLabels()));
        values.put(Columns.SERVING_SIZE.getColumnName(), food.getServingSize());
        values.put(Columns.CALORIES.getColumnName(), food.getCalories());
        values.put(Columns.CARBS.getColumnName(), food.getCarbs());
        values.put(Columns.PROTEIN.getColumnName(), food.getProtein());
        values.put(Columns.FAT.getColumnName(), food.getFat());
        values.put(Columns.NOTES.getColumnName(), food.getNotes());

        return values;
    }

    // Convert Cursor to Food
    public static Food fromCursor(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndexOrThrow(Columns.FOOD_ID.getColumnName()));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(Columns.NAME.getColumnName()));
        String description = cursor.getString(cursor.getColumnIndexOrThrow(Columns.DESCRIPTION.getColumnName()));
        String category = cursor.getString(cursor.getColumnIndexOrThrow(Columns.CATEGORY.getColumnName()));
        ArrayList<String> label = new ArrayList<>(Arrays.asList(cursor.getString(cursor.getColumnIndexOrThrow(Columns.LABEL.getColumnName())).split(",")));
        int servingSize = cursor.getInt(cursor.getColumnIndexOrThrow(Columns.SERVING_SIZE.getColumnName()));
        int calories = cursor.getInt(cursor.getColumnIndexOrThrow(Columns.CALORIES.getColumnName()));
        int carbs = cursor.getInt(cursor.getColumnIndexOrThrow(Columns.CARBS.getColumnName()));
        int protein = cursor.getInt(cursor.getColumnIndexOrThrow(Columns.PROTEIN.getColumnName()));
        int fat = cursor.getInt(cursor.getColumnIndexOrThrow(Columns.FAT.getColumnName()));
        String notes = cursor.getString(cursor.getColumnIndexOrThrow(Columns.NOTES.getColumnName()));

        return new Food(id, name, description, category, label, servingSize, calories, carbs, protein, fat, notes);
    }

    // Get a food by its ID
    public static Food getFoodByID(Context context, int foodID) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);

        int index = dbHelper.existsInDB(DatabaseHelper.Tables.FOODS, FoodsTable.Columns.FOOD_ID, String.valueOf(foodID));

        if (index == -1)
            return null;

        return dbHelper.getRecord(DatabaseHelper.Tables.FOODS, FoodsTable.Columns.FOOD_ID, new String[]{String.valueOf(foodID)});
    }

    // Populate the table with data from the notification templates JSON file
    public static void populateFoodsTable(Context context) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        String json = loadJSONFromAsset(context, JSON_FILE_NAME);

        if (isDatabasePopulated(dbHelper) && !isDatabaseUpdateNeeded(json)) {
            dbHelper.close();
            return; // Table already populated, skip seeding
        }

        // Seed the table with data from the JSON file
        ArrayList<Food> foods = parseFoods(context, json);
        seedFoods(dbHelper, foods);

        // Close the database helper
        dbHelper.close();

        //editJson(json);
    }

    // Check if the database is populated
    private static boolean isDatabasePopulated(DatabaseHelper dbHelper) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // Check if the table already has data
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + DatabaseHelper.Tables.FOODS, null);
        if (cursor.moveToFirst() && cursor.getInt(0) > 0) {
            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }

    // Check if the database needs to be updated
    private static boolean isDatabaseUpdateNeeded(String json) {
        try {
            JSONObject jsonObject = (new JSONArray(json)).getJSONObject(0);
            if (jsonObject.getInt("id") == -1)
                return true;
            else
                return false;

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    // Seed the table with data from the JSON file
    public static void seedFoods(DatabaseHelper dbHelper, ArrayList<Food> foods) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        for (int i = 0; i < foods.size(); i++) {
            Food food = foods.get(i);
            ContentValues values = dbHelper.toContentValues(food, DatabaseHelper.Tables.FOODS);

            db.insertWithOnConflict(DatabaseHelper.Tables.FOODS.getTableName(), null, values, SQLiteDatabase.CONFLICT_REPLACE);
        }
    }

    // Parse the JSON data into a list of Food objects
    public static ArrayList<Food> parseFoods(Context context, String json) {
        ArrayList<Food> foods = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 1; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                JSONArray labelsArray = jsonObject.getJSONArray("labels");
                ArrayList<String> labelsList = new ArrayList<>();
                for (int j = 0; j < labelsArray.length(); j++) {
                    labelsList.add(labelsArray.getString(j));
                }

                Food food = new Food(
                        jsonObject.getInt("id"),
                        jsonObject.getString("name"),
                        "",
                        jsonObject.getString("category"),
                        labelsList,
                        100,
                        jsonObject.getInt("calories"),
                        jsonObject.getInt("carbs"),
                        jsonObject.getInt("protein"),
                        jsonObject.getInt("fat"),
                        ""
                );
                foods.add(food);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return foods;
    }

    // Load JSON data from an asset file
    public static String loadJSONFromAsset(Context context, String fileName) {
        String json = null;
        try {
            AssetManager assetManager = context.getAssets();
            InputStream is = assetManager.open(fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            is.close();
            json = stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }

    // Edit the JSON file to change -1 to 0
    public static void editJson(String json) {
        try {
            // 1. Read the file content as a string
            String content = new String(
                    Files.readAllBytes(Paths.get(json)),
                    StandardCharsets.UTF_8
            );

            // 2. Parse as a JSON array
            JSONArray jsonArray = new JSONArray(content);

            // 3. If there's at least one item, check its 'id'
            if (jsonArray.length() > 0) {
                JSONObject firstObject = jsonArray.getJSONObject(0);
                if (firstObject.optInt("id", 0) == -1) {
                    // 4. Change -1 to -2
                    firstObject.put("id", -2);

                    // 5. Write the updated array back to the file
                    Files.write(
                            Paths.get(json),
                            jsonArray.toString(4).getBytes(StandardCharsets.UTF_8)
                    );
                }
            }
        }
        catch (JSONException e) {
            throw new RuntimeException(e);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
