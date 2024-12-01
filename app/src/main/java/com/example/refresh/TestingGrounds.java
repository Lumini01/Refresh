package com.example.refresh;

import android.app.AlarmManager;
import android.content.Context;
import android.content.SharedPreferences;

import com.example.refresh.Notification.NotificationScheduler;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class TestingGrounds {
    public static void test(Context context) {
        ArrayList<String> titles = new ArrayList<>();
        ArrayList<String> messages = new ArrayList<>();
        ArrayList<String> icons = new ArrayList<>();

        titles.add("Test");
        messages.add("This is a test notification!");
        icons.add("ic_notifications");

        LocalTime timeNow = LocalTime.now().plusMinutes(2);

        ArrayList<String> times = new ArrayList<>();
        times.add(timeNow.toString());
        NotificationScheduler.addNotificationTimes(context, titles, messages, icons, times);
    }

    public static void testCleanup(Context context,  String time) {
        SharedPreferences prefs = context.getSharedPreferences("NotificationPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        NotificationScheduler.cancelExistingAlarm(context, time, alarmManager);
    }
}
