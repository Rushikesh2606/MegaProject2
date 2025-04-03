package com.example.codebrains.job;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.codebrains.R;
import com.example.codebrains.model.JobController;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class JobFragment extends Fragment implements JobAdapter.OnJobCloseListener {
    private String filter;
    private List<JobController> allJobs = new ArrayList<>();
    private List<JobController> filteredJobs = new ArrayList<>();
    private JobAdapter adapter;
    private DatabaseReference databaseReference;
    private JobAdapter.OnJobCloseListener closeListener;

    public static JobFragment newInstance(String filter, JobAdapter.OnJobCloseListener listener) {
        JobFragment fragment = new JobFragment();
        Bundle args = new Bundle();
        args.putString("filter", filter);
        fragment.setArguments(args);
        fragment.closeListener = listener;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            filter = getArguments().getString("filter");
        }

        // Initialize Firebase reference
        databaseReference = FirebaseDatabase.getInstance().getReference("jobs");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_job, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new JobAdapter(filteredJobs, this);
        recyclerView.setAdapter(adapter);

        fetchJobsFromFirebase();

        return view;
    }

    private void fetchJobsFromFirebase() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allJobs.clear();
                for (DataSnapshot jobSnapshot : snapshot.getChildren()) {
                    JobController job = jobSnapshot.getValue(JobController.class);
                    if (job != null) {
                        allJobs.add(job);
                    }
                }
                filterJobs();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load jobs: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterJobs() {
        filteredJobs.clear();
        for (JobController job : allJobs) {
            if ("All".equalsIgnoreCase(filter) || job.getStatus().equalsIgnoreCase(filter)) {
                filteredJobs.add(job);
            }
        }
    }

    public void handleJobClosed(int position) {
        if (position >= 0 && position < filteredJobs.size()) {
            JobController removedJob = filteredJobs.remove(position);
            adapter.notifyItemRemoved(position);
            allJobs.remove(removedJob);
        }
    }

    @Override
    public void onJobClosed(int position) {
        if (closeListener != null) {
            closeListener.onJobClosed(position);
        }
        handleJobClosed(position);
    }

    public void updateJobs(List<JobController> newJobs) {
        allJobs.clear();
        allJobs.addAll(newJobs);
        filterJobs();
        adapter.notifyDataSetChanged();
    }
}
