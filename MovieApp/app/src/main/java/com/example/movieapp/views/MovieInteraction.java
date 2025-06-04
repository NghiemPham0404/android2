package com.example.movieapp.views;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.movieapp.data.Model.user.UserDTO;
import com.example.movieapp.views.movie.MovieInformation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

public class MovieInteraction {

    public static void openMovieInformation(Context context, int movie_id, UserDTO.UserInfo loginAccount){
        Intent movie_info_intent = new Intent(context, MovieInformation.class);
        movie_info_intent.putExtra("film_id", movie_id);
        movie_info_intent.putExtra("loginAccount", (Parcelable) loginAccount);
        context.startActivity(movie_info_intent);
    }
    public static void subcribeToNotification(int movie_id){
        FirebaseMessaging.getInstance().subscribeToTopic("notification"+movie_id)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Subscribed";
                        if (!task.isSuccessful()) {
                            msg = "Subscribe failed"+ task.getException();
                        }
                        Log.d("subcribe to movie " + movie_id, msg);
                    }
                });
    }

    public static void unsubcribeToNotification(int movie_id){
        FirebaseMessaging.getInstance().unsubscribeFromTopic("notification"+movie_id).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                String msg = "Unsubscribed";
                if (!task.isSuccessful()) {
                    msg = "Unsubscribe failed"+ task.getException();
                }
                Log.d("unsubcribe to movie " + movie_id, msg);
            }
        });
    }

}
