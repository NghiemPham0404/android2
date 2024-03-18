package com.example.movieapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.movieapp.Model.MovieModel;
import com.example.movieapp.Request.MyService;
import com.example.movieapp.Response.MovieSearchResponse;
import com.example.movieapp.View.HomeActivity;
import com.example.movieapp.utils.Credentials;
import com.example.movieapp.utils.MovieApi;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    Button loginBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginBtn = findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intend_home = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(intend_home);
            }
        });

//        getRetrofitResponse();
    }

    //test search movie
//    public void getRetrofitResponse(){
//        MovieApi movieApi = MyService.getMovieApi();
//        Call<MovieSearchResponse> responseCall = movieApi.
//                searchMovie(
//                        Credentials.SEARCH_MOVIE_URL,
//                        Credentials.API_KEY,
//                "Avengers",
//                        "1");
//
//        responseCall.enqueue(new Callback<MovieSearchResponse>() {
//            @Override
//            public void onResponse(Call<MovieSearchResponse> call, Response<MovieSearchResponse> response) {
//                if(response.code() == 200){
//                    Log.v("Tag", response.body().toString());
//                    List<MovieModel> movies = new ArrayList<>(response.body().getMovies());
//                    for(MovieModel movieModel : movies){
//                        Log.v("Tag", movieModel.getTitle());
//                    }
//
//                }
//            }
//
//            @Override
//            public void onFailure(Call<MovieSearchResponse> call, Throwable t) {
//                Toast.makeText(getApplicationContext(), "Faluire", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
}