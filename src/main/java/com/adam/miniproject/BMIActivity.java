package com.aditi.miniproject;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class BMIActivity extends AppCompatActivity {
    private EditText etHeight, etWeight;
    private Button btnCalculateBMI;
    private TextView tvResult, tvCategory;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmi_calculator);

        etHeight = findViewById(R.id.etHeight);
        etWeight = findViewById(R.id.etWeight);
        btnCalculateBMI = findViewById(R.id.btnCalculateBMI);
        tvResult = findViewById(R.id.tvResult);
        tvCategory = findViewById(R.id.tvCategory);
        progressBar = findViewById(R.id.progressBar);

        btnCalculateBMI.setOnClickListener(view -> calculateBMI());
    }

    private void calculateBMI() {
        String heightStr = etHeight.getText().toString();
        String weightStr = etWeight.getText().toString();

        if (!heightStr.isEmpty() && !weightStr.isEmpty()) {
            double height = Double.parseDouble(heightStr);
            double weight = Double.parseDouble(weightStr);

            if (height > 0 && weight > 0) {
                // Convert height to meters if input is in centimeters
                if (height > 3) { // Assuming height is in cm if it's greater than 3 meters
                    height = height / 100;
                }

                double bmi = weight / (height * height);
                String category = getBMICategory(bmi);
                tvResult.setText(String.format("Your BMI is: %.2f", bmi));
                tvCategory.setText(category);
                updateProgressBar(bmi);
            } else {
                tvResult.setText("Height and weight must be positive.");
                tvCategory.setText("");
                progressBar.setProgress(0);
            }
        } else {
            tvResult.setText("Please enter valid height and weight.");
            tvCategory.setText("");
            progressBar.setProgress(0);
        }
    }

    private String getBMICategory(double bmi) {
        if (bmi < 18.5) return "Underweight";
        else if (bmi < 24.9) return "Normal weight";
        else if (bmi < 29.9) return "Overweight";
        else return "Obese";
    }

    private void updateProgressBar(double bmi) {
        int progress = (int) Math.round((bmi / 40) * 100); // Assuming 40 as max BMI for scaling
        progressBar.setProgress(progress);

        // Change color based on category
        if (bmi < 18.5) {
            progressBar.setBackgroundColor(Color.BLUE);
        } else if (bmi < 24.9) {
            progressBar.setBackgroundColor(Color.GREEN);
        } else if (bmi < 29.9) {
            progressBar.setBackgroundColor(Color.YELLOW);
        } else {
            progressBar.setBackgroundColor(Color.RED);
        }
    }
}
