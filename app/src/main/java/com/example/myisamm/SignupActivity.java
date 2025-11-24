package com.example.myisamm;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.EditText;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignupActivity extends AppCompatActivity {

    private EditText usernameInput, emailInput;
    private TextInputEditText passwordInput, confirmPasswordInput;
    private Button signupButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextInputLayout passwordLayout, confirmPasswordLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance(); // Initialize Firestore


        usernameInput = findViewById(R.id.home_username_text);
        emailInput = findViewById(R.id.signup_email_input);
        passwordInput = findViewById(R.id.signup_password_input);
        confirmPasswordInput = findViewById(R.id.signup_confirm_password_input);
        signupButton = findViewById(R.id.signup_button);


        passwordLayout = findViewById(R.id.signup_password_layout);
        confirmPasswordLayout = findViewById(R.id.signup_confirm_password_layout);


        passwordLayout.setPasswordVisibilityToggleEnabled(true);
        confirmPasswordLayout.setPasswordVisibilityToggleEnabled(true);

        signupButton.setOnClickListener(view -> {
            String username = usernameInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            String confirmPassword = confirmPasswordInput.getText().toString().trim();

            // Validate input
            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(SignupActivity.this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(SignupActivity.this, "Adresse e-mail invalide", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(SignupActivity.this, "Les mots de passe ne correspondent pas", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 6) {
                Toast.makeText(SignupActivity.this, "Le mot de passe doit contenir au moins 6 caractères", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create user with Firebase Auth
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Get the user ID (UID) from FirebaseAuth
                            String userId = mAuth.getCurrentUser().getUid();

                            // Create a user object to save in Firestore
                            User user = new User(username, email);

                            // Save user to Firestore
                            db.collection("users")
                                    .document(userId)
                                    .set(user)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(SignupActivity.this, "Inscription réussie", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(SignupActivity.this, "Échec de l'inscription dans Firestore: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                    });
                        } else {
                            Toast.makeText(SignupActivity.this, "Échec de l'inscription: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        });

        TextView loginLink = findViewById(R.id.signup_login_link);
        loginLink.setOnClickListener(v -> {
            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            finish();
        });
    }

    // User class to store user details in Firestore
    public static class User {
        private String username;
        private String email;

        public User(String username, String email) {
            this.username = username;
            this.email = email;
        }

        public String getUsername() {
            return username;
        }

        public String getEmail() {
            return email;
        }
    }
}
