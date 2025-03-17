package com.example.codebrains;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class JobpostingActivity extends AppCompatActivity {

    private View step1, step2, step3, step4;
    private View stepLine1, stepLine2, stepLine3;
    private FragmentManager fragmentManager;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_jobposting);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        // Initialize Views for Steps and Step Lines
        step1 = findViewById(R.id.step1);
        step2 = findViewById(R.id.step2);
        step3 = findViewById(R.id.step3);
        step4 = findViewById(R.id.step4);

        stepLine1 = findViewById(R.id.stepLine1);
        stepLine2 = findViewById(R.id.stepLine2);
        stepLine3 = findViewById(R.id.stepLine3);

        // Initialize Fragment Manager
        fragmentManager = getSupportFragmentManager();

        // Load the first fragment only if it's a fresh instance
        if (savedInstanceState == null) {
            loadJobDetailsFragment();
        }

        // Setup Spinner for Experience Level
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Spinner spinner = findViewById(R.id.experience_level);
        if (spinner != null) {
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                    R.array.experience_levels_array, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
        }
    }

    private void loadJobDetailsFragment() {
        if (findViewById(R.id.fragment_container) == null) {
            throw new RuntimeException("fragment_container is missing in activity_main.xml");
        }

        // Load the first fragment (Job Details)
        JobDetailsFragment fragment = new JobDetailsFragment();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commitAllowingStateLoss();

        updateStepIndicators(1);
    }

    public void replaceFragment(Fragment fragment, int step) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commitAllowingStateLoss();

        // Update step indicator after replacing the fragment
        updateStepIndicators(step);
    }

    void updateStepIndicators(int currentStep) {
        // Update step backgrounds
        step1.setBackground(ContextCompat.getDrawable(this,
                currentStep >= 1 ? R.drawable.circle_active : R.drawable.circle_inactive));
        step2.setBackground(ContextCompat.getDrawable(this,
                currentStep >= 2 ? R.drawable.circle_active : R.drawable.circle_inactive));
        step3.setBackground(ContextCompat.getDrawable(this,
                currentStep >= 3 ? R.drawable.circle_active : R.drawable.circle_inactive));
        step4.setBackground(ContextCompat.getDrawable(this,
                currentStep >= 4 ? R.drawable.circle_active : R.drawable.circle_inactive));

        // Reset all step lines
        stepLine1.setBackgroundResource(R.color.green);
        stepLine2.setBackgroundResource(R.color.green);
        stepLine3.setBackgroundResource(R.color.green);

        // Activate the corresponding steps and lines based on the current step
        if (currentStep > 1) stepLine1.setBackgroundResource(R.color.active_step);
        if (currentStep > 2) stepLine2.setBackgroundResource(R.color.active_step);
        if (currentStep > 3) stepLine3.setBackgroundResource(R.color.active_step);
    }
    }
