package com.aditi.miniproject;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class NicotineTrackingActivity extends AppCompatActivity {
    private EditText editTextNicotineIntake;
    private TextView nicotineLog, summaryTextView;
    private ArrayList<String> nicotineIntakeLog;
    private FirebaseFirestore firestore;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nicotine_tracking);

        editTextNicotineIntake = findViewById(R.id.editTextNicotineIntake);
        Button btnSaveNicotineIntake = findViewById(R.id.btnSaveNicotineIntake);
        nicotineLog = findViewById(R.id.nicotineLog);
        summaryTextView = findViewById(R.id.summaryTextView);
        nicotineIntakeLog = new ArrayList<>();
        firestore = FirebaseFirestore.getInstance();
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        // Load existing intake log if available
        loadNicotineLog();

        btnSaveNicotineIntake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String intake = editTextNicotineIntake.getText().toString();
                if (!intake.isEmpty()) {
                    String timestamp = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());
                    String logEntry = timestamp + ": " + intake + " mg";
                    nicotineIntakeLog.add(logEntry);
                    updateLog();
                    saveIntake(logEntry); // Save intake to either local or Firestore
                    editTextNicotineIntake.setText(""); // Clear input field
                } else {
                    Toast.makeText(NicotineTrackingActivity.this, "Please enter your nicotine intake", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateLog() {
        StringBuilder log = new StringBuilder();
        for (String intake : nicotineIntakeLog) {
            log.append(intake).append("\n");
        }
        nicotineLog.setText(log.toString());
        updateSummary();
    }

    private void updateSummary() {
        int totalIntake = 0;
        for (String intake : nicotineIntakeLog) {
            String intakeValue = intake.split(": ")[1].replace(" mg", "");
            totalIntake += Integer.parseInt(intakeValue);
        }
        summaryTextView.setText(String.format("Total Nicotine Intake Today: %d mg", totalIntake));
    }

    private void saveIntake(String logEntry) {
        String username = sharedPreferences.getString("username", "");

        if (username.isEmpty()) {
            // If guest, save locally
            saveIntakeLocally(logEntry);
        } else {
            // If registered user, save to Firestore
            saveIntakeToFirestore(logEntry, username);
        }
    }

    private void saveIntakeLocally(String logEntry) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("lastNicotineIntake", logEntry);
        editor.apply();
    }

    private void loadNicotineLog() {
        String lastIntake = sharedPreferences.getString("lastNicotineIntake", "");
        if (!lastIntake.isEmpty()) {
            nicotineIntakeLog.add(lastIntake);
            updateLog();
        }
    }

    private void saveIntakeToFirestore(String logEntry, String username) {
        firestore.collection("nicotineTracking").document(username)
                .collection("dailyIntake").add(new NicotineRecord(logEntry))
                .addOnSuccessListener(documentReference -> {
                    // Successfully saved to Firestore
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(this, "Error saving to Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
