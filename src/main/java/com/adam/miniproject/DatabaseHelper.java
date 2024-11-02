package com.adam.miniproject;

import android.content.Context;
import android.widget.Toast;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class DatabaseHelper {
    private FirebaseFirestore db;
    private Context context;

    public DatabaseHelper(Context context) {
        this.context = context;
        db = FirebaseFirestore.getInstance();
    }

    // Method to add new user to the Firestore database
    public void insertUser(String username, String password) {
        User user = new User(username, password);
        db.collection("users").document(username).set(user)
                .addOnSuccessListener(aVoid -> Toast.makeText(context, "User added successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(context, "Error adding user: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }


    public void checkUser(String username, String password, OnUserCheckListener listener) {
        db.collection("users")
                .whereEqualTo("username", username)
                .whereEqualTo("password", password)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            listener.onUserCheck(true);
                        } else {
                            listener.onUserCheck(false);
                        }
                    } else {
                        Toast.makeText(context, "Error checking user: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        listener.onUserCheck(false);
                    }
                });
    }

    // Listener interface for user check callback
    public interface OnUserCheckListener {
        void onUserCheck(boolean exists);
    }


    public static class User {
        private String username;
        private String password;

        public User() {} // Empty constructor needed for Firestore

        public User(String username, String password) {
            this.username = username;
            this.password = password;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }
    }
}
