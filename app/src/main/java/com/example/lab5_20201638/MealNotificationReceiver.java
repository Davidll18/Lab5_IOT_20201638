package com.example.lab5_20201638;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.app.NotificationManager;
import androidx.core.app.NotificationCompat;

public class MealNotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String mealType = intent.getStringExtra("meal_type");
        String title = "Recordatorio de " + mealType;
        String message = "Es hora de registrar tu " + mealType + " en la aplicación";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "calorie_alert_channel")
                .setSmallIcon(R.drawable.ic_meal)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            // Usar un ID diferente para cada tipo de comida
            int notificationId = getMealNotificationId(mealType);
            manager.notify(notificationId, builder.build());
        }
    }

    private int getMealNotificationId(String mealType) {
        switch (mealType.toLowerCase()) {
            case "desayuno":
                return 100;
            case "almuerzo":
                return 101;
            case "cena":
                return 102;
            default:
                return 103;
        }
    }
}
