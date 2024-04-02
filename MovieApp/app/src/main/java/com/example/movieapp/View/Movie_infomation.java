package com.example.movieapp.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.movieapp.Adapters.CastAdapter;
import com.example.movieapp.Adapters.FilmAdapter;
import com.example.movieapp.AsyncTasks.DownloadImageTask;
import com.example.movieapp.AsyncTasks.GetMovieVideo;
import com.example.movieapp.Interfaces.Form_validate;
import com.example.movieapp.Model.CastModel;
import com.example.movieapp.Model.MovieModel;
import com.example.movieapp.Model.VideoModel;
import com.example.movieapp.R;
import com.example.movieapp.Request.MyService;
import com.example.movieapp.Response.CastResponse;
import com.example.movieapp.Response.VideoResponse;
import com.example.movieapp.utils.Credentials;
import com.example.movieapp.utils.MovieApi;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Movie_infomation extends AppCompatActivity{

    TextView film_title, film_overview, rating, genres_info, time_info, date_info;
    private ImageView poster_image;
    ImageButton backBtn;
    Button playButton;
    MovieModel movie;
    int id;

    RecyclerView castRecyclerView;
    private MovieApi movieApi;

    private ImageView imageView2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_film_infomation);
       id = getIntent().getIntExtra("film_id",-1);
       init();

    }

    public void init(){
        movieApi = MyService.getMovieApi();
        int orientation = getResources().getConfiguration().orientation;
        Log.i("IS LANDCAPSE ", ""+ (orientation == Configuration.ORIENTATION_PORTRAIT));
        if(orientation == Configuration.ORIENTATION_PORTRAIT){
            poster_image = findViewById(R.id.filmImage);
            backBtn = findViewById(R.id.backBtn_info);
            film_title = findViewById(R.id.film_title_info);
            film_overview = findViewById(R.id.film_overview);
            castRecyclerView = findViewById(R.id.castRecyclerView);

            rating = findViewById(R.id.movie_rating);
            date_info = findViewById(R.id.publish_date_info);
            time_info = findViewById(R.id.time_info);
            genres_info = findViewById(R.id.genre_info);
            imageView2 = findViewById(R.id.imageView2);
            playButton = findViewById(R.id.playBtn_info);
            initComponent();
        }
    }

    public void initComponent(){
        initDetails();
        initCast();
    }

    private void initCast() {
        Call<CastResponse> castResponseCall = movieApi.searchCastByFilmID(
                id,
                Credentials.API_KEY
        );
        castResponseCall.enqueue(new Callback<CastResponse>() {
            @Override
            public void onResponse(Call<CastResponse> call, Response<CastResponse> response) {
                List<CastModel> castModels = response.body().getCastModels();
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(Movie_infomation.this, LinearLayoutManager.HORIZONTAL, false);
                CastAdapter castAdapter = new CastAdapter(Movie_infomation.this, castModels);
                castRecyclerView.setAdapter(castAdapter);
                castRecyclerView.setLayoutManager(linearLayoutManager);
            }

            @Override
            public void onFailure(Call<CastResponse> call, Throwable t) {
                Toast.makeText(Movie_infomation.this, "failure cast", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initDetails() {


        Call<MovieModel> detailsResponseCall = movieApi.searchMovieDetail(
                            id,
                        Credentials.API_KEY
        );

        detailsResponseCall.enqueue(new Callback<MovieModel>() {
            @Override
            public void onResponse(Call<MovieModel> call, Response<MovieModel> response) {
                if(response.code() == 200){
                        movie = response.body();
//                        Log.i("TAG DETAIL", movie.getGenres().get(0).getName());

                    float rate = Math.round(movie.getVote_average()*100)*1.0f/100;
                    rating.setText(rate+"");
                    date_info.setText(movie.getRelease_date() + "");
                    time_info.setText(movie.getMaxDurationTime());
                    genres_info.setText(movie.getGenresString());
                    new DownloadImageTask(poster_image, findViewById(R.id.shimmerLayout_info), Credentials.BASE_IMAGE_URL + movie.getPoster_path()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        imageView2.setRenderEffect(RenderEffect.createBlurEffect(100,100, Shader.TileMode.MIRROR));
                        new DownloadImageTask(imageView2, Credentials.BASE_IMAGE_URL + movie.getPoster_path()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        findViewById(R.id.darken_screen).setVisibility(View.GONE);
                    }else{
                        new DownloadImageTask(imageView2, Credentials.BASE_IMAGE_URL + movie.getPoster_path()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }

                    new GetMovieVideo("https://script.google.com/macros/s/AKfycbwb6OpcqdD1yJifVIZMeR5x2Ae1R5Ak-V04ASpXUNnkF1IjzbClCW8ZAdC0hoCE6QRp/exec",id, (Button) findViewById(R.id.playBtn_info), Movie_infomation.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


                    film_title.setText(movie.getTitle());
                    film_overview.setText(movie.getOverriew());
                    backBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onBackPressed();
                        }
                    });
                    playButton.setEnabled(false);
                    playButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Movie_infomation.this, PlayingFilm.class);
                            intent.putExtra("film_id", movie.getId());
                            intent.putExtra("movie_name", movie.getTitle());
                            startActivity(intent);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<MovieModel> call, Throwable t) {
                Toast.makeText(getBaseContext(), "Failure Details", Toast.LENGTH_SHORT).show();
            }
        });
    }
}