package com.example.refresh.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.refresh.Notification.NotificationHelper;
import com.example.refresh.Notification.NotificationScheduler;
import com.example.refresh.Start;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Retrieve notification details from the Intent extras
        String notificationTitle = intent.getStringExtra("NOTIFICATION_TITLE");
        String notificationMessage = intent.getStringExtra("NOTIFICATION_MESSAGE");
        String notificationIconLocation = intent.getStringExtra("NOTIFICATION_ICON");

        // Set default values if extras are not provided
        if (notificationTitle == null) notificationTitle = "Reminder";
        if (notificationMessage == null) notificationMessage = "Don't forget your scheduled activity!";
        if (notificationIconLocation == null) notificationIconLocation = "ic_today";

        // Call NotificationHelper to display the notification
        NotificationHelper.showNotification(context, notificationTitle, notificationMessage, notificationIconLocation, Start.class);

        // Call NotificationScheduler to reschedule the notification if needed
        NotificationScheduler.rescheduleDailyNotification(context, intent);
    }
}
