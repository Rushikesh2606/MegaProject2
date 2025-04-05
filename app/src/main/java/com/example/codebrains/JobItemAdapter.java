package com.example.codebrains;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.codebrains.analyzer.pythoncode;
import com.example.codebrains.model.JobController;
import java.util.ArrayList;
import java.util.List;

public class JobItemAdapter extends RecyclerView.Adapter<JobItemAdapter.ViewHolder> {

    private Context context;
    private List<JobController> jobList;

    public JobItemAdapter(Context context, List<JobController> jobList) {
        this.context = context;
        this.jobList = jobList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_job2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        JobController job = jobList.get(position);

        // Set basic info
        holder.jobIdText.setText("Job ID: #" + job.getId());
        holder.jobTitleText.setText(job.getJobTitle());
        holder.jobStatusText.setText(job.getStatus());
        holder.companyText.setText(job.getUsername());
        holder.salaryText.setText(formatSalary(job.getBudget()));
        holder.jobTypeText.setText(job.getExperienceLevel());

        // Handle skills
        List<String> skills = new ArrayList<>();
        if(job.getPrimarySkill() != null) skills.add(job.getPrimarySkill());
        if(job.getAdditionalSkills() != null) {
            String[] additional = job.getAdditionalSkills().split(",");
            for(String skill : additional) {
                if(!skill.trim().isEmpty()) skills.add(skill.trim());
            }
        }

        // Update skill views
        TextView[] skillViews = {holder.skill1, holder.skill2, holder.skill3};
        for(int i = 0; i < skillViews.length; i++) {
            if(i < skills.size()) {
                skillViews[i].setText(skills.get(i));
                skillViews[i].setVisibility(View.VISIBLE);
            } else {
                skillViews[i].setVisibility(View.GONE);
            }
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, pythoncode.class);
            intent.putExtra("jobId", job.getId());
            context.startActivity(intent);
        });
    }

    private String formatSalary(double budget) {
        return "$" + (budget / 1000) + "K"; // Converts 150000 to $150K
    }

    @Override
    public int getItemCount() {
        return jobList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView jobIdText, jobTitleText, jobStatusText, companyText,
                locationText, salaryText, jobTypeText, skill1, skill2, skill3;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            jobIdText = itemView.findViewById(R.id.jobIdText);
            jobTitleText = itemView.findViewById(R.id.jobTitleText);
            jobStatusText = itemView.findViewById(R.id.jobStatusText);
            companyText = itemView.findViewById(R.id.companyText);
            locationText = itemView.findViewById(R.id.locationText);
            salaryText = itemView.findViewById(R.id.salaryText);
            jobTypeText = itemView.findViewById(R.id.jobTypeText);
            skill1 = itemView.findViewById(R.id.skill1);
            skill2 = itemView.findViewById(R.id.skill2);
            skill3 = itemView.findViewById(R.id.skill3);
        }
    }
}