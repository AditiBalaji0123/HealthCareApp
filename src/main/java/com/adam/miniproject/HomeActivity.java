package com.adam.miniproject;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeActivity extends AppCompatActivity {
    private TextView txtUserProfile;
    private Button btnProfile, btnAssessment, btnEmergencyCall, buttonStepTracker;
    private FirebaseFirestore firestore;

    private Button btnBack;
    private Button btnNutritionVideos, btnDietaryGuidelines, btnNicotineTracking, btnBMICalculator;

    private boolean isGuest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        FirebaseApp.initializeApp(this);
        firestore = FirebaseFirestore.getInstance();


        btnBack = findViewById(R.id.btnBack);
        txtUserProfile = findViewById(R.id.txtUserProfile);
        btnProfile = findViewById(R.id.btnProfile);
        btnAssessment = findViewById(R.id.btnAssessment);
        btnNutritionVideos = findViewById(R.id.btnNutritionVideos);
        btnDietaryGuidelines = findViewById(R.id.btnDietaryGuidelines);
        btnNicotineTracking = findViewById(R.id.btnNicotineTracking);
        btnBMICalculator = findViewById(R.id.btnBMICalculator);
        buttonStepTracker = findViewById(R.id.buttonStepTracker); // Setup buttonStepTracker
        btnEmergencyCall = findViewById(R.id.btnEmergencyCall);


        isGuest = getIntent().getBooleanExtra("isGuest", false); // Pass this value from the login activity


        setupButtonListeners();


        displayUserProfile();
    }

    private void setupButtonListeners() {

        btnNutritionVideos.setOnClickListener(view -> startActivity(new Intent(HomeActivity.this, NutritionVideosActivity.class)));
        btnDietaryGuidelines.setOnClickListener(view -> startActivity(new Intent(HomeActivity.this, DietaryGuidelinesActivity.class)));
        btnNicotineTracking.setOnClickListener(view -> startActivity(new Intent(HomeActivity.this, NicotineTrackingActivity.class)));
        btnBMICalculator.setOnClickListener(view -> startActivity(new Intent(HomeActivity.this, BMIActivity.class)));

        btnProfile.setOnClickListener(view -> startActivity(new Intent(HomeActivity.this, UserProfileActivity.class)));
        btnAssessment.setOnClickListener(view -> startActivity(new Intent(HomeActivity.this, AssessmentActivity.class)));


        buttonStepTracker.setOnClickListener(view -> startActivity(new Intent(HomeActivity.this, StepTrackerActivity.class)));

        btnEmergencyCall.setOnClickListener(view -> makeEmergencyCall());
        btnBack.setOnClickListener(view -> {

            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void displayUserProfile() {
        if (isGuest) {

            btnProfile.setVisibility(View.GONE);
            btnAssessment.setVisibility(View.GONE);
            txtUserProfile.setText("Signed in as guest.");
        } else {
            String username = getIntent().getStringExtra("username");
            firestore.collection("users").document(username).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String name = documentSnapshot.getString("username");
                            txtUserProfile.setText("Hello, " + name);
                        } else {
                            txtUserProfile.setText("Hello, User");
                        }
                    })
                    .addOnFailureListener(e -> txtUserProfile.setText("Hello, User"));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults); // Call super method

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                makeEmergencyCall();
            } else {

                Toast.makeText(this, "Permission denied to make calls", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void makeEmergencyCall() {

        new AlertDialog.Builder(this)
                .setTitle("Emergency Call")
                .setMessage("This will make an emergency call to 112. Do you want to proceed?")
                .setPositiveButton("Yes", (dialog, which) -> {

                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                            != PackageManager.PERMISSION_GRANTED) {


                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.CALL_PHONE}, 1);
                    } else {

                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:112"));
                        startActivity(callIntent);
                    }
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
