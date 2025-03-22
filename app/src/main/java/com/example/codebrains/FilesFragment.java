package com.example.codebrains;

import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;

public class FilesFragment extends Fragment {

    private Button downloadButton;
    private ProgressBar progressBar;
    private DatabaseReference databaseReference;
    private String jobId;
    private String fileBase64;

    private static final int PERMISSION_REQUEST_CODE = 1;
    private DownloadManager downloadManager;
    private long downloadId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_files, container, false);

        // Initialize UI components
        downloadButton = view.findViewById(R.id.downloadButton);
        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        // Get job ID from Intent
        if (getActivity() != null && getActivity().getIntent().hasExtra("jobId")) {
            jobId = getActivity().getIntent().getStringExtra("jobId");
        }

        if (jobId != null) {
            fetchFileBase64();
        } else {
            Toast.makeText(getContext(), "Job ID not found", Toast.LENGTH_SHORT).show();
        }

        downloadButton.setOnClickListener(v -> {
            if (fileBase64 != null) {
                if (checkStoragePermission()) {
                    downloadDocument();
                }
            } else {
                Toast.makeText(getContext(), "No file available for download", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void fetchFileBase64() {
        if (databaseReference == null) {
            databaseReference = FirebaseDatabase.getInstance().getReference("jobs").child(jobId);
        }

        databaseReference.child("attachments").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressBar.setVisibility(View.GONE);

                if (snapshot.exists()) {
                    Object value = snapshot.getValue();
                    if (value instanceof String) {
                        fileBase64 = (String) value;
                        downloadButton.setVisibility(View.VISIBLE);
                        Toast.makeText(getContext(), "File ready for download", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Invalid data type for attachment (expected String, got " + value.getClass().getSimpleName() + ")", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "No attachment found at path: jobs/" + jobId + "/attachment", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error code " + error.getCode() + ": " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void downloadDocument() {
        if (getContext() == null) return;

        progressBar.setVisibility(View.VISIBLE);
        downloadButton.setEnabled(false);

        // Create a unique filename for the document
        String filename = "attachment_" + System.currentTimeMillis() + ".doc";
        File fileDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), filename);

        try (FileOutputStream fos = new FileOutputStream(fileDir)) {
            byte[] fileBytes = Base64.getDecoder().decode(fileBase64);
            fos.write(fileBytes);
            fos.flush();

            // Show download complete message
            Toast.makeText(getContext(), "File downloaded successfully to: " + fileDir.getAbsolutePath(), Toast.LENGTH_LONG).show();

            // Hide progress bar and re-enable button
            progressBar.setVisibility(View.GONE);
            downloadButton.setEnabled(true);

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Failed to save file: " + e.getMessage(), Toast.LENGTH_SHORT).show();

            // Hide progress bar and re-enable button
            progressBar.setVisibility(View.GONE);
            downloadButton.setEnabled(true);
        }
    }

    private boolean checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (fileBase64 != null) {
                    downloadDocument();
                }
            } else {
                Toast.makeText(getContext(), "Permission denied", Toast.LENGTH_SHORT).show();

                // Hide progress bar and re-enable button
                progressBar.setVisibility(View.GONE);
                downloadButton.setEnabled(true);
            }
        }
    }
}