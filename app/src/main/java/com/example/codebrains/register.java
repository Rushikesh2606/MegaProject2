package com.example.codebrains;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;

public class register extends AppCompatActivity {
    Button btn_profile, btn_register;
    ArrayList<String> gender = new ArrayList<>();
    AutoCompleteTextView spinner;
    RadioGroup radiogrp;
    EditText dob, username, password, rePassword, contactNo;
    private static final int CAMERA_REQ = 100, GALLERY_REQ = 200;
    private Bitmap profileImageBitmap;  // Changed to Bitmap

    FirebaseAuth auth;
    FirebaseDatabase database;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Firebase Initialization
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        // Retrieving data from the previous activity (Intent)
        String surname = getIntent().getStringExtra("last_name");
        String name = getIntent().getStringExtra("first_name");
        String email = getIntent().getStringExtra("email");
        String country = getIntent().getStringExtra("country");

        // Initialize views
        btn_profile = findViewById(R.id.btn_profile);
        btn_register = findViewById(R.id.btn_register);
        dob = findViewById(R.id.edt_dob);
        username = findViewById(R.id.edt_username);
        password = findViewById(R.id.edt_password);
        rePassword = findViewById(R.id.edt_reenter_password);
        contactNo = findViewById(R.id.edt_contact_no);
        radiogrp = findViewById(R.id.radiogrp);

        // DatePicker logic for Date of Birth
        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(register.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                                dob.setText(selectedDate);
                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });

        // Profile Picture Logic
        btn_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder ad = new AlertDialog.Builder(register.this);
                ad.setIcon(R.drawable.baseline_account_circle_24);
                ad.setTitle("Profile Picture ");
                ad.setNegativeButton("Gallery", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(Intent.ACTION_PICK);
                        i.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(i, GALLERY_REQ);
                    }
                });
                ad.setPositiveButton("Camera", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(i, CAMERA_REQ);
                    }
                });
                ad.show();
            }
        });

        // Gender Spinner Setup
        spinner = findViewById(R.id.spinner);
        gender.add("Male");
        gender.add("Female");
        ArrayAdapter<String> gender_aa = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, gender);
        spinner.setAdapter(gender_aa);

        // Register Button Logic
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateFields()) {
                    String pass = password.getText().toString();
                    String uname = username.getText().toString();
                    String date = dob.getText().toString();
                    String contact = contactNo.getText().toString();
                    String spin = spinner.getText().toString();
                    String profile_base64 = "";

                    if (profileImageBitmap != null) {
                        profile_base64 = bitmapToBase64(profileImageBitmap);
                    }

                    // Listen to the radio group to check the selected role
                    int selectedId = radiogrp.getCheckedRadioButtonId();
                    if (selectedId == R.id.freelancer) {
                        Bundle bundle = new Bundle();
                        // Create the intent to send data to freelancer_form1
                        Intent i = new Intent(register.this, freelancer_form1.class);
                        bundle.putString("first_name", name);
                        bundle.putString("last_name", surname);
                        bundle.putString("email", email);
                        bundle.putString("password", pass);
                        bundle.putString("country", country);
                        bundle.putString("username", uname);
                        bundle.putString("dob", date);
                        bundle.putString("contact_no", contact);
                        bundle.putString("gender", spin);
                        bundle.putString("profile_image_base64", profile_base64);

                        i.putExtras(bundle);
                        startActivity(i);
                    } else {
                        // Register as a client
                        String finalProfile_base6 = profile_base64;
                        auth.createUserWithEmailAndPassword(email, pass)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            String id = task.getResult().getUser().getUid();
                                            DatabaseReference reference = database.getReference().child("user").child(id);

                                            // Create a client object
                                            client user = new client(name, surname, email, pass, country, uname, contact, spin, "Client", finalProfile_base6, date, 0, 0, 0, 0);

                                            reference.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Intent i = new Intent(register.this, MainActivity2.class);
                                                        startActivity(i);
                                                        finish();
                                                    } else {
                                                        Toast.makeText(register.this, "Error in creating a user", Toast.LENGTH_SHORT).show();
                                                        Log.e("RegistrationError", task.getException().getMessage());
                                                    }
                                                }
                                            });
                                        } else {
                                            Toast.makeText(register.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                }
            }
        });
    }

    // Handle image selection from gallery or camera
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_REQ) {
                // Get the image from the camera
                profileImageBitmap = (Bitmap) data.getExtras().get("data");
            } else if (requestCode == GALLERY_REQ) {
                // Get the image from the gallery
                Uri selectedImage = data.getData();
                try {
                    profileImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Convert Bitmap to Base64
    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    // Validate fields before proceeding
    private boolean validateFields() {
        // Validate username
        if (TextUtils.isEmpty(username.getText())) {
            username.setError("Username is required");
            return false;
        }

        // Validate password
        if (TextUtils.isEmpty(password.getText())) {
            password.setError("Password is required");
            return false;
        }

        // Validate re-entered password
        if (TextUtils.isEmpty(rePassword.getText())) {
            rePassword.setError("Please re-enter the password");
            return false;
        } else if (!password.getText().toString().equals(rePassword.getText().toString())) {
            rePassword.setError("Passwords do not match");
            return false;
        }

        // Validate contact number
        if (TextUtils.isEmpty(contactNo.getText())) {
            contactNo.setError("Contact number is required");
            return false;
        } else if (contactNo.getText().toString().length() != 10) {
            contactNo.setError("Invalid contact number");
            return false;
        }

        // Validate gender selection
        int selectedId = radiogrp.getCheckedRadioButtonId();
        if (selectedId == -1) {
            Toast.makeText(this, "Please select a role", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validate date of birth
        if (TextUtils.isEmpty(dob.getText())) {
            dob.setError("Date of birth is required");
            return false;
        }

        return true;
    }
}