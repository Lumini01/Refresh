package com.example.refresh;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

/**
 * Helper class to interact with the SQLite database for storing and retrieving user data.
 */
public class HelperDB extends SQLiteOpenHelper {

    // Database and table names
    public static final String DB_FILE = "refresh_database.db";
    public static final String USERS_TABLE = "Users";
    public static final String USER_NAME = "UserName";
    public static final String USER_EMAIL = "UserEmail";
    public static final String USER_PHONE = "UserPhone";
    public static final String USER_PWD = "UserPassword";

    /**
     * Constructor to initialize the HelperDB class.
     * @param context The context to use for database creation.
     */
    public HelperDB(@Nullable Context context) {
        super(context, DB_FILE, null, 1);
    }

    /**
     * Creates the database tables upon first creation.
     * @param db The database to be created.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the Users table
        db.execSQL(buildUserTable());
    }

    /**
     * Handles database upgrade logic (currently not implemented).
     * @param db The database to upgrade.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Implement database upgrade logic here if necessary
    }

    /**
     * Builds the SQL query to create the Users table.
     * @return The SQL query string.
     */
    public String buildUserTable() {
        // SQL query to create the Users table
        String st = "CREATE TABLE IF NOT EXISTS " + USERS_TABLE;
        st += "(" + USER_NAME + " TEXT, ";
        st += USER_EMAIL + " TEXT, ";
        st += USER_PHONE + " TEXT, ";
        st += USER_PWD + " TEXT);";

        return st;
    }

    /**
     * Registers a new user account in the database.
     * @param user The user information to insert.
     * @param db The database to insert into.
     * @return true if the account was successfully registered, false otherwise.
     */
    public boolean registerNewAccount(UserInfo user, SQLiteDatabase db) {
        ContentValues cv = new ContentValues();

        // Add user details to the content values
        cv.put(USER_NAME, user.getUserName());
        cv.put(USER_EMAIL, user.getUserEmail());
        cv.put(USER_PHONE, user.getUserPhone());
        cv.put(USER_PWD, user.getUserPwd());

        db = this.getWritableDatabase();
        // Insert the new user data into the Users table
        boolean success = db.insert(USERS_TABLE, null, cv) != -1;

        db.close();

        return success;
    }

    /**
     * Retrieves all user records from the database.
     * @param db The database to retrieve records from.
     * @return A list of all user records.
     */
    public ArrayList<UserInfo> getAllRecords(SQLiteDatabase db) {
        int index;
        String name, email, phone, pwd;
        db = getReadableDatabase();
        ArrayList<UserInfo> list = new ArrayList<>();

        // Query the Users table to get all records
        Cursor cursor = db.query(USERS_TABLE, null, null, null, null, null, null);
        cursor.moveToFirst();

        // Iterate through each record and add to the list
        while (cursor.moveToNext()) {
            index = cursor.getColumnIndex(USER_NAME);
            name = cursor.getString(index);
            index = cursor.getColumnIndex(USER_EMAIL);
            email = cursor.getString(index);
            index = cursor.getColumnIndex(USER_PHONE);
            phone = cursor.getString(index);
            index = cursor.getColumnIndex(USER_PWD);
            pwd = cursor.getString(index);

            UserInfo record = new UserInfo(name, email, phone, pwd);
            list.add(record);
        }
        cursor.close();

        db.close();

       return list;
    }

    /**
     * Retrieves a specific user record by index.
     * @param index The index of the user record.
     * @param db The database to retrieve the record from.
     * @return The user record at the specified index.
     */
    public UserInfo getRecord(int index, SQLiteDatabase db) {
        ArrayList<UserInfo> list = getAllRecords(db);

        // Return the user at the specified index
        return list.get(index);
    }

    /**
     * Retrieves a specific column value from a user record.
     * @param index The index of the user record.
     * @param column The column from which to retrieve the value.
     * @param db The database to retrieve the record from.
     * @return The value of the specified column for the user record.
     */
    public String getFromRecord(int index, UserCols column, SQLiteDatabase db) {
        UserInfo user = getRecord(index, db);

        // Return the appropriate column value based on the column type
        switch (column) {
            case NAME:
                return user.getUserName();
            case EMAIL:
                return user.getUserEmail();
            case PHONE:
                return user.getUserPhone();
            case PWD:
                return user.getUserPwd();
            default:
                return "Input Error";
        }
    }

    /**
     * Checks if a specific value exists in the database for a given column.
     * @param column The column to check (Name, Email, Phone, or Password).
     * @param value The value to search for in the column.
     * @param db The database to check.
     * @return The index of the record if found, -1 if not found.
     */
    public int existsInDB(UserCols column, String value, SQLiteDatabase db) {
        ArrayList<UserInfo> table = getAllRecords(db);

        // Iterate through the records and check if the value matches any column
        for (int i = 0; i < table.size(); i++) {
            switch (column) {
                case NAME:
                    if (value.equals(table.get(i).getUserName()))
                        return i;
                    break;
                case EMAIL:
                    if (value.equals(table.get(i).getUserEmail()))
                        return i;
                    break;
                case PHONE:
                    if (value.equals(table.get(i).getUserPhone()))
                        return i;
                    break;
                case PWD:
                    if (value.equals(table.get(i).getUserPwd()))
                        return i;
                    break;
                default:
                    return -1;
            }
        }

        // Return -1 if the value was not found
        return -1;
    }
}
