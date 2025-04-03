package com.example.codebrains;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.codebrains.model.JobController;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DetailsFragment extends Fragment {

    private TextView tvTitle, tvDescription, tvBudget, tvDeadline, tvBids, tvStatus, tvPostedDate;
    private ChipGroup chipGroupSkills;
    private DatabaseReference databaseReference;

    public DetailsFragment() {
        // Required empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details, container, false);

        // Initialize UI elements
        tvTitle = view.findViewById(R.id.tvTitle);
        tvDescription = view.findViewById(R.id.tvDescription);
        chipGroupSkills = view.findViewById(R.id.chipGroupSkills);
        tvBudget = view.findViewById(R.id.tvBudget);
        tvDeadline = view.findViewById(R.id.tvDeadline);
        tvBids = view.findViewById(R.id.tvBids);
        tvStatus = view.findViewById(R.id.tvStatus);
        tvPostedDate = view.findViewById(R.id.tvPostedDate);

        // Get the job ID from arguments
        String jobId = getArguments() != null ? getArguments().getString("jobId") : "";

        if (!jobId.isEmpty()) {
            fetchJobDetails(jobId);
        } else {
            Toast.makeText(getContext(), "No job selected!", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    private void fetchJobDetails(String jobId) {
        databaseReference = FirebaseDatabase.getInstance().getReference("jobs").child(jobId);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    JobController job = snapshot.getValue(JobController.class);
                    if (job != null) {
                        // Set basic text fields
                        tvTitle.setText(job.getJobTitle());
                        tvDescription.setText(job.getJobDescription());
                        tvBudget.setText("$" + job.getBudget());
                        tvDeadline.setText(job.getDeadline());
                        tvBids.setText(job.getNoOfBidsReceived() + " Received");
                        tvStatus.setText(job.getStatus());

                        // Calculate and set posted days ago
                        String postedDate = job.getPostedDate(); // Assuming it's stored in "yyyy-MM-dd" format
                        tvPostedDate.setText(calculateDaysAgo(postedDate));

                        // Set status background color
//                        setStatusBackground(job.getStatus());

                        // Create and add skill chips
                        addSkillChips(job.getPrimarySkill(), job.getAdditionalSkills());
                    }
                } else {
                    Toast.makeText(getContext(), "Job details not found!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to fetch data!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String calculateDaysAgo(String postedDate) {
        try {
            // Parse the date in "dd/MM/yyyy" format
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date posted = sdf.parse(postedDate);
            Date currentDate = Calendar.getInstance().getTime();

            // Calculate difference in days
            long diffInMillis = currentDate.getTime() - posted.getTime();
            long daysAgo = TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS);

            if (daysAgo == 0) {
                return "Today";
            } else if (daysAgo == 1) {
                return "Yesterday";
            } else {
                return daysAgo + " days ago";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Unknown";
        }
    }


//    private void setStatusBackground(String status) {
//        int backgroundColor;
//
//        switch (status.toLowerCase()) {
//            case "open":
//                backgroundColor = Color.parseColor("#4CAF50"); // Green
//                break;
//            case "closed":
//                backgroundColor = Color.parseColor("#F44336"); // Red
//                break;
//            case "in progress":
//                backgroundColor = Color.parseColor("#2196F3"); // Blue
//                break;
//            default:
//                backgroundColor = Color.parseColor("#9E9E9E"); // Grey
//                break;
//        }

        // Ensure tvStatus has a background before setting color
//        if (tvStatus.getBackground() instanceof GradientDrawable) {
//            ((GradientDrawable) tvStatus.getBackground()).setColor(backgroundColor);
//        } else {
//            tvStatus.setBackgroundTintList(ColorStateList.valueOf(backgroundColor));
//        }
//    }

    private void addSkillChips(String primarySkill, String additionalSkills) {
        // Clear existing chips
        chipGroupSkills.removeAllViews();

        // Create a list of all skills
        List<String> allSkills = new ArrayList<>();
        if (primarySkill != null && !primarySkill.trim().isEmpty()) {
            allSkills.add(primarySkill.trim());
        }
        if (additionalSkills != null && !additionalSkills.trim().isEmpty()) {
            allSkills.addAll(Arrays.asList(additionalSkills.split(",")));
        }

        // Add a chip for each skill
        for (String skill : allSkills) {
            String trimmedSkill = skill.trim();
            if (!trimmedSkill.isEmpty()) {
                Chip chip = new Chip(getContext());
                chip.setText(trimmedSkill);
                chip.setChipBackgroundColorResource(R.color.white);
                chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
                chipGroupSkills.addView(chip);
            }
        }
    }
}
