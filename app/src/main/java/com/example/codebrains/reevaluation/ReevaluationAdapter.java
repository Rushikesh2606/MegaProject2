package com.example.codebrains.reevaluation;

import android.content.Context;
import android.content.Intent;
import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.codebrains.R;
import com.example.codebrains.reevaluation.ReevaluationDetailActivity;
import com.example.codebrains.model.Reevaluation;

import java.util.List;

public class ReevaluationAdapter extends RecyclerView.Adapter<ReevaluationAdapter.ViewHolder> {

    private List<Reevaluation> reevaluationList;
    private Context context;

    public ReevaluationAdapter(List<Reevaluation> list, Context ctx) {
        this.reevaluationList = list;
        this.context = ctx;
    }

    @NonNull
    @Override
    public ReevaluationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_reevaluation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReevaluationAdapter.ViewHolder holder, int position) {
        Reevaluation reevaluation = reevaluationList.get(position);
        holder.reasonText.setText(reevaluation.getReason());
        holder.timestampText.setText(String.valueOf(reevaluation.getTimestamp()));
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ReevaluationDetailActivity.class);
            intent.putExtra("reevaluationId", reevaluation.getId());
            intent.putExtra("freelancer", reevaluation.getFreelancer());
            intent.putExtra("jobId", reevaluation.getJobId());
            intent.putExtra("reason", reevaluation.getReason());
            intent.putExtra("timestamp", reevaluation.getTimestamp());
            intent.putExtra("analyzerReasons", reevaluation.getAnalyzer_reasons());
            intent.putExtra("isPending", reevaluation.isPending());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return reevaluationList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView reasonText, timestampText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            reasonText = itemView.findViewById(R.id.textReason);
            timestampText = itemView.findViewById(R.id.textTimestamp);
        }
    }
}
