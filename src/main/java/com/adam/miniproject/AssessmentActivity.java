package com.aditi.miniproject;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Calendar;

public class AssessmentActivity extends AppCompatActivity {
    private EditText editTextHeight, editTextWeight, editTextNotes, editTextDate;
    private Button buttonSave, buttonShowSubmissions;
    private TextView textViewAssessmentResults;
    private FirebaseFirestore firestore;
    private String username;

    private static final String TAG = "AssessmentActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment);

        initializeUI();
        setupFirestore();
        setupListeners();
        Log.d(TAG, "Activity created, UI initialized.");
    }

    private void initializeUI() {
        editTextHeight = findViewById(R.id.editTextHeight);
        editTextWeight = findViewById(R.id.editTextWeight);
        editTextNotes = findViewById(R.id.editTextNotes);
        editTextDate = findViewById(R.id.editTextDate);
        buttonSave = findViewById(R.id.buttonSave);
        buttonShowSubmissions = findViewById(R.id.buttonShowSubmissions);
        textViewAssessmentResults = findViewById(R.id.textViewAssessmentResults);
        Log.d(TAG, "UI components initialized.");
    }

    private void setupFirestore() {
        firestore = FirebaseFirestore.getInstance();
        username = getSharedPreferences("UserPrefs", MODE_PRIVATE).getString("username", "");
//        Log.d(TAG, "Firestore initialized. Username: " + username);
    }

    private void setupListeners() {
        editTextDate.setOnClickListener(v -> showDatePicker());
        buttonSave.setOnClickListener(view -> saveAssessmentData());
        buttonShowSubmissions.setOnClickListener(view -> showAllSubmissions());
//        Log.d(TAG, "Listeners set up for date, save button, and show submissions button.");
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                    editTextDate.setText(selectedDate);
                    Log.d(TAG, "Date selected: " + selectedDate);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void saveAssessmentData() {
        String height = editTextHeight.getText().toString().trim();
        String weight = editTextWeight.getText().toString().trim();
        String notes = editTextNotes.getText().toString().trim();
        String date = editTextDate.getText().toString().trim();

        Log.d(TAG, "Attempting to save assessment data: " +
                "Height: " + height + ", Weight: " + weight + ", Notes: " + notes + ", Date: " + date);

        if (validateInput(height, weight, notes, date)) {
            saveAssessmentToFirestore(height, weight, notes, date);
        }
    }

    private boolean validateInput(String height, String weight, String notes, String date) {
        if (height.isEmpty() || weight.isEmpty() || notes.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            Log.w(TAG, "Validation failed: One or more fields are empty.");
            return false;
        }
        Log.d(TAG, "Validation successful.");
        return true;
    }

    private void saveAssessmentToFirestore(String height, String weight, String notes, String date) {
        Assessment assessment = new Assessment(height, weight, notes, date);
        String documentId = "assessment_" + System.currentTimeMillis();

        Log.d(TAG, "Saving assessment to Firestore. Document ID: " + documentId);

        firestore.collection("assessments").document(username)
                .collection("userAssessments").document(documentId)
                .set(assessment)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Assessment saved successfully.");
                    Toast.makeText(this, "Assessment saved.", Toast.LENGTH_SHORT).show();
                    displayAssessmentResult(height, weight, notes, date);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error saving assessment: ", e);
                    Toast.makeText(this, "Error saving assessment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void displayAssessmentResult(String height, String weight, String notes, String date) {
        textViewAssessmentResults.setText(String.format("Assessment Saved:\nHeight: %s cm\nWeight: %s kg\nNotes: %s\nDate: %s",
                height, weight, notes, date));
        Log.d(TAG, "Displaying saved assessment result.");
    }

    private void showAllSubmissions() {
        // Code to retrieve and display all submissions from Firestore
        firestore.collection("assessments").document(username)
                .collection("userAssessments").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        StringBuilder results = new StringBuilder();
                        for (DocumentSnapshot document : task.getResult()) {
                            Assessment assessment = document.toObject(Assessment.class);
                            results.append(String.format("Height: %s cm, Weight: %s kg, Notes: %s, Date: %s\n",
                                    assessment.getHeight(), assessment.getWeight(), assessment.getNotes(), assessment.getDate()));
                        }
                        textViewAssessmentResults.setText(results.toString());
                    } else {
                        Log.e(TAG, "Error getting submissions: ", task.getException());
                        Toast.makeText(this, "Error fetching submissions: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
