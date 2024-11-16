package com.example.refreshapp;

import android.content.ContentValues;
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

    public boolean registerNewAccount(UserInfo user, SQLiteDatabase db) {

        ContentValues cv = new ContentValues();

        cv.put(USER_NAME, user.getUserName());
        cv.put(USER_PWD, user.getUserPwd());
        cv.put(USER_EMAIL, user.getUserEmail());
        cv.put(USER_PHONE, user.getUserPhone());

        db = this.getWritableDatabase();
        if (db.insert(USERS_TABLE, null, cv) != -1)
            return true;

        return false;
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
        cursor.close();

        return list;
    }

    public UserInfo getRecord(int index, SQLiteDatabase db) {
        ArrayList<UserInfo> list = getAllRecords(db);

        return list.get(index);
    }
    public String getFromRecord(int index, String column, SQLiteDatabase db) {
         UserInfo user = getRecord(index, db);

         switch(column) {
             case "name":
                 return user.getUserName();
             case "pwd":
                 return user.getUserPwd();
             case "email":
                 return user.getUserEmail();
             case "phone":
                 return user.getUserPhone();
             default:
                 return "Input Error";
         }
    }

    public int isEmailInUse(String email, SQLiteDatabase db) {
        db = this.getReadableDatabase();
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

    public int isPhoneInUse(String phone, SQLiteDatabase db) {
        db = this.getReadableDatabase();
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

    public int isNameInUse(String name, SQLiteDatabase db) {
        db = this.getReadableDatabase();
        Cursor cursor = db.query(USERS_TABLE, null, USER_NAME + " = ?", new String[]{name}, null, null, null);

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
