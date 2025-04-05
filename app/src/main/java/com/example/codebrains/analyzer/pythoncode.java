package com.example.codebrains.analyzer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
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

                        String result = module.callAttr("analyze_zip_input", selectedFilePath.getAbsolutePath()).toString();
                        tvResult.setText(result);

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
            if (selectedFilePath != null && receivedJobId != null) {
                String resultText = tvResult.getText().toString();
                String ratingValue = extractRating(resultText);

                if (ratingValue != null) {
                    int rating = Integer.parseInt(ratingValue);
                    saveToFirebase(receivedJobId, resultText, rating);
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
    private void saveToFirebase(String jobId, String feedback, int rating) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Rated_jobs");

        String key = databaseRef.push().getKey();
        if (key != null) {
            Rated_Job newJob = new Rated_Job(System.currentTimeMillis(), jobId, feedback, rating);
            databaseRef.child(key).setValue(newJob);
        }
    }
}
