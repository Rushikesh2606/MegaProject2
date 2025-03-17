package com.example.codebrains;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Arrays;

public class login extends AppCompatActivity {
    EditText email, password;
    AutoCompleteTextView profession;
    FirebaseAuth firebaseAuth;
    private ArrayList<String> arr_profession = new ArrayList<>(Arrays.asList("Client", "Freelancer"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        // Initialize UI components
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        profession = findViewById(R.id.profession);
        Button loginButton = findViewById(R.id.login_button); // Corrected ID to match XML

        // Configure AutoCompleteTextView for profession
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arr_profession);
        profession.setAdapter(adapter);

        // Login button click listener
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailStr = email.getText().toString().trim();
                String passwordStr = password.getText().toString().trim();
                String role = profession.getText().toString().trim();

                // Validate inputs
                if (TextUtils.isEmpty(emailStr)) {
                    email.setError("Email cannot be empty");
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(emailStr).matches()) {
                    email.setError("Enter a valid email");
                    return;
                }
                if (TextUtils.isEmpty(passwordStr)) {
                    password.setError("Password cannot be empty");
                    return;
                }
                if (passwordStr.length() < 6) {
                    password.setError("Password must be at least 6 characters");
                    return;
                }
                if (TextUtils.isEmpty(role)) {
                    profession.setError("Please select a profession");
                    return;
                }

                // Authenticate with Firebase
                firebaseAuth.signInWithEmailAndPassword(emailStr, passwordStr)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Save profession in SharedPreferences
                                    saveProfession(role);

                                    // Navigate to appropriate activity
                                    if (role.equals("Client")) {
                                        startActivity(new Intent(login.this, Homepage.class));
                                    } else if (role.equals("Freelancer")) {
                                        startActivity(new Intent(login.this, Homepage_developer.class));
                                    } else {
                                        startActivity(new Intent(login.this, Homepage.class));
                                    }
                                    finish(); // Close login activity
                                    Toast.makeText(login.this, "Login successful!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(login.this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });
    }

    private void saveProfession(String role) {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        prefs.edit().putString("profession", role).apply();
    }
}