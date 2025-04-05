package com.example.codebrains;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.codebrains.model.Connection;
import com.example.codebrains.model.Proposal;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class ProposalsAdapter extends RecyclerView.Adapter<ProposalsAdapter.ViewHolder> {
    private List<Proposal> proposalsList;
    private FirebaseAuth mAuth;
    private DatabaseReference connectionsRef;
    private Context context;

    public ProposalsAdapter(List<Proposal> proposalsList, Context context) {
        this.proposalsList = proposalsList;
        this.context = context;
        mAuth = FirebaseAuth.getInstance();
        connectionsRef = FirebaseDatabase.getInstance().getReference("Connections");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_proposal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Proposal proposal = proposalsList.get(position);
        holder.name.setText(proposal.getName());
        holder.location.setText(proposal.getLocation());
        holder.rating.setText(String.format("%.1f", proposal.getRating()));
        holder.description.setText(proposal.getDescription());
        holder.price.setText(proposal.getPrice());

        holder.hireNow.setOnClickListener(v -> {
            String clientId = mAuth.getUid();
            if (clientId == null) return;

            String jobId = proposal.getJobId();
            String selectedPrice = proposal.getPrice();

            // 1. Delete all proposals for this job
            DatabaseReference proposalsRef = FirebaseDatabase.getInstance().getReference("proposals");
            proposalsRef.get().addOnSuccessListener(snapshot -> {
                for (DataSnapshot proposalSnap : snapshot.getChildren()) {
                    String proposalJobId = proposalSnap.child("jobId").getValue(String.class);
                    if (jobId.equals(proposalJobId)) {
                        proposalSnap.getRef().removeValue();
                    }
                }

                // 2. Create a connection
                String connectionId = connectionsRef.push().getKey();
                if (connectionId == null) return;

                Connection connection = new Connection(
                        connectionId,
                        clientId,
                        proposal.getFreelancerId(),
                        jobId,
                        "pending",
                        System.currentTimeMillis()
                );

                connectionsRef.child(connectionId).setValue(connection)
                        .addOnSuccessListener(aVoid -> {
                            // 3. Update job status and budget
                            DatabaseReference jobsRef = FirebaseDatabase.getInstance().getReference("jobs");
                            jobsRef.child(jobId).child("status").setValue("in_progress");
                            jobsRef.child(jobId).child("budget").setValue(selectedPrice);
                            jobsRef.child(jobId).child("freelancer").setValue(proposal.getFreelancerId()); // 👈 This adds the freelancerId


                            // 4. Update freelancer stats
                            String freelancerId = proposal.getFreelancerId();
                            DatabaseReference freelancerRef = FirebaseDatabase.getInstance().getReference("freelancer").child(freelancerId);
                            freelancerRef.get().addOnSuccessListener(freelancerSnap -> {
                                long freelancerInProgress = 0;
                                if (freelancerSnap.hasChild("in_progress")) {
                                    freelancerInProgress = freelancerSnap.child("in_progress").getValue(Long.class);
                                }
                                freelancerRef.child("in_progress").setValue(freelancerInProgress + 1);
                            });

                            // 5. Update client stats
                            DatabaseReference clientRef = FirebaseDatabase.getInstance().getReference("user").child(clientId);
                            clientRef.get().addOnSuccessListener(clientSnap -> {
                                long totalJobs = 0;
                                long inProgress = 0;

                                if (clientSnap.hasChild("total_jobs")) {
                                    totalJobs = clientSnap.child("total_jobs").getValue(Long.class);
                                }
                                if (clientSnap.hasChild("in_progress")) {
                                    inProgress = clientSnap.child("in_progress").getValue(Long.class);
                                }

                                clientRef.child("total_jobs").setValue(Math.max(0, totalJobs - 1));
                                clientRef.child("in_progress").setValue(inProgress + 1);
                            });

                            // 6. Start payment activity
                            Intent intent = new Intent(context, payment1.class);
                            intent.putExtra("price", selectedPrice);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        });
            });
        });

        holder.viewProfile.setOnClickListener(v -> {
            // TODO: Implement view profile
        });

        holder.message.setOnClickListener(v -> {
            // TODO: Implement messaging
        });
    }

    @Override
    public int getItemCount() {
        return proposalsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, location, rating, description, price;
        Button message, hireNow, viewProfile;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvName);
            location = itemView.findViewById(R.id.tvLocation);
            rating = itemView.findViewById(R.id.tvRating);
            description = itemView.findViewById(R.id.tvDescription);
            price = itemView.findViewById(R.id.tvPrice);
            message = itemView.findViewById(R.id.btnMessage);
            hireNow = itemView.findViewById(R.id.btnHireNow);
            viewProfile = itemView.findViewById(R.id.btnViewProfile);
        }
    }
}
