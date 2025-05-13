package com.example.refresh.Database;

import static com.example.refresh.Database.NotificationInstancesTable.Columns.INSTANCE_ID;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.example.refresh.Helper.DatabaseHelper;
import com.example.refresh.Model.NotificationInstance;
import com.example.refresh.Model.NotificationTemplate;

import java.util.ArrayList;

public class NotificationInstancesTable {
    public static final String TABLE_NAME = "notification_instances";

    // Enum for table columns
    public enum Columns {
        INSTANCE_ID("instance_id"),
        TEMPLATE_ID("template_id"),
        USER_ID("user_id"),
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
                    Columns.USER_ID.getColumnName()     + " INTEGER NOT NULL, " +
                    Columns.TIME.getColumnName()        + " TEXT NOT NULL, " +
                    "FOREIGN KEY(" + Columns.TEMPLATE_ID.getColumnName() + ") " +
                    "REFERENCES " + NotificationTemplatesTable.TABLE_NAME +
                    "(" + NotificationTemplatesTable.Columns.TEMPLATE_ID.getColumnName() + "), " +
                    "FOREIGN KEY(" + Columns.USER_ID.getColumnName() + ") " +
                    "REFERENCES " + UsersTable.TABLE_NAME +
                    "(" + UsersTable.Columns.ID.getColumnName() + ")" +
                    ");";

    // Convert NotificationInstance to ContentValues
    public static ContentValues toContentValues(NotificationInstance instance) {

        ContentValues values = new ContentValues();
        if (instance.getInstanceID() != -1)
            values.put(INSTANCE_ID.getColumnName(), instance.getInstanceID());
        values.put(Columns.TEMPLATE_ID.getColumnName(), instance.getTemplateID());
        values.put(Columns.USER_ID.getColumnName(), instance.getUserID());
        values.put(Columns.TIME.getColumnName(), instance.getTime());

        return values;
    }

    // Convert Cursor to NotificationInstance
    public static NotificationInstance fromCursor(Cursor cursor) {

        int instanceID = cursor.getInt(cursor.getColumnIndexOrThrow(Columns.INSTANCE_ID.getColumnName()));
        int templateID = cursor.getInt(cursor.getColumnIndexOrThrow(Columns.TEMPLATE_ID.getColumnName()));
        int userID = cursor.getInt(cursor.getColumnIndexOrThrow(Columns.USER_ID.getColumnName()));
        String time = cursor.getString(cursor.getColumnIndexOrThrow(Columns.TIME.getColumnName()));

        return new NotificationInstance(instanceID, templateID, userID, time);
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

    public static ArrayList<NotificationInstance> getUserDefaultNotifications(Context context, int userID) {
        ArrayList<NotificationInstance> instances = new ArrayList<>();
        DatabaseHelper dbHelper = new DatabaseHelper(context);

        instances.add(
                dbHelper.getRecord(
                DatabaseHelper.Tables.NOTIFICATION_INSTANCES,
                new Enum<?>[]{Columns.TEMPLATE_ID, Columns.USER_ID},
                new String[]{2 + "", userID + ""}
                )
        );
        instances.add(
                dbHelper.getRecord(
                        DatabaseHelper.Tables.NOTIFICATION_INSTANCES,
                        new Enum<?>[]{Columns.TEMPLATE_ID, Columns.USER_ID},
                        new String[]{3 + "", userID + ""}
                )
        );
        instances.add(
                dbHelper.getRecord(
                        DatabaseHelper.Tables.NOTIFICATION_INSTANCES,
                        new Enum<?>[]{Columns.TEMPLATE_ID, Columns.USER_ID},
                        new String[]{4 + "", userID + ""}
                )
        );
        instances.add(
                dbHelper.getRecord(
                        DatabaseHelper.Tables.NOTIFICATION_INSTANCES,
                        new Enum<?>[]{Columns.TEMPLATE_ID, Columns.USER_ID},
                        new String[]{6 + "", userID + ""}
                )
        );

        for (NotificationInstance instance : instances) {
            if (instance == null)
                return null;
        }

        return instances;
    }

    public static ArrayList<String> getUserDefaultNotificationTimes(Context context, int userID) {
        ArrayList<String> instanceTimes = new ArrayList<>();

        ArrayList<NotificationInstance> instances = getUserDefaultNotifications(context,
                userID);
        for (NotificationInstance instance : instances) {
            instanceTimes.add(instance.getTime());
        }

        return instanceTimes;
    }
}
