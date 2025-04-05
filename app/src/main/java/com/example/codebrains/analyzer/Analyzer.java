package com.example.codebrains.analyzer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.codebrains.BuildConfig;
import com.example.codebrains.R;
import com.example.codebrains.analyzer.AnalysisAdapter;
import com.example.codebrains.databinding.ActivityAnalyzerBinding;
import com.example.codebrains.databinding.ActivityMainBinding;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.*;

import java.io.*;
import java.util.*;
import java.util.regex.*;
import java.util.zip.*;

public class Analyzer extends AppCompatActivity {

    private ActivityAnalyzerBinding binding;
    private final Map<String, String> codeFilesContent = new HashMap<>();
    private AnalysisAdapter analysisAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyzer);
        binding = ActivityAnalyzerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupRecyclerView();

        // Get ZIP file path from Intent
        String filePath = getIntent().getStringExtra("filepath");
        if (filePath != null) {
            File file = new File(filePath);
            handleZipFile(file);
        } else {
            Toast.makeText(this, "No ZIP file path provided", Toast.LENGTH_SHORT).show();
        }

        // Analyze Button
        binding.buttonSend.setOnClickListener(v -> {
            if (codeFilesContent.isEmpty()) {
                Toast.makeText(this, "No code files to analyze", Toast.LENGTH_SHORT).show();
            } else {
                analyzeAllCode();
            }
        });

        // Reset Button
        binding.buttonReset.setOnClickListener(v -> resetAnalysis());

        // Hide select button since it’s no longer needed
    }

    private void setupRecyclerView() {
        analysisAdapter = new AnalysisAdapter();
        binding.recyclerViewAnalysis.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewAnalysis.setAdapter(analysisAdapter);
    }

    private void handleZipFile(File file) {
        codeFilesContent.clear();
        try (InputStream inputStream = new FileInputStream(file);
             ZipInputStream zipInputStream = new ZipInputStream(inputStream)) {
            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                if (!zipEntry.isDirectory() && isCodeFile(zipEntry.getName())) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(zipInputStream));
                    StringBuilder contentBuilder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        contentBuilder.append(line).append("\n");
                    }
                    codeFilesContent.put(zipEntry.getName(), contentBuilder.toString());
                    zipInputStream.closeEntry();
                }
            }
            binding.tvSelectedFile.setText("Selected ZIP with " + codeFilesContent.size() + " code files");
        } catch (IOException e) {
            Toast.makeText(this, "Error reading ZIP file", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private boolean isCodeFile(String fileName) {
        return fileName.matches(".*\\.(java|kt|xml|py|js|html|css|cpp|h|swift)$");
    }

    private void analyzeAllCode() {
        binding.progressBar.setVisibility(View.VISIBLE);
        new Thread(() -> {
            GeminiPro model = new GeminiPro();
            List<AnalysisResult> analysisResults = new ArrayList<>();

            for (Map.Entry<String, String> entry : codeFilesContent.entrySet()) {
                String codeContent = entry.getValue();
                String prompt = createAnalysisPrompt(codeContent);
                try {
                    String response = model.getResponseSync(prompt);
                    AnalysisResult result = parseResponse(entry.getKey(), response);
                    analysisResults.add(result);
                } catch (Exception e) {
                    analysisResults.add(new AnalysisResult(entry.getKey(), null, "Error: " + e.getMessage()));
                }
            }

            runOnUiThread(() -> {
                binding.progressBar.setVisibility(View.GONE);
                analysisAdapter.setAnalysisResults(analysisResults);
            });
        }).start();
    }

    private void resetAnalysis() {
        codeFilesContent.clear();
        binding.tvSelectedFile.setText("No file selected");
        analysisAdapter.setAnalysisResults(new ArrayList<>());
        binding.progressBar.setVisibility(View.GONE);
        Toast.makeText(this, "Analysis cleared", Toast.LENGTH_SHORT).show();
    }

    private AnalysisResult parseResponse(String fileName, String response) {
        String formattedResponse = response.replaceAll("\\*\\*(.*?)\\*\\*", "<b>$1</b>");
        Float rating = extractRating(response);
        return new AnalysisResult(fileName, rating, formattedResponse);
    }

    private String createAnalysisPrompt(String code) {
        return "### Code:\n\n```\n" + code + "\n```\n\n### Analysis Request:\n\n" +
                "Please provide a detailed rating (out of 10) in **bold**, followed by strengths, weaknesses, and suggestions.";
    }

    private Float extractRating(String response) {
        Pattern pattern = Pattern.compile("\\*\\*(\\d+(\\.\\d+)?)\\*\\*");
        Matcher matcher = pattern.matcher(response);
        return matcher.find() ? Float.parseFloat(matcher.group(1)) : null;
    }

    public static class AnalysisResult {
        String fileName;
        Float rating;
        String response;

        public AnalysisResult(String fileName, Float rating, String response) {
            this.fileName = fileName;
            this.rating = rating;
            this.response = response;
        }
    }

    public class GeminiPro {
        private GenerativeModelFutures getModel() {
            SafetySetting harassmentSafety = new SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.ONLY_HIGH);

            GenerationConfig.Builder configBuilder = new GenerationConfig.Builder();
            configBuilder.temperature = 0.8f;
            configBuilder.topK = 16;
            configBuilder.topP = 0.1f;
            GenerationConfig generationConfig = configBuilder.build();

            GenerativeModel gm = new GenerativeModel(
                    "gemini-1.5-flash",
                    BuildConfig.API_KEY,
                    generationConfig,
                    Collections.singletonList(harassmentSafety)
            );

            return GenerativeModelFutures.from(gm);
        }

        public String getResponseSync(String query) throws Exception {
            return getModel().generateContent(new Content.Builder().addText(query).build()).get().getText();
        }
    }
}
