package com.example.refresh.Notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.refresh.Start;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Retrieve notification details from the Intent extras
        String notificationTitle = intent.getStringExtra("NOTIFICATION_TITLE");
        String notificationMessage = intent.getStringExtra("NOTIFICATION_MESSAGE");

        // Set default values if extras are not provided
        if (notificationTitle == null) notificationTitle = "Reminder";
        if (notificationMessage == null) notificationMessage = "Don't forget your scheduled activity!";

        // Call NotificationHelper to display the notification
        NotificationHelper.showNotification(context, notificationTitle, notificationMessage, Start.class);
    }
}
