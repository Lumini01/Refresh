package com.example.refresh;

import android.content.Context;

import com.example.refresh.Notification.NotificationScheduler;

import java.util.ArrayList;

public class TestingGrounds {
    public static void test(Context context) {
        ArrayList<String> titles = new ArrayList<>();
        ArrayList<String> messages = new ArrayList<>();
        ArrayList<String> icons = new ArrayList<>();

        titles.add("Test");
        messages.add("This is a test notification!");
        icons.add("ic_notifications");

        ArrayList<String> times = new ArrayList<>();
        times.add("23:30");
        NotificationScheduler.addNotificationTimes(context, titles, messages, icons, times);
    }
}
