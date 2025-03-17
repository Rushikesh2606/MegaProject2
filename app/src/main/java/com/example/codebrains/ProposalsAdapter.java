package com.example.codebrains;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ProposalsAdapter extends RecyclerView.Adapter<ProposalsAdapter.ViewHolder> {

    private List<Proposal> proposalsList;

    public ProposalsAdapter(List<Proposal> proposalsList) {
        this.proposalsList = proposalsList;
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
    }

    @Override
    public int getItemCount() {
        return proposalsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
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