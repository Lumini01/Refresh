package com.example.refresh.Database;

import static com.example.refresh.Database.Tables.UsersTable.Columns.*;
import static com.example.refresh.Database.Tables.NotificationTemplatesTable.Columns.*;
import static com.example.refresh.Database.Tables.NotificationInstancesTable.Columns.*;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.refresh.Database.Tables.*;
import com.example.refresh.Model.*;

import java.util.ArrayList;
import java.util.List;

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

        db.execSQL("DROP TABLE IF EXISTS " + Tables.USERS.name());
        db.execSQL("DROP TABLE IF EXISTS " + Tables.NOTIFICATION_TEMPLATES.name());
        db.execSQL("DROP TABLE IF EXISTS " + Tables.NOTIFICATION_INSTANCES.name());
        // Add more table drop logic here
        onCreate(db);
    }

    public void buildTable(String createStatement) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(createStatement);
    }

    public <T> Integer insert(Tables table, T model) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = toContentValues(model, table);

        return (int) db.insert(table.getTableName(), null, values);
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

    public <T> T getRecord(Tables table, Enum<?> columnEnum, String[] selectionArgs) {
        SQLiteDatabase db = this.getReadableDatabase();
        String columnName = columnEnum.name();

        Cursor cursor = null;

        try {
            cursor = db.query(table.getTableName(), new String[]{columnName}, null, selectionArgs, null, null, null);
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

    public <T> Integer editRecords(Tables table, T model,  Enum<?> columnEnum, String[] selectionArgs) {
        SQLiteDatabase db = this.getWritableDatabase();
        String columnName = columnEnum.name();
        ContentValues values = toContentValues(model, table);

        return db.update(table.getTableName(), values, columnName + "=?", selectionArgs);
    }

    public int deleteRecords(Tables table,  Enum<?> columnEnum, String[] selectionArgs) {
        SQLiteDatabase db = this.getWritableDatabase();
        String columnName = columnEnum.name();

        return db.delete(table.getTableName(), columnName + "=?", selectionArgs);
    }

    public int existsInDB(Tables table, Enum<?> columnEnum, String[] selectionArgs) {
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

    public <T> T getRandomRecord(Tables table) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + Tables.NOTIFICATION_TEMPLATES.name() + " ORDER BY RANDOM() LIMIT 1", null);

        if (cursor != null && cursor.moveToFirst()) {
            T randomRecord = createRecord(cursor, table);
            // Populate your NotificationInstance object from the cursor
            // Example: randomInstance.setId(cursor.getInt(cursor.getColumnIndex("id")));
            cursor.close();
            return randomRecord;
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    private <T> T createRecord(Cursor cursor, Tables table) {
        switch (table) {
            case USERS:
                // Example: User table mapping
                return (T) new UserInfo(
                        cursor.getString(cursor.getColumnIndexOrThrow(NAME.name())),
                        cursor.getString(cursor.getColumnIndexOrThrow(EMAIL.name())),
                        cursor.getString(cursor.getColumnIndexOrThrow(PHONE.name())),
                        cursor.getString(cursor.getColumnIndexOrThrow(PWD.name()))
                );
            case NOTIFICATION_TEMPLATES:
                // Example: NotificationTemplates table mapping
                return (T) new NotificationTemplate(
                        cursor.getInt(cursor.getColumnIndexOrThrow(NotificationTemplatesTable.Columns.TEMPLATE_ID.name())),
                        cursor.getString(cursor.getColumnIndexOrThrow(CATEGORY.name())),
                        cursor.getString(cursor.getColumnIndexOrThrow(TITLE.name())),
                        cursor.getString(cursor.getColumnIndexOrThrow(MESSAGE.name())),
                        cursor.getString(cursor.getColumnIndexOrThrow(ICON_ID.name())),
                        cursor.getString(cursor.getColumnIndexOrThrow(ACTIVITY_CLASS.name()))
                );
            case NOTIFICATION_INSTANCES:
                // Example: NotificationInstances table mapping
                return (T) new NotificationInstance(
                        cursor.getInt(cursor.getColumnIndexOrThrow(INSTANCE_ID.name())),
                        cursor.getInt(cursor.getColumnIndexOrThrow(NotificationInstancesTable.Columns.TEMPLATE_ID.name())),
                        cursor.getString(cursor.getColumnIndexOrThrow(TIME.name()))
                );
            default:
                throw new IllegalArgumentException("Unsupported table: " + table.name());
        }
    }

    private <T> ContentValues toContentValues(T model, Tables table) {
        ContentValues values = new ContentValues();

        switch (table) {
            case USERS:
                UserInfo user = (UserInfo) model;
                values.put(NAME.name(), user.getName()); // Assuming UserInfo has a name
                values.put(EMAIL.name(), user.getEmail()); // Assuming UserInfo has an email
                values.put(PHONE.name(), user.getPhone()); // Assuming UserInfo has a phone
                values.put(PWD.name(), user.getPwd()); // Assuming UserInfo has a password
                break;

            case NOTIFICATION_TEMPLATES:
                NotificationTemplate template = (NotificationTemplate) model;
                values.put(NotificationInstancesTable.Columns.TEMPLATE_ID.name(), template.getTemplateID()); // Assuming NotificationTemplate has an ID
                values.put(TITLE.name(), template.getTitle()); // Assuming NotificationTemplate has a title
                values.put(MESSAGE.name(), template.getMessage()); // Assuming NotificationTemplate has a message
                values.put(ICON_ID.name(), template.getIconID()); // Assuming NotificationTemplate has an icon
                values.put(ACTIVITY_CLASS.name(), template.getActivityClassName());
                break;

            case NOTIFICATION_INSTANCES:
                NotificationInstance instance = (NotificationInstance) model;
                values.put(INSTANCE_ID.name(), instance.getInstanceID()); // Assuming NotificationInstance has an ID
                values.put(NotificationInstancesTable.Columns.TEMPLATE_ID.name(), instance.getTemplateID()); // Assuming NotificationInstance has a template_id
                values.put(TIME.name(), instance.getTime()); // Assuming NotificationInstance has a timestamp
                break;

            default:
                throw new IllegalArgumentException("Unsupported table: " + table.name());
        }

        return values;
    }

    public static <T> String[] toSelectionArgs(T value) {
        return new String[] {String.valueOf(value)};
    }

    public boolean isDatabaseOpen() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.isOpen();
    }
}