package com.example.refresh.Notification;

import static com.example.refresh.TestingGrounds.testCleanup;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.refresh.Database.NotificationInstancesTable;
import com.example.refresh.Model.NotificationInstance;
import com.example.refresh.Helper.NotificationHelper;
import com.example.refresh.MyApplication;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Retrieve notification details from the Intent extras
        String notificationInstanceID = intent.getStringExtra("NOTIFICATION_INSTANCE_ID");
        NotificationInstance instance = NotificationInstancesTable.getInstanceByID(context, Integer.parseInt(notificationInstanceID));

        if (notificationInstanceID != null) {
            // Call NotificationHelper to display the notification
            SharedPreferences userPreferences = context.getSharedPreferences(MyApplication.getInstance().getLoggedUserSPName(), Context.MODE_PRIVATE);
            if (userPreferences.getBoolean("notificationsEnabled", true)
                    && MyApplication.getInstance().getLoggedUserID() == instance.getUserID()) {

                NotificationHelper.showNotification(context, instance);
            }

            NotificationScheduler.rescheduleDailyNotification(context, intent);
        }
    }
}
