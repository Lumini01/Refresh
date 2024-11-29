package com.example.refresh.Notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

public class NotificationScheduler {
    public void scheduleNotification(Context context, String title, String message, long triggerAtMillis) {
        // Create an Intent that will trigger the AlarmReceiver
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("NOTIFICATION_TITLE", title);
        intent.putExtra("NOTIFICATION_MESSAGE", message);

        // Create a PendingIntent that will be sent to the AlarmReceiver
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Get the AlarmManager system service
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Schedule the alarm to trigger at the specified time
        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + triggerAtMillis,
                    pendingIntent);  // Trigger the alarm after the specified time
        }
    }
}
