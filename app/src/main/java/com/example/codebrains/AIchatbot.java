package com.example.codebrains;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.codebrains.databinding.ActivityAichatbotBinding;

public class AIchatbot extends AppCompatActivity {

    ActivityAichatbotBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAichatbotBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.Ai), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.buttonSend.setOnClickListener(v -> {
            String query="In this we are registering clients and developers. In which we are providing feature for clients to post their job requests on our website with information and  developers are able to bid on that jobs. Every time client post jobs it sends notification to the mail of developer with same skills taken by developer while registration. Client can decide the best developer for his job based on skills and experience. In this process first payment is taken from client and when developer completes his job and client approves then the payment sent to the developer. Also we are incorporating  feature for project analysis. Which analyses project provided by developer and generate report. And also client can rate the developer.   this is  freelance website and android app freelance platform which have name 'CODEBRAINS' . analyze our platform and give me answoer of the following question i ask to you the question is : ";
             query = query + binding.inputPrompt.getText().toString().trim();

            if (query.isEmpty()) {
                Toast.makeText(this, "Please enter a prompt!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Show progress bar
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.textViewAnswer.setText(""); // Clear previous response
            binding.textViewAnswer.setVisibility(View.GONE); // Hide text initially
            binding.inputPrompt.setText(""); // Clear input after sending request

            GeminiPro model = new GeminiPro();
            model.getResponse(query, new ResponseCallback() {
                @Override
                public void onResponse(String response) {
                    binding.textViewAnswer.setText(response);
                    binding.textViewAnswer.setVisibility(View.VISIBLE);
                    binding.progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onError(Throwable throwable) {
                    Toast.makeText(AIchatbot.this, "Error! " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    binding.progressBar.setVisibility(View.GONE);
                }
            });
        });
    }
}
