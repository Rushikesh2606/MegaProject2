package com.example.codebrains.freelancer;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.codebrains.model.JobController;
import com.example.codebrains.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class CategoryFragment extends Fragment {
    private static final String ARG_CATEGORY = "category";
    private String category;
    private RecyclerView recyclerView;
    private JobAdapter adapter;

    public static CategoryFragment newInstance(String category) {
        CategoryFragment fragment = new CategoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CATEGORY, category);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            category = getArguments().getString(ARG_CATEGORY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new JobAdapter();
        recyclerView.setAdapter(adapter);

        loadJobsFromFirebase();
        setupClickListener();

        return view;
    }

    private void loadJobsFromFirebase() {
        DatabaseReference jobsRef = FirebaseDatabase.getInstance().getReference("jobs");
        Query query = jobsRef.orderByChild("jobCategory").equalTo(category);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    List<JobController> jobs = new ArrayList<>();
                    for (DataSnapshot jobSnapshot : snapshot.getChildren()) {
                        JobController job = jobSnapshot.getValue(JobController.class);
                        if (job != null) {
                            job.setId(jobSnapshot.getKey());
                            jobs.add(job);
                        }
                    }
                    adapter.setJobs(jobs);
                } else {
                    // No data found
                    Log.d("CategoryFragment", "No jobs found for category: " + category);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
                Log.e("CategoryFragment", "Failed to read jobs", error.toException());
            }
        });
    }

    private void setupClickListener() {
        adapter.setOnItemClickListener(job -> {
            Intent intent = new Intent(getActivity(), JobDetailActivity.class);
            intent.putExtra("JOB_ID", job.getId());
            startActivity(intent);
        });
    }
}