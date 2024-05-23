package com.example.movieapp.Services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Parcelable;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.movieapp.Model.AccountModel;
import com.example.movieapp.Model.NotificationModel;
import com.example.movieapp.R;
import com.example.movieapp.Request.LoginAccountRequest;
import com.example.movieapp.View.MovieInfomation;
import com.example.movieapp.View.UserPackage.Notification.NotificationContentView;
import com.example.movieapp.ViewModel.NotificationViewModel;
import com.example.movieapp.ViewModel.UserViewModel;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.regex.Pattern;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    String title, body;
    Map<String, String> data;
    NotificationViewModel notificationViewModel;
    UserViewModel userViewModel;
    public final static String regexPattern = ".*\\.(jpg|jpeg|png|gif|bmp|webp)$";
    public final static Pattern pattern = Pattern.compile(regexPattern, Pattern.CASE_INSENSITIVE);

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        getFirebaseMessage(message.getNotification().getTitle(), message.getNotification().getBody(), message.getData());
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
        String content = data.get("content");

        // lưu lại Notification
        NotificationModel notification = new NotificationModel();
        notification.setTitle(title);
        notification.setBody(body);
        notification.setContent(content);
        notification.setMovieId(movie_id);
        notification.setUserId(accountModel.getUser_id());
        notification.setLargeImage(large_image);
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-YYYY HH:mm:ss");
        notification.setTime(df.format(new Date(System.currentTimeMillis())));

        Intent intent ;
        if(movie_id > -1){
            intent = new Intent(this, MovieInfomation.class);
            intent.putExtra("film_id", movie_id);
            intent.putExtra("loginAccount", (Parcelable) accountModel);
        }else{
            intent = new Intent(this, NotificationContentView.class);
            intent.putExtra("loginAccount", (Parcelable) accountModel);
            intent.putExtra("notification", notification);
        }

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


        if(large_image!=null){
            if(large_image.contains("http") && pattern.matcher(large_image).matches() || large_image.contains("http")){
                Glide.with(this)
                        .asBitmap()
                        .load(large_image)
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
                                // Set the loaded bitmap as the large icon of the notification
                                builder.setLargeIcon(bitmap);
                            }
                        });
            }
        }

        int notificationId = (int) System.currentTimeMillis();
        notificationManager.notify(notificationId, builder.build());
        notificationViewModel = new NotificationViewModel(getApplication());
        notificationViewModel.insert(notification);
    }
}
