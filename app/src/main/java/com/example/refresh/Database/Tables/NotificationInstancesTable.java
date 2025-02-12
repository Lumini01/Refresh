package com.example.refresh.Database.Tables;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.example.refresh.Database.DatabaseHelper;
import com.example.refresh.Model.NotificationInstance;
import com.example.refresh.Model.NotificationTemplate;

public class NotificationInstancesTable {

    // TODO: Add user id to table. to enable multiple users feature - else, no notification.
    public static final String TABLE_NAME = "notification_instances";

    // Enum for table columns
    public enum Columns {
        INSTANCE_ID("instance_id"),
        TEMPLATE_ID("template_id"),
        TIME("time");

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
            Columns.INSTANCE_ID.getColumnName() + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            Columns.TEMPLATE_ID.getColumnName() + " INTEGER NOT NULL, " +
            Columns.TIME.getColumnName() + " TEXT NOT NULL, " +
            "FOREIGN KEY(" + Columns.TEMPLATE_ID.getColumnName() + ") " +
            "REFERENCES " + NotificationTemplatesTable.TABLE_NAME + " (" +
            NotificationTemplatesTable.Columns.TEMPLATE_ID.getColumnName() + "));";

    // Convert NotificationInstance to ContentValues
    public static ContentValues toContentValues(NotificationInstance instance) {

        ContentValues values = new ContentValues();
        values.put(Columns.TEMPLATE_ID.getColumnName(), instance.getTemplateID());
        values.put(Columns.TIME.getColumnName(), instance.getTime());

        return values;
    }

    // Convert Cursor to NotificationInstance
    public static NotificationInstance fromCursor(Cursor cursor) {

        int instanceID = cursor.getInt(cursor.getColumnIndexOrThrow(Columns.INSTANCE_ID.getColumnName()));
        int templateID = cursor.getInt(cursor.getColumnIndexOrThrow(Columns.TEMPLATE_ID.getColumnName()));
        String time = cursor.getString(cursor.getColumnIndexOrThrow(Columns.TIME.getColumnName()));

        return new NotificationInstance(instanceID, templateID, time);
    }

    // Create a new notification instance ID
    public static int createInstanceID(Context context) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        int id = 0;
        while (true) {
            if (dbHelper.existsInDB(DatabaseHelper.Tables.NOTIFICATION_INSTANCES, Columns.INSTANCE_ID, String.valueOf(id)) == -1)
                break;

            id++;
        }

        return id;
    }

    // Get a notification instance by its ID
    public static NotificationInstance getInstanceByID(Context context, int instanceID) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);

        int index = dbHelper.existsInDB(DatabaseHelper.Tables.NOTIFICATION_INSTANCES, Columns.INSTANCE_ID, String.valueOf(instanceID));

        if (index == -1)
            return null;

        return dbHelper.getRecord(DatabaseHelper.Tables.NOTIFICATION_INSTANCES, Columns.INSTANCE_ID, new String[]{String.valueOf(instanceID)});
    }

    // Get the notification template associated with a notification instance
    public static NotificationTemplate getNotificationTemplate(Context context, NotificationInstance instance) {
        int templateID = instance.getTemplateID();

        return NotificationTemplatesTable.getTemplateByID(context, templateID);
    }
}
