package com.example.refresh.Database;

import android.content.ContentValues;
import android.database.Cursor;

import com.example.refresh.Model.User;

public class UsersTable {
    public static final String TABLE_NAME = "users";

    // Enum for table columns
    public enum Columns {
        ID("id"),
        NAME("name"),
        EMAIL("email"),
        PHONE("phone"),
        PWD("pwd"),
        PWD_CONFIRM("pwd_confirm");

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
                    Columns.ID.getColumnName() + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    Columns.NAME.getColumnName() + " TEXT NOT NULL, " +
                    Columns.EMAIL.getColumnName() + " TEXT UNIQUE NOT NULL, " +
                    Columns.PHONE.getColumnName() + " TEXT UNIQUE NOT NULL, " +
                    Columns.PWD.getColumnName() + " TEXT NOT NULL);";

    // Convert UserInfo to ContentValues
    public static ContentValues toContentValues(User user) {
        ContentValues values = new ContentValues();
        values.put(Columns.NAME.getColumnName(), user.getName());
        values.put(Columns.EMAIL.getColumnName(), user.getEmail());
        values.put(Columns.PHONE.getColumnName(), user.getPhone());
        values.put(Columns.PWD.getColumnName(), user.getPwd());

        return values;
    }

    // Convert Cursor to UserInfo
    public static User fromCursor(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndexOrThrow(Columns.ID.getColumnName()));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(Columns.NAME.getColumnName()));
        String email = cursor.getString(cursor.getColumnIndexOrThrow(Columns.EMAIL.getColumnName()));
        String phone = cursor.getString(cursor.getColumnIndexOrThrow(Columns.PHONE.getColumnName()));
        String password = cursor.getString(cursor.getColumnIndexOrThrow(Columns.PWD.getColumnName()));

        return new User(id, name, email, phone, password);
    }
}