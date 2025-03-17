package com.example.codebrains;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class BudgetFragment2 extends Fragment {

    private TextInputEditText budgetAmountEdit, deadlineEdit;
    private AutoCompleteTextView budgetTypeDropdown, projectVisibilityDropdown;
    private MaterialButton previousButton, nextButton;
    private DatabaseReference databaseReference;
    private String jobId;

    public BudgetFragment2() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_budget2, container, false);

        // Initialize views from XML
        budgetAmountEdit = view.findViewById(R.id.budget_amount);
        deadlineEdit = view.findViewById(R.id.deadline);
        budgetTypeDropdown = view.findViewById(R.id.budget_type_spinner);
        projectVisibilityDropdown = view.findViewById(R.id.project_visibility_spinner);
        previousButton = view.findViewById(R.id.previous_button);
        nextButton = view.findViewById(R.id.next_button);

        // Setup dropdown for Budget Type
        ArrayAdapter<String> budgetTypeAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                getResources().getStringArray(R.array.budget_types));
        budgetTypeDropdown.setAdapter(budgetTypeAdapter);

        // Setup dropdown for Project Visibility
        ArrayAdapter<String> visibilityAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                getResources().getStringArray(R.array.project_visibility_options));
        projectVisibilityDropdown.setAdapter(visibilityAdapter);

        // Set click listeners for navigation buttons
        previousButton.setOnClickListener(v -> navigateToPrevious());
        nextButton.setOnClickListener(v -> navigateToNext());

        // Retrieve data from previous fragments (JobDetailsFragment and SkillsFragment)
        Bundle bundle = getArguments();
        if (bundle != null) {
            jobId = bundle.getString("id");
            if (jobId == null) {
                Toast.makeText(requireContext(), "Error: Missing job ID", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(requireContext(), "Error: Missing data from previous fragments", Toast.LENGTH_SHORT).show();
        }

        // Initialize Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("jobs");

        return view;
    }

    private void navigateToPrevious() {
        // Navigate back to the previous fragment (SkillsFragment)
        getParentFragmentManager().popBackStack();
    }

    private void navigateToNext() {
        if (validateInputs()) {
            // Collect data from BudgetFragment2
            String budgetAmount = budgetAmountEdit.getText().toString().trim();
            String deadline = deadlineEdit.getText().toString().trim();
            String budgetType = budgetTypeDropdown.getText().toString().trim();
            String projectVisibility = projectVisibilityDropdown.getText().toString().trim();

            // Update the job in Firebase Realtime Database
            if (jobId != null) {
                databaseReference.child(jobId).child("budgetAmount").setValue(budgetAmount)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                databaseReference.child(jobId).child("deadline").setValue(deadline)
                                        .addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {
                                                databaseReference.child(jobId).child("budgetType").setValue(budgetType)
                                                        .addOnCompleteListener(task2 -> {
                                                            if (task2.isSuccessful()) {
                                                                databaseReference.child(jobId).child("projectVisibility").setValue(projectVisibility)
                                                                        .addOnCompleteListener(task3 -> {
                                                                            if (task3.isSuccessful()) {
                                                                                // Navigate to the next fragment (ReviewSubmitFragment)
                                                                                ReviewSubmitFragment reviewSubmitFragment = new ReviewSubmitFragment();
                                                                                Bundle reviewBundle = new Bundle();
                                                                                reviewBundle.putString("id", jobId);
                                                                                reviewSubmitFragment.setArguments(reviewBundle);

                                                                                getParentFragmentManager().beginTransaction()
                                                                                        .replace(R.id.fragment_container, reviewSubmitFragment)
                                                                                        .addToBackStack(null)
                                                                                        .commit();
                                                                            } else {
                                                                                Toast.makeText(requireContext(), "Failed to update project visibility", Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        });
                                                            } else {
                                                                Toast.makeText(requireContext(), "Failed to update budget type", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                            } else {
                                                Toast.makeText(requireContext(), "Failed to update deadline", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            } else {
                                Toast.makeText(requireContext(), "Failed to update budget amount", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(requireContext(), "Job ID is missing", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean validateInputs() {
        String budgetAmount = budgetAmountEdit.getText().toString().trim();
        String deadline = deadlineEdit.getText().toString().trim();
        String budgetType = budgetTypeDropdown.getText().toString().trim();
        String projectVisibility = projectVisibilityDropdown.getText().toString().trim();

        if (budgetAmount.isEmpty()) {
            budgetAmountEdit.setError("Budget amount is required");
            return false;
        }
        if (deadline.isEmpty()) {
            deadlineEdit.setError("Deadline is required");
            return false;
        }
        if (budgetType.isEmpty() || budgetType.equalsIgnoreCase("Select Budget Type")) {
            Toast.makeText(requireContext(), "Please select a valid budget type", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (projectVisibility.isEmpty() || projectVisibility.equalsIgnoreCase("Select Visibility")) {
            Toast.makeText(requireContext(), "Please select a valid project visibility", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}