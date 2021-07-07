package com.test.feulmgmt.stripe;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.PaymentIntentResult;
import com.stripe.android.Stripe;
import com.stripe.android.model.ConfirmPaymentIntentParams;
import com.stripe.android.model.PaymentIntent;
import com.stripe.android.model.PaymentMethodCreateParams;
import com.stripe.android.view.CardInputWidget;
import com.test.feulmgmt.FuelApplication;
import com.test.feulmgmt.MainActivity;
import com.test.feulmgmt.R;
import com.test.feulmgmt.ui.order.OrderConfirmFragment;
import com.test.feulmgmt.utils.ApiUrls;
import com.test.feulmgmt.utils.FuelCaches;
import com.test.feulmgmt.utils.FuelPrefs;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CheckoutActivityJava extends AppCompatActivity {
    /**
     * This example collects card payments, implementing the guide here: https://stripe.com/docs/payments/accept-a-payment#android
     * <p>
     * To run this app, follow the steps here: https://github.com/stripe-samples/accept-a-card-payment#how-to-run-locally
     */

    private OkHttpClient httpClient = new OkHttpClient();
    private String paymentIntentClientSecret;
    private Stripe stripe;
    private String clientSectet;
    private float liters;
    private int selectedFuelId;
    FuelPrefs prefs;
    private double price;
    private ProgressBar progressBar;
    private static DecimalFormat df2 = new DecimalFormat("#.##");
    private AppCompatImageView imgBack;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        prefs = new FuelPrefs();
        progressBar = findViewById(R.id.progress_bar);
        liters = getIntent().getFloatExtra("liters",0.0f);
        selectedFuelId = getIntent().getIntExtra("fuelTypeId",0);
        price = getIntent().getDoubleExtra("price",0);

        df2.setRoundingMode(RoundingMode.DOWN);
        startCheckout();

        imgBack = findViewById(R.id.imgBack);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FuelCaches.toOrderConfirm(false);
               onBackPressed();
            }
        });
    }

    private void startCheckout() {
        // Create a PaymentIntent by calling the sample server's /create-payment-intent endpoint.
        JsonObject jsonObject = new JsonObject();
        //jsonObject.addProperty("amount", Float.parseFloat(df2.format(liters * price)));
        jsonObject.addProperty("amount", 1150);
        Ion.with(FuelApplication.getContext()).load(ApiUrls.CREATE_TRANSACTION_TOKEN).
                setHeader("Authorization", "Bearer " + prefs.getUserData().getToken()).
                setJsonObjectBody(jsonObject).asJsonObject().setCallback(new FutureCallback<JsonObject>() {
            @Override
            public void onCompleted(Exception e, JsonObject result) {
                //clientSectet = result.get("data").getAsJsonObject().get("paymentIntent").getAsJsonObject().get("client_secret").getAsString();
                if (result != null)
                    new PayCallback(CheckoutActivityJava.this, result);
            }
        });
       /* MediaType mediaType = MediaType.get("application/json; charset=utf-8");
        String json = "{"
                + "\"amount\":\"112\""
                + "}";
        RequestBody body = RequestBody.create(json, mediaType);
        Request request = new Request.Builder()
                .header("Authorization","Bearer "+prefs.getUserData().getToken())
                .url(ApiUrls.CREATE_TRANSACTION_TOKEN)
                .post(body)
                .build();
        httpClient.newCall(request)
                .enqueue(new PayCallback(this));*/

        // Hook up the pay button to the card widget and stripe instance
        Button payButton = findViewById(R.id.payButton);
        payButton.setOnClickListener((View view) -> {
            CardInputWidget cardInputWidget = findViewById(R.id.cardInputWidget);
            PaymentMethodCreateParams params = cardInputWidget.getPaymentMethodCreateParams();
            if (params != null) {
                ConfirmPaymentIntentParams confirmParams = ConfirmPaymentIntentParams
                        .createWithPaymentMethodCreateParams(params, paymentIntentClientSecret);
                stripe.confirmPayment(CheckoutActivityJava.this, confirmParams);
            }
        });
    }

    private void displayAlert(@NonNull String title,
                              @Nullable String message,
                              boolean restartDemo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message);
        if (restartDemo) {
            builder.setPositiveButton("Restart demo",
                    (DialogInterface dialog, int index) -> {
                        CardInputWidget cardInputWidget = findViewById(R.id.cardInputWidget);
                        cardInputWidget.clear();
                        startCheckout();
                    });
        } else {
            builder.setPositiveButton("Ok", null);
        }
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Handle the result of stripe.confirmPayment
        stripe.onPaymentResult(requestCode, data, new PaymentResultCallback(this));
    }

    private void onPaymentSuccess(@NonNull final JsonObject response){
        /*Gson gson = new Gson();
        Type type = new TypeToken<Map<String, String>>(){}.getType();
        Map<String, String> responseMap = gson.fromJson(
                Objects.requireNonNull(response.body()).string(),
                type
        );*/

        // The response from the server includes the Stripe publishable key and
        // PaymentIntent details.
        // For added security, our sample app gets the publishable key from the server
        //String stripePublishableKey = "pk_test_51IPvMdKaIC0N0fJIoh3uf8V1wu5iGn49xXNzZW2aE68yclBcA4kh6BT4bKj03wf8F0oNcPCn4ZmSYRl24Jy35tMP00eaEYzgjy";
        //paymentIntentClientSecret = "sk_test_51IPvMdKaIC0N0fJIVfgv9SbhQDY2NrfTwA1hJz1sEZg0tMJBDUDPhvDGSst7Qsjn29dn9hwpcMYzlrQDEBPYW7W900fhUdQVlH";
        paymentIntentClientSecret = response.get("data").getAsJsonObject().get("paymentIntent").getAsJsonObject().get("client_secret").getAsString();

        // Configure the SDK with your Stripe publishable key so that it can make requests to the Stripe API
        stripe = new Stripe(
                getApplicationContext(),
                Objects.requireNonNull("pk_test_TYooMQauvdEDq54NiTphI7jx")
        );
    }

    private static final class PayCallback {
        @NonNull
        private final WeakReference<CheckoutActivityJava> activityRef;

        PayCallback(@NonNull CheckoutActivityJava activity, JsonObject result) {
            activityRef = new WeakReference<>(activity);
            onResponse(result);
        }

        private void onResponse(JsonObject result) {
            final CheckoutActivityJava activity = activityRef.get();
            if (activity == null) {
                return;
            }

            if (!result.get("meta").getAsJsonObject().get("success").getAsBoolean()) {
                activity.runOnUiThread(() ->
                        Toast.makeText(
                                activity, "Error: " + result.get("meta").getAsJsonObject().get("message").getAsString(), Toast.LENGTH_LONG
                        ).show()
                );
            } else {
                activity.onPaymentSuccess(result);
            }
        }
    }

    private static final class PaymentResultCallback
            implements ApiResultCallback<PaymentIntentResult> {
        @NonNull
        private final WeakReference<CheckoutActivityJava> activityRef;

        PaymentResultCallback(@NonNull CheckoutActivityJava activity) {
            activityRef = new WeakReference<>(activity);

        }

        @Override
        public void onSuccess(@NonNull PaymentIntentResult result) {
            final CheckoutActivityJava activity = activityRef.get();
            if (activity == null) {
                return;
            }

            PaymentIntent paymentIntent = result.getIntent();
            PaymentIntent.Status status = paymentIntent.getStatus();
            if (status == PaymentIntent.Status.Succeeded) {
                Toast.makeText(activity, "Payment Completed", Toast.LENGTH_SHORT).show();
                activity.submitOrders(paymentIntent.getPaymentMethodId());
            } else if (status == PaymentIntent.Status.RequiresPaymentMethod) {

                Toast.makeText(activity, "Payment Incomplete", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onError(@NonNull Exception e) {
            final CheckoutActivityJava activity = activityRef.get();
            if (activity == null) {
                return;
            }

            // Payment request failed – allow retrying using the same payment method
            Toast.makeText(activity, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void submitOrders(String paymentMethodId) {
        progressBar.setVisibility(View.VISIBLE);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("quantity", String.valueOf(liters));
        jsonObject.addProperty("lattitude", String.valueOf(FuelCaches.getSelectedLatLng().latitude));
        jsonObject.addProperty("longitude", String.valueOf(FuelCaches.getSelectedLatLng().longitude));
        jsonObject.addProperty("transactionToken", paymentMethodId);
        jsonObject.addProperty("fuelTypeId", String.valueOf(selectedFuelId));
        Ion.with(this).load(ApiUrls.SUBMIT_ORDERS).
                setHeader("Authorization", "Bearer " + prefs.getUserData().getToken()).
                setJsonObjectBody(jsonObject).
                asJsonObject().
                setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        progressBar.setVisibility(View.GONE);
                        if (result.get("meta").getAsJsonObject().get("success").getAsBoolean()) {
                            FuelCaches.toOrderConfirm(true);
                            moveToOrderConfirm();
                        } else {
                            Toast.makeText(CheckoutActivityJava.this, result.get("meta").getAsJsonObject().get("message").getAsString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void moveToOrderConfirm() {
        onBackPressed();
    }
}