package com.example.refresh.Notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.refresh.BroadcastReceiver.AlarmReceiver;
import com.example.refresh.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

public class NotificationScheduler {
    public static void addNotificationTimes(Context context, ArrayList<String> titles, ArrayList<String> messages, ArrayList<String> icons, ArrayList<String> newTimes) {
        SharedPreferences prefs = context.getSharedPreferences("NotificationPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // Retrieve the current set of notification times
        Set<String> notificationTimes = prefs.getStringSet("notification_times", new HashSet<>());

        // Add validated new times to the set
        for (String newTime : newTimes) {
            if (newTime != null && newTime.matches("\\d{1,2}:\\d{2}")) { // Validate format
                notificationTimes.add(newTime);
            }
        }

        // Save updated notification times
        editor.putStringSet("notification_times", notificationTimes);
        editor.apply();

        // Schedule notifications again
        NotificationScheduler.scheduleDailyNotifications(context, titles, messages, icons);
    }

    public static void scheduleDailyNotifications(Context context, ArrayList<String> titles, ArrayList<String> messages, ArrayList<String> icons) {
        SharedPreferences prefs = context.getSharedPreferences("NotificationPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // Load notification times
        Set<String> notificationTimes = prefs.getStringSet("notification_times", new HashSet<>());
        Set<String> scheduledTimes = prefs.getStringSet("scheduled_times", new HashSet<>());

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Cancel removed alarms
        for (String time : scheduledTimes) {
            if (!notificationTimes.contains(time)) {
                cancelExistingAlarm(context, time, alarmManager);
            }
        }

        // Validate and align parameters
        int requiredSize = notificationTimes.size();
        while (titles.size() < requiredSize) titles.add("Generated Title");
        while (messages.size() < requiredSize) messages.add("Generated Message");
        while (icons.size() < requiredSize) icons.add("ic_placeholder");

        // Schedule alarms
        int index = 0;
        for (String time : notificationTimes) {
            if (!scheduledTimes.contains(time)) {
                Calendar calendar = getNextAlarmTime(time);

                // Create notification intent
                Intent intent = createNotificationIntent(context, titles.get(index), messages.get(index), icons.get(index), time);

                // Create PendingIntent
                PendingIntent pendingIntent = createPendingIntent(context, time, intent);

                // Schedule alarm
                scheduleAlarm(context, alarmManager, calendar, pendingIntent);
            }
            index++;
        }

        // Update scheduled times
        editor.putStringSet("scheduled_times", new HashSet<>(notificationTimes));
        editor.apply();
    }

    // Method to reschedule the daily notifications
    public static void rescheduleDailyNotification(Context context, Intent intent, String title, String message, String icon) {
        // Get the AlarmManager and the time from the intent
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        String time = intent.getStringExtra("NOTIFICATION_TIME");

        // If the time is null, cancel the existing alarm
        if (time == null)
            return;

        // Calculate the next alarm time
        Calendar calendar = getNextAlarmTime(time);

        // Create the Intent and PendingIntent
        Intent rescheduleIntent = createNotificationIntent(context, title, message, icon, time);

        // Schedule the alarm
        PendingIntent pendingIntent = createPendingIntent(context, time, rescheduleIntent);
        scheduleAlarm(context, alarmManager, calendar, pendingIntent);
    }

    // Method to cancel an existing alarm if needed
    public static void cancelExistingAlarm(Context context, String time, AlarmManager alarmManager) {
        Intent intent = createNotificationIntent(context, null, null, null, time);
        PendingIntent pendingIntent = createPendingIntent(context, time, intent);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }

        removeScheduledTimeFromSP(context, time);
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
    private static Intent createNotificationIntent(Context context, String title, String message, String icon, String time) {
        // Set default values if extras are not provided
        if (icon == null) {
            icon = "ic_placeholder";
        }

        // Get the resource ID for the icon based on the provided icon
        int resourceId = context.getResources().getIdentifier(icon, "drawable", context.getPackageName());

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("NOTIFICATION_TITLE", title);
        intent.putExtra("NOTIFICATION_MESSAGE", message);
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
    private static void scheduleAlarm(Context context, AlarmManager alarmManager, Calendar calendar, PendingIntent pendingIntent) {
        if (alarmManager != null && alarmManager.canScheduleExactAlarms()) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

            addScheduledTimeToSP(context, calendar.getTime().toString());
        }
    }

    public static void addNotificationTimeToSP(Context context, String time) {
        SharedPreferences prefs = context.getSharedPreferences("NotificationPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // Save updated notification times
        Set<String> notificationTimes = prefs.getStringSet("notification_times", new HashSet<>());
        notificationTimes.add(time);

        editor.putStringSet("notification_times", notificationTimes);
        editor.apply();
    }

    public static void addScheduledTimeToSP(Context context, String time) {
        SharedPreferences prefs = context.getSharedPreferences("NotificationPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // Update the scheduled times
        Set<String> scheduledTimes = prefs.getStringSet("scheduled_times", new HashSet<>());
        scheduledTimes.add(time);

        editor.putStringSet("scheduled_times", scheduledTimes);
        editor.apply();
    }

    public static void removeNotificationTimeFromSP(Context context, String time) {
        SharedPreferences prefs = context.getSharedPreferences("NotificationPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // Save updated notification times
        Set<String> notificationTimes = prefs.getStringSet("notification_times", new HashSet<>());
        notificationTimes.remove(time);

        editor.putStringSet("notification_times", notificationTimes);
        editor.apply();
    }

    public static void removeScheduledTimeFromSP(Context context, String time) {
        SharedPreferences prefs = context.getSharedPreferences("NotificationPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // Update the scheduled times
        Set<String> scheduledTimes = prefs.getStringSet("scheduled_times", new HashSet<>());
        scheduledTimes.remove(time);

        editor.putStringSet("scheduled_times", scheduledTimes);
        editor.apply();
    }
}