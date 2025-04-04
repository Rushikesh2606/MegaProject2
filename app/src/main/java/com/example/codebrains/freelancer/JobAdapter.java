package com.example.codebrains.freelancer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.codebrains.model.JobController;
import com.example.codebrains.R;
import java.util.List;

public class JobAdapter extends RecyclerView.Adapter<JobAdapter.ViewHolder> {
    private List<JobController> jobs;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(String jobId);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_job, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        JobController job = jobs.get(position);
        holder.title.setText(job.getJobTitle());
        holder.status.setText(job.getStatus()); // Assuming getStatus() exists
        holder.deadline.setText("Deadline: " + job.getDeadline());
    }

    @Override
    public int getItemCount() {
        return jobs != null ? jobs.size() : 0;
    }

    public void setJobs(List<JobController> jobs) {
        this.jobs = jobs;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, status, deadline;

        ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tv_title);
            status = itemView.findViewById(R.id.tv_status);
            deadline = itemView.findViewById(R.id.tv_deadline);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    JobController job = jobs.get(position);
                    listener.onItemClick(job.getId()); // Assuming getId() exists
                }
            });
        }
    }
}