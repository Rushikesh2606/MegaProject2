package com.example.codebrains.analyzer;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.codebrains.R;
import com.example.codebrains.model.Reevaluation;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class RatedJobDetailsActivity extends AppCompatActivity {

    private static final int STORAGE_PERMISSION_CODE = 1001;

    private TextView titleTextView, descriptionTextView;
    private Button approveButton, reEvaluationButton, downloadButton;
    private String jobId, downloadUrl, feedback, rated_job_id, freelancerId;
    private float rating;
    private DatabaseReference ratedJobsRef, jobsRef, freelancerRef, reEvaluationsRef;
    private float userNewRating = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rated_job_details);

        titleTextView = findViewById(R.id.titleTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        approveButton = findViewById(R.id.approveButton);
        reEvaluationButton = findViewById(R.id.reEvaluationButton);
        downloadButton = findViewById(R.id.downloadButton);

        downloadButton.setVisibility(Button.GONE);

        jobId = getIntent().getStringExtra("jobId");
        rated_job_id = getIntent().getStringExtra("rated_job_id");

        ratedJobsRef = FirebaseDatabase.getInstance().getReference("Rated_jobs");
        jobsRef = FirebaseDatabase.getInstance().getReference("jobs");
        freelancerRef = FirebaseDatabase.getInstance().getReference("freelancer");
        reEvaluationsRef = FirebaseDatabase.getInstance().getReference("ReEvaluations");

        fetchRatedJobDetails();
        approveButton.setOnClickListener(v -> {
            downloadButton.setVisibility(Button.VISIBLE);
            Toast.makeText(this, "You approved the submission!", Toast.LENGTH_SHORT).show();
        });

        reEvaluationButton.setOnClickListener(v -> showReEvaluationDialog());

        downloadButton.setOnClickListener(v -> {
            if (downloadUrl != null && !downloadUrl.isEmpty()) {
                if (checkStoragePermission()) {
                    showRatingDialog();
                } else {
                    requestStoragePermission();
                }
            } else {
                Toast.makeText(this, "Download URL not available", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchRatedJobDetails() {
        ratedJobsRef.child(rated_job_id).get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                String title = snapshot.child("jobTitle").getValue(String.class);
                feedback = snapshot.child("feedback").getValue(String.class);
                downloadUrl = snapshot.child("zip").getValue(String.class); // base64 string
                freelancerId = snapshot.child("freelancer").getValue(String.class);
                try {
                    rating = snapshot.child("rating").getValue(Float.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                titleTextView.setText(title);
                descriptionTextView.setText(feedback);
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to fetch rated job details.", Toast.LENGTH_SHORT).show();
        });
    }

    private void startDownload() {
        if (downloadUrl == null || downloadUrl.isEmpty()) {
            Toast.makeText(this, "Download data not available.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            byte[] zipBytes = Base64.decode(downloadUrl, Base64.DEFAULT);

            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (!downloadsDir.exists()) {
                downloadsDir.mkdirs();
            }

            File zipFile = new File(downloadsDir, "submission_downloaded.zip");

            FileOutputStream fos = new FileOutputStream(zipFile);
            fos.write(zipBytes);
            fos.flush();
            fos.close();

            Toast.makeText(this, "ZIP File downloaded successfully!", Toast.LENGTH_SHORT).show();

            openZipFile(zipFile);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to decode and save ZIP file.", Toast.LENGTH_SHORT).show();
        }
    }

    private void showRatingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Rate the Developer");

        RatingBar ratingBar = new RatingBar(this);
        ratingBar.setNumStars(5);
        ratingBar.setStepSize(1.0f);
        builder.setView(ratingBar);

        builder.setPositiveButton("Submit", (dialog, which) -> {
            userNewRating = ratingBar.getRating();
            startDownload();
            updateDeveloperRating();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    private void openZipFile(File zipFile) {
        Uri fileUri;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            fileUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", zipFile);
        } else {
            fileUri = Uri.fromFile(zipFile);
        }

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(fileUri, "application/zip");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(intent, "Open ZIP file"));
    }

    private void updateDeveloperRating() {
        if (freelancerId != null) {
            freelancerRef.child(freelancerId).get().addOnSuccessListener(snapshot -> {
                if (snapshot.exists()) {
                    Float oldRating = snapshot.child("rating").getValue(Float.class);
                    if (oldRating == null) oldRating = 0f;
                    float firstRating = (rating + oldRating) / 2;
                    float finalRating = (firstRating + userNewRating) / 2;

                    freelancerRef.child(freelancerId).child("rating").setValue(finalRating)
                            .addOnSuccessListener(aVoid -> {
                                ratedJobsRef.child(rated_job_id).removeValue();
                                Toast.makeText(this, "Rating updated successfully!", Toast.LENGTH_SHORT).show();
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Failed to update rating.", Toast.LENGTH_SHORT).show();
                            });

                } else {
                    Toast.makeText(this, "Freelancer not found.", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(this, "Failed to fetch freelancer details.", Toast.LENGTH_SHORT).show();
            });
        } else {
            Toast.makeText(this, "Freelancer ID not found.", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return true;
        } else {
            int result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                STORAGE_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showRatingDialog();
            } else {
                Toast.makeText(this, "Storage Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void showReEvaluationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Request Re-evaluation");

        final EditText input = new EditText(this);
        input.setHint("Enter reason for re-evaluation...");
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        builder.setView(input);

        builder.setPositiveButton("Submit", (dialog, which) -> {
            String reevaluationReason = input.getText().toString().trim();
            if (reevaluationReason.isEmpty()) {
                Toast.makeText(this, "Please enter a reason for re-evaluation.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Generate a unique ID for the reevaluation
            String reevaluationId = reEvaluationsRef.push().getKey(); // Generate a unique ID

            if (reevaluationId != null) {
                // Get current timestamp
                long currentTimestamp = System.currentTimeMillis();

                // Create a new Reevaluation object
                Reevaluation reevaluation = new Reevaluation(reevaluationId, true, currentTimestamp, reevaluationReason);

                // Save the reevaluation request to Firebase under ReEvaluations node
                reEvaluationsRef.child(reevaluationId).setValue(reevaluation)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "Re-evaluation request submitted successfully!", Toast.LENGTH_SHORT).show();
                            // Optionally, you can show the new reevaluation or perform any other actions
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Failed to submit re-evaluation: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            } else {
                Toast.makeText(this, "Failed to generate reevaluation ID.", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.show();
    }

}
