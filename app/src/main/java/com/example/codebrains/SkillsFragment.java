package com.example.codebrains;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.widget.AutoCompleteTextView;

public class SkillsFragment extends Fragment {

    private TextInputEditText primarySkillEdit, additionalSkillsEdit;
    private AutoCompleteTextView experienceLevelDropdown;
    private MaterialButton previousButton, nextButton;
    private DatabaseReference databaseReference;
    private String jobId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_skills, container, false);

        primarySkillEdit = view.findViewById(R.id.primary_skill);
        additionalSkillsEdit = view.findViewById(R.id.additional_skills);
        experienceLevelDropdown = view.findViewById(R.id.experience_level);
        previousButton = view.findViewById(R.id.previous_button);
        nextButton = view.findViewById(R.id.next_button);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                getResources().getStringArray(R.array.experience_levels));
        experienceLevelDropdown.setAdapter(adapter);

        previousButton.setOnClickListener(v -> navigateToPrevious());
        nextButton.setOnClickListener(v -> navigateToNext());

        // Retrieve data from JobDetailsFragment
        Bundle bundle = getArguments();
        if (bundle != null) {
            jobId = bundle.getString("id");
            String jobTitle = bundle.getString("title");
            String jobDescription = bundle.getString("desc");
            String jobCategory = bundle.getString("category");
            String fileBase64 = bundle.getString("fileBase64");

            // Use the data as needed (e.g., display or store it)
            Toast.makeText(requireContext(), "Job Title: " + jobTitle, Toast.LENGTH_SHORT).show();
        }

        // Initialize Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("jobs");

        return view;
    }

    private void navigateToPrevious() {
        getParentFragmentManager().popBackStack();
    }

    private void navigateToNext() {
        if (validateInputs()) {
            // Collect data from SkillsFragment
            String primarySkill = primarySkillEdit.getText().toString().trim();
            String additionalSkills = additionalSkillsEdit.getText().toString().trim();
            String experienceLevel = experienceLevelDropdown.getText().toString().trim();

            // Update the job in Firebase Realtime Database
            if (jobId != null) {
                databaseReference.child(jobId).child("primarySkill").setValue(primarySkill)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                databaseReference.child(jobId).child("additionalSkills").setValue(additionalSkills)
                                        .addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {
                                                databaseReference.child(jobId).child("experienceLevel").setValue(experienceLevel)
                                                        .addOnCompleteListener(task2 -> {
                                                            if (task2.isSuccessful()) {
                                                                // Navigate to the next fragment (BudgetFragment2)
                                                                BudgetFragment2 budgetFragment2 = new BudgetFragment2();
                                                                Bundle bundle = new Bundle();
                                                                bundle.putString("id", jobId);
                                                                budgetFragment2.setArguments(bundle);

                                                                getParentFragmentManager().beginTransaction()
                                                                        .replace(R.id.fragment_container, budgetFragment2)
                                                                        .addToBackStack(null)
                                                                        .commit();
                                                            } else {
                                                                Toast.makeText(requireContext(), "Failed to update experience level", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                            } else {
                                                Toast.makeText(requireContext(), "Failed to update additional skills", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            } else {
                                Toast.makeText(requireContext(), "Failed to update primary skill", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(requireContext(), "Job ID is missing", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean validateInputs() {
        if (primarySkillEdit.getText().toString().trim().isEmpty()) {
            primarySkillEdit.setError("Primary skill is required");
            return false;
        }
        if (experienceLevelDropdown.getText().toString().trim().isEmpty()) {
            experienceLevelDropdown.setError("Experience level is required");
            return false;
        }
        return true;
    }
}