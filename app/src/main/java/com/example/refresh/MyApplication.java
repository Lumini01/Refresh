package com.example.refresh;

import android.content.SharedPreferences;

public class MyApplication extends android.app.Application {
    private static MyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize global state or resources here
        SharedPreferences sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        sharedPreferences.edit().putBoolean("isFirstLaunch", true).apply();
    }

    public static MyApplication getInstance() {
        return instance;
    }

    public int getLoggedUserID() {
        SharedPreferences sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        return sharedPreferences.getInt("loggedUserID", -1);
    }
}
