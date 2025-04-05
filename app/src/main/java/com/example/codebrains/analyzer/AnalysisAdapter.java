package com.example.codebrains.analyzer;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.codebrains.R;

import java.util.List;

public class AnalysisAdapter extends RecyclerView.Adapter<AnalysisAdapter.AnalysisViewHolder> {

    private List<Analyzer.AnalysisResult> analysisResults;

    public void setAnalysisResults(List<Analyzer.AnalysisResult> analysisResults) {
        this.analysisResults = analysisResults;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AnalysisViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AnalysisViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.analysis_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AnalysisViewHolder holder, int position) {
        Analyzer.AnalysisResult result = analysisResults.get(position);

        holder.textViewFileName.setText(result.fileName);
        if (result.rating != null) {
            holder.textViewRating.setText(String.format("🌟 %.1f/10", result.rating));
            holder.textViewRating.setVisibility(View.VISIBLE);
        } else {
            holder.textViewRating.setVisibility(View.GONE);
        }
        holder.textViewResponse.setText(Html.fromHtml(result.response, Html.FROM_HTML_MODE_LEGACY));
    }

    @Override
    public int getItemCount() {
        return analysisResults != null ? analysisResults.size() : 0;
    }

    public static class AnalysisViewHolder extends RecyclerView.ViewHolder {
        TextView textViewFileName;
        TextView textViewRating;
        TextView textViewResponse;

        public AnalysisViewHolder(View itemView) {
            super(itemView);
            textViewFileName = itemView.findViewById(R.id.textViewFileName);
            textViewRating = itemView.findViewById(R.id.textViewRating);
            textViewResponse = itemView.findViewById(R.id.textViewResponse);
        }
    }
}