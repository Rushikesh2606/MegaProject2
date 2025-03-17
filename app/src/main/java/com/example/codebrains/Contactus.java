package com.example.codebrains;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Contactus extends Fragment {

    private EditText etFullName, etMessage;
    private Button btnSendMessage;

    // Required public no-argument constructor
    public Contactus() {
        // No custom parameters; use newInstance pattern if you need to pass data.
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contactus, container, false);

        etFullName = view.findViewById(R.id.etFullName);
        etMessage = view.findViewById(R.id.etMessage);
        btnSendMessage = view.findViewById(R.id.btnSendMessage);

        // Set click listener for the send button
        btnSendMessage.setOnClickListener(v -> sendEmail());
        return view;
    }

    private void sendEmail() {
        String fullName = etFullName.getText().toString().trim();
        String message = etMessage.getText().toString().trim();

        if (fullName.isEmpty() || message.isEmpty()) {
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Email details
        String subject = "Message from " + fullName;
        String body = "Full Name: " + fullName + "\n\nMessage:\n" + message;

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"omkarpatil2246@gmail.com"});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, body);

        try {
            startActivity(Intent.createChooser(emailIntent, "Send email using:"));
        } catch (Exception e) {
            Toast.makeText(getContext(), "No email apps installed.", Toast.LENGTH_SHORT).show();
        }
    }
}
