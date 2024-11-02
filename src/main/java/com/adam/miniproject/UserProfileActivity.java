package com.adam.miniproject;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserProfileActivity extends AppCompatActivity {
    private TextView textViewGreeting, textViewName, textViewAge, textViewHeight, textViewWeight, textViewBMI, textViewHealthStatus;
    private EditText editTextName, editTextAge, editTextHeight, editTextWeight;
    private Button buttonSave, buttonEdit;
    private SharedPreferences sharedPreferences;
    private FirebaseFirestore firestore;
    private String username;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);


        textViewGreeting = findViewById(R.id.textViewGreeting);
        textViewName = findViewById(R.id.textViewName);
        textViewAge = findViewById(R.id.textViewAge);
        textViewHeight = findViewById(R.id.textViewHeight);
        textViewWeight = findViewById(R.id.textViewWeight);
        textViewBMI = findViewById(R.id.textViewBMI);
        textViewHealthStatus = findViewById(R.id.textViewHealthStatus);


        editTextName = findViewById(R.id.editTextName);
        editTextAge = findViewById(R.id.editTextAge);
        editTextHeight = findViewById(R.id.editTextHeight);
        editTextWeight = findViewById(R.id.editTextWeight);


        buttonSave = findViewById(R.id.buttonSave);
        buttonEdit = findViewById(R.id.buttonEdit);


        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        firestore = FirebaseFirestore.getInstance();
        username = sharedPreferences.getString("username", "");


        textViewGreeting.setText("Hello, " + username);


        loadUserProfile();


        buttonEdit.setOnClickListener(view -> enableEditMode());
        buttonSave.setOnClickListener(view -> saveUserProfile());
    }

    private void loadUserProfile() {
        firestore.collection("users").document(username)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("name");
                        String age = documentSnapshot.getString("age");
                        String height = documentSnapshot.getString("height");
                        String weight = documentSnapshot.getString("weight");


                        textViewName.setText(name);
                        textViewAge.setText(age);
                        textViewHeight.setText(height);
                        textViewWeight.setText(weight);

                        editTextName.setText(name);
                        editTextAge.setText(age);
                        editTextHeight.setText(height);
                        editTextWeight.setText(weight);


                        float bmi = calculateBMI(height, weight);
                        textViewBMI.setText(String.format("%.2f", bmi));


                        String healthStatus = getHealthStatus(bmi);
                        textViewHealthStatus.setText(healthStatus);

                        disableEditMode();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void saveUserProfile() {
        String name = editTextName.getText().toString();
        String age = editTextAge.getText().toString();
        String height = editTextHeight.getText().toString();
        String weight = editTextWeight.getText().toString();


        if (name.isEmpty() || age.isEmpty() || height.isEmpty() || weight.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }


        firestore.collection("users").document(username)
                .update("name", name, "age", age, "height", height, "weight", weight)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Profile saved successfully!", Toast.LENGTH_SHORT).show();
                    loadUserProfile(); // Reload after saving
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error saving profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void enableEditMode() {
        isEditMode = true;
        editTextName.setVisibility(View.VISIBLE);
        editTextAge.setVisibility(View.VISIBLE);
        editTextHeight.setVisibility(View.VISIBLE);
        editTextWeight.setVisibility(View.VISIBLE);

        textViewName.setVisibility(View.GONE);
        textViewAge.setVisibility(View.GONE);
        textViewHeight.setVisibility(View.GONE);
        textViewWeight.setVisibility(View.GONE);

        buttonSave.setVisibility(View.VISIBLE);
        buttonEdit.setVisibility(View.GONE);
    }

    private void disableEditMode() {
        isEditMode = false;
        editTextName.setVisibility(View.GONE);
        editTextAge.setVisibility(View.GONE);
        editTextHeight.setVisibility(View.GONE);
        editTextWeight.setVisibility(View.GONE);

        textViewName.setVisibility(View.VISIBLE);
        textViewAge.setVisibility(View.VISIBLE);
        textViewHeight.setVisibility(View.VISIBLE);
        textViewWeight.setVisibility(View.VISIBLE);

        buttonSave.setVisibility(View.GONE);
        buttonEdit.setVisibility(View.VISIBLE);
    }

    private float calculateBMI(String heightStr, String weightStr) {

        float height = (heightStr == null || heightStr.trim().isEmpty()) ? 0 : Float.parseFloat(heightStr.trim()) / 100; // Convert to meters
        float weight = (weightStr == null || weightStr.trim().isEmpty()) ? 0 : Float.parseFloat(weightStr.trim());

        if (height == 0 || weight == 0) {
            return 0;
        }

        return weight / (height * height);
    }

    private String getHealthStatus(float bmi) {
        if (bmi < 18.5) {
            return "Underweight";
        } else if (bmi >= 18.5 && bmi < 24.9) {
            return "Healthy Weight";
        } else if (bmi >= 25 && bmi < 29.9) {
            return "Overweight";
        } else {
            return "Obese";
        }
    }
}
