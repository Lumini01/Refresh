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

    /**
     * Performs a test by adding a random notification template and scheduling it.
     * @param context Application context
     */
    public static void test(Context context) {

    }

    /**
     * Cleans up a notification instance by canceling its alarm and removing it from the database.
     * @param context Application context
     */
    public static void testCleanup(Context context) {

    }
}