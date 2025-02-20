package com.example.refresh.Database;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.refresh.Helper.DatabaseHelper;
import com.example.refresh.Model.NotificationTemplate;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class NotificationTemplatesTable {

    public static final String TABLE_NAME = "notification_templates";

    // Enum for table columns
    public enum Columns {
        TEMPLATE_ID("template_id"),
        CATEGORY("category"),
        TITLE("title"),
        MESSAGE("message"),
        ICON_ID("icon_id"),
        ACTIVITY_CLASS("activity_class");

        private final String columnName;

        Columns(String columnName) {
            this.columnName = columnName;
        }

        public String getColumnName() {
            return columnName;
        }
    }

    public static final String JSON_FILE_NAME = "notification_templates.json";

    // Create table query
    public static final String CREATE_TABLE =

            "CREATE TABLE " + TABLE_NAME + " (" +
            Columns.TEMPLATE_ID.getColumnName() + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            Columns.CATEGORY.getColumnName() + " TEXT NOT NULL, " +
            Columns.TITLE.getColumnName() + " TEXT NOT NULL, " +
            Columns.MESSAGE.getColumnName() + " TEXT NOT NULL, " +
            Columns.ICON_ID.getColumnName() + " INTEGER, " +
            Columns.ACTIVITY_CLASS.getColumnName() + " TEXT);";

    // Convert NotificationTemplate to ContentValues
    public static ContentValues toContentValues(NotificationTemplate template) {

        ContentValues values = new ContentValues();
        values.put(Columns.CATEGORY.getColumnName(), template.getCategory());
        values.put(Columns.TITLE.getColumnName(), template.getTitle());
        values.put(Columns.MESSAGE.getColumnName(), template.getMessage());
        values.put(Columns.ICON_ID.getColumnName(), template.getIconID());
        values.put(Columns.ACTIVITY_CLASS.getColumnName(), template.getActivityClassName());

        return values;
    }

    // Convert Cursor to NotificationTemplate
    public static NotificationTemplate fromCursor(Context context, Cursor cursor) {

        int templateID = cursor.getInt(cursor.getColumnIndexOrThrow(Columns.TEMPLATE_ID.getColumnName()));
        String category = cursor.getString(cursor.getColumnIndexOrThrow(Columns.CATEGORY.getColumnName()));
        String title = cursor.getString(cursor.getColumnIndexOrThrow(Columns.TITLE.getColumnName()));
        String message = cursor.getString(cursor.getColumnIndexOrThrow(Columns.MESSAGE.getColumnName()));
        String iconIDName = cursor.getString(cursor.getColumnIndexOrThrow(Columns.ICON_ID.getColumnName()));
        String activityClassName = cursor.getString(cursor.getColumnIndexOrThrow(Columns.ACTIVITY_CLASS.getColumnName()));

        return new NotificationTemplate(context, templateID, category, title, message, iconIDName, activityClassName);
    }

    // Get a notification template by its ID
    public static NotificationTemplate getTemplateByID(Context context, int templateID) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);

        int index = dbHelper.existsInDB(DatabaseHelper.Tables.NOTIFICATION_TEMPLATES, Columns.TEMPLATE_ID, String.valueOf(templateID));

        if (index == -1)
            return null;

        NotificationTemplate template = dbHelper.getRecord(DatabaseHelper.Tables.NOTIFICATION_TEMPLATES, Columns.TEMPLATE_ID, new String[]{String.valueOf(templateID)});

        return template;
    }

    // Populate the table with data from the notification templates JSON file
    public static void populateNotificationTemplatesTable(Context context) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        if (isDatabasePopulated(dbHelper)) {
            dbHelper.close();
            return; // Table already populated, skip seeding
        }

        // Seed the table with data from the JSON file
        String json = loadJSONFromAsset(context, JSON_FILE_NAME);
        ArrayList<NotificationTemplate> templates = parseTemplates(context, json);
        seedNotificationTemplates(dbHelper, templates);

        // Close the database helper
        dbHelper.close();
    }

    // Check if the table already has data
    private static boolean isDatabasePopulated(DatabaseHelper dbHelper) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // Check if the table already has data
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + DatabaseHelper.Tables.NOTIFICATION_TEMPLATES, null);
        if (cursor.moveToFirst() && cursor.getInt(0) > 0) {
            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }

    // Seed the table with data from the JSON file
    public static void seedNotificationTemplates(DatabaseHelper dbHelper, ArrayList<NotificationTemplate> templates) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        for (NotificationTemplate template : templates) {
            ContentValues values = dbHelper.toContentValues(template, DatabaseHelper.Tables.NOTIFICATION_TEMPLATES);

            db.insertWithOnConflict(DatabaseHelper.Tables.NOTIFICATION_TEMPLATES.getTableName(), null, values, SQLiteDatabase.CONFLICT_REPLACE);
        }
    }

    // Parse the notification templates JSON data
    public static ArrayList<NotificationTemplate> parseTemplates(Context context, String json) {
        ArrayList<NotificationTemplate> templates = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                NotificationTemplate template = new NotificationTemplate(
                        context,
                        jsonObject.getInt("templateID"),
                        jsonObject.getString("category"),
                        jsonObject.getString("title"),
                        jsonObject.getString("message"),
                        jsonObject.getString("iconID"),
                        jsonObject.getString("activityClass")
                );
                templates.add(template);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return templates;
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
}