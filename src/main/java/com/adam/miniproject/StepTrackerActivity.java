package com.adam.miniproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class StepTrackerActivity extends AppCompatActivity implements SensorEventListener {

    private static final String TAG = "StepTrackerActivity";
    private TextView stepCountTextView, motivationTextView;
    private EditText goalInputEditText;
    private SensorManager sensorManager;
    private Sensor stepCounterSensor;
    private boolean isSensorPresent;
    private int stepCount = 0;
    private int dailyStepGoal = 10000; // Default goal
    private int baselineStepCount = 0;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_tracker);

        // Initialize UI elements
        stepCountTextView = findViewById(R.id.stepCountTextView);
        motivationTextView = findViewById(R.id.motivationTextView);
        goalInputEditText = findViewById(R.id.goalInputEditText);
        Button setGoalButton = findViewById(R.id.setGoalButton);

        // SharedPreferences for saving data
        sharedPreferences = getSharedPreferences("StepTrackerPrefs", Context.MODE_PRIVATE);
        baselineStepCount = sharedPreferences.getInt("baselineStepCount", 0);
        dailyStepGoal = sharedPreferences.getInt("dailyStepGoal", 10000);

        // Set goal button listener
        setGoalButton.setOnClickListener(v -> {
            String goalStr = goalInputEditText.getText().toString();
            if (!goalStr.isEmpty()) {
                dailyStepGoal = Integer.parseInt(goalStr);
                sharedPreferences.edit().putInt("dailyStepGoal", dailyStepGoal).apply();
                Toast.makeText(this, "Daily step goal set to " + dailyStepGoal, Toast.LENGTH_SHORT).show();
                updateMotivationalMessage();
            } else {
                Toast.makeText(this, "Please enter a valid step goal.", Toast.LENGTH_SHORT).show();
            }
        });

        // Sensor initialization
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            isSensorPresent = stepCounterSensor != null;
        }

        if (!isSensorPresent) {
            stepCountTextView.setText("Step Counter Sensor not available!");
            Log.e(TAG, "Step Counter Sensor not available!");
        } else {
            Log.d(TAG, "Step Counter Sensor is available.");
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            // Get the total steps recorded by the sensor
            int totalSteps = (int) event.values[0];
            if (baselineStepCount == 0) {
                // Set the baseline step count on the first reading
                baselineStepCount = totalSteps;
                sharedPreferences.edit().putInt("baselineStepCount", baselineStepCount).apply();
            }
            // Calculate the current step count
            stepCount = totalSteps - baselineStepCount;
            stepCountTextView.setText("Steps: " + stepCount);
            updateMotivationalMessage();
        }
    }

    private void updateMotivationalMessage() {
        if (stepCount >= dailyStepGoal) {
            motivationTextView.setText("Amazing! You reached your goal!");
        } else if (stepCount >= dailyStepGoal * 0.75) {
            motivationTextView.setText("Almost there! Keep going!");
        } else if (stepCount >= dailyStepGoal * 0.5) {
            motivationTextView.setText("Halfway there! You can do it!");
        } else {
            motivationTextView.setText("Keep moving! You're doing great!");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not used, but required by the interface
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isSensorPresent) {
            // Register the sensor listener to start receiving updates
            sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isSensorPresent) {
            // Unregister the sensor listener to stop receiving updates
            sensorManager.unregisterListener(this);
        }
    }
}
