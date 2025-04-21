package com.example.refresh;

import android.app.AlarmManager;
import android.content.Context;

import androidx.core.app.NotificationCompat;

import com.example.refresh.Helper.DatabaseHelper;
import com.example.refresh.Database.NotificationInstancesTable;
import com.example.refresh.Model.NotificationInstance;
import com.example.refresh.Model.NotificationTemplate;
import com.example.refresh.Notification.NotificationScheduler;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class TestingGrounds {

    // Constant for DateTimeFormatter pattern
    private static final String TIME_FORMAT_PATTERN = "HH:mm";

    /**
     * Performs a test by adding a random notification template and scheduling it.
     * @param context Application context
     */
    public static void test(Context context) {
        ArrayList<Integer> templateIDs = new ArrayList<>();
        ArrayList<String> times = new ArrayList<>();
        DatabaseHelper dbHelper = new DatabaseHelper(context);

        // Fetch a random notification template from the database
        NotificationTemplate template = dbHelper.getRandomRecord(DatabaseHelper.Tables.NOTIFICATION_TEMPLATES);
        templateIDs.add(template.getTemplateID());

        // Add current time plus 2 minutes as the notification time
        String formattedTime = getFormattedTimeWithOffset(2);  // Get time 2 minutes ahead
        times.add(formattedTime);

        // Schedule the notification
        NotificationScheduler.addNotificationInstances(context, templateIDs, times);
    }

    /**
     * Cleans up a notification instance by canceling its alarm and removing it from the database.
     * @param context Application context
     * @param instance The notification instance to clean up
     */
        public static void testCleanup(Context context, NotificationInstance instance) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Cancel any existing alarm for the notification instance
        NotificationScheduler.cancelExistingAlarm(context, instance, alarmManager);

        // Delete the notification instance from the database
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        dbHelper.deleteRecords(DatabaseHelper.Tables.NOTIFICATION_INSTANCES, NotificationInstancesTable.Columns.INSTANCE_ID, new String[]{String.valueOf(instance.getInstanceID())});
    }

    /**
     * Placeholder for a draft method, potentially for future implementation.
     */
    public static void draft() {
        String str = NotificationCompat.CATEGORY_ALARM;
    }

    /**
     * Helper method to get the current time formatted as "HH:mm" with an optional minute offset.
     * @param offsetMinutes The number of minutes to add to the current time
     * @return Formatted time string
     */
    private static String getFormattedTimeWithOffset(int offsetMinutes) {
        LocalTime timeNow = LocalTime.now().plusMinutes(offsetMinutes);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TIME_FORMAT_PATTERN);
        return timeNow.format(formatter);
    }
}