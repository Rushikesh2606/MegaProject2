package com.example.codebrains.analyzer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.example.codebrains.R;
import com.example.codebrains.model.Rated_Job;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class pythoncode extends AppCompatActivity {

    private ActivityResultLauncher<String> getContentLauncher;
    private TextView tvResult;
    private File selectedFilePath = null;
    private Button btnUpload;
    private String receivedJobId = null; // Job ID from intent
    private String base64EncodedZip = null; // Store Base64-encoded zip
    private String freelancer = null; // Freelancer name
    private String jobTitle = null; // Job title
    private String jobDescription = null; // Job description

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pythoncode);

        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }

        Python py = Python.getInstance();
        final com.chaquo.python.PyObject module = py.getModule("codeanalyzer");

        tvResult = findViewById(R.id.textViewResult);
        Button btnSelectFile = findViewById(R.id.buttonSelectFile);
        Button btnMoreInfo = findViewById(R.id.buttonMoreInfo);
        btnUpload = findViewById(R.id.upload);

        // Hide upload button initially
        btnUpload.setVisibility(View.GONE);

        // Check if the previous activity sent an intent with job_id
        Intent intent = getIntent();
        if (intent.hasExtra("jobId")) {
            receivedJobId = intent.getStringExtra("jobId");
            btnUpload.setVisibility(View.VISIBLE); // Show upload button
            fetchJobDetails(receivedJobId);
        }

        getContentLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri == null) {
                        Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    File tempFile = new File(getCacheDir(), "temp.zip");

                    try (InputStream inputStream = getContentResolver().openInputStream(uri);
                         FileOutputStream outputStream = new FileOutputStream(tempFile)) {

                        byte[] buffer = new byte[4096];
                        int bytesRead;

                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }

                        selectedFilePath = tempFile;

                        // Analyze using Python
                        String result = module.callAttr("analyze_zip_input", selectedFilePath.getAbsolutePath()).toString();
                        tvResult.setText(result);

                        // Convert the file to Base64
                        base64EncodedZip = encodeFileToBase64(selectedFilePath);

                    } catch (Exception e) {
                        Toast.makeText(this, "Error processing file: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
        );

        btnSelectFile.setOnClickListener(v -> getContentLauncher.launch("application/zip"));

        btnMoreInfo.setOnClickListener(v -> {
            if (selectedFilePath != null) {
                Intent newIntent = new Intent(pythoncode.this, Analyzer.class);
                newIntent.putExtra("filepath", selectedFilePath.getAbsolutePath());
                startActivity(newIntent);
            } else {
                Toast.makeText(this, "Please select a ZIP file first.", Toast.LENGTH_SHORT).show();
            }
        });

        // Upload button stores data in Firebase
        btnUpload.setOnClickListener(v -> {
            if (selectedFilePath != null && receivedJobId != null && base64EncodedZip != null) {
                String resultText = tvResult.getText().toString();
                String ratingValue = extractRating(resultText);

                if (ratingValue != null) {
                    int rating = Integer.parseInt(ratingValue);
                    saveToFirebase(receivedJobId, resultText, rating, base64EncodedZip);
                    Toast.makeText(this, "Feedback uploaded!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Failed to extract rating!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Missing job ID or file!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to extract rating from tvResult text
    private String extractRating(String text) {
        Pattern pattern = Pattern.compile("Overall Rating:\\s*(\\d+)");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    // Method to save feedback and rating to Firebase
    private void saveToFirebase(String jobId, String feedback, int rating, String encodedZip) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Rated_jobs");

        String key = databaseRef.push().getKey();
        if (key != null) {
            Rated_Job newJob = new Rated_Job(System.currentTimeMillis(), jobId, feedback, rating, encodedZip, key,jobDescription,jobTitle,freelancer);
            databaseRef.child(key).setValue(newJob);
        }
    }

    // Helper method to encode file to Base64 String
    private String encodeFileToBase64(File file) {
        try (FileInputStream fis = new FileInputStream(file);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }

            byte[] fileBytes = baos.toByteArray();
            return Base64.encodeToString(fileBytes, Base64.DEFAULT);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Method to fetch job details from Firebase
    private void fetchJobDetails(String jobId) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("jobs");

        databaseRef.child(jobId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    freelancer = dataSnapshot.child("freelancer").getValue(String.class);
                    jobTitle = dataSnapshot.child("jobTitle").getValue(String.class);
                    jobDescription = dataSnapshot.child("jobDescription").getValue(String.class);

                    // You can now use these variables as needed
                    Toast.makeText(pythoncode.this, "Job details fetched successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(pythoncode.this, "Job details not found!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(pythoncode.this, "Error fetching job details: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}