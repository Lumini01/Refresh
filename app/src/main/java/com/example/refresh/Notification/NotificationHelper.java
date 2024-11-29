package com.example.refresh.Notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.example.refresh.R;

public class NotificationHelper {

    // Declare CHANNEL_ID as a static final variable
    public static final String CHANNEL_ID = "notificationChannel";

    // Show the notification
    public static void showNotification(Context context, String title, String message, Class<?> targetActivity) {
        // Ensure the notification channel exists
        AppNotificationChannel.createNotificationChannel(context);

        // Create the PendingIntent
        PendingIntent pendingIntent = createPendingIntent(context, targetActivity);

        // Build the Notification
        NotificationCompat.Builder notificationBuilder = buildNotification(context, title, message, pendingIntent);

        // Display the Notification
        displayNotification(context, notificationBuilder);
    }

    // Create PendingIntent for launching the target activity
    private static PendingIntent createPendingIntent(Context context, Class<?> targetActivity) {
        Intent intent = new Intent(context, targetActivity);
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
    }

    // Build the notification with content, action, and channel
    private static NotificationCompat.Builder buildNotification(Context context, String title, String message, PendingIntent pendingIntent) {
        return new NotificationCompat.Builder(context, CHANNEL_ID)  // Use the channel ID defined in this class
                .setSmallIcon(R.drawable.ic_today)  // Replace with your app's icon
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
    }

    // Display the notification
    private static void displayNotification(Context context, NotificationCompat.Builder builder) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(1, builder.build());  // 1 is the notification ID
        }
    }
}
