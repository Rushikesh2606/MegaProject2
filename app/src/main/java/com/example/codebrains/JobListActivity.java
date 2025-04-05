package com.example.codebrains;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.codebrains.JobItemAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class JobListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private JobItemAdapter adapter;
    private List<String> jobIdList;
    private ProgressBar progressBar;
    private TextView emptyView;
    private SwipeRefreshLayout swipeRefreshLayout;

    private DatabaseReference connectionRef;
    private String currentFreelancerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_list);

        setupToolbar();

        recyclerView = findViewById(R.id.recyclerViewJobs);
        progressBar = findViewById(R.id.progressBar);
        emptyView = findViewById(R.id.emptyView);
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        jobIdList = new ArrayList<>();
        adapter = new JobItemAdapter(this, jobIdList);
        recyclerView.setAdapter(adapter);

        currentFreelancerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        connectionRef = FirebaseDatabase.getInstance().getReference("Connections");

        swipeRefreshLayout.setOnRefreshListener(this::fetchJobIds);

        fetchJobIds();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("My Connected Jobs");
        setSupportActionBar(toolbar);
    }

    private void fetchJobIds() {
        progressBar.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);

        connectionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                jobIdList.clear();
                for (DataSnapshot connectionSnap : snapshot.getChildren()) {
                    String freelancerId = connectionSnap.child("freelancer").getValue(String.class);
                    if (freelancerId != null && freelancerId.equals(currentFreelancerId)) {
                        String jobId = connectionSnap.child("jobId").getValue(String.class);
                        if (jobId != null) {
                            jobIdList.add(jobId);
                        }
                    }
                }

                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);

                if (jobIdList.isEmpty()) {
                    emptyView.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(JobListActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
