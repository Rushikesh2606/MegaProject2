package com.example.codebrains.reevaluation;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.codebrains.R;
import com.example.codebrains.model.Reevaluation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class ReevaluationListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Reevaluation> reevaluationList;
    private ReevaluationAdapter adapter;
    private DatabaseReference reevaluationsRef;

    private String profession, currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reevaluation_list);

        recyclerView = findViewById(R.id.recyclerViewReevaluations);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        reevaluationList = new ArrayList<>();
        adapter = new ReevaluationAdapter(reevaluationList, this);
        recyclerView.setAdapter(adapter);

        // Load profession from SharedPreferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        profession = preferences.getString("profession", "");

        // Get current user ID from FirebaseAuth
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            currentUserId = currentUser.getUid();
        } else {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Get Firebase reference to ReEvaluations node
        reevaluationsRef = FirebaseDatabase.getInstance().getReference("ReEvaluations");

        // Listen for reevaluation changes
        reevaluationsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                reevaluationList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Reevaluation reevaluation = data.getValue(Reevaluation.class);

                    if (reevaluation != null) {
                        if ("freelancer".equalsIgnoreCase(profession)) {
                            // Only show reevaluations assigned to this freelancer
                            if (reevaluation.getFreelancer() != null &&
                                    reevaluation.getFreelancer().equals(currentUserId)) {
                                reevaluationList.add(reevaluation);
                            }
                        } else {
                            // Clients or admins can see all
                            reevaluationList.add(reevaluation);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ReevaluationListActivity.this, "Failed to load reevaluations.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
