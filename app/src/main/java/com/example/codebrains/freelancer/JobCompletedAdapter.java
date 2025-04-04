package com.example.codebrains.freelancer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.codebrains.R;

import java.util.List;

public class JobCompletedAdapter extends RecyclerView.Adapter<JobCompletedAdapter.JobViewHolder> {

    private List<Job> jobList;

    public JobCompletedAdapter(List<Job> jobList) {
        this.jobList = jobList;
    }

    @NonNull
    @Override
    public JobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_completed_job, parent, false);
        return new JobViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JobViewHolder holder, int position) {
        Job job = jobList.get(position);
        holder.tvCategory.setText(job.getCategory());
        holder.tvTitle.setText(job.getTitle());
        holder.tvDescription.setText(job.getDescription());
        holder.tvDate.setText("Completed on: " + job.getCompletedDate());
    }

    @Override
    public int getItemCount() {
        return jobList.size();
    }

    public static class JobViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategory, tvTitle, tvDescription, tvDate;

        public JobViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }
}