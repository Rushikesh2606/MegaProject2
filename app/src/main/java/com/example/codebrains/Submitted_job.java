package com.example.codebrains;

import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.codebrains.SubmittedJobAdapter;
import com.example.codebrains.model.JobController;
import com.example.codebrains.model.Rated_Job;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Submitted_job extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SubmittedJobAdapter adapter;
    private List<JobController> submittedJobList = new ArrayList<>();
    private DatabaseReference jobsRef, ratedJobsRef;
    private String currentUserId;
    private Map<String, Rated_Job> ratedJobsMap = new HashMap<>(); // To store jobid -> RatedJob

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submitted_job);

        recyclerView = findViewById(R.id.recyclerViewSubmittedJobs);
        adapter = new SubmittedJobAdapter(this, submittedJobList, ratedJobsMap);
        recyclerView.setAdapter(adapter);


        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        jobsRef = FirebaseDatabase.getInstance().getReference("jobs");
        ratedJobsRef = FirebaseDatabase.getInstance().getReference("Rated_jobs");

        fetchRatedJobs();
    }

    private void fetchRatedJobs() {
        ratedJobsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    Rated_Job ratedJob = data.getValue(Rated_Job.class);
                    if (ratedJob != null) {
                        ratedJobsMap.put(ratedJob.getJobId(), ratedJob);
                    }
                }
                fetchJobs();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("Firebase", "Rated_jobs fetch error: " + error.getMessage());
            }
        });
    }

    private void fetchJobs() {
        jobsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                submittedJobList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    JobController job = data.getValue(JobController.class);
                    if (job != null && ratedJobsMap.containsKey(job.getId())) {
                        if (job.getUsername().equals(currentUserId)) {
                            submittedJobList.add(job);

                            // Optional: Decode zip if needed
                            String zipBase64 = ratedJobsMap.get(job.getId()).getZip();
                            if (zipBase64 != null) {
                                byte[] decodedBytes = Base64.decode(zipBase64, Base64.DEFAULT);
                                // Save decodedBytes as zip file if needed
                            }
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("Firebase", "Jobs fetch error: " + error.getMessage());
            }
        });
    }
}
