package com.example.refresh;

import android.app.AlarmManager;
import android.content.Context;
import android.content.SharedPreferences;

import com.example.refresh.Database.DatabaseHelper;
import com.example.refresh.Model.NotificationInstance;
import com.example.refresh.Model.NotificationTemplate;
import com.example.refresh.Notification.NotificationScheduler;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class TestingGrounds {
    public static void test(Context context) {
        ArrayList<Integer> templateIDs = new ArrayList<>();
        ArrayList<String> times = new ArrayList<>();
        DatabaseHelper dbHelper = new DatabaseHelper(context);

        // Add a random notification template for testing
        NotificationTemplate template = dbHelper.getRandomRecord(DatabaseHelper.Tables.NOTIFICATION_TEMPLATES);
        templateIDs.add(template.getTemplateID());

        // Add the current time for testing
        LocalTime timeNow = LocalTime.now().plusMinutes(2);
        times.add(timeNow.toString());

        NotificationScheduler.addNotificationInstances(context, templateIDs, times);
    }

    public static void testCleanup(Context context, NotificationInstance instance) {

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        NotificationScheduler.cancelExistingAlarm(context, instance, alarmManager);
    }
}
