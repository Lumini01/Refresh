package com.example.refresh.Database.Tables;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.example.refresh.Database.DatabaseHelper;
import com.example.refresh.Model.NotificationInstance;
import com.example.refresh.Model.NotificationTemplate;

public class NotificationTemplatesTable {

    public static final String TABLE_NAME = "notification_templates";

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

    public static final String CREATE_TABLE =

            "CREATE TABLE " + TABLE_NAME + " (" +
            Columns.TEMPLATE_ID.getColumnName() + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            Columns.CATEGORY.getColumnName() + " TEXT NOT NULL, " +
            Columns.TITLE.getColumnName() + " TEXT NOT NULL, " +
            Columns.MESSAGE.getColumnName() + " TEXT NOT NULL, " +
            Columns.ICON_ID.getColumnName() + " INTEGER, " +
            Columns.ACTIVITY_CLASS.getColumnName() + " TEXT);";

    public static ContentValues toContentValues(NotificationTemplate template) {

        ContentValues values = new ContentValues();
        values.put(Columns.CATEGORY.getColumnName(), template.getCategory());
        values.put(Columns.TITLE.getColumnName(), template.getTitle());
        values.put(Columns.MESSAGE.getColumnName(), template.getMessage());
        values.put(Columns.ICON_ID.getColumnName(), template.getIconID());
        values.put(Columns.ACTIVITY_CLASS.getColumnName(), template.getActivityClassName());

        return values;
    }

    public static NotificationTemplate fromCursor(Cursor cursor) {

        int templateID = cursor.getInt(cursor.getColumnIndexOrThrow(Columns.TEMPLATE_ID.getColumnName()));
        String category = cursor.getString(cursor.getColumnIndexOrThrow(Columns.CATEGORY.getColumnName()));
        String title = cursor.getString(cursor.getColumnIndexOrThrow(Columns.TITLE.getColumnName()));
        String message = cursor.getString(cursor.getColumnIndexOrThrow(Columns.MESSAGE.getColumnName()));
        String iconIDName = cursor.getString(cursor.getColumnIndexOrThrow(Columns.ICON_ID.getColumnName()));
        String activityClassName = cursor.getString(cursor.getColumnIndexOrThrow(Columns.ACTIVITY_CLASS.getColumnName()));

        return new NotificationTemplate(templateID, category, title, message, iconIDName, activityClassName);
    }

    public static NotificationTemplate getTemplateByID(Context context, int templateID) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);

        int index = dbHelper.existsInDB(DatabaseHelper.Tables.NOTIFICATION_TEMPLATES, Columns.TEMPLATE_ID, new String[]{String.valueOf(templateID)});

        if (index == -1)
            return null;

        NotificationTemplate template = dbHelper.getRecord(DatabaseHelper.Tables.NOTIFICATION_TEMPLATES, Columns.TEMPLATE_ID, new String[]{String.valueOf(templateID)});

        return template;
    }
}
