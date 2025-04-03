package com.example.codebrains;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.codebrains.model.Proposal;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class BidActivity extends AppCompatActivity {

    private EditText nameEditText;
    private EditText locationEditText;
    private EditText ratingEditText;
    private EditText descriptionEditText;
    private EditText priceEditText;
    private EditText freelancerIdEditText;
    private String jobId;
    private DatabaseReference proposalsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bid);

        // Initialize Firebase Database reference
        proposalsRef = FirebaseDatabase.getInstance().getReference("proposals");

        // Get the jobId from the intent
        Intent intent = getIntent();
        jobId = intent.getStringExtra("jobId");

        // Initialize UI elements
        nameEditText = findViewById(R.id.nameEditText);
        locationEditText = findViewById(R.id.locationEditText);
        ratingEditText = findViewById(R.id.ratingEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        priceEditText = findViewById(R.id.priceEditText);
        freelancerIdEditText = findViewById(R.id.freelancerIdEditText);

        Button submitButton = findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitProposal();
            }
        });
    }

    private void submitProposal() {
        // Get user input
        String name = nameEditText.getText().toString().trim();
        String location = locationEditText.getText().toString().trim();
        float rating = 0.0f;
        try {
            rating = Float.parseFloat(ratingEditText.getText().toString().trim());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid rating", Toast.LENGTH_SHORT).show();
            return;
        }
        String description = descriptionEditText.getText().toString().trim();
        String price = priceEditText.getText().toString().trim();
        String freelancerId = freelancerIdEditText.getText().toString().trim();

        // Generate a unique proposalId
        String proposalId = proposalsRef.push().getKey();

        // Create a new Proposal object
        Proposal proposal = new Proposal(name, location, rating, description, price, jobId, proposalId, freelancerId);

        // Store the proposal in Firebase Realtime Database
        if (proposalId != null) {
            proposalsRef.child(proposalId).setValue(proposal)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(BidActivity.this, "Proposal submitted successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(BidActivity.this, "Failed to submit proposal", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}