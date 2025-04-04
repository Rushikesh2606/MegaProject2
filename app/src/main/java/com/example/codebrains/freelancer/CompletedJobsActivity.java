package com.example.codebrains.freelancer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;

import com.example.codebrains.R;

import java.util.ArrayList;
import java.util.List;

public class CompletedJobsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed_jobs);

        RecyclerView recyclerView = findViewById(R.id.rvCompletedJobs);
        List<Job> jobs = generateSampleJobs();
        JobCompletedAdapter adapter = new JobCompletedAdapter(jobs);
        recyclerView.setAdapter(adapter);
    }

    private List<Job> generateSampleJobs() {
        List<Job> jobs = new ArrayList<>();
        String[] categories = getResources().getStringArray(R.array.job_categories);

        // Generate sample jobs
        for (int i = 0; i < categories.length; i++) {
            jobs.add(new Job(
                    categories[i],
                    "Project " + (i + 1),
                    "Completed project in " + categories[i] + " category",
                    "2023-11-" + (i < 9 ? "0" + (i + 1) : (i + 1))
            ));
        }
        return jobs;
    }
}