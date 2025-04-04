package com.example.codebrains.freelancer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.codebrains.BidActivity;
import com.example.codebrains.model.JobController;
import com.example.codebrains.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class JobDetailActivity extends AppCompatActivity {

    private TextView title, category, description, skills, budget, deadline, experience;
    private DatabaseReference jobsRef;
    private Button btn, btnDownloadAttachment;
    private static final int STORAGE_PERMISSION_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_detail);

        // Initialize Firebase Database reference
        jobsRef = FirebaseDatabase.getInstance().getReference("jobs");

        // Initialize UI components
        initializeViews();

        // Get the job ID from the intent
        String jobId = getIntent().getStringExtra("JOB_ID");

        if (jobId != null) {
            Log.d("JobDetailActivity", "Job ID: " + jobId);
            fetchJobDetails(jobId);
        } else {
            Toast.makeText(this, "Job ID not found", Toast.LENGTH_SHORT).show();
            Log.e("JobDetailActivity", "Job ID is null");
        }

        setupButtonClickListeners(jobId);
        animateViews();
    }

    private void initializeViews() {
        title = findViewById(R.id.tv_title);
        category = findViewById(R.id.tv_category);
        description = findViewById(R.id.tv_description);
        skills = findViewById(R.id.tv_skills);
        budget = findViewById(R.id.tv_budget);
        deadline = findViewById(R.id.tv_deadline);
        experience = findViewById(R.id.tv_experience);
        btn = findViewById(R.id.btn_submit_proposal);
        btnDownloadAttachment = findViewById(R.id.btn_download_attachment);
    }

    private void setupButtonClickListeners(String jobId) {
        btn.setOnClickListener(v -> {
            if (jobId != null) {
                Intent i = new Intent(JobDetailActivity.this, BidActivity.class);
                i.putExtra("JOB_ID", jobId);
                startActivity(i);
            } else {
                Toast.makeText(this, "Job ID not found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchJobDetails(String jobId) {
        DatabaseReference jobRef = jobsRef.child(jobId);
        jobRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    JobController job = snapshot.getValue(JobController.class);
                    if (job != null) {
                        updateJobDetailsUI(job);
                        handleAttachmentDownload(job);
                    }
                } else {
                    title.setText("Job not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                title.setText("Error fetching job details");
            }
        });
    }

    private void updateJobDetailsUI(JobController job) {
        title.setText(job.getJobTitle());
        category.setText(job.getJobCategory());
        description.setText(job.getJobDescription());
        skills.setText("Skills: " + job.getPrimarySkill() + ", " + job.getAdditionalSkills());
        budget.setText("Budget: $" + job.getBudget());
        deadline.setText("Deadline: " + job.getDeadline());
        experience.setText("Experience: " + job.getExperienceLevel());
    }

    private void handleAttachmentDownload(JobController job) {
        if (job.getAttachments() != null && !job.getAttachments().isEmpty()) {
            btnDownloadAttachment.setVisibility(View.VISIBLE);
            btnDownloadAttachment.setOnClickListener(v -> {
                if (checkStoragePermission()) {
                    downloadAttachment(job.getAttachments(), job.getJobTitle());
                } else {
                    requestStoragePermission();
                }
            });
        }
    }

    private boolean checkStoragePermission() {
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                STORAGE_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE && grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
        }
    }

    private void downloadAttachment(String base64Data, String jobTitle) {
        try {
            byte[] fileData = Base64.decode(base64Data, Base64.DEFAULT);
            File downloadsDir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
            if (!downloadsDir.exists()) {
                downloadsDir.mkdirs();
            }

            String fileName = "attachment_" + jobTitle.replace(" ", "_") + ".pdf";
            File file = new File(downloadsDir, fileName);

            FileOutputStream fos = new FileOutputStream(file);
            fos.write(fileData);
            fos.close();

            Toast.makeText(this, "File downloaded to " + file.getAbsolutePath(),
                    Toast.LENGTH_LONG).show();
            Log.d("download",file.getAbsolutePath());
        } catch (IOException e) {
            Log.e("DownloadAttachment", "Error saving file", e);
            Toast.makeText(this, "Failed to download file", Toast.LENGTH_SHORT).show();
        }
    }


    // ... rest of the existing animation code remains exactly the same ...
    @SuppressLint({"ObjectAnimatorBinding", "ClickableViewAccessibility"})
    private void animateViews() {
        // Existing animation code remains unchanged
    }
}