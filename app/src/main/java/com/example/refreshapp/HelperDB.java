package com.example.refreshapp;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class HelperDB extends SQLiteOpenHelper {

    public static final String DB_FILE = "refresh_database.db";
    public static final String USERS_TABLE = "Users";
    public static final String USER_NAME = "UserName";
    public static final String USER_PWD = "UserPassword";
    public static final String USER_EMAIL = "UserEmail";
    public static final String USER_PHONE = "UserPhone";

    public HelperDB(@Nullable Context context) {
        super(context, DB_FILE, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(buildUserTable());

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public String buildUserTable() {
        String st = "CREATE TABLE IF NOT EXISTS " + USERS_TABLE;
        st += "(" + USER_NAME + " TEXT, ";
        st += USER_PWD + " TEXT, ";
        st += USER_EMAIL + " TEXT, ";
        st += USER_PHONE + " TEXT);";

        return st;
    }

    public ArrayList<UserInfo> getAllRecords(SQLiteDatabase db) {
        int index;
        String name, pwd, email, phone;
        db = getReadableDatabase();
        ArrayList<UserInfo> list = new ArrayList<>();

        Cursor cursor = db.query(USERS_TABLE, null, null, null, null, null, null);
        cursor.moveToFirst();

        while (cursor.moveToNext()) {

            index = cursor.getColumnIndex(USER_NAME);
            name = cursor.getString(index);
            index = cursor.getColumnIndex(USER_PWD);
            pwd = cursor.getString(index);
            index = cursor.getColumnIndex(USER_EMAIL);
            email = cursor.getString(index);
            index = cursor.getColumnIndex(USER_PHONE);
            phone = cursor.getString(index);
            UserInfo record = new UserInfo(name, pwd, email, phone);
            list.add(record);
        }

        db.close();

        return list;
    }

    public int isEmailInUse(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(USERS_TABLE, null, USER_EMAIL + " = ?", new String[]{email}, null, null, null);

        if (cursor.getCount() > 0) {

            cursor.moveToFirst();

            int matchedIndex = cursor.getPosition();

            cursor.close();

            return matchedIndex;
        }

        cursor.close();

        return -1;
    }

    public int isPhoneInUse(String phone) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(USERS_TABLE, null, USER_PHONE + " = ?", new String[]{phone}, null, null, null);

        if (cursor.getCount() > 0) {

            cursor.moveToFirst();

            int matchedIndex = cursor.getPosition();

            cursor.close();

            return matchedIndex;
        }

        cursor.close();

        return -1;
    }
}
