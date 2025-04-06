package com.example.codebrains;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.codebrains.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private TextView name, email, mobile, gender, dob, total, completed, pending, welcome, in_progress;
    private CircleImageView profileImage;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    private String currentUserId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        initializeViews(view);
        checkAuthenticationState();

        return view;
    }

    private void initializeViews(View view) {
        name = view.findViewById(R.id.name);
        email = view.findViewById(R.id.email);
        mobile = view.findViewById(R.id.mobile);
        gender = view.findViewById(R.id.gender);
        dob = view.findViewById(R.id.dob);
        total = view.findViewById(R.id.total);
        completed = view.findViewById(R.id.completed);
        pending = view.findViewById(R.id.pending);
        welcome = view.findViewById(R.id.welcome_message);
        profileImage = view.findViewById(R.id.profile_image);
        progressBar = view.findViewById(R.id.progress_bar);
        in_progress = view.findViewById(R.id.in_progress);
    }

    private void checkAuthenticationState() {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            redirectToLogin();
        } else {
            currentUserId = currentUser.getUid();
            setupDatabaseReference();
            fetchUserData();
        }
    }

    private void setupDatabaseReference() {
        userRef = FirebaseDatabase.getInstance().getReference("user").child(currentUserId);
    }

    private void fetchUserData() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {

                    showDataNotFound();
                    return;
                }


                User user = snapshot.getValue(User.class);
                Log.d("hi",user.getFirstName());
                if (user != null && isAdded()) {
                    updateUI(user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                handleDatabaseError(error);
            }
        });
    }

    private void updateUI(User user) {
        name.setText(formatName(user.getFirstName(), user.getLastName()));
        email.setText(formatField("Email", user.getEmail()));
        mobile.setText(formatField("Contact", user.getContactNo()));
        gender.setText(formatField("Gender", user.getGender()));
        dob.setText(formatField("DOB", user.getDob()));

        completed.setText(String.valueOf(user.getCompleted()));
        pending.setText(String.valueOf(user.getPending()));
        total.setText(String.valueOf(user.getTotal_jobs()));
        in_progress.setText(String.valueOf(user.getIn_progress()));

        welcome.setText(getWelcomeMessage(user.getFirstName()));
        loadProfileImage(user.getProfileImage());

        progressBar.setVisibility(View.GONE);
    }

    private String formatName(String firstName, String lastName) {
        return String.format("%s %s",
                getValidString(firstName),
                getValidString(lastName));
    }

    private String formatField(String label, String value) {
        return String.format("%s: %s", label, getValidString(value));
    }

    private String getWelcomeMessage(String firstName) {
        return String.format("Welcome, %s!", getValidString(firstName));
    }

    private void loadProfileImage(String base64Image) {
        if (base64Image != null && !base64Image.isEmpty()) {
            try {
                byte[] decodedBytes = Base64.decode(base64Image, Base64.DEFAULT);
                Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                profileImage.setImageBitmap(decodedBitmap);
            } catch (IllegalArgumentException e) {
                       }
        }
    }

    private void redirectToLogin() {
        if (getActivity() != null) {
            Toast.makeText(getContext(), "Authentication required", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getActivity(), login.class));
            getActivity().finish();
        }
    }

    private void showDataNotFound() {
        if (getActivity() != null) {
            Toast.makeText(getContext(), "User data not found", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        }
    }

    private void handleDatabaseError(DatabaseError error) {
        if (getActivity() != null) {
            Toast.makeText(getContext(), "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        }
    }

    private String getValidString(String value) {
        return (value != null && !value.isEmpty()) ? value : "N/A";
    }

    @Override
    public void onStart() {
        super.onStart();
        // Add authentication state listener
        mAuth.addAuthStateListener(authStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        // Remove authentication state listener
        if (mAuth != null) {
            mAuth.removeAuthStateListener(authStateListener);
        }
    }

    private FirebaseAuth.AuthStateListener authStateListener = firebaseAuth -> {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            redirectToLogin();
        }
    };
}