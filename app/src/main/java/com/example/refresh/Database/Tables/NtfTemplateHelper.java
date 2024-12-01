/*
package com.example.refresh.Database.Tables;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.refresh.Database.DatabaseHelper;

import java.util.ArrayList;

public class NtfTemplateHelper extends DatabaseHelper {

    // Table name and columns
    public static final String TABLE_NAME = "NotificationTemplates";
    public static final String COLUMN_ID = "TemplateID";
    public static final String COLUMN_TITLE = "Title";
    public static final String COLUMN_MESSAGE = "Message";
    public static final String COLUMN_ICON = "Icon";

    // Template attributes
    private int id;
    private String title;
    private String message;
    private String icon;

    public NotificationTemplate(int id, String title, String message, String icon) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.icon = icon;
    }

    public NotificationTemplate() {}

    @Override
    public String buildTable() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_MESSAGE + " TEXT, " +
                COLUMN_ICON + " TEXT);";
    }

    @Override
    public boolean insert(SQLiteDatabase db) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ID, id);
        cv.put(COLUMN_TITLE, title);
        cv.put(COLUMN_MESSAGE, message);
        cv.put(COLUMN_ICON, icon);

        return db.insert(TABLE_NAME, null, cv) != -1;
    }

    @Override
    public ArrayList<NotificationTemplate> getAllRecords(SQLiteDatabase db) {
        ArrayList<NotificationTemplate> templates = new ArrayList<>();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
            String title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE));
            String message = cursor.getString(cursor.getColumnIndex(COLUMN_MESSAGE));
            String icon = cursor.getString(cursor.getColumnIndex(COLUMN_ICON));
            templates.add(new NotificationTemplate(id, title, message, icon));
        }

        cursor.close();
        return templates;
    }

    @Override
    public NotificationTemplate getRecord(int index, SQLiteDatabase db) {
        ArrayList<NotificationTemplate> templates = getAllRecords(db);
        return templates.get(index);
    }
}
*/
