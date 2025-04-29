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
import com.example.codebrains.payment1;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
public class RatedJobDetailsActivity extends AppCompatActivity {
    private static final int STORAGE_PERMISSION_CODE = 1001;

    private TextView titleTextView, descriptionTextView;
    private Button approveButton, reEvaluationButton, downloadButton, paymentButton;
    private String jobId, downloadUrl, feedback, rated_job_id, freelancerId, amount;
    private float rating;
    private DatabaseReference ratedJobsRef, jobsRef, freelancerRef, reEvaluationsRef;
    private float userNewRating = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rated_job_details);

        // Initialize UI components
        titleTextView = findViewById(R.id.titleTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        approveButton = findViewById(R.id.approveButton);
        reEvaluationButton = findViewById(R.id.reEvaluationButton);
        downloadButton = findViewById(R.id.downloadButton);
        paymentButton = findViewById(R.id.PaymentButton);

        // Set initial visibility states
        downloadButton.setVisibility(Button.GONE);
        paymentButton.setVisibility(Button.GONE);

        // Get intent extras
        jobId = getIntent().getStringExtra("jobId");
        rated_job_id = getIntent().getStringExtra("rated_job_id");

        // Initialize Firebase references
        ratedJobsRef = FirebaseDatabase.getInstance().getReference("Rated_jobs");
        jobsRef = FirebaseDatabase.getInstance().getReference("jobs");
        freelancerRef = FirebaseDatabase.getInstance().getReference("freelancer");
        reEvaluationsRef = FirebaseDatabase.getInstance().getReference("ReEvaluations");

        fetchRatedJobDetails();

        // Approve Button Click Listener
        approveButton.setOnClickListener(v -> {
            downloadButton.setVisibility(Button.VISIBLE);
            paymentButton.setVisibility(Button.VISIBLE);
            Toast.makeText(this, "You approved the submission!", Toast.LENGTH_SHORT).show();
        });

        // Re-evaluation Button Click Listener
        reEvaluationButton.setOnClickListener(v -> {
            downloadButton.setVisibility(Button.GONE);
            paymentButton.setVisibility(Button.GONE);
            showReEvaluationDialog();
        });

        // Download Button Click Listener
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

        // Payment Button Click Listener
        paymentButton.setOnClickListener(v -> {
            Intent intent = new Intent(RatedJobDetailsActivity.this, payment1.class);
            intent.putExtra("freelancerContactNo", freelancerId);
            intent.putExtra("amount", amount);
            startActivity(intent);
        });
    }

    private void fetchRatedJobDetails() {
        ratedJobsRef.child(rated_job_id).get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                String title = snapshot.child("jobTitle").getValue(String.class);
                feedback = snapshot.child("feedback").getValue(String.class);
                downloadUrl = snapshot.child("zip").getValue(String.class);
                freelancerId = snapshot.child("freelancer").getValue(String.class);
                amount = snapshot.child("budget").getValue(String.class);
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

    private void showReEvaluationDialog() {
        reEvaluationsRef.orderByChild("jobId").equalTo(jobId).get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                Toast.makeText(this, "A re-evaluation request already exists for this job.", Toast.LENGTH_LONG).show();
            } else {
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

                    String reevaluationId = reEvaluationsRef.push().getKey();
                    if (reevaluationId != null) {
                        long currentTimestamp = System.currentTimeMillis();

                        Reevaluation reevaluation = new Reevaluation(
                                reevaluationId,
                                true,
                                currentTimestamp,
                                reevaluationReason,
                                freelancerId,
                                jobId,
                                feedback
                        );

                        reEvaluationsRef.child(reevaluationId).setValue(reevaluation)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(this, "Re-evaluation request submitted successfully!", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Failed to submit re-evaluation: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    }
                });

                builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
                builder.show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error checking re-evaluation status", Toast.LENGTH_SHORT).show();
        });
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

    private void startDownload() {
        try {
            byte[] zipBytes = Base64.decode(downloadUrl, Base64.DEFAULT);
            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File zipFile = new File(downloadsDir, "submission_" + System.currentTimeMillis() + ".zip");

            try (FileOutputStream fos = new FileOutputStream(zipFile)) {
                fos.write(zipBytes);
                Toast.makeText(this, "ZIP file downloaded successfully!", Toast.LENGTH_SHORT).show();
                openZipFile(zipFile);
            }
        } catch (IOException e) {
            Toast.makeText(this, "File download failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void openZipFile(File zipFile) {
        Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", zipFile);
        Intent intent = new Intent(Intent.ACTION_VIEW)
                .setDataAndType(uri, "application/zip")
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(intent, "Open ZIP file"));
    }

    private void updateDeveloperRating() {
        freelancerRef.child(freelancerId).get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                Float currentRating = snapshot.child("rating").getValue(Float.class);
                float newRating = currentRating != null ?
                        (currentRating + userNewRating) / 2 :
                        userNewRating;

                freelancerRef.child(freelancerId).child("rating").setValue(newRating)
                        .addOnSuccessListener(aVoid -> {
                            ratedJobsRef.child(rated_job_id).removeValue();
                            finish();
                        });
            }
        });
    }

    private boolean checkStoragePermission() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_GRANTED;
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                STORAGE_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showRatingDialog();
            } else {
                Toast.makeText(this, "Storage permission required for downloads", Toast.LENGTH_SHORT).show();
            }
        }
    }
}