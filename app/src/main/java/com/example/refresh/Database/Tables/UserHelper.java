package com.example.refresh.Database.Tables;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;

import com.example.refresh.Database.DatabaseHelper;
import com.example.refresh.Database.TableCols;
import com.example.refresh.Database.TableCols.*;
import com.example.refresh.User.UserInfo;

import java.util.ArrayList;

public class UserHelper extends DatabaseHelper<UserInfo> {

    // Table name and columns
    public static final String TABLE = Tables.USERS.name();
    public static final String COLUMN_NAME = UserCols.NAME.name();
    public static final String COLUMN_EMAIL = UserCols.EMAIL.name();
    public static final String COLUMN_PHONE = UserCols.PHONE.name();
    public static final String COLUMN_PWD = UserCols.PWD.name();;

    /**
     * Constructor to initialize the HelperDB class.
     *
     * @param context The context to use for database creation.
     */
    public UserHelper(@Nullable Context context) {
        super(context);
    }

    public String buildTable() {
        // SQL query to create the Users table
        String st = "CREATE TABLE IF NOT EXISTS " + TABLE;
        st += "(" + COLUMN_NAME + " TEXT, ";
        st += COLUMN_EMAIL + " TEXT, ";
        st += COLUMN_PHONE + " TEXT, ";
        st += COLUMN_PWD + " TEXT);";

        return st;
    }

    /**
     * Registers a new user account in the database.
     * @param user The user information to insert.
     * @param db The database to insert into.
     * @return true if the account was successfully registered, false otherwise.
     */
    @Override
    public boolean insert(UserInfo user, SQLiteDatabase db) {
        ContentValues cv = new ContentValues();

        // Add user details to the content values
        cv.put(COLUMN_NAME, user.getUserName());
        cv.put(COLUMN_EMAIL, user.getUserEmail());
        cv.put(COLUMN_PHONE, user.getUserPhone());
        cv.put(COLUMN_PWD, user.getUserPwd());

        db = this.getWritableDatabase();
        // Insert the new user data into the Users table
        boolean success = db.insert(TABLE, null, cv) != -1;

        db.close();

        return success;
    }

    /**
     * Retrieves all user records from the database.
     * @param db The database to retrieve records from.
     * @return A list of all user records.
     */
    @Override
    public ArrayList<UserInfo> getAllRecords(SQLiteDatabase db) {
        int index;
        String name, email, phone, pwd;
        db = getReadableDatabase();
        ArrayList<UserInfo> list = new ArrayList<>();

        // Query the Users table to get all records
        Cursor cursor = db.query(TABLE, null, null, null, null, null, null);
        cursor.moveToFirst();

        // Iterate through each record and add to the list
        while (cursor.moveToNext()) {
            index = cursor.getColumnIndex(COLUMN_NAME);
            name = cursor.getString(index);
            index = cursor.getColumnIndex(COLUMN_EMAIL);
            email = cursor.getString(index);
            index = cursor.getColumnIndex(COLUMN_PHONE);
            phone = cursor.getString(index);
            index = cursor.getColumnIndex(COLUMN_PWD);
            pwd = cursor.getString(index);

            UserInfo record = new UserInfo(name, email, phone, pwd);
            list.add(record);
        }
        cursor.close();

        db.close();

        return list;
    }

    /**
     * Retrieves a specific column value from a user record.
     * @param index The index of the user record.
     * @param column The column from which to retrieve the value.
     * @param db The database to retrieve the record from.
     * @return The value of the specified column for the user record.
     */
    public String getFromRecord(int index, ColumnEnum column, SQLiteDatabase db) {
        UserInfo user = getRecord(index, db);
        if (column instanceof UserCols) {
            // Return the appropriate column value based on the column type
            switch ((UserCols) column) {
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

        return "Input Error";
    }

    /**
     * Checks if a specific value exists in the database for a given column.
     * @param column The column to check (Name, Email, Phone, or Password).
     * @param value The value to search for in the column.
     * @param db The database to check.
     * @return The index of the record if found, -1 if not found.
     */
    public int existsInDB(ColumnEnum column, String value, SQLiteDatabase db) {
        ArrayList<UserInfo> table = getAllRecords(db);

        // Iterate through the records and check if the value matches any column
        if (column instanceof UserCols) {
            for (int i = 0; i < table.size(); i++) {
                switch ((UserCols) column) {
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
        }

        // Return -1 if the value was not found
        return -1;
    }
}
