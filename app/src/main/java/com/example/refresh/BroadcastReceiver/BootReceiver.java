package com.example.refresh.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.refresh.Database.DatabaseHelper;
import com.example.refresh.Model.NotificationInstance;
import com.example.refresh.Notification.NotificationScheduler;

import java.util.ArrayList;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {

            //retrieveNotificationInstances(context);
            ArrayList<NotificationInstance> instances = retrieveNotificationInstances(context);

            // Schedule daily notifications
            NotificationScheduler.scheduleDailyNotifications(context, instances);
        }
    }

    // Retrieve all notification instances from the database
    private ArrayList<NotificationInstance> retrieveNotificationInstances(Context context) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);

        return new ArrayList<>(dbHelper.getAllRecords(DatabaseHelper.Tables.NOTIFICATION_INSTANCES));
    }
}
