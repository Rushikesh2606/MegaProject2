package com.example.codebrains.freelancer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.codebrains.BidActivity;
import com.example.codebrains.model.JobController;
import com.example.codebrains.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class JobDetailActivity extends AppCompatActivity {

    private TextView title, category, description, skills, budget, deadline, experience;
    private DatabaseReference jobsRef;
Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_detail);

        // Initialize Firebase Database reference
        jobsRef = FirebaseDatabase.getInstance().getReference("jobs");

        // Initialize UI components
        title = findViewById(R.id.tv_title);
        category = findViewById(R.id.tv_category);
        btn=findViewById(R.id.btn_submit_proposal);
        description = findViewById(R.id.tv_description);
        skills = findViewById(R.id.tv_skills);
        budget = findViewById(R.id.tv_budget);
        deadline = findViewById(R.id.tv_deadline);
        experience = findViewById(R.id.tv_experience);

        // Get the job ID from the intent
        String jobId = getIntent().getStringExtra("JOB_ID");
Log.d("id",jobId);
        btn.setOnClickListener(v->
        {
            Intent i=new Intent(JobDetailActivity.this, BidActivity.class);

                i.putExtra("",jobId);
                startActivity(i);

            Toast.makeText(this, "Job id not found", Toast.LENGTH_SHORT).show();
            Log.d("error: ","Job id not found");
        });

        if (jobId != null) {
            // Fetch job details from Firebase using the job ID
            fetchJobDetails(jobId);
        }
    }

    private void fetchJobDetails(String jobId) {
        DatabaseReference jobRef = jobsRef.child(jobId);
        jobRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    JobController job = snapshot.getValue(JobController.class);
                    if (job != null) {
                        // Update UI with job details
                        title.setText(job.getJobTitle());
                        category.setText(job.getJobCategory());
                        description.setText(job.getJobDescription());
                        skills.setText("Skills: " + job.getPrimarySkill() + ", " + job.getAdditionalSkills());
                        budget.setText("Budget: $" + job.getBudget());
                        deadline.setText("Deadline: " + job.getDeadline());
                        experience.setText("Experience: " + job.getExperienceLevel());
                    }
                } else {
                    // Handle case where job ID does not exist in the database
                    title.setText("Job not found");
                    category.setText("");
                    description.setText("");
                    skills.setText("");
                    budget.setText("");
                    deadline.setText("");
                    experience.setText("");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
                title.setText("Error fetching job details");
                category.setText("");
                description.setText("");
                skills.setText("");
                budget.setText("");
                deadline.setText("");
                experience.setText("");
            }
        });
    }
}