package com.example.codebrains.reevaluation;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.codebrains.R;

public class ReevaluationDetailActivity extends AppCompatActivity {

    TextView txtId, txtJobId, txtFreelancer, txtReason, txtAnalyzer, txtPending, txtTimestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reevaluation_detail);

        txtId = findViewById(R.id.txtId);
        txtJobId = findViewById(R.id.txtJobId);
        txtFreelancer = findViewById(R.id.txtFreelancer);
        txtReason = findViewById(R.id.txtReason);
        txtAnalyzer = findViewById(R.id.txtAnalyzer);
        txtPending = findViewById(R.id.txtPending);
        txtTimestamp = findViewById(R.id.txtTimestamp);

        txtId.setText("ID: " + getIntent().getStringExtra("reevaluationId"));
        txtJobId.setText("Job ID: " + getIntent().getStringExtra("jobId"));
        txtFreelancer.setText("Freelancer: " + getIntent().getStringExtra("freelancer"));
        txtReason.setText("Reason: " + getIntent().getStringExtra("reason"));
        txtAnalyzer.setText("Analyzer Comments: " + getIntent().getStringExtra("analyzerReasons"));
        txtPending.setText("Pending: " + getIntent().getBooleanExtra("isPending", true));
        txtTimestamp.setText("Timestamp: " + getIntent().getLongExtra("timestamp", 0));
    }
}
