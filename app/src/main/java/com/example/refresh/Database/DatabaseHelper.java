package com.example.refresh.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.refresh.Database.Tables.NotificationInstancesTable;
import com.example.refresh.Database.Tables.NotificationTemplatesTable;
import com.example.refresh.Database.Tables.UsersTable;
import com.example.refresh.Model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class DatabaseHelper extends SQLiteOpenHelper {

    public enum Tables {
        USERS(UsersTable.TABLE_NAME),
        NOTIFICATION_TEMPLATES(NotificationTemplatesTable.TABLE_NAME),
        NOTIFICATION_INSTANCES(NotificationInstancesTable.TABLE_NAME);

        private final String tableName;

        Tables(String tableName) {
            this.tableName = tableName;
        }

        public String getTableName() {
            return tableName;
        }
    }

    private static final String DATABASE_FILE = "app_database.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_FILE, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(UsersTable.CREATE_TABLE);
        db.execSQL(NotificationTemplatesTable.CREATE_TABLE);
        db.execSQL(NotificationInstancesTable.CREATE_TABLE);
        // Add more table creation logic here
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + UsersTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + NotificationTemplatesTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + NotificationInstancesTable.TABLE_NAME);
        // Add more table drop logic here
        onCreate(db);
    }

    public void buildTable(String createStatement) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(createStatement);
    }

    public long insert(Tables table, ContentValues values) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.insert(table.getTableName(), null, values);
    }

    public <T> List<T> getAllRecords(Tables table) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<T> records = new ArrayList<>();
        Cursor cursor = null;

        try {
            cursor = db.query(table.getTableName(), null, null, null, null, null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    T record = createRecord(cursor, table);
                    if (record != null) {
                        records.add(record);
                    }
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return records;
    }

    public <T> T getRecord(Tables table, String selection, String[] selectionArgs) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.query(table.getTableName(), null, selection, selectionArgs, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                return createRecord(cursor, table);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null; // Return null if no record is found
    }


    public <T> T getFromRecord(Tables table, Enum<?> columnEnum, int index) {
        SQLiteDatabase db = this.getReadableDatabase();
        String columnName = columnEnum.name();  // Convert the enum to the column name (e.g., "name")

        Cursor cursor = db.query(table.getTableName(), new String[]{columnName}, null, null, null, null, null);

        T result = null;

        if (cursor != null && cursor.moveToPosition(index)) {
            // Retrieve the value from the specified column
            int columnIndex = cursor.getColumnIndex(columnName);

            if (columnIndex != -1) {
                // If you know the expected type (e.g., String, Integer), cast it accordingly
                result = (T) cursor.getString(columnIndex);  // Assuming it's a String for this example
            }
            cursor.close();
        }

        return result;  // Return the value or null if not found
    }

    public int editRecords(Tables table, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.update(table.getTableName(), values, selection, selectionArgs);
    }

    public int removeRecords(Tables table, String selection, String[] selectionArgs) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(table.getTableName(), selection, selectionArgs);
    }

    public int existsInDB(Tables table, String[] selectionArgs, Enum<?> columnEnum) {
        SQLiteDatabase db = this.getReadableDatabase();
        String columnName = columnEnum.name();  // Convert the enum to the column name

        Cursor cursor = db.query(table.getTableName(), new String[]{columnName}, null, selectionArgs, null, null, null);

        int index = -1;  // Default to -1 (not found)

        if (cursor != null && cursor.moveToFirst()) {
            index = cursor.getPosition();  // Get the index of the first matching record
            cursor.close();
        }

        return index;
    }

    public void closeDB() {
        this.close();
    }

    @SuppressWarnings("unchecked")
    private <T> T createRecord(Cursor cursor, Tables table) {
        switch (table) {
            case USERS:
                // Example: User table mapping
                return (T) new UserInfo(
                        cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        cursor.getString(cursor.getColumnIndexOrThrow("email")),
                        cursor.getString(cursor.getColumnIndexOrThrow("phone")),
                        cursor.getString(cursor.getColumnIndexOrThrow("pwd"))
                );
            case NOTIFICATION_TEMPLATES:
                // Example: NotificationTemplates table mapping
                return (T) new NotificationTemplate(
                        cursor.getInt(cursor.getColumnIndexOrThrow("template_id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("category")),
                        cursor.getString(cursor.getColumnIndexOrThrow("title")),
                        cursor.getString(cursor.getColumnIndexOrThrow("message")),
                        cursor.getString(cursor.getColumnIndexOrThrow("icon")),
                        cursor.getString(cursor.getColumnIndexOrThrow("color"))
                );
            case NOTIFICATION_INSTANCES:
                // Example: NotificationInstances table mapping
                return (T) new NotificationInstance(
                        cursor.getInt(cursor.getColumnIndexOrThrow("instance_id")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("template_id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("time"))
                );
            default:
                throw new IllegalArgumentException("Unsupported table: " + table.name());
        }
    }

    /*private ContentValues toContentValues(Object object, Tables table) {
        ContentValues values = new ContentValues();

        switch (table) {
            case USERS:
                UserInfo user = (UserInfo) object;
                values.put("id", user.getId()); // Assuming UserInfo has an ID
                values.put("name", user.getName()); // Assuming UserInfo has a name
                values.put("email", user.getEmail()); // Assuming UserInfo has an email
                values.put("phone", user.getPhone()); // Assuming UserInfo has a phone
                values.put("pwd", user.getPwd()); // Assuming UserInfo has a password
                break;

            case NOTIFICATION_TEMPLATES:
                NotificationTemplate template = (NotificationTemplate) object;
                values.put("id", template.getId()); // Assuming NotificationTemplate has an ID
                values.put("title", template.getTitle()); // Assuming NotificationTemplate has a title
                values.put("message", template.getMessage()); // Assuming NotificationTemplate has a message
                values.put("icon", template.getIcon()); // Assuming NotificationTemplate has an icon
                values.put("color", template.getColor()); // Assuming NotificationTemplate has a color
                break;

            case NOTIFICATION_INSTANCES:
                NotificationInstance instance = (NotificationInstance) object;
                values.put("id", instance.getId()); // Assuming NotificationInstance has an ID
                values.put("template_id", instance.getTemplateId()); // Assuming NotificationInstance has a template_id
                values.put("user_id", instance.getUserId()); // Assuming NotificationInstance has a user_id
                values.put("timestamp", instance.getTimestamp()); // Assuming NotificationInstance has a timestamp
                break;

            default:
                throw new IllegalArgumentException("Unsupported table: " + table.name());
        }

        return values;
    }
*/
}