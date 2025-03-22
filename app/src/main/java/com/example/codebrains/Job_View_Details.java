package com.example.codebrains;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Job_View_Details extends AppCompatActivity {

    private TextView tvJobTitle, tvJobStatus, tvProjectID;
    private Button btnReopenJob;
    private DatabaseReference jobRef;
    private String jobId;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_job_view_details);

        // Apply system window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.jobviewdETAILSID), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI elements
        tvJobTitle = findViewById(R.id.tvJobTitle);
        tvJobStatus = findViewById(R.id.tvJobStatus);
        tvProjectID = findViewById(R.id.tvProjectID);
        btnReopenJob = findViewById(R.id.btnReopenJob);

        // Get jobId from intent
        jobId = getIntent().getStringExtra("jobId");

        if (jobId == null || jobId.isEmpty()) {
            Toast.makeText(this, "Invalid Job ID"+jobId, Toast.LENGTH_SHORT).show();
            finish(); // Close the activity if jobId is invalid
            return;
        }

        // Initialize Firebase Database reference
        jobRef = FirebaseDatabase.getInstance().getReference("jobs").child(jobId);

        // Fetch job details from Firebase
        fetchJobDetails();

        // Setup ViewPager and Tabs
        setupTabs();
    }

    private void fetchJobDetails() {
        jobRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String title = snapshot.child("jobTitle").getValue(String.class);
                    String status = snapshot.child("status").getValue(String.class);

                    tvJobTitle.setText(title != null ? title : "No Title");
                    tvJobStatus.setText(status != null ? status : "Open");
                    tvProjectID.setText("Project ID: " + jobId);
                } else {
                    Toast.makeText(Job_View_Details.this, "Job not found", Toast.LENGTH_SHORT).show();
                    finish(); // Close the activity if job doesn't exist
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(Job_View_Details.this, "Failed to load job details", Toast.LENGTH_SHORT).show();
                Log.e("FirebaseError", error.getMessage());
            }
        });
    }

    private void setupTabs() {
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager2 viewPager = findViewById(R.id.viewPager);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this,jobId);
        viewPager.setAdapter(viewPagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Details");
                    break;
                case 1:
                    tab.setText("Proposals");
                    break;
                case 2:
                    tab.setText("Files");
                    break;
            }
        }).attach();
    }
}
