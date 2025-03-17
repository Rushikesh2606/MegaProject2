package com.example.codebrains;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

//import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private TextView name, email, mobile, gender, dob, total, completed, pending, welcome, in_progress;
    private CircleImageView profileImage;
    private ProgressBar progressBar;
    private Button edit_profile_button;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
//    private NestedScrollView scrollView;
private LinearLayout linear;
ScrollView scroll;
    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        String currentUserId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;

        if (currentUserId == null) {
            Toast.makeText(getContext(), "User not authenticated", Toast.LENGTH_SHORT).show();
            return view;
        }
        Toast.makeText(getContext(), currentUserId, Toast.LENGTH_SHORT).show();

        userRef = FirebaseDatabase.getInstance().getReference("user").child(currentUserId);

        // Initialize views
        initializeViews(view);

        // Hide profile content & show progress bar
        scroll.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        // Fetch data from Firebase
        fetchClientData();

        edit_profile_button.setOnClickListener(View -> editProfile());

        return view;
    }

    private void editProfile() {
        FragmentManager fm = requireActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.nav_host_fragment_content_homepage, new Edit_profile_client());
        ft.commit();
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
//        profileImage = view.findViewById(R.id.profile_image);
        progressBar = view.findViewById(R.id.progress_bar);
        in_progress = view.findViewById(R.id.in_progress);
        edit_profile_button = view.findViewById(R.id.edit_profile_button);
        linear=view.findViewById(R.id.linear);
        scroll=view.findViewById(R.id.scroll);
    }

    private void fetchClientData() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Toast.makeText(getContext(), "User data not found", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                Log.d("FirebaseData", snapshot.toString());

                // Map data to Client object
                client client = snapshot.getValue(client.class);

                if (client != null && getActivity() != null) {
                    name.setText("Name : "+getValidString(client.getFirstName()) + " " + getValidString(client.getLastName()));
                    email.setText("Email : "+getValidString(client.getEmail()));
                    mobile.setText("Contact : "+getValidString(client.getContactNo()));
                    gender.setText("Gender : "+getValidString(client.getGender()));
                    dob.setText("DOB : "+getValidString(client.getDob()));
                    completed.setText(String.valueOf(client.getCompleted()));
                    pending.setText(String.valueOf(client.getPending()));
                    welcome.setText("Welcome , " + client.getFirstName());
                    total.setText(String.valueOf(client.getTotal_jobs()));
                    in_progress.setText( String.valueOf(client.getIn_progress()));

                    progressBar.setVisibility(View.GONE);
                    scroll.setVisibility(View.VISIBLE);
                    Toast.makeText(getContext(), "Profile data loaded successfully", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Failed to load data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }


    private String getValidString(String value) {
        return value != null ? value : "N/A";
    }
}
