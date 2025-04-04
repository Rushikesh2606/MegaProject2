package com.example.codebrains.messaging;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.codebrains.R;
import com.example.codebrains.model.Connection;
import com.example.codebrains.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Chat extends AppCompatActivity {
    private static final String TAG = "ChatActivity";

    private RecyclerView userRecyclerView;
    private UsersTimeStampAdapter userAdapter;
    private ArrayList<User> userList = new ArrayList<>();
    private ArrayList<Connection> connectionList = new ArrayList<>();
    private ProgressBar progressBar;

    private FirebaseAuth auth;
    private String currentUserId;
    private String profession;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Initialize Firebase Authentication
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            showToast("User not authenticated!");
            finish();
            return;
        }
        currentUserId = auth.getCurrentUser().getUid();

        // Get profession from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        profession = sharedPreferences.getString("profession", "");

        if (profession == null || profession.isEmpty()) {
            showToast("User profession not found. Please re-login.");
            finish();
            return;
        }

        // Initialize UI Components
        userRecyclerView = findViewById(R.id.chatRecyclerView);
        progressBar = findViewById(R.id.progressBar);
        userRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Fetch Data
        fetchConnections();
        fetchUsers();
    }

    /**
     * Fetches user connections from Firebase.
     */
    private void fetchConnections() {
        progressBar.setVisibility(View.VISIBLE);
        connectionList.clear();

        DatabaseReference connectionsRef = FirebaseDatabase.getInstance().getReference("Connections");
        connectionsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot connectionSnapshot : snapshot.getChildren()) {
                        String connectionId = connectionSnapshot.getKey();
                        String client = connectionSnapshot.child("client").getValue(String.class);
                        String freelancer = connectionSnapshot.child("freelancer").getValue(String.class);
                        String jobId = connectionSnapshot.child("jobId").getValue(String.class);
                        String status = connectionSnapshot.child("status").getValue(String.class);

                        if (connectionId != null && client != null && freelancer != null && jobId != null && status != null) {
                            Connection connection = new Connection(connectionId, client, freelancer, jobId, status);
                            connectionList.add(connection);
                        } else {
                            Log.e(TAG, "Invalid connection data found: " + connectionId);
                        }
                    }
                    Log.d(TAG, "Total Connections Fetched: " + connectionList.size());
                } else {
                    showToast("No connections found.");
                }

                // Check if both data sets are fetched
                if (!userList.isEmpty()) {
                    initializeAdapter();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "Failed to load connections: " + error.getMessage());
                showToast("Failed to load connections. Please check your network.");
            }
        });
    }

    /**
     * Fetches users from Firebase.
     */
    private void fetchUsers() {
        progressBar.setVisibility(View.VISIBLE);
        userList.clear();
        Log.d("profession",profession);
        String targetNode = "Client".equals(profession) ? "freelancer" : "user";
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference(targetNode);

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String firstName = dataSnapshot.child("firstName").getValue(String.class);
                        String id = dataSnapshot.getKey();
                        String lastName = dataSnapshot.child("lastName").getValue(String.class);

                        if (id != null && !id.equals(currentUserId)) {
                            User user = new User(id, firstName, lastName, "", 0);
                            userList.add(user);
                            Log.d("id",user.getId());
                        }
                    }
                    Log.d(TAG, "Total Users Fetched: " + userList.size());
                } else {
                    showToast("No users found.");
                }

                // Check if both data sets are fetched
                if (!connectionList.isEmpty()) {
                    initializeAdapter();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "Failed to load users: " + error.getMessage());
                showToast("Failed to load users. Please check your network.");
            }
        });
    }

    /**
     * Initializes the adapter once both user and connection data are fetched.
     */
    private void initializeAdapter() {
        userAdapter = new UsersTimeStampAdapter(userList, connectionList, this, user -> {});
        userRecyclerView.setAdapter(userAdapter);
        progressBar.setVisibility(View.GONE);
    }

    /**
     * Shows a toast message.
     */
    private void showToast(String message) {
        Toast.makeText(Chat.this, message, Toast.LENGTH_SHORT).show();
    }
}