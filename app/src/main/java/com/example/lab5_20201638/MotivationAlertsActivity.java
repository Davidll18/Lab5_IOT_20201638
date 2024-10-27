package com.example.lab5_20201638;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MotivationAlertsActivity extends AppCompatActivity {
    private EditText editTextInterval;
    private EditText editTextMessage;
    private Switch switchEnableAlerts;
    private Button buttonSaveAlerts;
    private AlarmManager alarmManager;
    private PendingIntent motivationPendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motivation_alerts);

        editTextInterval = findViewById(R.id.editTextInterval);
        editTextMessage = findViewById(R.id.editTextMessage);
        switchEnableAlerts = findViewById(R.id.switchEnableAlerts);
        buttonSaveAlerts = findViewById(R.id.buttonSaveAlerts);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        loadSavedConfiguration();

        buttonSaveAlerts.setOnClickListener(v -> saveConfiguration());

        switchEnableAlerts.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isChecked) {
                cancelMotivationAlerts();
            }
        });
    }

    private void loadSavedConfiguration() {
        SharedPreferences prefs = getSharedPreferences("MotivationPrefs", MODE_PRIVATE);
        editTextInterval.setText(String.valueOf(prefs.getInt("interval", 30)));
        editTextMessage.setText(prefs.getString("message", "¡Sigue adelante!"));
        switchEnableAlerts.setChecked(prefs.getBoolean("enabled", false));
    }

    private void saveConfiguration() {
        String intervalStr = editTextInterval.getText().toString();
        String message = editTextMessage.getText().toString();
        boolean isEnabled = switchEnableAlerts.isChecked();

        if (intervalStr.isEmpty() || message.isEmpty()) {
            Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        int interval = Integer.parseInt(intervalStr);
        if (interval < 1) {
            Toast.makeText(this, "El intervalo debe ser mayor a 0", Toast.LENGTH_SHORT).show();
            return;
        }

        // Guardar configuración en SharedPreferences
        SharedPreferences.Editor editor = getSharedPreferences("MotivationPrefs", MODE_PRIVATE).edit();
        editor.putInt("interval", interval);
        editor.putString("message", message);
        editor.putBoolean("enabled", isEnabled);
        editor.apply();

        if (isEnabled) {
            scheduleMotivationAlerts(interval, message);
        } else {
            cancelMotivationAlerts();
        }

        Toast.makeText(this, "Configuración guardada", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void scheduleMotivationAlerts(int intervalMinutes, String message) {
        // Cancelar alarmas existentes primero
        cancelMotivationAlerts();

        // Crear nuevo intent para la notificación
        Intent intent = new Intent(this, MotivationNotificationReceiver.class);
        intent.putExtra("motivation_message", message);

        motivationPendingIntent = PendingIntent.getBroadcast(
                this,
                300,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        long intervalMillis = intervalMinutes * 60 * 1000; // Convertir minutos a milisegundos
        long firstTrigger = System.currentTimeMillis() + intervalMillis;

        alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                firstTrigger,
                intervalMillis,
                motivationPendingIntent
        );

        Toast.makeText(this,
                "Notificaciones programadas cada " + intervalMinutes + " minutos",
                Toast.LENGTH_SHORT).show();
    }
    private void cancelMotivationAlerts() {
        Intent intent = new Intent(this, MotivationNotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                300,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }
}