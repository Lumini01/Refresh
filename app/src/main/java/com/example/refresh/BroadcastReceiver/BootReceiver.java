package com.example.refresh.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.refresh.Notification.NotificationScheduler;

import java.util.ArrayList;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {

            //retrieveNotificationDetails(context);

            // Temporary values
            ArrayList<String> titles = new ArrayList<>();
            ArrayList<String> messages = new ArrayList<>();
            ArrayList<String> icons = new ArrayList<>();

            titles.add("Test");
            messages.add("This is a test notification!");
            icons.add("ic_notifications");

            NotificationScheduler.scheduleDailyNotifications(context, titles, messages, icons);
        }
    }
}
