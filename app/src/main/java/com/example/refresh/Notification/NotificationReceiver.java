package com.example.refresh.Notification;

import static com.example.refresh.TestingGrounds.testCleanup;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.refresh.Database.NotificationInstancesTable;
import com.example.refresh.Model.NotificationInstance;
import com.example.refresh.Helper.NotificationHelper;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Retrieve notification details from the Intent extras
        String notificationInstanceID = intent.getStringExtra("NOTIFICATION_INSTANCE_ID");
        NotificationInstance instance = NotificationInstancesTable.getInstanceByID(context, Integer.parseInt(notificationInstanceID));

        if (notificationInstanceID != null) {
            // Call NotificationHelper to display the notification
            NotificationHelper.showNotification(context, instance);

            if (intent.getBooleanExtra("TEST_NOTIFICATION", false)) {
                // If it's a test notification, skip scheduling and remove the notification from the database
                testCleanup(context, instance);
            }
            else {
                // If not a test notification, reschedule the notification
                NotificationScheduler.rescheduleDailyNotification(context, intent);
            }
        }
    }
}
