package com.example.movieapp.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.movieapp.Adapters.CastAdapter;
import com.example.movieapp.Model.AccountModel;
import com.example.movieapp.Model.CastModel;
import com.example.movieapp.Model.DetailModel;
import com.example.movieapp.Model.MovieModel;
import com.example.movieapp.Model.VideoModel;
import com.example.movieapp.R;
import com.example.movieapp.Request.ImageLoader;
import com.example.movieapp.Request.MyService;
import com.example.movieapp.Request.MyService2;
import com.example.movieapp.utils.Credentials;
import com.example.movieapp.utils.MovieApi;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Movie_infomation extends AppCompatActivity {

    TextView film_title, film_overview, rating, genres_info, time_info, date_info;
    private ImageView poster_image;
    ImageButton backBtn;
    Button playButton;
    ToggleButton favorButton;
    MovieModel movie;
    int movieId;

    private AccountModel loginAccount;
    RecyclerView castRecyclerView;
    RecyclerView crewRecyclerView;
    private MovieApi movieApi;

    private ImageView imageView2;

    Call<DetailModel> changeFavorCall;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_film_infomation);
        movieId = getIntent().getIntExtra("film_id", -1);
        loginAccount = (AccountModel) getIntent().getParcelableExtra("loginAccount");
//        Toast.makeText(this, loginAccount.getUser_id(), Toast.LENGTH_SHORT).show();
        init();
    }

    @Override
    public void onResume(){
        super.onResume();
        initFavor();
    }
    public void init() {
        initComponent();
        initDetails();
    }

    public void initComponent() {
        movieApi = MyService.getMovieApi();
        poster_image = findViewById(R.id.filmImage);
        backBtn = findViewById(R.id.backBtn_info);
        film_title = findViewById(R.id.film_title_info);
        film_overview = findViewById(R.id.film_overview);
        castRecyclerView = findViewById(R.id.castRecyclerView);
        crewRecyclerView = findViewById(R.id.crewRecyclerView);

        rating = findViewById(R.id.movie_rating);
        date_info = findViewById(R.id.publish_date_info);
        time_info = findViewById(R.id.time_info);
        genres_info = findViewById(R.id.genre_info);
        imageView2 = findViewById(R.id.imageView2);
        playButton = findViewById(R.id.playBtn_info);
    }

    private void initCast() {
                List<CastModel> castModels = movie.getCredits().getCast();
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(Movie_infomation.this, LinearLayoutManager.HORIZONTAL, false);
                CastAdapter castAdapter = new CastAdapter(Movie_infomation.this, castModels, loginAccount);
                castRecyclerView.setAdapter(castAdapter);
                castRecyclerView.setLayoutManager(linearLayoutManager);
    }
    private void initCrew(){
        List<CastModel> crewModels = movie.getCredits().getCrew();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(Movie_infomation.this, LinearLayoutManager.HORIZONTAL, false);
        CastAdapter crewAdapter = new CastAdapter(Movie_infomation.this,  crewModels, loginAccount);
        crewRecyclerView.setAdapter(crewAdapter);
        crewRecyclerView.setLayoutManager(linearLayoutManager);
    }



    private void initDetails() {
        Call<MovieModel> detailsResponseCall = movieApi.searchMovieDetail(
                movieId,
                Credentials.API_KEY,
                "credits,videos"
        );

        detailsResponseCall.enqueue(new Callback<MovieModel>() {
            @Override
            public void onResponse(Call<MovieModel> call, Response<MovieModel> response) {
                if (response.code() == 200) {
                    movie = response.body();
                    initCast();
                    initCrew();
                    initFavor();
                    initVideo();
//                        Log.i("TAG DETAIL", movie.getGenres().get(0).getName());

                    float rate = Math.round(movie.getVote_average() * 100) * 1.0f / 100;
                    rating.setText(rate + "");
                    try {
                        String year =movie.getRelease_date().split("-")[0];
                        date_info.setText("("+year + ")");
                    }catch (Exception ex){
                        date_info.setText(movie.getRelease_date() + "");
                    }
                    time_info.setText(movie.getMaxDurationTime());
                    genres_info.setText(movie.getGenresString());
                    new ImageLoader().loadImageIntoImageView(Movie_infomation.this,Credentials.BASE_IMAGE_URL + movie.getPoster_path(), poster_image, findViewById(R.id.shimmerLayout_info));
                    new ImageLoader().loadImageIntoImageView(Movie_infomation.this,  Credentials.BASE_IMAGE_URL + movie.getPoster_path(), imageView2);
                        initVideo();

                    film_title.setText(movie.getTitle());
                    film_overview.setText(movie.getOverriew());
                    backBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onBackPressed();
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

    private void initVideo() {
        playButton.setEnabled(false);
        playButton.setText("Loading...");
        Call<VideoModel> videoModelCall = MyService2.getApi().getMovieVideo(Credentials.functionname_video, movieId);
        videoModelCall.enqueue(new Callback<VideoModel>() {
            @Override
            public void onResponse(Call<VideoModel> call, Response<VideoModel> response) {
                if(response.code() == 200){
                    if(response.body().getUrl()!=null && !response.body().getUrl().equalsIgnoreCase("")){
                        playButton.setBackgroundResource(R.drawable.gradient_corner_bg);
                        playButton.setText("Watch now");
                        playButton.setEnabled(true);
                        Log.i("VIDEO TASK", response.body().getUrl());
                        playButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent playMovieIntent = new Intent(Movie_infomation.this, PlayingFilm.class);
                                playMovieIntent.putExtra("movie",movie);
                                playMovieIntent.putExtra("videoUrl",response.body().getUrl());
                                playMovieIntent.putExtra("loginAccount", loginAccount );
                                startActivity(playMovieIntent);
                            }
                        });
                    }else{
                        if(movie.getTrailer()!=null){
                            playButton.setText("Play Trailer");
                            playButton.setBackgroundResource(R.drawable.play_trailer_btn);
                            playButton.setEnabled(true);
                            playButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(Movie_infomation.this, PlayingTrailer.class);
                                    intent.putExtra("video_string", movie.getTrailer());
                                    startActivity(intent);
                                }
                            });
                        }else{
                            playButton.setText("Upcoming");
                        }

                    }
                }
            }

            @Override
            public void onFailure(Call<VideoModel> call, Throwable t) {
                Log.e("VIDEO TASK", "FAILURE");
            }
        });
    }

    public void initFavor() {
        favorButton = findViewById(R.id.favorBtn);
        favorButton.setText("loading");
        favorButton.setChecked(false);
        favorButton.setEnabled(false);
        Call<List<DetailModel>> favorListCall = MyService2.getApi().getFavorListByUserId(Credentials.functionname_detail, loginAccount.getUser_id());
        favorListCall.enqueue(new Callback<List<DetailModel>>() {
            @Override
            public void onResponse(Call<List<DetailModel>> call, Response<List<DetailModel>>response) {
                if (response.code() == 200) {
                    for (DetailModel favor : response.body()) {
                        if (favor.getMovieId() ==  movieId) {
                            movie.setDuration(favor.getDuration());
                            favorButton.setChecked(true);
                            favorButton.setEnabled(true);
                            return;
                        }
                    }
                    favorButton.setChecked(false);
                    favorButton.setEnabled(true);
                }
            }

            @Override
            public void onFailure(Call<List<DetailModel>> call, Throwable t) {
                Log.e("FAVOR TASK", "Cant init favor");
            }
        });
        favorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFavorite();
            }
        });
    }

    public void changeFavorite() {
            changeFavorCall = MyService2.getApi().addToFavor("detail", loginAccount.getUser_id(), movieId);
            changeFavorCall.enqueue(new Callback<DetailModel>() {
                @Override
                public void onResponse(Call<DetailModel> call, Response<DetailModel> response) {
                    if (response.code() == 200) {
                        changeFavorCall = null;
                    }
                }

                @Override
                public void onFailure(Call<DetailModel> call, Throwable t) {
                    Log.e("FAVOR TASK", "change favor fail");
                    favorButton.setEnabled(true);
                }
            });
    }
}