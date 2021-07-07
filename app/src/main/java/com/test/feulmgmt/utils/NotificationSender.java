package com.test.feulmgmt.utils;

import android.content.Context;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.test.feulmgmt.FuelApplication;

import org.json.JSONException;
import org.json.JSONObject;

public class NotificationSender {
    private static final String FCM_API = "https://fcm.googleapis.com/fcm/send";
    private static final String KEY = "AAAAQEetUlE:APA91bHUDbWqI8bPVOhqUbmLBiprMbJeWJcWNcCMlxYqAac54tjDZn9R_IY2xeHKnPc8b9NrkELp0caGP7tQF_O6S1vy6yq3aS9YQ-uCIx4IaGmIRD5VZR1EF8ytxg0zhgx6DSh4dxW3";
    private static final String SERVER_KEY = "key=" + KEY;
    private static final String CONTENT_TYPE = "application/json";
    private static final String RECEIVER_ID = "registration_ids";
    //276080448081

    public static final String TAG = "Notificationsender";
    private static String result2;

    public static void sendNotification( String title, String message) {
        AndroidNetworking.initialize(FuelApplication.getContext());
        Log.d(TAG, "in sendNotification : YES");

        JSONObject notification = new JSONObject();
        JSONObject body = new JSONObject();
        try {
            body.put("title", title);
            body.put("message", message);
            notification.put("to", "/topics/bookings");
            notification.put("data", body);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        Log.d(TAG, "before Ion.with : YES");

        AndroidNetworking.post(NotificationSender.FCM_API)
                .addJSONObjectBody(notification)
                .addHeaders("Authorization", SERVER_KEY)
                .addHeaders("Content-Type", CONTENT_TYPE)
                .setTag("test")
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String result) {
                        // do anything with response
                        result2 = result;
                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error
                        Log.e("error",error.toString());
                    }
                });


        Log.d(TAG, result2 + " = result");
    }

}
