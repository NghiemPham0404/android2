package com.example.movieapp.Services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Parcelable;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.movieapp.Model.AccountModel;
import com.example.movieapp.R;
import com.example.movieapp.Request.LoginAccountRequest;
import com.example.movieapp.View.Movie_infomation;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    String title, body;
    Map<String, String> data;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        getFirebaseMessage(message.getNotification().getTitle(), message.getNotification().getBody(), message.getData());
        super.onMessageReceived(message);
    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        // Send the token to your server or perform any other actions
        Log.d("FCM Token", token);
    }

    private void getFirebaseMessage(String title, String body, Map<String, String> data) {
        AccountModel accountModel = LoginAccountRequest.readUserFromFile(this);
        if(accountModel == null){
            return ;
        }

        String large_image = data.get("large_image");
        int movie_id = Integer.parseInt(data.get("movie_id"));
        Intent intent = new Intent(this, Movie_infomation.class);
        intent.putExtra("film_id", movie_id);
        intent.putExtra("loginAccount", (Parcelable) accountModel);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Create a notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("notify", "My Notification", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("My channel");
            notificationManager.createNotificationChannel(channel);
        }

        // Use 'this' instead of 'getBaseContext()'
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "notify")
                .setContentTitle(title)
                .setSmallIcon(R.drawable.movie_app)
                .setContentIntent(pendingIntent)
                .setContentText(body)
                .setAutoCancel(false);

        Glide.with(this)
                .asBitmap()
                .load(large_image)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
                        // Set the loaded bitmap as the large icon of the notification
                        builder.setLargeIcon(bitmap);
                        int notificationId = (int) System.currentTimeMillis();
                        notificationManager.notify(notificationId, builder.build());
                    }
                });
    }
}
