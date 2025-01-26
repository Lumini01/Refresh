package com.example.refresh.Notification;

import static com.example.refresh.Database.DatabaseHelper.Tables.*;
import static com.example.refresh.Database.Tables.NotificationInstancesTable.Columns.*;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.refresh.BroadcastReceiver.AlarmReceiver;
import com.example.refresh.Database.DatabaseHelper;
import com.example.refresh.Database.Tables.*;
import com.example.refresh.Model.NotificationInstance;
import com.example.refresh.Model.NotificationTemplate;
import com.example.refresh.R;

import java.util.ArrayList;
import java.util.Calendar;

// Notification Scheduler Class which provides methods for scheduling notifications
public class NotificationScheduler {
    // Declare the table names as constants
    public static void addDefaultNotifications(Context context, ArrayList<Integer> instanceIDs, ArrayList<Integer> templateIDs, ArrayList<String> times) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        ArrayList<NotificationInstance> instances = new ArrayList<>();
        // Add validated new times to the set
        if (templateIDs.size() == times.size() && instanceIDs.size() == times.size()) {
            for (int i=0 ; i<templateIDs.size() ; i++) {
                int instanceID = instanceIDs.get(i);
                int templateID = templateIDs.get(i);
                String time = times.get(i);

                if (time != null && time.matches("\\d{1,2}:\\d{2}") && dbHelper.existsInDB(NOTIFICATION_TEMPLATES, NotificationTemplatesTable.Columns.TEMPLATE_ID, String.valueOf(templateID)) != -1) {
                    NotificationInstance instance = new NotificationInstance(instanceID, templateID, time);
                    NotificationInstance oldInstance = dbHelper.getRecord(NOTIFICATION_INSTANCES, INSTANCE_ID, new String[]{String.valueOf(instanceID)});

                    dbHelper.editRecords(NOTIFICATION_INSTANCES, instance, INSTANCE_ID, new String[]{String.valueOf(instanceID)});
                    instances.add(instance);

                    Intent intent = createNotificationIntent(context, oldInstance);
                    PendingIntent pendingIntent = createPendingIntent(context, oldInstance, intent);
                    if (alarmManager != null) {
                        alarmManager.cancel(pendingIntent);
                    }
                }
            }

            dbHelper.close();


            if (!instances.isEmpty()) {
                // Schedule notifications again
                NotificationScheduler.scheduleDailyNotifications(context, instances);
            }
        }
    }

    // Method to add new notifications
    public static void addNotificationInstances(Context context, ArrayList<Integer> templateIDs, ArrayList<String> times) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);

        ArrayList<NotificationInstance> instances = new ArrayList<>();
        // Add validated new times to the set
        if (templateIDs.size() == times.size()) {
            for (int i=0 ; i<templateIDs.size() ; i++) {
                int templateID = templateIDs.get(i);
                String time = times.get(i);

                if (time != null && time.matches("\\d{1,2}:\\d{2}") && dbHelper.existsInDB(NOTIFICATION_TEMPLATES, NotificationTemplatesTable.Columns.TEMPLATE_ID, String.valueOf(templateID)) != -1) {
                    NotificationInstance instance = new NotificationInstance(templateID, time);
                    int instanceID = dbHelper.insert(NOTIFICATION_INSTANCES, instance);

                    if (instanceID != -1) {
                        instance.setInstanceID(instanceID);
                        instances.add(instance);
                    }
                }
            }

            dbHelper.close();

            if (!instances.isEmpty()) {
                // Schedule notifications again
                NotificationScheduler.scheduleDailyNotifications(context, instances);
            }
        }
    }

    // Schedule daily notifications
    public static void scheduleDailyNotifications(Context context, ArrayList<NotificationInstance> instances) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);


        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        ArrayList<Integer> instanceIDs = new ArrayList<>();

        for (NotificationInstance instance : instances)
            instanceIDs.add(instance.getInstanceID());

        // Cancel removed alarms
        for (int instanceID : instanceIDs) {
            if (dbHelper.existsInDB(NOTIFICATION_INSTANCES, INSTANCE_ID, String.valueOf(instanceID)) == -1) {
                cancelExistingAlarm(context, NotificationInstancesTable.getInstanceByID(context, instanceID), alarmManager);
            }
        }

        // Schedule alarms
        int index = 0;
        for (int instanceID : instanceIDs) {
            if (dbHelper.existsInDB(NOTIFICATION_INSTANCES, INSTANCE_ID, String.valueOf(instanceID)) != -1) {
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

        dbHelper.close();
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

        if (dbHelper.isDatabaseOpen())
            dbHelper.close();
    }

    // Method to cancel an existing alarm if needed
    public static void cancelExistingAlarm(Context context, NotificationInstance instance, AlarmManager alarmManager) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);

        Intent intent = createNotificationIntent(context, instance);
        PendingIntent pendingIntent = createPendingIntent(context, instance, intent);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }

        String instanceID = String.valueOf(instance.getInstanceID());

        dbHelper.deleteRecords(NOTIFICATION_INSTANCES, INSTANCE_ID, new String[]{instanceID});
        dbHelper.close();
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
        if (instance == null) {
            throw new IllegalStateException("NotificationInstance is null. Cannot retrieve template.");
        }

        DatabaseHelper dbHelper = new DatabaseHelper(context);
        NotificationTemplate template = dbHelper.getRecord(NOTIFICATION_TEMPLATES, NotificationTemplatesTable.Columns.TEMPLATE_ID, DatabaseHelper.toStringArray(instance.getTemplateID()));

        dbHelper.close();

        if (template.getIconID() == 0) {
            template.setIconID(R.drawable.ic_placeholder);
        }

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("NOTIFICATION_INSTANCE_ID", String.valueOf(instance.getInstanceID()));

        // Get the current stack trace
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

        // Check if "TestingGrounds.test" is in the call chain
        boolean calledByTest = false;

        for (StackTraceElement element : stackTrace) {
            String className = element.getClassName();
            String methodName = element.getMethodName();

            if (className.equals("com.example.refresh.TestingGrounds") && methodName.equals("test")) {
                calledByTest = true;
                break;
            }
        }

        // Add extra logic based on the caller
        if (calledByTest) {
            intent.putExtra("TEST_NOTIFICATION", true);
            Log.d("NotificationScheduler", "Notification for " + instance.getTime() + " flagged as test notification - will not be rescheduled");
        }
        else {
            Log.d("NotificationScheduler", "Notification not flagged as a test notification");
        }

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
        }
    }
}