package com.example.refresh.Notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.refresh.BroadcastReceiver.AlarmReceiver;
import com.example.refresh.R;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

public class NotificationScheduler {

    public static void scheduleDailyNotifications(Context context) {
        // Load notification times from SharedPreferences
        SharedPreferences prefs = context.getSharedPreferences("NotificationPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Set<String> notificationTimes = prefs.getStringSet("notification_times", new HashSet<>());
        Set<String> scheduledTimes = prefs.getStringSet("scheduled_times", new HashSet<>()); // Previously scheduled times

        // Get the AlarmManager
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Cancel alarms for times that were removed
        for (String time : scheduledTimes) {
            if (!notificationTimes.contains(time)) {
                cancelExistingAlarm(context, time, alarmManager);
            }
        }

        // Schedule alarms for new times
        for (String time : notificationTimes) {
            if (!scheduledTimes.contains(time)) {
                Calendar calendar = getNextAlarmTime(time);

                // Create the Intent and PendingIntent
                Intent intent = createNotificationIntent(context, time);

                // Schedule the alarm
                PendingIntent pendingIntent = createPendingIntent(context, time, intent);
                scheduleAlarm(alarmManager, calendar, pendingIntent);
            }
        }

        // Update scheduled_times to match notification_times
        editor.putStringSet("scheduled_times", new HashSet<>(notificationTimes));
        editor.apply();
    }


    // Method to reschedule the daily notifications
    public static void rescheduleDailyNotification(Context context, Intent intent) {
        // Get the AlarmManager and the time from the intent
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        String time = intent.getStringExtra("NOTIFICATION_TIME");

        // If the time is null, cancel the existing alarm
        if (time == null)
            return;

        // Calculate the next alarm time
        Calendar calendar = getNextAlarmTime(time);

        // Create the Intent and PendingIntent
        Intent rescheduleIntent = createNotificationIntent(context, time);

        // Schedule the alarm
        PendingIntent pendingIntent = createPendingIntent(context, time, rescheduleIntent);
        scheduleAlarm(alarmManager, calendar, pendingIntent);
    }

    // Method to cancel an existing alarm if needed
    private static void cancelExistingAlarm(Context context, String time, AlarmManager alarmManager) {
        Intent intent = createNotificationIntent(context, time);
        PendingIntent pendingIntent = createPendingIntent(context, time, intent);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }

    // Method to get the next alarm time based on the time from SharedPreferences
    private static Calendar getNextAlarmTime(String time) {
        String[] parts = time.split(":");
        int hour = Integer.parseInt(parts[0]);
        int minute = Integer.parseInt(parts[1]);

        // Set the trigger time
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        // Ensure the time is in the future
        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        return calendar;
    }

    // Method to create the Intent for the alarm notification
    private static Intent createNotificationIntent(Context context, String time) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("NOTIFICATION_TITLE", "Meal Reminder");
        intent.putExtra("NOTIFICATION_MESSAGE", "It's time to log your meal!");
        intent.putExtra("NOTIFICATION_ICON", R.drawable.ic_today);
        intent.putExtra("NOTIFICATION_TIME", time);  // Include the time as an extra

        return intent;
    }

    // Method to create the PendingIntent for the alarm
    private static PendingIntent createPendingIntent(Context context, String time, Intent intent) {
        int hour = Integer.parseInt(time.split(":")[0]);
        int minute = Integer.parseInt(time.split(":")[1]);

        return PendingIntent.getBroadcast(
                context,
                hour * 100 + minute,  // Unique requestCode for each time
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
    }

    // Method to schedule the alarm with the AlarmManager
    private static void scheduleAlarm(AlarmManager alarmManager, Calendar calendar, PendingIntent pendingIntent) {
        if (alarmManager != null) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }
}