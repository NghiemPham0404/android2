package com.example.movieapp.View;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.movieapp.Model.AccountModel;
import com.example.movieapp.Model.MovieModel;
import com.example.movieapp.View.LoginPackage.LoginViewActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

public class MovieInteractive {

    public static void openMovieInformation(Context context, int movie_id,AccountModel loginAccount){
        Intent movie_info_intent = new Intent(context, Movie_infomation.class);
        movie_info_intent.putExtra("film_id", movie_id);
        movie_info_intent.putExtra("loginAccount", (Parcelable) loginAccount);
        context.startActivity(movie_info_intent);
    }

    public static void initPlayMovieButton(Context context, String url, String url720, Button playButton, MovieModel movie, AccountModel loginAccount){

    }

    public void subcribeToNotification(int movie_id){
        FirebaseMessaging.getInstance().subscribeToTopic("notification/"+movie_id)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Subscribed";
                        if (!task.isSuccessful()) {
                            msg = "Subscribe failed";
                        }
                        Log.d("subcribe to movie", msg);
                    }
                });
    }

}
