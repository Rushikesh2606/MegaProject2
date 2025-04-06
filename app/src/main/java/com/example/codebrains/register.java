package com.example.codebrains;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.codebrains.model.Freelancer;
import com.example.codebrains.model.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;

public class register extends AppCompatActivity {

    private TextInputEditText edtUsername, edtPassword, edtRePassword, edtContact, edtDob;
    private AutoCompleteTextView genderSpinner;
    private RadioGroup roleRadioGroup;
    private MaterialButton btnProfile, btnRegister;
    RadioGroup rdb;
    private Bitmap profileImageBitmap;

    private static final int CAMERA_REQUEST = 100;
    private static final int GALLERY_REQUEST = 200;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Get data from previous activity
        Intent intent = getIntent();
        String firstName = intent.getStringExtra("first_name");
        String lastName = intent.getStringExtra("last_name");
        String email = intent.getStringExtra("email");
        String country = intent.getStringExtra("country");

        initializeViews();
        setupGenderSpinner();
        setupDatePicker();
        setupButtons(firstName, lastName, email, country);
    }

    private void initializeViews() {
        edtUsername = findViewById(R.id.edt_username);
        edtPassword = findViewById(R.id.edt_password);
        edtRePassword = findViewById(R.id.edt_reenter_password);
        edtContact = findViewById(R.id.edt_contact_no);
        edtDob = findViewById(R.id.edt_dob);
        genderSpinner = findViewById(R.id.spinner);
        roleRadioGroup = findViewById(R.id.radiogrp);
        btnProfile = findViewById(R.id.btn_profile);
        btnRegister = findViewById(R.id.btn_register);
    }

    private void setupGenderSpinner() {
        ArrayList<String> genders = new ArrayList<>();
        genders.add("Male");
        genders.add("Female");
        genders.add("Other");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1
        );
        genderSpinner.setAdapter(adapter);
    }

    private void setupDatePicker() {
        edtDob.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePicker = new DatePickerDialog(
                    register.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String date = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                        edtDob.setText(date);
                    },
                    year, month, day
            );
            datePicker.show();
        });
    }

    private void setupButtons(String firstName, String lastName, String email, String country) {
        btnProfile.setOnClickListener(v -> showImagePickerDialog());

        roleRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // Find the RadioButton that was clicked
                RadioButton rdb = findViewById(checkedId);

                if (rdb != null) {
                    // Handle the selected radio button
                    if (checkedId == R.id.freelancer) {
                           registerFreelancer(firstName,lastName,email,country);
                    } else if (checkedId == R.id.employer) {
                        registerUser(firstName,lastName,email,country);
                    }
                }
            }
        });
        btnRegister.setOnClickListener(v -> {
            if (validateForm()) {
                registerUser(firstName, lastName, email, country);
            }
        });
    }
    private void registerFreelancer(String firstName, String lastName, String email, String country) {
        String password = edtPassword.getText().toString().trim();
        String username = edtUsername.getText().toString().trim();
        String contact = edtContact.getText().toString().trim();
        String dob = edtDob.getText().toString().trim();
        String gender = genderSpinner.getText().toString();
        String profileImage = "";

        if (profileImageBitmap != null) {
            profileImage = bitmapToBase64(profileImageBitmap);
        }

        String finalProfileImage = profileImage;
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            // Check which radio button is selected
                            int selectedId = roleRadioGroup.getCheckedRadioButtonId();
                            if (selectedId == R.id.freelancer) {
                                // Create Freelancer object
                                Freelancer freelancer = new Freelancer(
                                        firebaseUser.getUid(),
                                        firstName,
                                        lastName,
                                        email,
                                        password,
                                        country,
                                        username,
                                        dob,
                                        gender,
                                        contact,
                                        finalProfileImage
                                );

                                // Save Freelancer object to Firebase
                                mDatabase.child("freelancer").child(firebaseUser.getUid()).setValue(freelancer)
                                        .addOnCompleteListener(dbTask -> {
                                            if (dbTask.isSuccessful()) {
                                                startActivity(new Intent(register.this, MainActivity.class));
                                                finish();
                                            } else {
                                                Toast.makeText(register.this, "Registration failed: " + dbTask.getException(),
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            } else {
                                // Handle other roles (e.g., Employer) if needed
                                Toast.makeText(register.this, "Only Freelancer registration is supported currently.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Toast.makeText(register.this, "Authentication failed: " + task.getException(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showImagePickerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Profile Picture");
        builder.setItems(new String[]{"Camera", "Gallery"}, (dialog, which) -> {
            if (which == 0) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_REQUEST) {
                profileImageBitmap = (Bitmap) data.getExtras().get("data");
            } else if (requestCode == GALLERY_REQUEST) {
                try {
                    Uri imageUri = data.getData();
                    profileImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                } catch (Exception e) {
                    Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private boolean validateForm() {
        boolean isValid = true;

        if (TextUtils.isEmpty(edtUsername.getText())) {
            edtUsername.setError("Username required");
            isValid = false;
        }

        if (TextUtils.isEmpty(edtPassword.getText())) {
            edtPassword.setError("Password required");
            isValid = false;
        } else if (edtPassword.getText().length() < 6) {
            edtPassword.setError("Password must be at least 6 characters");
            isValid = false;
        }

        if (!TextUtils.equals(edtPassword.getText(), edtRePassword.getText())) {
            edtRePassword.setError("Passwords don't match");
            isValid = false;
        }

        if (TextUtils.isEmpty(edtContact.getText())) {
            edtContact.setError("Contact required");
            isValid = false;
        } else if (edtContact.getText().length() != 10) {
            edtContact.setError("Invalid contact number");
            isValid = false;
        }

        if (roleRadioGroup.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Please select a role", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        return isValid;
    }

    private void registerUser(String firstName, String lastName, String email, String country) {
        String password = edtPassword.getText().toString().trim();
        String username = edtUsername.getText().toString().trim();
        String contact = edtContact.getText().toString().trim();
        String dob = edtDob.getText().toString().trim();
        String gender = genderSpinner.getText().toString();
        String profileImage = "";
        Bundle bundle=getIntent().getExtras();

        if (profileImageBitmap != null) {
            profileImage = bitmapToBase64(profileImageBitmap);
        }
// public User(String id, String firstName, String lastName, String username,
//        long timestamp,String country,String contactNo,String profileImage,String password,String email,String gender,String dob,int total_jobs,int completed,String profession,int pending,int in_progress) {

        String finalProfileImage = profileImage;
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            User user = new User(
                                    firebaseUser.getUid(),
                                    firstName,
                                    lastName,
                                    username,
                                    System.currentTimeMillis(),
                                     country,contact, finalProfileImage,password,email,"male",dob,0,0,"Client",0,0
                            );

                            mDatabase.child("user").child(firebaseUser.getUid()).setValue(user)
                                    .addOnCompleteListener(dbTask -> {
                                        if (dbTask.isSuccessful()) {
                                            startActivity(new Intent(register.this, MainActivity.class));
                                            finish();
                                        } else {
                                            Toast.makeText(register.this, "Registration failed: " + dbTask.getException(),
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        Toast.makeText(register.this, "Authentication failed: " + task.getException(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, signup.class));
        finish();
    }
}