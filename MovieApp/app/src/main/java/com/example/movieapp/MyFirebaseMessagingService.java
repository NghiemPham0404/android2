package com.example.movieapp;

import android.accounts.Account;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.movieapp.Model.AccountModel;
import com.example.movieapp.Model.LoginModel;
import com.example.movieapp.Request.MyService2;
import com.example.movieapp.View.Movie_infomation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    AccountModel accountModel;
    String title, body;
    Map<String, String > data;
    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        Log.i("FirebaseMessage", "Message received: " + message.getNotification());
        Log.i("FirebaseMessage", "Message received: " + message.getData());
        Log.i("FirebaseMessage", "success");
        getFirebaseMassage(message.getNotification().getTitle(), message.getNotification().getBody(), message.getData());
        super.onMessageReceived(message);
    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        // Send the token to your server or perform any other actions
        Log.d("FCM Token", token);
    }

    private void getFirebaseMassage(String title, String body, Map<String, String> data) {
        Log.i("movie_id",data.get("movie_id"));
        Log.i("movie_id","123124124251");
        this.title = title;
        this.body = body;
        this.data = data;

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){

        }

        NotificationCompat.Builder buider = new NotificationCompat.Builder(getBaseContext(), "notify")
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.small_notify_icon)
                .setAutoCancel(true)
                ;
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(getBaseContext());
        if (ActivityCompat.checkSelfPermission(getBaseContext(), android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        managerCompat.notify(102, buider.build());
    }
}
