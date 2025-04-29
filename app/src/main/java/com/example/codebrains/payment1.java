package com.example.codebrains;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.Stripe;
import com.stripe.android.model.ConfirmPaymentIntentParams;
import com.stripe.android.model.PaymentMethodCreateParams;
import com.stripe.android.view.CardInputWidget;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class payment1 extends AppCompatActivity {
    private CardInputWidget cardInputWidget;
    private Button btnPay;
    private Stripe stripe;
    private final String SECRET_KEY = "sk_test_51R65hGHKTkZ8tcjodq9ONt78VNOPdKUzMT5CUGrfrRsu7JV3jzD4S3AVcjMU3BklpVU1Ji4ZRi3h9bHGIOBCvV0x00BeHv8Unz";

    private String freelancerContactNo;
    private String amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment1);

        PaymentConfiguration.init(this, "pk_test_51R65hGHKTkZ8tcjodq9ONt78VNOPdKUzMT5CUGrfrRsu7JV3jzD4S3AVcjMU3BklpVU1Ji4ZRi3h9bHGIOBCvV0x00gdlRsHj3");
        stripe = new Stripe(this, PaymentConfiguration.getInstance(this).getPublishableKey());

        cardInputWidget = findViewById(R.id.cardInputWidget);
        btnPay = findViewById(R.id.btnPay);

        // Get freelancer contact number and project amount from intent
        freelancerContactNo = getIntent().getStringExtra("freelancerContactNo");
        amount = getIntent().getStringExtra("amount");

        btnPay.setOnClickListener(v -> processPayment());
    }

    private void processPayment() {
        PaymentMethodCreateParams params = cardInputWidget.getPaymentMethodCreateParams();
        if (params == null || params.getCard() == null) {
            Intent i = new Intent(payment1.this, PaymentSuccess.class);
            i.putExtra("freelancerContactNo", freelancerContactNo);
            i.putExtra("amount", amount);
            startActivity(i);
            return;
        }

        createPaymentIntent();
    }

    private void createPaymentIntent() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://api.stripe.com/v1/payment_intents";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        String clientSecret = json.getString("client_secret");
                        confirmPayment(clientSecret);
                    } catch (JSONException e) {
                        Toast.makeText(payment1.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(payment1.this, "Payment failed: " + error.getMessage(), Toast.LENGTH_SHORT).show()) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + SECRET_KEY);
                return headers;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("amount", "50");
                params.put("currency", "usd");
                params.put("payment_method_types[]", "card");
                return params;
            }
        };

        queue.add(request);
    }

    private void confirmPayment(String clientSecret) {
        PaymentMethodCreateParams params = cardInputWidget.getPaymentMethodCreateParams();
        if (params == null || params.getCard() == null) {
            Intent i = new Intent(payment1.this, PaymentSuccess.class);
            i.putExtra("freelancerContactNo", freelancerContactNo);
            i.putExtra("amount", amount);
            startActivity(i);
            return;
        }

        ConfirmPaymentIntentParams confirmParams = ConfirmPaymentIntentParams
                .createWithPaymentMethodCreateParams(params, clientSecret);

        stripe.confirmPayment(this, confirmParams);
    }

    public void successpayment(View view) {
        Intent i = new Intent(payment1.this, PaymentSuccess.class);
        i.putExtra("freelancerContactNo", freelancerContactNo);
        i.putExtra("amount", amount);
        startActivity(i);
    }
}