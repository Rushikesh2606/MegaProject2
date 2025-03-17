package com.example.codebrains;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
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

public class developer_profile extends Fragment {

    private TextView name, email, mobile, gender, dob, skills, total, tagline, completed, pending,welcome;
    private CircleImageView profileImage;
    private ProgressBar progressBar;
    Button edit_profile_button;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    private NestedScrollView scrollView;

    public developer_profile() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_developer_profile, container, false);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        String currentUserId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;

        if (currentUserId == null) {
            Toast.makeText(getContext(), "User not authenticated", Toast.LENGTH_SHORT).show();
            return view;
        }

        userRef = FirebaseDatabase.getInstance().getReference("freelancer").child(currentUserId);

        // Initialize views
        initializeViews(view);
        // Hide profile content & show progress bar
        scrollView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        // Fetch data from Firebase
        fetchDeveloperData();

//        edit profile
        edit_profile_button.setOnClickListener(View -> editProfile());

        return view;
    }

    private void editProfile() {
        FragmentManager fm = requireActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.nav_host_fragment_content_homepage_developer, new Edit_profile_freelancer());
        ft.commit();

    }

    private void initializeViews(View view) {
        name = view.findViewById(R.id.name);
        email = view.findViewById(R.id.email);
        mobile = view.findViewById(R.id.mobile);
        gender = view.findViewById(R.id.gender);
        dob = view.findViewById(R.id.dob);
        skills = view.findViewById(R.id.skills);
        total = view.findViewById(R.id.total);
        tagline = view.findViewById(R.id.tagline);
        completed = view.findViewById(R.id.completed);
        pending = view.findViewById(R.id.pending);
        welcome=view.findViewById(R.id.welcome_message);
        profileImage = view.findViewById(R.id.profile_image);
        progressBar = view.findViewById(R.id.progress_bar);
        scrollView = view.findViewById(R.id.profile);

        edit_profile_button=view.findViewById(R.id.edit_profile_button);
    }

    private void fetchDeveloperData() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Toast.makeText(getContext(), "User data not found", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                Log.d("FirebaseData", snapshot.toString());

                // Map data to Freelancer object
                freelancer freelancer = snapshot.getValue(freelancer.class);

                if (freelancer != null && getActivity() != null) {
                    name.setText(getValidString(freelancer.getFirstName()) + " " + getValidString(freelancer.getLastName()));
                    email.setText(getValidString(freelancer.getEmail()));
                    mobile.setText(getValidString(freelancer.getContactNo()));
                    gender.setText(getValidString(freelancer.getGender()));
                    dob.setText(getValidString(freelancer.getDob()));
                    skills.setText("Skills: " + getValidString(freelancer.getSkills()));
                    total.setText("Total Jobs: " + freelancer.getTotal_jobs());
                    tagline.setText("Tagline: " + getValidString(freelancer.getTagLine()));
                    completed.setText(String.valueOf(freelancer.getCompleted()));
                    pending.setText(String.valueOf(freelancer.getPending()));
                    welcome.setText("Welcome , "+freelancer.getFirstName());
                    // Hide progress bar & show profile content
                    progressBar.setVisibility(View.GONE);
                    scrollView.setVisibility(View.VISIBLE);

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
