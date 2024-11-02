package com.aditi.miniproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {
    private EditText editTextUsername, editTextPassword;
    private Button buttonLogin, buttonSignUp, buttonGuestAccess;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonSignUp = findViewById(R.id.buttonSignUp);
        buttonGuestAccess = findViewById(R.id.buttonGuestAccess);


        firestore = FirebaseFirestore.getInstance();

        testFirestoreConnection();

        buttonLogin.setOnClickListener(view -> validateUser());


        buttonSignUp.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
        });

        // Handle guest login
        buttonGuestAccess.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            intent.putExtra("username", "Guest"); // Pass a default username for guest
            intent.putExtra("isGuest", true); // Pass guest status as true
            startActivity(intent);
            sendLoginNotification("Logged in as Guest", "You are now logged in as a guest.");
            finish();
        });
    }
    private void testFirestoreConnection() {
        firestore.collection("users").limit(1).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        Log.d("FirestoreConnection", "Connected successfully! Found " + queryDocumentSnapshots.size() + " users.");
                    } else {
                        Log.d("FirestoreConnection", "Connected successfully! No users found.");
                        Toast.makeText(this, "Connected to Firestore, but no users found.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreConnection", "Error connecting to Firestore: " + e.getMessage());
                    Toast.makeText(this, "Error connecting to Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void validateUser() {
        String username = editTextUsername.getText().toString();
        String password = editTextPassword.getText().toString();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter both username and password.", Toast.LENGTH_SHORT).show();
            return;
        }


        firestore.collection("users")
                .document(username)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String storedPassword = documentSnapshot.getString("password");
                        if (storedPassword != null && storedPassword.equals(password)) {
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            intent.putExtra("username", username); // Pass the username
                            intent.putExtra("isGuest", false); // Pass guest status as false
                            startActivity(intent);
                            sendLoginNotification("Welcome back", "Welcome back, " + username);
                            finish();
                            Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error fetching user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    private void sendLoginNotification(String title, String message) {
        Intent intent = new Intent(this, NotificationReceiver.class);
        intent.putExtra("title", title);
        intent.putExtra("message", message);
        sendBroadcast(intent);
    }
}
