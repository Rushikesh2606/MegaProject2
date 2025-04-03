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

import com.example.codebrains.Freelancer_register;
import com.example.codebrains.R;

public class freelancer_form1 extends AppCompatActivity {

    Button btn_submit;
    EditText calendertext, Tools, desc, tagLine;
    AutoCompleteTextView skills;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_freelancer_form1);

        // Declarations
        calendertext = findViewById(R.id.calendertext);
        btn_submit = findViewById(R.id.btn_submit);
        Tools = findViewById(R.id.tools);
        skills = findViewById(R.id.skills);
        desc = findViewById(R.id.desc);
        tagLine = findViewById(R.id.tagLine);

        // Set up the dropdown adapter for skills
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line,
                getResources().getStringArray(R.array.job_categories));
        skills.setAdapter(adapter);

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validateFields()) {
                    // Retrieve the existing bundle or create a new one if null
                    Bundle bundle = getIntent().getExtras();
                    if (bundle == null) {
                        bundle = new Bundle();
                    }
                    // Put values into the bundle
                    bundle.putString("Desc", desc.getText().toString());
                    bundle.putString("skills", skills.getText().toString());
                    bundle.putString("tagLine", tagLine.getText().toString());
                    bundle.putString("Tools", Tools.getText().toString());
                    bundle.putString("Years_of_experience", calendertext.getText().toString());

                    // Navigate to Freelancer_register activity
                    Intent i = new Intent(freelancer_form1.this, Freelancer_register.class);
                    i.putExtras(bundle);
                    startActivity(i);
                    // Optionally call finish() to remove freelancer_form1 from the back stack
                    finish();
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
