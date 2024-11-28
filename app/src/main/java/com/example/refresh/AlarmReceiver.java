package com.example.refresh;

import static com.example.refresh.NotificationHelper.showNotification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // Show notification
            String title = "Alarm Triggered";
            String message = "It's time to do something!";
            showNotification(context, title, message, HomeDashboard.class);
        }

}
