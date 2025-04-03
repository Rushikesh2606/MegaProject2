package com.example.codebrains;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment1);

        PaymentConfiguration.init(this, "pk_test_51R65hGHKTkZ8tcjoICqusYPwAF9MYTxsq8nPYq6uHAm0LmNQzmhX4Rgdq9Trzn09caKZy6lvM7oa1MPMP4uSZN2x00gdlRsHj3");
        stripe = new Stripe(this, PaymentConfiguration.getInstance(this).getPublishableKey());

        cardInputWidget = findViewById(R.id.cardInputWidget);
        btnPay = findViewById(R.id.btnPay);

        btnPay.setOnClickListener(v -> processPayment());
    }

    private void processPayment() {
        // Get payment method parameters
        PaymentMethodCreateParams.Card card = cardInputWidget.getPaymentMethodCreateParams().getCard();
        if (card == null) {
            Toast.makeText(this, "Invalid card details", Toast.LENGTH_SHORT).show();
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
                params.put("amount", "50"); // Ensuring minimum amount is 50 cents
                params.put("currency", "usd");
                params.put("payment_method_types[]", "card");
                return params;
            }
        };

        queue.add(request);
    }

    private void confirmPayment(String clientSecret) {
        // Create payment parameters
        PaymentMethodCreateParams params = cardInputWidget.getPaymentMethodCreateParams();
        if (params == null) {
            Toast.makeText(this, "Invalid card details", Toast.LENGTH_SHORT).show();
            return;
        }

        ConfirmPaymentIntentParams confirmParams = ConfirmPaymentIntentParams
                .createWithPaymentMethodCreateParams(params, clientSecret);
    }
}