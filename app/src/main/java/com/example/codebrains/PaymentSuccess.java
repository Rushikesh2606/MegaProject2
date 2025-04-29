package com.example.codebrains;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import static androidx.core.content.ContextCompat.checkSelfPermission;

public class PaymentSuccess extends AppCompatActivity {
    private Button doneButton;
    private String freelancerContactNo;
    private String amount;
    private static final int SMS_PERMISSION_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_success);
        doneButton = findViewById(R.id.btnContinue);

        // Get freelancer contact number and project amount from intent
        freelancerContactNo = getIntent().getStringExtra("freelancerContactNo");
        amount = getIntent().getStringExtra("amount");

        doneButton.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(PaymentSuccess.this, "android.permission.SEND_SMS") == PackageManager.PERMISSION_GRANTED) {
                sendPaymentConfirmationSms();
            } else {
                requestPermissions(new String[]{"android.permission.SEND_SMS"}, SMS_PERMISSION_REQUEST_CODE);
            }
        });
    }

    private void sendPaymentConfirmationSms() {
        try {
            // Get the default SMS manager
            SmsManager smsManager = SmsManager.getDefault();

            // Send SMS using SmsManager
            smsManager.sendTextMessage(
                    freelancerContactNo,
                    null,
                    "Your payment of $" + amount + " has been credited to your account by the client.",
                    null,
                    null
            );

            Toast.makeText(this, "Payment confirmation SMS sent successfully to " + freelancerContactNo + "!", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity
        } catch (Exception e) {
            Toast.makeText(this, "Failed to send SMS: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            finish(); // Close the activity even if SMS fails
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SMS_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendPaymentConfirmationSms();
            } else {
                Toast.makeText(this, "SMS permission required to send payment confirmation", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}