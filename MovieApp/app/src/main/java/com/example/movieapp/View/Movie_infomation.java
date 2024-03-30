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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Movie_infomation extends AppCompatActivity{

    TextView film_title, film_overview, rating, genres_info, time_info, date_info;
    private ImageView poster_image;
    ImageButton backBtn;
    Button playButton;
    MovieModel movie;

    RecyclerView castRecyclerView;
    private MovieApi movieApi;

    private WebView trailer_webView;
    private ImageView imageView2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_film_infomation);
        MovieModel passMovie = getIntent().getParcelableExtra("movie");

        if (passMovie != null) {
            // Use the retrieved object
            movie = passMovie;
        }
        init();

    }

    public void init(){
        poster_image = findViewById(R.id.filmImage);
        backBtn = findViewById(R.id.backBtn_info);
        film_title = findViewById(R.id.film_title_info);
        film_overview = findViewById(R.id.film_overview);
        castRecyclerView = findViewById(R.id.castRecyclerView);
        trailer_webView = findViewById(R.id.trailer_webview);
        rating = findViewById(R.id.movie_rating);
        date_info = findViewById(R.id.publish_date_info);
        time_info = findViewById(R.id.time_info);
        genres_info = findViewById(R.id.genre_info);
        imageView2 = findViewById(R.id.imageView2);
        playButton = findViewById(R.id.playBtn_info);

        int orientation = getResources().getConfiguration().orientation;
        if(orientation == Configuration.ORIENTATION_PORTRAIT){
            initComponent();
        }else{
            initTrailers();
        }

    }

    public void initComponent(){
        new DownloadImageTask(poster_image).execute(Credentials.BASE_IMAGE_URL + movie.getPoster_path());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            imageView2.setRenderEffect(RenderEffect.createBlurEffect(100,100, Shader.TileMode.MIRROR));
            new DownloadImageTask(imageView2).execute(Credentials.BASE_IMAGE_URL + movie.getPoster_path());
        }else{
            imageView2.setVisibility(View.GONE);
        }
        this.film_title.setText(this.movie.getTitle());
        this.film_overview.setText(this.movie.getOverriew());
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Movie_infomation.this, PlayingFilm.class);
                intent.putExtra("film id", movie.getId());
                startActivity(intent);
            }
        });

        movieApi = MyService.getMovieApi();
        initDetails();
        initCast();
        initTrailers();
    }

    private void initTrailers() {
        Call<VideoResponse> videoResponseCall = movieApi.searchVideoByFilmID(
                movie.getId(),
                Credentials.API_KEY
        );
        videoResponseCall.enqueue(new Callback<VideoResponse>() {
            @Override
            public void onResponse(Call<VideoResponse> call, Response<VideoResponse> response) {
                if(response.code() == 200){
                    List<VideoModel> videoModels = response.body().getVideoModels();
                    if(videoModels.size() == 0){
                        trailer_webView.setVisibility(View.GONE);
                    }else{
                        boolean found = false;
                        for(int i = 0; i<videoModels.size(); i++){
                            if(videoModels.get(i).getType().equalsIgnoreCase("Trailer")){
                                String video = "<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/"+videoModels.get(i).getKey()+"\" title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\" allowfullscreen=\"true\"></iframe>";
                                trailer_webView.loadData(video, "text/html","utf-8");
                                trailer_webView.getSettings().setJavaScriptEnabled(true);
                                trailer_webView.setWebChromeClient(new WebChromeClient());
                            }
                        }
                        if(found == false){

                        }
                    }
                }

            }

            @Override
            public void onFailure(Call<VideoResponse> call, Throwable t) {
                Toast.makeText(Movie_infomation.this, "failure trailers", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initCast() {
        Call<CastResponse> castResponseCall = movieApi.searchCastByFilmID(
                movie.getId(),
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
                            movie.getId(),
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
                }
            }

            @Override
            public void onFailure(Call<MovieModel> call, Throwable t) {
                Toast.makeText(getBaseContext(), "Failure Details", Toast.LENGTH_SHORT).show();
            }
        });
    }
}