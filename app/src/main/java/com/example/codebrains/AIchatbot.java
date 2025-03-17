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
            String query = binding.inputPrompt.getText().toString().trim();

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
