package com.example.codebrains;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class ProposalsAdapter extends RecyclerView.Adapter<ProposalsAdapter.ViewHolder> {

    private List<Proposal> proposalsList;
    private FirebaseAuth mAuth;
    private DatabaseReference connectionsRef;

    public ProposalsAdapter(List<Proposal> proposalsList) {
        this.proposalsList = proposalsList;
        mAuth = FirebaseAuth.getInstance();
        connectionsRef = FirebaseDatabase.getInstance().getReference("Connections");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_proposal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Proposal proposal = proposalsList.get(position);
        holder.name.setText(proposal.getName());
        holder.location.setText(proposal.getLocation());
        holder.rating.setText(String.format("%.1f (%d reviews)", proposal.getRating(), 128));
        holder.description.setText(proposal.getDescription());
        holder.price.setText(proposal.getPrice());

        holder.hireNow.setOnClickListener(v -> {
            String clientId = mAuth.getUid();
            if (clientId == null) {
                // User is not authenticated
                return;
            }

            // Generate a unique connection ID
            String connectionId = connectionsRef.push().getKey();
            if (connectionId == null) {
                return;
            }

            // Create a new connection object
            Connection connection = new Connection(
                    connectionId,
                    clientId,
                    "PyHZUmua86MUV9iioq1UMqlYdwG2", // Assuming you have a method getFreelancerId() in Proposal
                    proposal.getJobId(), // Assuming job ID is available in Proposal
                    "pending" // Initial status
            );

            // Save the connection object to Firebase
            connectionsRef.child(connectionId).setValue(connection)
                    .addOnSuccessListener(aVoid -> {
                        // Handle success (e.g., show a success message)
                    })
                    .addOnFailureListener(e -> {
                        // Handle failure (e.g., show an error message)
                    });
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
