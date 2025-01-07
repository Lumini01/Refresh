package com.example.refresh.Database.Tables;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.example.refresh.Database.DatabaseHelper;
import com.example.refresh.Model.Food;
import com.example.refresh.Model.NotificationInstance;

public class FoodsTable {

    public static final String TABLE_NAME = "foods";

    public enum Columns {
        FOOD_ID("food_id"),
        NAME("name"),
        CATEGORY("category"),
        LABEL("label"),
        SERVING_SIZE("serving_size"),
        CALORIES("calories"),
        CARBS("carbs"),
        PROTEIN("protein"),
        FAT("fat");

        private final String columnName;

        Columns(String columnName) {
            this.columnName = columnName;
        }

        public String getColumnName() {
            return columnName;
        }
    }

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    Columns.FOOD_ID.getColumnName() + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    Columns.NAME.getColumnName() + " TEXT NOT NULL, " +
                    Columns.CATEGORY.getColumnName() + " TEXT NOT NULL, " +
                    Columns.LABEL.getColumnName() + " TEXT, " +
                    Columns.SERVING_SIZE.getColumnName() + " INTEGER NOT NULL, " +
                    Columns.CALORIES.getColumnName() + " INTEGER NOT NULL, " +
                    Columns.CARBS.getColumnName() + " INTEGER NOT NULL, " +
                    Columns.PROTEIN.getColumnName() + " INTEGER NOT NULL, " +
                    Columns.FAT.getColumnName() + " INTEGER NOT NULL);";

    public static ContentValues toContentValues(Food food) {
        ContentValues values = new ContentValues();
        values.put(Columns.NAME.getColumnName(), food.getName());
        values.put(Columns.CATEGORY.getColumnName(), food.getCategory());
        values.put(Columns.LABEL.getColumnName(), food.getLabel());
        values.put(Columns.SERVING_SIZE.getColumnName(), food.getServingSize());
        values.put(Columns.CALORIES.getColumnName(), food.getCalories());
        values.put(Columns.CARBS.getColumnName(), food.getCarbs());
        values.put(Columns.PROTEIN.getColumnName(), food.getProtein());
        values.put(Columns.FAT.getColumnName(), food.getFat());

        return values;
    }

    public static Food fromCursor(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndexOrThrow(Columns.FOOD_ID.getColumnName()));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(Columns.NAME.getColumnName()));
        String category = cursor.getString(cursor.getColumnIndexOrThrow(Columns.CATEGORY.getColumnName()));
        String label = cursor.getString(cursor.getColumnIndexOrThrow(Columns.LABEL.getColumnName()));
        int servingSize = cursor.getInt(cursor.getColumnIndexOrThrow(Columns.SERVING_SIZE.getColumnName()));
        int calories = cursor.getInt(cursor.getColumnIndexOrThrow(Columns.CALORIES.getColumnName()));
        int carbs = cursor.getInt(cursor.getColumnIndexOrThrow(Columns.CARBS.getColumnName()));
        int protein = cursor.getInt(cursor.getColumnIndexOrThrow(Columns.PROTEIN.getColumnName()));
        int fat = cursor.getInt(cursor.getColumnIndexOrThrow(Columns.FAT.getColumnName()));

        return new Food(id, name, category, label, servingSize, calories, carbs, protein, fat);
    }

    public static Food getFoodByID(Context context, int foodID) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);

        int index = dbHelper.existsInDB(DatabaseHelper.Tables.FOODS, FoodsTable.Columns.FOOD_ID, String.valueOf(foodID));

        if (index == -1)
            return null;

        return dbHelper.getRecord(DatabaseHelper.Tables.FOODS, FoodsTable.Columns.FOOD_ID, new String[]{String.valueOf(foodID)});
    }
}
