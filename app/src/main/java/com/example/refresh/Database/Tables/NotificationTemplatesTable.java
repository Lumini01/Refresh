package com.example.refresh.Database.Tables;

import android.content.ContentValues;
import android.database.Cursor;

import com.example.refresh.Database.DatabaseHelper;
import com.example.refresh.Model.NotificationTemplate;

public class NotificationTemplatesTable {

    public static final String TABLE_NAME = "notification_templates";

    public enum Columns {
        TEMPLATE_ID("templateID"),
        CATEGORY("category"),
        TITLE("title"),
        MESSAGE("message"),
        ICON("icon"),
        COLOR("color");

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
            Columns.TEMPLATE_ID.getColumnName() + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            Columns.CATEGORY.getColumnName() + " TEXT NOT NULL, " +
            Columns.TITLE.getColumnName() + " TEXT NOT NULL, " +
            Columns.MESSAGE.getColumnName() + " TEXT NOT NULL, " +
            Columns.ICON.getColumnName() + " TEXT, " +
            Columns.COLOR.getColumnName() + " TEXT);";

    public static ContentValues toContentValues(NotificationTemplate template) {

        ContentValues values = new ContentValues();
        values.put(Columns.CATEGORY.getColumnName(), template.getCategory());
        values.put(Columns.TITLE.getColumnName(), template.getTitle());
        values.put(Columns.MESSAGE.getColumnName(), template.getMessage());
        values.put(Columns.ICON.getColumnName(), template.getIcon());
        values.put(Columns.COLOR.getColumnName(), template.getColor());

        return values;
    }

    public static NotificationTemplate fromCursor(Cursor cursor) {

        int templateID = cursor.getInt(cursor.getColumnIndexOrThrow(Columns.TEMPLATE_ID.getColumnName()));
        String category = cursor.getString(cursor.getColumnIndexOrThrow(Columns.CATEGORY.getColumnName()));
        String title = cursor.getString(cursor.getColumnIndexOrThrow(Columns.TITLE.getColumnName()));
        String message = cursor.getString(cursor.getColumnIndexOrThrow(Columns.MESSAGE.getColumnName()));
        String icon = cursor.getString(cursor.getColumnIndexOrThrow(Columns.ICON.getColumnName()));
        String color = cursor.getString(cursor.getColumnIndexOrThrow(Columns.COLOR.getColumnName()));

        return new NotificationTemplate(templateID, category, title, message, icon, color);
    }
}
