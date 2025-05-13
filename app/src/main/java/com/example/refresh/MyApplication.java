package com.example.refresh;

import android.content.SharedPreferences;

import com.example.refresh.Helper.DatabaseHelper;
import com.example.refresh.Database.UsersTable;

public class MyApplication extends android.app.Application {
    private static MyApplication instance;
    public static SharedPreferences sharedPreferences;

    public static final String PREFS_NAME = "AppPreferences";
    public static final String LOGGED_USER_ID_KEY = "loggedUserID";
    public static final String LOGGED_USER_SP_NAME_KEY = "loggedUserSPName";
    public static final String FIRST_LAUNCH_KEY = "isFirstLaunch";

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Initialize shared preferences
        initializeSharedPreferences();
    }

    /**
     * Returns the singleton instance of MyApplication.
     */
    public static MyApplication getInstance() {
        return instance;
    }

    /**
     * Initializes shared preferences for first launch and calorie goal.
     */
    private void initializeSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // If user is logged in, check if they exist in the database
        int loggedUserID = getLoggedUserID();
        if (loggedUserID != -1 && !loggedUserExistsInDB()) {
            sharedPreferences.edit().putInt(LOGGED_USER_ID_KEY, -1).apply();
        }
    }

    /**
     * Returns the logged user's ID.
     */
    public int getLoggedUserID() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return sharedPreferences.getInt(LOGGED_USER_ID_KEY, -1);
    }

    /**
     * Returns the logged user's shared preferences name.
     */
    public String getLoggedUserSPName() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return sharedPreferences.getString(LOGGED_USER_SP_NAME_KEY, null);
    }

    /**
     * Checks if the logged-in user exists in the database.
     */
    public boolean loggedUserExistsInDB() {
        int loggedUserID = getLoggedUserID();

        if (loggedUserID == -1) {
            return false;
        }

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        boolean userExists = dbHelper.existsInDB(DatabaseHelper.Tables.USERS, UsersTable.Columns.ID, String.valueOf(loggedUserID)) != -1;
        dbHelper.close();

        return userExists;
    }
}