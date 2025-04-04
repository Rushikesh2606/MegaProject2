package com.example.codebrains;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.widget.AutoCompleteTextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ReviewSubmitFragment extends Fragment {

    private AutoCompleteTextView locationPreferenceDropdown;
    private TextInputEditText additionalQuestionsEditText;
    private MaterialButton previousButton, submitButton;

    // Firebase Database Reference
    private DatabaseReference databaseReference;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private String jobId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_review_submit, container, false);

        // Initialize Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("jobs");

        // Initialize UI elements
        locationPreferenceDropdown = view.findViewById(R.id.spinner_location_preference);
        additionalQuestionsEditText = view.findViewById(R.id.edittext_additional_questions);
        previousButton = view.findViewById(R.id.button_previous);
        submitButton = view.findViewById(R.id.button_submit);

        // Setup the AutoCompleteTextView dropdown with location options
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                getResources().getStringArray(R.array.location_preferences));
        locationPreferenceDropdown.setAdapter(adapter);

        // Handle "PREVIOUS" button click
        previousButton.setOnClickListener(v -> navigateToPrevious());

        // Handle "SUBMIT" button click
        submitButton.setOnClickListener(v -> handleSubmit());

        // Retrieve data from previous fragments (BudgetFragment2)
        Bundle bundle = getArguments();
        if (bundle != null) {
            jobId = bundle.getString("id");
            if (jobId == null) {
                Toast.makeText(requireContext(), "Error: Missing job ID", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(requireContext(), "Error: Missing data from previous fragments", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    private void navigateToPrevious() {
        // Navigate back to the previous fragment (BudgetFragment2)
        getParentFragmentManager().popBackStack();
    }

    private void handleSubmit() {
        if (validateInputs()) {
            // Retrieve data from the Bundle
            Bundle bundle = getArguments();
            if (bundle != null) {
                // Collect data from the current fragment
                String locationPreference = locationPreferenceDropdown.getText().toString().trim();
                String additionalQuestions = additionalQuestionsEditText.getText().toString().trim();

                // Get the current date as the posted date
                String postedDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());

                // Update the job in Firebase Realtime Database
                if (jobId != null) {
                    databaseReference.child(jobId).child("locationPreference").setValue(locationPreference)
                            .addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    databaseReference.child(jobId).child("additionalQuestions").setValue(additionalQuestions)
                                            .addOnCompleteListener(task2 -> {
                                                if (task2.isSuccessful()) {
                                                    databaseReference.child(jobId).child("postedDate").setValue(postedDate)
                                                            .addOnCompleteListener(task3 -> {
                                                                if (task3.isSuccessful()) {
                                                                    // Update total_jobs for the user
                                                                    updateTotalJobsForUser();

                                                                    // Navigate to ProfileFragment
                                                                    navigateToProfileFragment();
                                                                } else {
                                                                    Toast.makeText(requireContext(), "Failed to update posted date", Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                } else {
                                                    Toast.makeText(requireContext(), "Failed to update additional questions", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                } else {
                                    Toast.makeText(requireContext(), "Failed to update location preference", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(requireContext(), "Job ID is missing", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(requireContext(), "Error: Missing data from previous fragments", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateTotalJobsForUser() {
        // Get the current user ID
        String currentUserId = auth.getCurrentUser().getUid();
        if (currentUserId != null) {
            // Get the current total_jobs value
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("user").child(currentUserId);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        client client = snapshot.getValue(client.class);
                        if (client != null) {
                            int totalJobs = client.getTotal_jobs();
                            totalJobs++; // Increment total_jobs

                            // Update the total_jobs field in the database
                            userRef.child("total_jobs").setValue(totalJobs)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            Log.d("ReviewSubmitFragment", "Total jobs updated successfully");
                                        } else {
                                            Log.e("ReviewSubmitFragment", "Failed to update total jobs: " + task.getException());
                                        }
                                    });
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("ReviewSubmitFragment", "Failed to read user data: " + error.getMessage());
                }
            });
        }
    }

    private void navigateToProfileFragment() {
        // Navigate to ProfileFragment
        Intent i=new Intent(getContext(),Homepage.class);
        startActivity(i);
    }
    private boolean validateInputs() {
        String locationPreference = locationPreferenceDropdown.getText().toString().trim();
        if (locationPreference.isEmpty() || locationPreference.equalsIgnoreCase("Select Location")) {
            Toast.makeText(requireContext(), "Please select a valid location preference", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}