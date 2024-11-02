// NotificationReceiver.java
package com.adam.miniproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationHelper notificationHelper = new NotificationHelper(context);
        String title = intent.getStringExtra("title");
        String message = intent.getStringExtra("message");
        notificationHelper.sendNotification(title, message);
    }
}
