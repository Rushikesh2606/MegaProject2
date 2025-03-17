package com.example.codebrains.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.codebrains.R;
import com.example.codebrains.Freelancer_register;

import java.util.Calendar;

public class freelancer_form1 extends AppCompatActivity {

    Button btn_submit;
    EditText calendertext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_freelancer_form1);

        calendertext = findViewById(R.id.calendertext);
        btn_submit = findViewById(R.id.btn_submit);

        // Set onClickListener for the Years of Experience field
        calendertext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showYearPickerDialog();
            }
        });

        // Set onClickListener for the Next button
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(freelancer_form1.this, Freelancer_register.class);
                startActivity(i);
            }
        });
    }

    private void showYearPickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    // Set the selected year in the field
                    calendertext.setText(String.valueOf(year));
                },
                currentYear, // Initial year
                calendar.get(Calendar.MONTH), // Month (not used)
                calendar.get(Calendar.DAY_OF_MONTH) // Day (not used)
        );

        // Limit picker to only display years
        datePickerDialog.getDatePicker().setCalendarViewShown(false);
        datePickerDialog.getDatePicker().setSpinnersShown(true);

        datePickerDialog.show();
    }
}
