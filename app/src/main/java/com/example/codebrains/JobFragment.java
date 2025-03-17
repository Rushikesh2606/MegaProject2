package com.example.codebrains;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class JobFragment extends Fragment implements JobAdapter.OnJobCloseListener {
    private String filter;
    private List<JobController> allJobs = new ArrayList<>();
    private List<JobController> filteredJobs = new ArrayList<>();
    private JobAdapter adapter;
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

        // Initialize sample data
        allJobs.add(new JobController("freelance work", "2025-03-02 17:37:49", "In Progress", 0));
        allJobs.add(new JobController("React Developer", "2025-03-03 08:43:55", "In Progress", 0));
        allJobs.add(new JobController("Video Editor", "2025-03-05 07:37:27", "In Progress", 0));
        allJobs.add(new JobController("Mobile Developer", "2025-03-06 12:38:27", "Open", 2));
        allJobs.add(new JobController("Web Project", "2025-02-05 12:38:27", "Completed", 20));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_job, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        filterJobs();
        adapter = new JobAdapter(filteredJobs, this);
        recyclerView.setAdapter(adapter);

        return view;
    }

    private void filterJobs() {
        filteredJobs.clear();
        for (JobController job : allJobs) {
            if ("All".equalsIgnoreCase(filter)) {
                filteredJobs.add(job);
            } else if (job.getStatus().equalsIgnoreCase(filter)) {
                filteredJobs.add(job);
            }
        }
    }

    public void handleJobClosed(int position) {
        if (position >= 0 && position < allJobs.size()) {
            // Remove from main list
            JobController removedJob = allJobs.remove(position);

            // Remove from filtered list if present
            int filteredPosition = filteredJobs.indexOf(removedJob);
            if (filteredPosition != -1) {
                filteredJobs.remove(filteredPosition);
                adapter.notifyItemRemoved(filteredPosition);
            }
        }
    }

    @Override
    public void onJobClosed(int position) {
        if (closeListener != null) {
            closeListener.onJobClosed(position);
        }
        handleJobClosed(position);
    }

    // For updating data from outside
    public void updateJobs(List<JobController> newJobs) {
        allJobs.clear();
        allJobs.addAll(newJobs);
        filterJobs();
        adapter.notifyDataSetChanged();
    }
}