package com.example.refresh;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

public class ApplicationClass extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize global state or resources here
        SharedPreferences sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        sharedPreferences.edit().putBoolean("isFirstLaunch", true).apply();
        sharedPreferences.edit().remove("foodSearchResultIDs").apply();
    }
}
