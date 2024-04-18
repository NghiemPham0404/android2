package com.example.movieapp;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.graphics.drawable.IconCompat;

import com.example.movieapp.Model.AccountModel;
import com.example.movieapp.View.HomeActivity;
import com.example.movieapp.View.HomePage;
import com.example.movieapp.View.Movie_infomation;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);

        getFirebaseMassage(message.getNotification().getTitle(), message.getNotification().getBody(), message.getData());
    }

    private void getFirebaseMassage(String title, String body, Map<String, String> data) {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra("film_id", Integer.parseInt(data.get("movie_id")));
        intent.putExtra("loginAccount", new AccountModel());
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Action action = new NotificationCompat.Action(R.mipmap.ic_launcher_round, title, pendingIntent);

        NotificationCompat.Builder buider = new NotificationCompat.Builder(this, "notify")
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true);
        buider.addAction(action);
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        managerCompat.notify(102, buider.build());
    }

}
