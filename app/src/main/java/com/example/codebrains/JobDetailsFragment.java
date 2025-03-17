package com.example.codebrains;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

public class JobDetailsFragment extends Fragment {

    // Views from the layout
    private TextInputEditText jobTitleEdit, jobDescriptionEdit;
    private AutoCompleteTextView jobCategoryAutoComplete;
    private TextView fileNameText;
    private MaterialButton chooseFileButton, nextButton;

    // Request code for file picker
    private static final int FILE_PICKER_REQUEST_CODE = 1;

    // Progress bar for file conversion
    private View progressBar;
    DatabaseReference databaseReference;

    // Uri for the selected file
    private Uri selectedFileUri;
    FirebaseAuth auth;
    String id;

    public JobDetailsFragment() {
        // Required empty constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_job_details, container, false);

        databaseReference = FirebaseDatabase.getInstance().getReference("jobs");

        // Initialize views from XML
        jobTitleEdit = view.findViewById(R.id.job_title);
        jobDescriptionEdit = view.findViewById(R.id.job_description);
        jobCategoryAutoComplete = view.findViewById(R.id.job_category);
        fileNameText = view.findViewById(R.id.file_name);
        chooseFileButton = view.findViewById(R.id.choose_file);
        nextButton = view.findViewById(R.id.next_button);
        progressBar = view.findViewById(R.id.progress_bar); // Ensure you have a ProgressBar in your XML
        auth = FirebaseAuth.getInstance();

        // Setup the Job Category dropdown using an ArrayAdapter.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                getResources().getStringArray(R.array.job_categories));
        jobCategoryAutoComplete.setAdapter(adapter);

        // Setup click listener for file selection
        chooseFileButton.setOnClickListener(v -> openFilePicker());

        // Setup click listener for the NEXT button
        nextButton.setOnClickListener(v -> navigateToSkills());

        return view;
    }

    // Launches a file picker to select any file
    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, FILE_PICKER_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_PICKER_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            selectedFileUri = data.getData(); // Store the selected file Uri
            if (selectedFileUri != null) {
                String fileName = getFileName(selectedFileUri);
                fileNameText.setText(fileName);
            }
        }
    }

    // Retrieves the display name of the selected file
    @SuppressLint("Range")
    private String getFileName(Uri uri) {
        String result = null;
        if ("content".equals(uri.getScheme())) {
            try (Cursor cursor = requireActivity().getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            if (result != null) {
                int cut = result.lastIndexOf('/');
                if (cut != -1) {
                    result = result.substring(cut + 1);
                }
            }
        }
        return result;
    }

    // Validates user input; you can extend this as needed.
    private boolean validateInputs() {
        if (jobTitleEdit.getText().toString().trim().isEmpty()) {
            jobTitleEdit.setError("Job title is required");
            return false;
        }
        if (jobDescriptionEdit.getText().toString().trim().isEmpty()) {
            jobDescriptionEdit.setError("Job description is required");
            return false;
        }
        if (jobCategoryAutoComplete.getText().toString().trim().isEmpty()) {
            jobCategoryAutoComplete.setError("Job category is required");
            return false;
        }
        return true;
    }

    // Navigates to the next fragment (e.g., SkillsFragment) if inputs are valid.
    private void navigateToSkills() {
        if (validateInputs()) {
            if (selectedFileUri == null) {
                Toast.makeText(requireContext(), "No file selected", Toast.LENGTH_SHORT).show();
                return;
            }

            // Show progress bar
            progressBar.setVisibility(View.VISIBLE);

            // Convert the selected file to Base64
            new Thread(() -> {
                String encodedFile = null;
                try {
                    // Read the file content from the Uri
                    InputStream inputStream = requireActivity().getContentResolver().openInputStream(selectedFileUri);
                    byte[] fileBytes = new byte[inputStream.available()];
                    inputStream.read(fileBytes);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        encodedFile = Base64.getEncoder().encodeToString(fileBytes);
                    } else {
                        encodedFile = android.util.Base64.encodeToString(fileBytes, android.util.Base64.DEFAULT);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    requireActivity().runOnUiThread(() -> {
                        // Hide progress bar
                        progressBar.setVisibility(View.GONE);

                        Toast.makeText(requireContext(), "Failed to convert file to Base64", Toast.LENGTH_SHORT).show();
                    });
                    return;
                }

                // Run on UI thread to navigate to the next fragment
                String finalEncodedFile = encodedFile;
                requireActivity().runOnUiThread(() -> {
                    // Hide progress bar
                    progressBar.setVisibility(View.GONE);

                    if (finalEncodedFile != null) {
                        // Generate a unique ID for the job posting
                        String Uid = auth.getCurrentUser().getUid();
                        id = databaseReference.push().getKey();

                        // Create a JobController object
                        JobController job = new JobController(
                                finalEncodedFile,
                                jobDescriptionEdit.getText().toString(),
                                jobCategoryAutoComplete.getText().toString(),
                                jobTitleEdit.getText().toString(),
                                Uid,
                                id
                        );

                        if (id != null) {
                            databaseReference.child(id).setValue(job)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            if (isAdded()) {
                                                Toast.makeText(requireContext(), "Job Posted Successfully!", Toast.LENGTH_SHORT).show();

                                                // Navigate to the next fragment (SkillsFragment)
                                                SkillsFragment skillsFragment = new SkillsFragment();
                                                Bundle bundle = new Bundle();
                                                bundle.putString("id", id);
                                                bundle.putString("title", jobTitleEdit.getText().toString());
                                                bundle.putString("desc", jobDescriptionEdit.getText().toString());
                                                bundle.putString("category", jobCategoryAutoComplete.getText().toString());
                                                bundle.putString("fileBase64", finalEncodedFile);
                                                skillsFragment.setArguments(bundle);

                                                getParentFragmentManager().beginTransaction()
                                                        .replace(R.id.fragment_container, skillsFragment)
                                                        .addToBackStack(null)
                                                        .commit();
                                            }
                                        } else {
                                            if (isAdded()) {
                                                Toast.makeText(requireContext(), "Failed to post job. Please try again.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    } else {
                        if (isAdded()) {
                            Toast.makeText(requireContext(), "File conversion failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }).start();
        }
    }
}