package com.example.lab5_20201638;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private EditText inputWeight, inputHeight, inputAge, editTextFoodName, editTextCalories;
    private Spinner spinnerGender, spinnerActivityLevel;
    private RadioGroup radioGroupGoal;
    private TextView textResult, textViewCaloriesSummary;
    private List<Integer> mealCaloriesList = new ArrayList<>();
    private double recommendedCalories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar variables
        inputWeight = findViewById(R.id.input_weight);
        inputHeight = findViewById(R.id.input_height);
        inputAge = findViewById(R.id.input_age);
        spinnerGender = findViewById(R.id.spinner_gender);
        spinnerActivityLevel = findViewById(R.id.spinner_activity_level);
        radioGroupGoal = findViewById(R.id.radioGroup_goal);
        textResult = findViewById(R.id.text_result);

        editTextFoodName = findViewById(R.id.editTextFoodName);
        editTextCalories = findViewById(R.id.editTextCalories);
        textViewCaloriesSummary = findViewById(R.id.textViewCaloriesSummary);



        Button btnCalculate = findViewById(R.id.btn_calculate);
        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateCalories();
            }
        });

        Button buttonAddFood = findViewById(R.id.buttonAddFood);
        buttonAddFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFood();
            }
        });


        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "calorie_alert_channel",
                    "Alertas de Calorías",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Canal para alertas y recordatorios de calorías");
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }


    private void calculateCalories() {
        double weight = Double.parseDouble(inputWeight.getText().toString());
        double height = Double.parseDouble(inputHeight.getText().toString());
        int age = Integer.parseInt(inputAge.getText().toString());
        String gender = spinnerGender.getSelectedItem().toString();
        String activityLevel = spinnerActivityLevel.getSelectedItem().toString();
        int goalId = radioGroupGoal.getCheckedRadioButtonId();

        // Calcular las calorías diarias
        recommendedCalories = getDailyCalories(weight, height, age, gender, activityLevel, goalId);
        textResult.setText("Calorías diarias recomendadas: " + recommendedCalories + " kcal");
    }

    private void addFood() {
        String caloriesStr = editTextCalories.getText().toString();

        if (!caloriesStr.isEmpty()) {
            int calories = Integer.parseInt(caloriesStr);
            mealCaloriesList.add(calories);

            // Actualizar el resumen de calorías consumidas
            updateCaloriesSummary();

            // Limpiar campos de texto
            editTextFoodName.setText("");
            editTextCalories.setText("");
        }
    }

    private void updateCaloriesSummary() {
        int totalConsumedCalories = 0;
        for (int calories : mealCaloriesList) {
            totalConsumedCalories += calories;
        }

        String summaryText = "Calorías consumidas: " + totalConsumedCalories + " / Recomendadas: " + recommendedCalories;
        textViewCaloriesSummary.setText(summaryText);

        if (totalConsumedCalories > recommendedCalories) {
            sendCalorieExcessNotification();
        }
    }

    private void sendCalorieExcessNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "calorie_alert_channel")
                .setSmallIcon(R.drawable.ic_warning) // Cambia este icono según tu proyecto
                .setContentTitle("¡Exceso de Calorías!")
                .setContentText("Has consumido más calorías de las recomendadas. Considera hacer ejercicio o reducir las calorías en la próxima comida.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(1, builder.build());
        }
    }



    // Método principal para calcular las calorías diarias
    private double getDailyCalories(double weight, double height, int age, String gender, String activityLevel, int goalId) {
        double tmb = calculateTMB(weight, height, age, gender);
        double activityMultiplier = getActivityMultiplier(activityLevel);
        double dailyCalories = tmb * activityMultiplier;

        if (goalId == R.id.radio_gain_weight) {
            dailyCalories += 500;
        } else if (goalId == R.id.radio_lose_weight) {
            dailyCalories -= 300;
        }

        return dailyCalories;
    }

    // Método para calcular la Tasa Metabólica Basal (TMB)
    private double calculateTMB(double weight, double height, int age, String gender) {
        if (gender.equals("Masculino")) {
            return (10 * weight) + (6.25 * height) - (5 * age) + 5;
        } else {
            return (10 * weight) + (6.25 * height) - (5 * age) - 161;
        }
    }

    // Método para obtener el multiplicador de actividad física
    private double getActivityMultiplier(String activityLevel) {
        switch (activityLevel) {
            case "Poco o ningún ejercicio":
                return 1.2;
            case "Ejercicio ligero":
                return 1.375;
            case "Ejercicio moderado":
                return 1.55;
            case "Ejercicio fuerte":
                return 1.725;
            case "Ejercicio muy fuerte":
                return 1.9;
            default:
                return 1.0;
        }
    }
}