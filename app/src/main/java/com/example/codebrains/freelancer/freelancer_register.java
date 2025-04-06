package com.example.codebrains.freelancer;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.codebrains.MainActivity2;
import com.example.codebrains.R;
import com.example.codebrains.model.Freelancer;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class freelancer_register extends AppCompatActivity {
    Button submit_btn;
    MaterialAutoCompleteTextView availability_spinner;
    EditText passout, degree, language, institute;
    ArrayList<String> available = new ArrayList<>();
    String userId;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_freelancer_register);

        database = FirebaseDatabase.getInstance();

        submit_btn = findViewById(R.id.submit_btn);
        institute = findViewById(R.id.institute);
        language = findViewById(R.id.languages);
        degree = findViewById(R.id.degree);
        passout = findViewById(R.id.passout);

        availability_spinner = findViewById(R.id.availability_spinner);
        available.add("Full-Time");
        available.add("Part-Time");

        ArrayAdapter<String> aa = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, available);
        availability_spinner.setAdapter(aa);

        userId = getIntent().getStringExtra("user_id");

        submit_btn.setOnClickListener(v -> {
            if (validated()) {
                Freelancer freelancer = new Freelancer(
                        userId, passout.getText().toString(), degree.getText().toString(),
                        language.getText().toString(), institute.getText().toString(),
                        availability_spinner.getText().toString()
                );

                DatabaseReference reference = database.getReference().child("freelancer").child(userId);
                reference.updateChildren(freelancer.toMap()).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(freelancer_register.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(freelancer_register.this, MainActivity2.class);
                        startActivity(i);
                        finish();
                    } else {
                        Toast.makeText(freelancer_register.this, "Error in saving data", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private boolean validated() {
        if (TextUtils.isEmpty(passout.getText().toString().trim())) {
            passout.setError("Passout year is required!");
            return false;
        }

        if (TextUtils.isEmpty(degree.getText().toString().trim())) {
            degree.setError("Degree is required!");
            return false;
        }

        if (TextUtils.isEmpty(language.getText().toString().trim())) {
            language.setError("Programming language is required!");
            return false;
        }

        if (TextUtils.isEmpty(institute.getText().toString().trim())) {
            institute.setError("Institute name is required!");
            return false;
        }

        return true;
    }
}