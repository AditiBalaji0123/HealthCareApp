// NotificationHelper.java
package com.adam.miniproject;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class NotificationHelper {
    private static final String CHANNEL_ID = "HealthWellnessChannel";
    private static final String CHANNEL_NAME = "Health and Wellness Notifications";

    private NotificationManager notificationManager;
    private Context context; // Store the context

    public NotificationHelper(Context context) {
        this.context = context; // Initialize the context
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
            Log.d("NotificationHelper", "Notification channel created");
        } else {
            Log.d("NotificationHelper", "Notification channel not created - SDK version < Oreo");
        }
    }

    public void sendNotification(String title, String message) {
        Intent intent = new Intent(context, HomeActivity.class); // Use the stored context
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID) // Use the stored context
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        notificationManager.notify(1, builder.build());
    }
}
