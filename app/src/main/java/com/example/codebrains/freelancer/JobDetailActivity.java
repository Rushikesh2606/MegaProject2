package com.example.codebrains.freelancer;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

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
    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_detail);

        // Initialize Firebase Database reference
        jobsRef = FirebaseDatabase.getInstance().getReference("jobs");

        // Initialize UI components
        title = findViewById(R.id.tv_title);
        category = findViewById(R.id.tv_category);
        description = findViewById(R.id.tv_description);
        skills = findViewById(R.id.tv_skills);
        budget = findViewById(R.id.tv_budget);
        deadline = findViewById(R.id.tv_deadline);
        experience = findViewById(R.id.tv_experience);
        btn = findViewById(R.id.btn_submit_proposal);

        // Get the job ID from the intent
        String jobId = getIntent().getStringExtra("JOB_ID");

        if (jobId != null) {
            Log.d("JobDetailActivity", "Job ID: " + jobId);
            fetchJobDetails(jobId);
        } else {
            Toast.makeText(this, "Job ID not found", Toast.LENGTH_SHORT).show();
            Log.e("JobDetailActivity", "Job ID is null");
        }

        btn.setOnClickListener(v -> {
            if (jobId != null) {
                Intent i = new Intent(JobDetailActivity.this, BidActivity.class);
                i.putExtra("JOB_ID", jobId); // Fixed: added key
                startActivity(i);
            } else {
                Toast.makeText(this, "Job ID not found", Toast.LENGTH_SHORT).show();
                Log.e("JobDetailActivity", "Job ID not found in intent");
            }
        });

        animateViews();
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

    private void animateViews() {
        // Get references to views
        LinearLayout budgetDeadlineLayout = (LinearLayout) budget.getParent();
        CardView cardView = (CardView) title.getParent().getParent();

        View[] views = {title, category, description, budgetDeadlineLayout, skills, experience, btn};

        // Set all views to be invisible initially
        for (View view : views) {
            view.setAlpha(0f);
        }

        // Animate each view with a delay
        for (int i = 0; i < views.length; i++) {
            View view = views[i];
            view.animate()
                    .alpha(1f)
                    .translationX(0f)
                    .translationY(0f)
                    .setDuration(300)
                    .setStartDelay(100L * i)
                    .setInterpolator(new OvershootInterpolator(0.8f))
                    .start();
        }

        // Add ripple effect to the entire card when touched
        cardView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                ObjectAnimator.ofFloat(v, "cardElevation", 8f, 16f)
                        .setDuration(100)
                        .start();
                return true;
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                ObjectAnimator.ofFloat(v, "cardElevation", 16f, 8f)
                        .setDuration(100)
                        .start();
                return false;
            }
            return false;
        });

        // Add pulse animation to submit button
        ObjectAnimator pulseAnimation = ObjectAnimator.ofPropertyValuesHolder(
                btn,
                PropertyValuesHolder.ofFloat("scaleX", 1f, 1.05f, 1f),
                PropertyValuesHolder.ofFloat("scaleY", 1f, 1.05f, 1f)
        );
        pulseAnimation.setDuration(1500);
        pulseAnimation.setRepeatCount(ValueAnimator.INFINITE);
        pulseAnimation.setRepeatMode(ValueAnimator.RESTART);
        pulseAnimation.setStartDelay(1000);
        pulseAnimation.start();
    }
}
