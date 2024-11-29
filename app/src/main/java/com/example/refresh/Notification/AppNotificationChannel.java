package com.example.refresh.Notification;

import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;

public class AppNotificationChannel {

    public static final String CHANNEL_ID = "notificationChannel";
    public static final String CHANNEL_NAME = "Main_Channel";
    public static final String CHANNEL_DESCRIPTION = "channelDescription";

    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            android.app.NotificationChannel channel = new android.app.NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription(CHANNEL_DESCRIPTION);
            channel.enableVibration(true);
            channel.setLightColor(Color.BLUE);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
