package com.example.refresh;

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
    public static final String USER_EMAIL = "UserEmail";
    public static final String USER_PHONE = "UserPhone";
    public static final String USER_PWD = "UserPassword";

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
        st += USER_EMAIL + " TEXT, ";
        st += USER_PHONE + " TEXT, ";
        st += USER_PWD + " TEXT);";

        return st;
    }

    public boolean registerNewAccount(UserInfo user, SQLiteDatabase db) {

        ContentValues cv = new ContentValues();

        cv.put(USER_NAME, user.getUserName());
        cv.put(USER_EMAIL, user.getUserEmail());
        cv.put(USER_PHONE, user.getUserPhone());
        cv.put(USER_PWD, user.getUserPwd());

        db = this.getWritableDatabase();
        if (db.insert(USERS_TABLE, null, cv) != -1)
            return true;

        return false;
    }

    public ArrayList<UserInfo> getAllRecords(SQLiteDatabase db) {
        int index;
        String name, email, phone, pwd;
        db = getReadableDatabase();
        ArrayList<UserInfo> list = new ArrayList<>();

      Cursor cursor = db.query(USERS_TABLE, null, null, null, null, null, null);
        cursor.moveToFirst();

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

        return list;
    }

    public UserInfo getRecord(int index, SQLiteDatabase db) {
        ArrayList<UserInfo> list = getAllRecords(db);

        return list.get(index);
    }
    public String getFromRecord(int index, UserCols column, SQLiteDatabase db) {
         UserInfo user = getRecord(index, db);

         switch(column) {
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

    public int existsInDB(UserCols column, String value, SQLiteDatabase db) {
        ArrayList<UserInfo> table = getAllRecords(db);

        for (int i=0 ; i<table.size() ; i++) {
            switch(column) {
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

        return -1;
    }
}
