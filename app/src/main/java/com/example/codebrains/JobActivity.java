package com.example.codebrains;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class JobActivity extends AppCompatActivity implements JobAdapter.OnJobCloseListener {

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_job);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.jobactivityid), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager2 viewPager = findViewById(R.id.viewPager);

        // Initialize adapter with the close listener
        JobPagerAdapter pagerAdapter = new JobPagerAdapter(this, this);
        viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("All Jobs");
                            break;
                        case 1:
                            tab.setText("Open Jobs");
                            break;
                        case 2:
                            tab.setText("In-Progress Jobs");
                            break;
                        case 3:
                            tab.setText("Completed Jobs");
                            break;
                    }
                }).attach();
    }

    @Override
    public void onJobClosed(int position) {
        // Handle job closure across all fragments

    }

    // Remove unused View_detalisbtn method
}