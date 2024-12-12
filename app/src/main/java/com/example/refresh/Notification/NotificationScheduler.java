package com.example.refresh.Notification;

import static com.example.refresh.Database.DatabaseHelper.Tables.*;
import static com.example.refresh.Database.Tables.NotificationInstancesTable.Columns.*;
import static com.example.refresh.Database.Tables.NotificationTemplatesTable.Columns.*;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.refresh.BroadcastReceiver.AlarmReceiver;
import com.example.refresh.Database.DatabaseHelper;
import com.example.refresh.Database.Tables.*;
import com.example.refresh.Model.NotificationInstance;
import com.example.refresh.Model.NotificationTemplate;
import com.example.refresh.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

public class NotificationScheduler {
    public static void addNotificationInstances(Context context, ArrayList<Integer> templateIDs, ArrayList<String> times) {
        SharedPreferences prefs = context.getSharedPreferences("NotificationPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        DatabaseHelper dbHelper = new DatabaseHelper(context);

        // Retrieve the current set of notification times
        Set<String> notificationInstances = prefs.getStringSet("notification_instances", new HashSet<>());

        ArrayList<NotificationInstance> instances = new ArrayList<>();
        int instanceID = 0;
        // Add validated new times to the set
        if (templateIDs.size() == times.size()) {
            for (int i=0 ; i<templateIDs.size() ; i++) {
                int templateID = templateIDs.get(i);
                String time = times.get(i);

                if (time != null && time.matches("\\d{1,2}:\\d{2}") && dbHelper.existsInDB(NOTIFICATION_TEMPLATES, NotificationTemplatesTable.Columns.TEMPLATE_ID, new String[]{String.valueOf(templateID)}) != -1) {
                    instanceID = NotificationInstancesTable.createInstanceID(context);
                    instances.add(new NotificationInstance(instanceID, templateID, time));

                    dbHelper.insert(NOTIFICATION_INSTANCES, instances.get(i));
                    notificationInstances.add(String.valueOf(instanceID));
                }
            }


            // Save updated notification times
            editor.putStringSet("notification_instances", notificationInstances);
            editor.apply();

            // Schedule notifications again
            NotificationScheduler.scheduleDailyNotifications(context, instances);

        }
    }

    public static void scheduleDailyNotifications(Context context, ArrayList<NotificationInstance> instances) {
        SharedPreferences prefs = context.getSharedPreferences("NotificationPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        DatabaseHelper dbHelper = new DatabaseHelper(context);

        // Load notification times
        Set<String> notificationInstances = prefs.getStringSet("notification_instances", new HashSet<>());
        Set<String> scheduledNotifications = prefs.getStringSet("scheduled_notifications", new HashSet<>());

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Cancel removed alarms
        for (String notificationID : scheduledNotifications) {
            if (!notificationInstances.contains(notificationID)) {
                cancelExistingAlarm(context, NotificationInstancesTable.getInstanceByID(context, Integer.parseInt(notificationID)), alarmManager);
            }
        }

        // Schedule alarms
        int index = 0;
        for (String instanceID : notificationInstances) {
            if (!scheduledNotifications.contains(instanceID)) {
                NotificationInstance instance = instances.get(index);

                Calendar calendar = getNextAlarmTime(instance);

                // Create notification intent
                Intent intent = createNotificationIntent(context, instance);

                // Create PendingIntent
                PendingIntent pendingIntent = createPendingIntent(context, instance, intent);

                // Schedule alarm
                scheduleAlarm(context, alarmManager, instance, calendar, pendingIntent);
            }

            index++;
        }

        // Update scheduled times
        editor.putStringSet("scheduled_notifications", new HashSet<>(notificationInstances));
        editor.apply();
    }

    // Method to reschedule the daily notifications
    public static void rescheduleDailyNotification(Context context, Intent intent) {
        // Get the AlarmManager and the time from the intent
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        String instanceID = intent.getStringExtra("NOTIFICATION_INSTANCE_ID");

        DatabaseHelper dbHelper = new DatabaseHelper(context);
        NotificationInstance instance = dbHelper.getRecord(NOTIFICATION_INSTANCES, INSTANCE_ID, new String[]{instanceID});
        // If the time is null, cancel the existing alarm
        if (instance == null)
            return;

        // Calculate the next alarm time
        Calendar calendar = getNextAlarmTime(instance);

        // Create the Intent and PendingIntent
        Intent rescheduleIntent = createNotificationIntent(context, instance);

        // Schedule the alarm
        PendingIntent pendingIntent = createPendingIntent(context, instance, rescheduleIntent);
        scheduleAlarm(context, alarmManager, instance, calendar, pendingIntent);
    }

    // Method to cancel an existing alarm if needed
    public static void cancelExistingAlarm(Context context, NotificationInstance instance, AlarmManager alarmManager) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);

        String time = instance.getTime();

        Intent intent = createNotificationIntent(context, instance);
        PendingIntent pendingIntent = createPendingIntent(context, instance, intent);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }

        String instanceID = String.valueOf(instance.getInstanceID());

        dbHelper.deleteRecords(NOTIFICATION_INSTANCES, INSTANCE_ID, new String[]{instanceID});
        removeScheduledNotificationFromSP(context, instance);
    }

    // Method to get the next alarm time based on the time from SharedPreferences
    private static Calendar getNextAlarmTime(NotificationInstance instance) {
        String time = instance.getTime();

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
    private static Intent createNotificationIntent(Context context, NotificationInstance instance) {
        // Set default values if extras are not provided
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        String index = String.valueOf(dbHelper.existsInDB(NOTIFICATION_TEMPLATES, NotificationTemplatesTable.Columns.TEMPLATE_ID, new String[]{String.valueOf(instance.getTemplateID())}));
        NotificationTemplate template = dbHelper.getRecord(NOTIFICATION_TEMPLATES, NotificationTemplatesTable.Columns.TEMPLATE_ID, new String[]{index});

        if (template.getIcon() == null)
            template.setIcon("ic_placeholder");


        // Get the resource ID for the icon based on the provided icon
        int resourceId = context.getResources().getIdentifier(template.getIcon(), "drawable", context.getPackageName());

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("NOTIFICATION_INSTANCE_ID", String.valueOf(instance.getInstanceID()));

        return intent;
    }

    // Method to create the PendingIntent for the alarm
    private static PendingIntent createPendingIntent(Context context, NotificationInstance instance, Intent intent) {
        String time = instance.getTime();

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
    private static void scheduleAlarm(Context context, AlarmManager alarmManager, NotificationInstance instance, Calendar calendar, PendingIntent pendingIntent) {
        if (alarmManager != null && alarmManager.canScheduleExactAlarms()) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

            addScheduledNotificationToSP(context, instance);
        }
    }

    public static void addNotificationInstanceToSP(Context context, NotificationInstance instance) {
        SharedPreferences prefs = context.getSharedPreferences("NotificationPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // Save updated notification times
        Set<String> notificationInstances = prefs.getStringSet("notification_instances", new HashSet<>());
        String instanceID = String.valueOf(instance.getInstanceID());
        notificationInstances.add(instanceID);

        editor.putStringSet("notification_instances", notificationInstances);
        editor.apply();
    }

    public static void addScheduledNotificationToSP(Context context, NotificationInstance instance) {
        SharedPreferences prefs = context.getSharedPreferences("NotificationPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // Update the scheduled times
        Set<String> scheduledNotifications = prefs.getStringSet("scheduled_notifications", new HashSet<>());
        String instanceID = String.valueOf(instance.getInstanceID());
        scheduledNotifications.add(instanceID);

        editor.putStringSet("scheduled_notifications", scheduledNotifications);
        editor.apply();
    }

    public static void removeNotificationInstanceFromSP(Context context, NotificationInstance instance) {
        SharedPreferences prefs = context.getSharedPreferences("NotificationPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // Save updated notification times
        Set<String> notificationInstances = prefs.getStringSet("notification_instances", new HashSet<>());
        String instanceID = String.valueOf(instance.getInstanceID());
        notificationInstances.remove(instanceID);

        editor.putStringSet("notification_times", notificationInstances);
        editor.apply();
    }

    public static void removeScheduledNotificationFromSP(Context context, NotificationInstance instance) {
        SharedPreferences prefs = context.getSharedPreferences("NotificationPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // Update the scheduled times
        Set<String> scheduledNotifications = prefs.getStringSet("scheduled_notifications", new HashSet<>());
        String instanceID = String.valueOf(instance.getInstanceID());
        scheduledNotifications.remove(instanceID);

        editor.putStringSet("scheduled_notifications", scheduledNotifications);
        editor.apply();
    }
}