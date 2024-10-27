package com.example.lab5_20201638;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.app.NotificationManager;
import androidx.core.app.NotificationCompat;

public class MotivationNotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String message = intent.getStringExtra("motivation_message");
        if (message == null) {
            message = "¡Sigue adelante con tus objetivos!"; // Mensaje por defecto
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "calorie_alert_channel")
                .setSmallIcon(R.drawable.ic_warning)
                .setContentTitle("¡Mensaje de Motivación!")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            notificationManager.notify(300, builder.build());
        }
    }
}