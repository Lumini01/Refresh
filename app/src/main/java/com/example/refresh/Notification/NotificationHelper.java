package com.example.refresh.Notification;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.refresh.Database.Tables.NotificationInstancesTable;
import com.example.refresh.Model.NotificationInstance;
import com.example.refresh.Model.NotificationTemplate;
import com.example.refresh.R;
import com.example.refresh.TestingGrounds;

public class NotificationHelper {

    // Declare CHANNEL_ID as a static final variable
    public static final String CHANNEL_ID = AppNotificationChannel.CHANNEL_ID;

    // Show the notification
    public static void showNotification(Context context, NotificationInstance instance) {
        // Ensure the notification channel exists
        AppNotificationChannel.createNotificationChannel(context);

        // Create the PendingIntent
        PendingIntent pendingIntent = createPendingIntent(context, instance);

        // Build the Notification
        NotificationCompat.Builder notificationBuilder = buildNotification(context, instance, pendingIntent);

        // Display the Notification
        displayNotification(context, notificationBuilder);

        Log.d("NotificationDisplay", "Notification displayed with ID: " + instance.getInstanceID());

    }

    // Create PendingIntent for launching the target activity
    private static PendingIntent createPendingIntent(Context context, NotificationInstance instance) {
        Intent intent = new Intent(context, NotificationInstancesTable.getNotificationTemplate(context, instance).getActivityClass());
        return PendingIntent.getActivity(context, 0, intent, FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

    // Build the notification with content, action, and channel
    private static NotificationCompat.Builder buildNotification(Context context,  NotificationInstance instance,PendingIntent pendingIntent) {

        NotificationTemplate template = NotificationInstancesTable.getNotificationTemplate(context, instance);

        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher_brand);

        if (largeIcon == null) {
            Log.e("NotificationBuilder", "Failed to decode large icon.");
        }

        return new NotificationCompat.Builder(context, CHANNEL_ID)  // Use the channel ID defined in this class
                .setSmallIcon(template.getIconID())
                .setLargeIcon(largeIcon)
                .setContentTitle(template.getTitle())
                .setContentText(template.getMessage())
                .setColor(Color.BLUE)
                .setCategory(template.getCategory())
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
