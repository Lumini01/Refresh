/*
package com.example.refresh;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class MonitorService extends Service {
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(1, new Notification());
        return super.onStartCommand(intent, flags, startId);
    }
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        // This will be called when the app is removed (e.g., swiped away)
        updateFlagsWhenAppCloses();
    }

    private void updateFlagsWhenAppCloses() {
        // Perform cleanup or update flags here
        // Set the first launch flag
        SharedPreferences sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isFirstLaunch", true);
        editor.apply();


        Log.d("AppLifecycle", "App task removed, updating flags...");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
*/
