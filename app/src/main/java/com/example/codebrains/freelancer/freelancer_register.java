package com.example.codebrains.freelancer;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.example.codebrains.MainActivity2;
import com.example.codebrains.R;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import com.example.codebrains.model.Freelancer;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.codebrains.register;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class freelancer_register extends AppCompatActivity {
    private static final int PICK_FILE_REQUEST = 100;

    TextView file_name, uploaded;
    Button resume, submit_btn;
    Spinner availability_spinner;
    EditText passout, degree, language, institute;
    ArrayList<String> available = new ArrayList<>();

    FirebaseAuth auth;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_freelancer_register);

        // Firebase Initialization
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        // UI Element Initialization
        submit_btn = findViewById(R.id.submit_btn);
        institute = findViewById(R.id.institute);
        language = findViewById(R.id.languages);
        degree = findViewById(R.id.degree);
        passout = findViewById(R.id.passout);

        availability_spinner = findViewById(R.id.availability_spinner);
        available.add("Full-Time");
        available.add("Part-Time");

        ArrayAdapter<String> aa = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, available);
        availability_spinner.setAdapter(aa);

        submit_btn.setOnClickListener(v -> {
            if (validated()) {
                Bundle bundle = getIntent().getExtras();

                if (bundle != null) {
                    String Passout = passout.getText().toString().trim();
                    String Degree = degree.getText().toString().trim();
                    String Institute = institute.getText().toString().trim();
                    String Language = language.getText().toString().trim();
                    String Availability = availability_spinner.getSelectedItem().toString();

                    // Retrieving Data from Bundle
                    String desc = bundle.getString("Desc");
                    Toast.makeText(freelancer_register.this, desc, Toast.LENGTH_SHORT).show();
                    String skills = bundle.getString("skills");
                    String tagLine = bundle.getString("tagLine");
                    String tools = bundle.getString("Tools");
                    String yearsOfExperience = bundle.getString("Years_of_experience");
                    String firstName = bundle.getString("first_name");
                    String lastName = bundle.getString("last_name");
                    String email = bundle.getString("email");
                    String password = bundle.getString("password");
                    String country = bundle.getString("country");
                    String username = bundle.getString("username");
                    String dob = bundle.getString("dob");
                    String contactNo = bundle.getString("contact_no");
                    String gender = bundle.getString("gender");
                    String profileImageBase64 = bundle.getString("profile_image_base64");

                    Log.d("FREELANCER_REGISTER", "Starting Firebase User Registration...");

                    // Firebase Authentication for New User
                    auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    String id = auth.getCurrentUser().getUid();
                                    DatabaseReference reference = database.getReference().child("freelancer").child(id);

                                    Freelancer freelancer = new Freelancer(
                                            "Freelancer", desc, Passout, Degree, Language, Institute, Availability,
                                            skills, tagLine, tools, yearsOfExperience, firstName, lastName, email,
                                            password, country, username, dob, gender, contactNo, 0, 0, 0, 0,profileImageBase64
                                    );

                                    Log.d("FREELANCER_REGISTER", "Saving user data to Firebase...");

                                    reference.setValue(freelancer)
                                            .addOnCompleteListener(saveTask -> {
                                                if (saveTask.isSuccessful()) {
                                                    Log.d("FREELANCER_REGISTER", "User Data Saved Successfully!");
                                                    auth.signOut();
                                                    Intent i = new Intent(freelancer_register.this, MainActivity2.class);
                                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(i);
                                                    finish();
                                                } else {
                                                    Toast.makeText(freelancer_register.this, "Error in saving user data", Toast.LENGTH_SHORT).show();
                                                    Log.e("FREELANCER_REGISTER", "Error: " + saveTask.getException().getMessage());
                                                }
                                            });
                                } else {
                                    Toast.makeText(freelancer_register.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    Log.e("FREELANCER_REGISTER", "Authentication Failed: " + task.getException().getMessage());
                                }
                            });
                } else {
                    Log.e("FREELANCER_REGISTER", "Error: Bundle Data is Null");
                }
            }
        });

        // Resume Upload Feature
        resume = findViewById(R.id.resume);
        file_name = findViewById(R.id.file_name);
        uploaded = findViewById(R.id.resume_uploaded);

        resume.setOnClickListener(v -> {
            openFileChooser();
            resume.setVisibility(View.GONE);
            uploaded.setText("Resume Uploaded");
            file_name.setVisibility(View.VISIBLE);
        });
    }

    // Method to Open File Picker
    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Select Resume"), PICK_FILE_REQUEST);
    }

    // Handling File Selection Result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri fileUri = data.getData();

            if (fileUri != null) {
                String fileName = getFileName(fileUri);
                Toast.makeText(this, "Selected File: " + fileName, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error: File selection failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Extract File Name from URI
    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        file_name.setText(result);
        return result;
    }

    // Input Validation
    public boolean validated() {
        if (TextUtils.isEmpty(passout.getText())) {
            passout.setError("Passout Year is required!");
            return false;
        }

        if (TextUtils.isEmpty(degree.getText())) {
            degree.setError("Degree field is required!");
            return false;
        }

        if (TextUtils.isEmpty(language.getText())) {
            language.setError("Programming Language is required!");
            return false;
        }

        if (TextUtils.isEmpty(institute.getText())) {
            institute.setError("Institute Name is required!");
            return false;
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(freelancer_register.this, register.class);
        startActivity(i);
        super.onBackPressed();
    }
}