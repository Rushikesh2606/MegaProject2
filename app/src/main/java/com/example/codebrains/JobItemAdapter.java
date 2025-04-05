package com.example.codebrains;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.codebrains.R;
import com.example.codebrains.analyzer.pythoncode;

import java.util.List;

public class JobItemAdapter extends RecyclerView.Adapter<JobItemAdapter.ViewHolder> {

    private Context context;
    private List<String> jobIdList;

    public JobItemAdapter(Context context, List<String> jobIdList) {
        this.context = context;
        this.jobIdList = jobIdList;
    }

    @NonNull
    @Override
    public JobItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_job2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JobItemAdapter.ViewHolder holder, int position) {
        String jobId = jobIdList.get(position);
        holder.jobIdText.setText("Job ID: " + jobId);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, pythoncode.class);
            intent.putExtra("jobId", jobId); // pass the jobId to the next activity
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return jobIdList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView jobIdText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            jobIdText = itemView.findViewById(R.id.jobIdText);
        }
    }
}
