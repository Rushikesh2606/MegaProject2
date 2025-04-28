package com.example.codebrains;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.codebrains.R;
import com.example.codebrains.analyzer.RatedJobDetailsActivity;
import com.example.codebrains.model.JobController;
import com.example.codebrains.model.Rated_Job;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;

public class SubmittedJobAdapter extends RecyclerView.Adapter<SubmittedJobAdapter.ViewHolder> {

    private Context context;
    private List<JobController> jobList;
    private Map<String, Rated_Job> ratedJobsMap; // Pass this map here!

    public SubmittedJobAdapter(Context context, List<JobController> jobList, Map<String, Rated_Job> ratedJobsMap) {
        this.context = context;
        this.jobList = jobList;
        this.ratedJobsMap = ratedJobsMap;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvTitle, tvDescription;
        public Button btnDownload;

        public ViewHolder(View view) {
            super(view);
            tvTitle = view.findViewById(R.id.textViewTitle);
            tvDescription = view.findViewById(R.id.textViewDescription);
            btnDownload = view.findViewById(R.id.buttonDownload);
        }
    }

    @Override
    public SubmittedJobAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_submitted_job, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SubmittedJobAdapter.ViewHolder holder, int position) {
        JobController job = jobList.get(position);
        holder.tvTitle.setText(job.getJobTitle());
        holder.tvDescription.setText(job.getJobDescription());

        // Remove the download button visibility
        holder.btnDownload.setVisibility(View.GONE);

        // Set click listener on item
        holder.itemView.setOnClickListener(v -> {
            Rated_Job ratedJob = ratedJobsMap.get(job.getId());
            if (ratedJob != null) {
                // Open new activity and pass jobId
                Intent intent = new Intent(context, RatedJobDetailsActivity.class);
                intent.putExtra("jobId", job.getId());
                intent.putExtra("rated_job_id",ratedJob.getId());

                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return jobList.size();
    }
}
