package com.example.refresh;

import android.content.SharedPreferences;

import com.example.refresh.Helper.DatabaseHelper;
import com.example.refresh.Database.UsersTable;

public class MyApplication extends android.app.Application {
    private static MyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        // Initialize global state or resources here
        SharedPreferences sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        sharedPreferences.edit().putBoolean("isFirstLaunch", true).apply();

        SharedPreferences userSP = getSharedPreferences(getLoggedUserSPName(), MODE_PRIVATE);
        userSP.edit().putInt("calorieGoal", 2500).apply();

        if (getLoggedUserID() != -1)
            if (!loggedUserExistsInDB())
                sharedPreferences.edit().putInt("loggedUserID", -1).apply();
    }

    public static MyApplication getInstance() {
        return instance;
    }

    public int getLoggedUserID() {
        SharedPreferences sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        return sharedPreferences.getInt("loggedUserID", -1);
    }

    public String getLoggedUserSPName() {
        SharedPreferences sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        return sharedPreferences.getString("loggedUserSPName", null);
    }

    public boolean loggedUserExistsInDB() {
        SharedPreferences sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        int loggedUserID = sharedPreferences.getInt("loggedUserID", -1);

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        boolean userExists = dbHelper.existsInDB(DatabaseHelper.Tables.USERS, UsersTable.Columns.ID, String.valueOf(loggedUserID)) != -1;
        dbHelper.close();

        return loggedUserID != -1 && userExists;
    }
}
