/*
package com.example.refresh.Database.Tables;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class NotificationInstance extends DatabaseTable {

    // Table name and columns
    public static final String TABLE_NAME = "NotificationInstances";
    public static final String COLUMN_ID = "InstanceID";
    public static final String COLUMN_TEMPLATE_ID = "TemplateID";
    public static final String COLUMN_USER_ID = "UserID";
    public static final String COLUMN_TIMESTAMP = "Timestamp";
    public static final String COLUMN_STATUS = "Status";

    // Instance attributes
    private int instanceId;
    private int templateId;
    private String userId;
    private long timestamp;
    private String status;

    public NotificationInstance(int instanceId, int templateId, String userId, long timestamp, String status) {
        this.instanceId = instanceId;
        this.templateId = templateId;
        this.userId = userId;
        this.timestamp = timestamp;
        this.status = status;
    }

    public NotificationInstance() {}

    @Override
    public String buildTable() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TEMPLATE_ID + " INTEGER, " +
                COLUMN_USER_ID + " TEXT, " +
                COLUMN_TIMESTAMP + " INTEGER, " +
                COLUMN_STATUS + " TEXT, " +
                "FOREIGN KEY(" + COLUMN_TEMPLATE_ID + ") REFERENCES " + NotificationTemplate.TABLE_NAME + "(" + NotificationTemplate.COLUMN_ID + "));";
    }

    @Override
    public boolean insert(SQLiteDatabase db) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TEMPLATE_ID, templateId);
        cv.put(COLUMN_USER_ID, userId);
        cv.put(COLUMN_TIMESTAMP, timestamp);
        cv.put(COLUMN_STATUS, status);

        return db.insert(TABLE_NAME, null, cv) != -1;
    }

    @Override
    public ArrayList<NotificationInstance> getAllRecords(SQLiteDatabase db) {
        ArrayList<NotificationInstance> instances = new ArrayList<>();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            int instanceId = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
            int templateId = cursor.getInt(cursor.getColumnIndex(COLUMN_TEMPLATE_ID));
            String userId = cursor.getString(cursor.getColumnIndex(COLUMN_USER_ID));
            long timestamp = cursor.getLong(cursor.getColumnIndex(COLUMN_TIMESTAMP));
            String status = cursor.getString(cursor.getColumnIndex(COLUMN_STATUS));
            instances.add(new NotificationInstance(instanceId, templateId, userId, timestamp, status));
        }

        cursor.close();
        return instances;
    }

    @Override
    public NotificationInstance getRecord(int index, SQLiteDatabase db) {
        ArrayList<NotificationInstance> instances = getAllRecords(db);
        return instances.get(index);
    }
}
*/
