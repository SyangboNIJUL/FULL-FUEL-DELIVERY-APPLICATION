package com.test.feulmgmt.stripe;

import androidx.annotation.NonNull;
import androidx.annotation.Size;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.stripe.android.EphemeralKeyProvider;
import com.stripe.android.EphemeralKeyUpdateListener;
import com.test.feulmgmt.FuelApplication;
import com.test.feulmgmt.utils.ApiUrls;
import com.test.feulmgmt.utils.FuelPrefs;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MyEphemeralKeyProvider implements EphemeralKeyProvider {

    @Override
    public void createEphemeralKey(
            @NonNull @Size(min = 4) String apiVersion,
            @NonNull final EphemeralKeyUpdateListener keyUpdateListener) {
        FuelPrefs prefs = new FuelPrefs();
        /*final Map<String, String> apiParamMap = new HashMap<>();
        apiParamMap.put("api_version", "16.4.0");*/
        Ion.with(FuelApplication.getContext()).load(ApiUrls.GET_EPHIMERAL_KEY).setHeader("Authorization","Bearer "+prefs.getUserData().getToken()).asJsonObject().setCallback(new FutureCallback<JsonObject>() {
            @Override
            public void onCompleted(Exception e, JsonObject response) {
                try {
                    final String rawKey = response.toString();
                    keyUpdateListener.onKeyUpdate("ephkey_1Ig11N2eZvKYlo2CaTlLR0sz");
                } catch (Exception ignored) {
                }
            }
        });
    }
}