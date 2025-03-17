package com.example.codebrains;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DetailsAdapter extends RecyclerView.Adapter<DetailsAdapter.ViewHolder> {

    private List<ProjectDetails> projectDetailsList;

    public DetailsAdapter(List<ProjectDetails> projectDetailsList) {
        this.projectDetailsList = projectDetailsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_details, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProjectDetails details = projectDetailsList.get(position);
        holder.title.setText(details.getTitle());
        holder.description.setText(details.getDescription());
        holder.skills.setText(details.getSkills());
    }

    @Override
    public int getItemCount() {
        return projectDetailsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, description, skills;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvTitle);
            description = itemView.findViewById(R.id.tvDescription);
            skills = itemView.findViewById(R.id.tvSkills);
        }
    }
}