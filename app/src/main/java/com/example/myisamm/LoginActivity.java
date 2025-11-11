package com.example.myisamm;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText emailInput, passwordInput;
    private Button loginButton;
    private TextView signUpLink;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        mAuth = FirebaseAuth.getInstance();


        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }


        emailInput = findViewById(R.id.login_email_input);
        passwordInput = findViewById(R.id.login_password_input);
        loginButton = findViewById(R.id.login_button);
        signUpLink = findViewById(R.id.signup_link_text);
        progressBar = findViewById(R.id.login_progress_bar);

        loginButton.setOnClickListener(view -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                return;
            }

            // Show progress bar
            progressBar.setVisibility(View.VISIBLE);
            loginButton.setEnabled(false);

            // Firebase login
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        progressBar.setVisibility(View.GONE);
                        loginButton.setEnabled(true);

                        if (task.isSuccessful()) {
                            // Login success
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        } else {
                            // Login failed
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthInvalidUserException e) {
                                Toast.makeText(LoginActivity.this, "Aucun compte trouvé avec cet e-mail.", Toast.LENGTH_SHORT).show();
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                Toast.makeText(LoginActivity.this, "Mot de passe incorrect.", Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Toast.makeText(LoginActivity.this, "Échec d'authentification.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        });

        // Handle Sign Up link click
        signUpLink.setOnClickListener(view -> {
            // Navigate to SignUpActivity
            startActivity(new Intent(LoginActivity.this, SignupActivity.class));
        });



    }
}
