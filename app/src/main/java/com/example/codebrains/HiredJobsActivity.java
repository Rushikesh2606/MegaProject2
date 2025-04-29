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

import com.example.codebrains.model.Connection;
import com.example.codebrains.model.JobController;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class HiredJobsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private JobItemAdapter adapter;
    private List<JobController> hiredJobList;
    private ProgressBar progressBar;
    private TextView emptyView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private DatabaseReference connectionsRef, jobsRef;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hired_jobs);

        // Initialize views
        recyclerView = findViewById(R.id.recyclerViewHiredJobs);
        progressBar = findViewById(R.id.progressBar);
        emptyView = findViewById(R.id.emptyView);
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        hiredJobList = new ArrayList<>();
        adapter = new JobItemAdapter(this, hiredJobList);
        recyclerView.setAdapter(adapter);

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        connectionsRef = FirebaseDatabase.getInstance().getReference("Connections");
        jobsRef = FirebaseDatabase.getInstance().getReference("jobs");

        swipeRefreshLayout.setOnRefreshListener(this::loadHiredJobs);
        loadHiredJobs();
    }

    private void loadHiredJobs() {
        progressBar.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);

        connectionsRef.orderByChild("freelancer").equalTo(currentUserId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        hiredJobList.clear();
                        List<String> hiredJobIds = new ArrayList<>();

                        for (DataSnapshot connectionSnapshot : snapshot.getChildren()) {
                            Connection connection = connectionSnapshot.getValue(Connection.class);
                            if (connection != null && connection.isHired()) {
                                hiredJobIds.add(connection.getJobId());
                            }
                        }

                        if (hiredJobIds.isEmpty()) {
                            updateUI();
                            return;
                        }

                        fetchJobDetails(hiredJobIds);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressBar.setVisibility(View.GONE);
                        swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(HiredJobsActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void fetchJobDetails(List<String> jobIds) {
        AtomicInteger counter = new AtomicInteger(0);
        for (String jobId : jobIds) {
            jobsRef.child(jobId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    JobController job = snapshot.getValue(JobController.class);
                    if (job != null) {
                        hiredJobList.add(job);
                    }
                    if (counter.incrementAndGet() == jobIds.size()) {
                        updateUI();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    if (counter.incrementAndGet() == jobIds.size()) {
                        updateUI();
                    }
                }
            });
        }
    }

    private void updateUI() {
        progressBar.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);

        if (hiredJobList.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter.notifyDataSetChanged();
        }
    }
}