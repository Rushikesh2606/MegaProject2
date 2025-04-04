package com.example.codebrains.job;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.codebrains.Job_View_Details;
import com.example.codebrains.R;
import com.example.codebrains.model.JobController;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class JobAdapter extends RecyclerView.Adapter<JobAdapter.ViewHolder> {
    private List<JobController> jobs;
    private OnJobCloseListener closeListener;

    public interface OnJobCloseListener {
        void onJobClosed(int position);
    }

    public JobAdapter(List<JobController> jobs, OnJobCloseListener closeListener) {
        this.jobs = jobs;
        this.closeListener = closeListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.job_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        JobController job = jobs.get(position);
        holder.tvTitle.setText(job.getJobTitle());
        holder.tvDate.setText("Posted Date: " + job.getPostedDate());
        holder.tvStatus.setText("Status: " + job.getStatus());
        holder.tvBids.setText("Bids Received: " + job.getNoOfBidsReceived());

        // View Details Click Listener
        holder.btnViewDetails.setOnClickListener(v -> {
            Context context = holder.itemView.getContext();
            Intent intent = new Intent(context, Job_View_Details.class);
            intent.putExtra("JOB_TITLE", job.getJobTitle());
            intent.putExtra("POSTED_DATE", job.getPostedDate());
            intent.putExtra("JOB_STATUS", job.getStatus());
            intent.putExtra("BIDS_COUNT", job.getNoOfBidsReceived());
            if(job.getId()==null){
                Toast.makeText(context, "Id is null!!", Toast.LENGTH_SHORT).show();
            }
            intent.putExtra("jobId", job.getId());
            context.startActivity(intent);
        });

        // Close Job Click Listener
        holder.btnCloseJob.setOnClickListener(v -> {
            if (closeListener != null) {
                closeListener.onJobClosed(holder.getAdapterPosition());
            }
        });
        holder.btnCloseJob.setOnClickListener(v -> {
            JobController jobToDelete = jobs.get(holder.getAdapterPosition());
            String jobId = jobToDelete.getId();

            if (jobId != null) {
                FirebaseDatabase.getInstance().getReference("jobs")
                        .child(jobId)
                        .removeValue()
                        .addOnSuccessListener(aVoid -> {
                            // Remove from list and notify adapter
                            int pos = holder.getAdapterPosition();
                            jobs.remove(pos);
                            notifyItemRemoved(pos);
                            Toast.makeText(holder.itemView.getContext(), "Job closed successfully", Toast.LENGTH_SHORT).show();

                            if (closeListener != null) {
                                closeListener.onJobClosed(pos);
                            }
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(holder.itemView.getContext(), "Failed to close job", Toast.LENGTH_SHORT).show();
                        });
            } else {
                Toast.makeText(holder.itemView.getContext(), "Job ID is null. Cannot delete.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return jobs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDate, tvStatus, tvBids;
        Button btnViewDetails, btnCloseJob;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvBids = itemView.findViewById(R.id.tvBids);
            btnViewDetails = itemView.findViewById(R.id.btnViewDetails);
            btnCloseJob = itemView.findViewById(R.id.btnCloseJob);
        }
    }
}