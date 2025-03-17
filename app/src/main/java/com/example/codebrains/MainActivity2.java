package com.example.codebrains;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ViewFlipper;

public class MainActivity2 extends AppCompatActivity {

    ViewFlipper v;
    Button btn;
    TextView txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if the user has already logged in by retrieving the stored profession.
        SharedPreferences sp = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String role = sp.getString("profession", "");

        if (!role.isEmpty()) {  // User has logged in before
            if (role.equals("Client")) {
                startActivity(new Intent(MainActivity2.this, Homepage.class));
            } else if (role.equals("Freelancer")) {
                startActivity(new Intent(MainActivity2.this, Homepage_developer.class));
            }
            finish();  // Close MainActivity2 since we’re redirecting the user.
            return;
        }

        // No stored role: show the welcome screen.
        setContentView(R.layout.activity_main2);

        // Initialize UI components
        v = findViewById(R.id.imgflip);
        btn = findViewById(R.id.login_button);
        txt = findViewById(R.id.signup);

        // Configure the ViewFlipper (auto-flipping images)
        v.setFlipInterval(2000);
        v.startFlipping();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity2.this, login.class);
                startActivity(i);
            }
        });

        txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i2 = new Intent(MainActivity2.this, signup.class);
                startActivity(i2);
            }
        });
    }
}
