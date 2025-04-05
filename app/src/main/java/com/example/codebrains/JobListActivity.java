package com.example.codebrains;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.codebrains.model.JobController;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class JobListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private JobItemAdapter adapter;
    private List<JobController> jobList;
    private ProgressBar progressBar;
    private TextView emptyView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private DatabaseReference connectionRef, jobsRef;
    private String currentFreelancerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_list);

        // Initialize views
        recyclerView = findViewById(R.id.recyclerViewJobs);
        progressBar = findViewById(R.id.progressBar);
        emptyView = findViewById(R.id.emptyView);
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        jobList = new ArrayList<>();
        adapter = new JobItemAdapter(this, jobList);
        recyclerView.setAdapter(adapter);

        currentFreelancerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        connectionRef = FirebaseDatabase.getInstance().getReference("Connections");
        jobsRef = FirebaseDatabase.getInstance().getReference("jobs");

        swipeRefreshLayout.setOnRefreshListener(this::loadJobs);
        loadJobs();
    }

    private void loadJobs() {
        progressBar.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);

        connectionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> jobIds = new ArrayList<>();
                for (DataSnapshot connSnapshot : snapshot.getChildren()) {
                    String freelancerId = connSnapshot.child("freelancer").getValue(String.class);
                    if (freelancerId != null && freelancerId.equals(currentFreelancerId)) {
                        String jobId = connSnapshot.child("jobId").getValue(String.class);
                        if (jobId != null) jobIds.add(jobId);
                    }
                }
                fetchJobDetails(jobIds);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(JobListActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void fetchJobDetails(List<String> jobIds) {
        jobList.clear();
        if(jobIds.isEmpty()) {
            updateUI();
            return;
        }

        AtomicInteger counter = new AtomicInteger(0);
        for(String jobId : jobIds) {
            jobsRef.child(jobId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    JobController job = snapshot.getValue(JobController.class);
                    if(job != null) {
                        jobList.add(job);
                    }

                    if(counter.incrementAndGet() == jobIds.size()) {
                        updateUI();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    if(counter.incrementAndGet() == jobIds.size()) {
                        updateUI();
                    }
                }
            });
        }
    }

    private void updateUI() {
        progressBar.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);

        if(jobList.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter.notifyDataSetChanged();
        }
    }
}