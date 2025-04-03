package com.example.codebrains.freelancer;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.codebrains.JobController;
import com.example.codebrains.R;

public class JobDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_detail);

        JobController job = getIntent().getParcelableExtra("JOB_DETAILS");

        TextView title = findViewById(R.id.tv_title);
        TextView category = findViewById(R.id.tv_category);
        TextView description = findViewById(R.id.tv_description);
        TextView skills = findViewById(R.id.tv_skills);
        TextView budget = findViewById(R.id.tv_budget);
        TextView deadline = findViewById(R.id.tv_deadline);
        TextView experience = findViewById(R.id.tv_experience);

        if (job != null) {
            title.setText(job.getJobTitle());
            category.setText(job.getJobCategory());
            description.setText(job.getJobDescription());
            skills.setText("Skills: " + job.getPrimarySkill() + ", " + job.getAdditionalSkills());
            budget.setText("Budget: $" + job.getBudget());
            deadline.setText("Deadline: " + job.getDeadline());
            experience.setText("Experience: " + job.getExperienceLevel());
        }
    }
}