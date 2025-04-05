package com.example.codebrains;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.codebrains.model.Proposal;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProposalsFragment extends Fragment {
    private RecyclerView recyclerView;
    private ProposalsAdapter proposalsAdapter;
    private List<Proposal> proposalsList;
    private DatabaseReference proposalsRef;
    private String jobId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            jobId = getArguments().getString("jobId");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_proposals, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        proposalsList = new ArrayList<>();
        proposalsAdapter = new ProposalsAdapter(proposalsList,getContext());
        recyclerView.setAdapter(proposalsAdapter);

        proposalsRef = FirebaseDatabase.getInstance().getReference("proposals");

        if (jobId != null && !jobId.isEmpty()) {
            fetchProposalsForJob();
        } else {
            // Handle case where jobId is not available
        }

        return view;
    }

    private void fetchProposalsForJob() {
        Query query = proposalsRef.orderByChild("jobId").equalTo(jobId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                proposalsList.clear();
                Map<String, Proposal> uniqueFreelancerProposals = new HashMap<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Proposal proposal = snapshot.getValue(Proposal.class);
                    if (proposal != null) {
                        // Only keep one proposal per freelancer (the latest one)
                        uniqueFreelancerProposals.put(proposal.getFreelancerId(), proposal);
                    }
                }

                // Add all unique proposals to the list
                proposalsList.addAll(uniqueFreelancerProposals.values());
                proposalsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    public static ProposalsFragment newInstance(String jobId) {
        ProposalsFragment fragment = new ProposalsFragment();
        Bundle args = new Bundle();
        args.putString("jobId", jobId);
        fragment.setArguments(args);
        return fragment;
    }
}