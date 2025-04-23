package com.example.refresh.Helper;

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

import com.example.refresh.Database.NotificationInstancesTable;
import com.example.refresh.Model.NotificationInstance;
import com.example.refresh.Model.NotificationTemplate;
import com.example.refresh.Notification.AppNotificationChannel;
import com.example.refresh.R;

/**
 * Helper class for displaying app notifications.
 * <p>
 * Provides methods to build and show notifications using predefined templates and channels.
 * </p>
 */
public final class NotificationHelper {

    private static final String TAG = "NotificationHelper";

    /** Notification channel ID used for all notifications. */
    public static final String CHANNEL_ID = AppNotificationChannel.CHANNEL_ID;

    /** Default ID used when instance ID is not available. */
    private static final int DEFAULT_NOTIFICATION_ID = 1;

    private NotificationHelper() {
        // Private constructor to prevent instantiation.
    }

    /**
     * Shows a notification based on the given NotificationInstance.
     *
     * @param context  the context used to access system services.
     * @param instance the notification instance containing data for the notification.
     */
    public static void showNotification(Context context, NotificationInstance instance) {
        // Ensure the notification channel exists.
        AppNotificationChannel.createNotificationChannel(context);

        // Retrieve the appropriate template.
        NotificationTemplate template = NotificationInstancesTable.getNotificationTemplate(context, instance);

        // Create the pending intent for notification tap actions.
        PendingIntent pendingIntent = createPendingIntent(context, template);

        // Build the notification.
        NotificationCompat.Builder builder = buildNotification(context, template, pendingIntent);

        // Display the notification.
        displayNotification(context, instance != null ? instance.getInstanceID() : DEFAULT_NOTIFICATION_ID, builder);

        Log.d(TAG, "Notification displayed with ID: " +
                (instance != null ? instance.getInstanceID() : DEFAULT_NOTIFICATION_ID));
    }

    /**
     * Creates a PendingIntent to launch the activity specified in the notification template.
     *
     * @param context  the context used to create the intent.
     * @param template the notification template containing activity class.
     * @return a PendingIntent that launches the target activity.
     */
    private static PendingIntent createPendingIntent(Context context, NotificationTemplate template) {
        Intent intent = new Intent(context, template.getActivityClass());
        return PendingIntent.getActivity(
                context,
                0,
                intent,
                FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
    }

    /**
     * Builds a NotificationCompat.Builder using the provided template and pending intent.
     *
     * @param context       the context used to access resources.
     * @param template      the notification template containing icon, title, and message.
     * @param pendingIntent the pending intent to attach to the notification.
     * @return a configured NotificationCompat.Builder.
     */
    private static NotificationCompat.Builder buildNotification(
            Context context,
            NotificationTemplate template,
            PendingIntent pendingIntent
    ) {
        Bitmap largeIcon = BitmapFactory.decodeResource(
                context.getResources(),
                R.mipmap.ic_launcher_brand
        );
        if (largeIcon == null) {
            Log.e(TAG, "Failed to decode large icon.");
        }

        return new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(template.getIconID())
                .setLargeIcon(largeIcon)
                .setContentTitle(template.getTitle())
                .setContentText(template.getMessage())
                .setColor(Color.BLUE)
                .setCategory(template.getCategory())
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
    }

    /**
     * Displays the notification using the Android NotificationManager.
     *
     * @param context        the context used to get NotificationManager.
     * @param notificationId the unique ID for the notification.
     * @param builder        the NotificationCompat.Builder to build and display.
     */
    private static void displayNotification(
            Context context,
            int notificationId,
            NotificationCompat.Builder builder
    ) {
        NotificationManager manager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(notificationId, builder.build());
        } else {
            Log.w(TAG, "NotificationManager is null; cannot display notification.");
        }
    }
}