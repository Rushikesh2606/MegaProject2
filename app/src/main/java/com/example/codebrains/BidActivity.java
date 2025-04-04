package com.example.codebrains;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.codebrains.model.Freelancer;
import com.example.codebrains.model.Proposal;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BidActivity extends AppCompatActivity {
    private EditText nameEditText, locationEditText, ratingEditText, descriptionEditText, priceEditText, freelancerIdEditText;
    private String jobId;
    private DatabaseReference proposalsRef, jobsRef;
    private FirebaseAuth mAuth;
    private Freelancer freelancer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bid);

        proposalsRef = FirebaseDatabase.getInstance().getReference("proposals");
        jobsRef = FirebaseDatabase.getInstance().getReference("jobs");
        mAuth = FirebaseAuth.getInstance();

        // Get jobId from intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("JOB_ID")) {
            jobId = intent.getStringExtra("JOB_ID");
        } else {
            Toast.makeText(this, "Job ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initializeViews();
        fetchCurrentUser();
    }

    private void initializeViews() {
        nameEditText = findViewById(R.id.nameEditText);
        locationEditText = findViewById(R.id.locationEditText);
        ratingEditText = findViewById(R.id.ratingEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        priceEditText = findViewById(R.id.priceEditText);
        freelancerIdEditText = findViewById(R.id.freelancerIdEditText);

        Button submitButton = findViewById(R.id.submitButton);
        submitButton.setOnClickListener(v -> submitProposal());
    }

    private void fetchCurrentUser() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("freelancer").child(userId);

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        freelancer = dataSnapshot.getValue(Freelancer.class);
                        if (freelancer != null) {
                            nameEditText.setText(freelancer.getFirstName() + " " + freelancer.getLastName());
                            locationEditText.setText(freelancer.getCountry());
                            ratingEditText.setText(String.valueOf(freelancer.getRating()));
                            descriptionEditText.setText(freelancer.getTagLine());
                            priceEditText.setText("0");
                            freelancerIdEditText.setText(freelancer.getUsername());
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(BidActivity.this, "Error fetching user data", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void submitProposal() {
        String name = nameEditText.getText().toString().trim();
        String location = locationEditText.getText().toString().trim();
        float rating;
        try {
            rating = Float.parseFloat(ratingEditText.getText().toString().trim());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid rating", Toast.LENGTH_SHORT).show();
            return;
        }
        String description = descriptionEditText.getText().toString().trim();
        String price = priceEditText.getText().toString().trim();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }
        String freelancerId = currentUser.getUid();

        String proposalId = proposalsRef.push().getKey();
        // Get the current timestamp as a long value
        long timestamp = System.currentTimeMillis();

        // Updated constructor with the timestamp parameter.
        Proposal proposal = new Proposal(name, location, rating, description, price, jobId, proposalId, freelancerId, timestamp);

        if (proposalId != null) {
            proposalsRef.child(proposalId).setValue(proposal)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            updateNoOfBidsReceived(jobId);
                            Toast.makeText(BidActivity.this, "Proposal submitted", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(BidActivity.this, "Submission failed", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }


    private void updateNoOfBidsReceived(String jobId) {
        DatabaseReference jobRef = jobsRef.child(jobId);
        jobRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    int currentBids = dataSnapshot.child("noOfBidsReceived").getValue(Integer.class);
                    jobRef.child("noOfBidsReceived").setValue(currentBids + 1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BidActivity.this, "Error updating bids", Toast.LENGTH_SHORT).show();
            }
        });
    }
}