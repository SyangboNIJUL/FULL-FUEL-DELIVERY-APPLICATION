package com.test.feulmgmt.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.test.feulmgmt.MainActivity;
import com.test.feulmgmt.R;

public class FuelNotificationService extends FirebaseMessagingService {
    int counter = 0;
    private RemoteViews contentViewBig;
    private FuelPrefs prefs = new FuelPrefs();

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.e("token", s);
        prefs.saveFirebaseToken(s);
        FirebaseMessaging.getInstance().subscribeToTopic("bookings");
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        counter++;
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        contentViewBig = new RemoteViews(getPackageName(), R.layout.custom_notification_large);

        contentViewBig.setTextViewText(R.id.txtTitle, "Fuel Delivery");
        if (remoteMessage.getNotification() != null) {
            contentViewBig.setTextViewText(R.id.txtNotTitle, remoteMessage.getNotification().getTitle());
            contentViewBig.setTextViewText(R.id.txtDesc, remoteMessage.getNotification().getBody());
        } else {
            if (!prefs.getUserData().getUserDetail().getRole().equalsIgnoreCase("user")) {
                contentViewBig.setTextViewText(R.id.txtNotTitle, remoteMessage.getData().get("title"));
                contentViewBig.setTextViewText(R.id.txtDesc, remoteMessage.getData().get("message"));
            }
        }

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round);
        Intent intent = new Intent(this, MainActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = "Default";
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(bitmap)
                .setCustomBigContentView(contentViewBig)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);


        if (manager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(
                        channelId,
                        "Default channel",
                        NotificationManager.IMPORTANCE_DEFAULT);

                manager.createNotificationChannel(channel);
            }
            manager.notify(counter, builder.build());
        }

    }
}
