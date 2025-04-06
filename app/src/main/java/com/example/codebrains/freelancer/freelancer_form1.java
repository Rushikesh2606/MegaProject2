package com.example.codebrains.freelancer;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.codebrains.R;
import com.example.codebrains.model.Freelancer;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class freelancer_form1 extends AppCompatActivity {

    Button btn_submit;
    EditText calendertext, Tools, desc, tagLine;
    AutoCompleteTextView skills;
    String userId;

    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_freelancer_form1);

        database = FirebaseDatabase.getInstance();

        calendertext = findViewById(R.id.calendertext);
        btn_submit = findViewById(R.id.btn_submit);
        Tools = findViewById(R.id.tools);
        skills = findViewById(R.id.skills);
        desc = findViewById(R.id.desc);
        tagLine = findViewById(R.id.tagLine);

        userId = getIntent().getStringExtra("user_id");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line,
                getResources().getStringArray(R.array.job_categories));
        skills.setAdapter(adapter);

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateFields()) {
                    Freelancer freelancer = new Freelancer(
                             desc.getText().toString(), calendertext.getText().toString(),
                            Tools.getText().toString(), skills.getText().toString(), tagLine.getText().toString()
                    );

                    DatabaseReference reference = database.getReference().child("freelancer").child(userId);
                    reference.updateChildren(freelancer.toMap()).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Intent i = new Intent(freelancer_form1.this, freelancer_register.class);
                            i.putExtra("user_id", userId);
                            startActivity(i);
                            finish();
                        } else {
                            Toast.makeText(freelancer_form1.this, "Error in saving data", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private boolean validateFields() {
        if (TextUtils.isEmpty(Tools.getText().toString().trim())) {
            Tools.setError("Please enter your Tools/Software expertise!");
            return false;
        }

        if (TextUtils.isEmpty(skills.getText().toString().trim())) {
            skills.setError("Please select your Job Category!");
            return false;
        }

        if (TextUtils.isEmpty(desc.getText().toString().trim())) {
            desc.setError("Please enter your description!");
            return false;
        }

        if (TextUtils.isEmpty(tagLine.getText().toString().trim())) {
            tagLine.setError("Please enter your tagline!");
            return false;
        }

        if (TextUtils.isEmpty(calendertext.getText().toString().trim())) {
            calendertext.setError("Years of experience is required!");
            return false;
        }
        return true;
    }
}