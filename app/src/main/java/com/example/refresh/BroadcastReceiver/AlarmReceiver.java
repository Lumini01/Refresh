package com.example.refresh.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.refresh.Database.Tables.NotificationInstancesTable;
import com.example.refresh.Model.NotificationInstance;
import com.example.refresh.Notification.NotificationHelper;
import com.example.refresh.Notification.NotificationScheduler;
import com.example.refresh.Start;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Retrieve notification details from the Intent extras
        String notificationInstanceID = intent.getStringExtra("NOTIFICATION_INSTANCE_ID");
        NotificationInstance instance = NotificationInstancesTable.getInstanceByID(context, Integer.parseInt(notificationInstanceID));

        if (notificationInstanceID != null) {
            // Call NotificationHelper to display the notification
            NotificationHelper.showNotification(context, instance);

            // Call NotificationScheduler to reschedule the notification if needed
            NotificationScheduler.rescheduleDailyNotification(context, intent);

        }
    }
}
