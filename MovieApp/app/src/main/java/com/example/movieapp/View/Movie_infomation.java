package com.example.movieapp.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
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
import com.example.movieapp.ViewModel.MovieListViewModel;
import com.example.movieapp.ViewModel.MovieViewModel;
import com.example.movieapp.utils.Credentials;
import com.example.movieapp.utils.MovieApi;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Movie_infomation extends AppCompatActivity {

    TextView film_title, film_overview, rating, genres_info, time_info, date_info, country_info;
    private ImageView poster_image;
    ImageButton backBtn;
    Button playButton, playTrailerButton;
    ToggleButton favorButton;

    ImageButton facebook_btn, twitter_btn, youtube_btn, instagram_btn, tiktok_btn;
    MovieModel movie;
    MovieViewModel movieViewModel;
    int movieId;

    private AccountModel loginAccount;
    RecyclerView cast_crew_recycler_view;
    private MovieApi movieApi;
    private ImageView imageView2;

    Call<DetailModel> changeFavorCall;
    private CastAdapter castAdapter, crewAdapter;
    Chip crewChip, castChip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_film_infomation);
        movieId = getIntent().getIntExtra("film_id", -1);
        loginAccount = (AccountModel) getIntent().getParcelableExtra("loginAccount");
        movieViewModel = new ViewModelProvider(this).get(MovieViewModel.class);
        ObserveAnyChange();
        init();
    }

    public void ObserveAnyChange(){
        if(movieViewModel!=null){
            movieViewModel.getMovie().observe(this, new Observer<MovieModel>() {
                @Override
                public void onChanged(MovieModel movieModel) {
                    movie = movieModel;
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initFavor();
    }

    public void init() {
        movieViewModel.searchMovieDetail(movieId);
        initComponent();
        initDetails();
    }

    public void initComponent() {
        movieApi = MyService.getMovieApi();
        poster_image = findViewById(R.id.filmImage);
        backBtn = findViewById(R.id.backBtn_info);
        film_title = findViewById(R.id.film_title_info);
        film_overview = findViewById(R.id.film_overview);
        cast_crew_recycler_view = findViewById(R.id.castRecyclerView);
        castChip = findViewById(R.id.cast_chip);
        crewChip = findViewById(R.id.crew_chip);

        rating = findViewById(R.id.movie_rating);
        date_info = findViewById(R.id.publish_date_info);
        time_info = findViewById(R.id.time_info);
        genres_info = findViewById(R.id.genre_info);
        country_info = findViewById(R.id.country_info);
        imageView2 = findViewById(R.id.imageView2);
        playButton = findViewById(R.id.playBtn_info);
        playTrailerButton =findViewById(R.id.play_trailer_btn);

        twitter_btn = findViewById(R.id.twitter_x_info);
        facebook_btn = findViewById(R.id.facebook_info);
        youtube_btn = findViewById(R.id.youtube_info);
        instagram_btn = findViewById(R.id.instagram_info);
        tiktok_btn = findViewById(R.id.tiktok_info);
    }

    private void initCast() {
        List<CastModel> castModels = movie.getCredits().getCast();
        castAdapter = new CastAdapter(Movie_infomation.this, castModels, loginAccount);
    }

    private void initCrew() {
        List<CastModel> crewModels = movie.getCredits().getCrew();
        crewAdapter = new CastAdapter(Movie_infomation.this, crewModels, loginAccount);
    }


    private void initDetails() {
        Call<MovieModel> detailsResponseCall = movieApi.searchMovieDetail(
                movieId,
                Credentials.API_KEY,
                "credits,videos,external_ids"
        );

        detailsResponseCall.enqueue(new Callback<MovieModel>() {
            @Override
            public void onResponse(Call<MovieModel> call, Response<MovieModel> response) {
                if (response.code() == 200) {
                    movie = response.body();
                    initCastCrew();
                    initFavor();
                    initVideo();
                    initSocialNetwork();
//                        Log.i("TAG DETAIL", movie.getGenres().get(0).getName());

                    float rate = Math.round(movie.getVote_average() * 100) * 1.0f / 100;
                    rating.setText(rate + "");
                    try {
                        String year = movie.getRelease_date().split("-")[0];
                        date_info.setText("(" + year + ")");
                    } catch (Exception ex) {
                        date_info.setText(movie.getRelease_date() + "");
                    }
                    time_info.setText(movie.getMaxDurationTime());
                    country_info.setText(movie.getProductionCountry());
                    genres_info.setText(movie.getGenresString());
                    new ImageLoader().loadImageIntoImageView(Movie_infomation.this, Credentials.BASE_IMAGE_URL + movie.getPoster_path(), poster_image, findViewById(R.id.shimmerLayout_info));
                    new ImageLoader().loadImageIntoImageView(Movie_infomation.this, Credentials.BASE_IMAGE_URL + movie.getPoster_path(), imageView2);
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

    private void initCastCrew() {
        initCast();
        initCrew();
        cast_crew_recycler_view.setAdapter(castAdapter);
        cast_crew_recycler_view.setLayoutManager(new LinearLayoutManager(Movie_infomation.this, LinearLayoutManager.HORIZONTAL, false));
        castChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crewChip.setChecked(false);
                cast_crew_recycler_view.setAdapter(castAdapter);
            }
        });

        crewChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                castChip.setChecked(false);
                cast_crew_recycler_view.setAdapter(crewAdapter);
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
                if (response.code() == 200) {
                    if(response.body().getUrl()!=null || response.body().getUrl720()!=null){
                            playButton.setText("Watch now");
                            playButton.setEnabled(true);
                            Log.i("VIDEO TASK", response.body().getUrl());
                            Intent playMovieIntent = new Intent(Movie_infomation.this, PlayingFilm.class);
                            playMovieIntent.putExtra("movie", movie);
                            playMovieIntent.putExtra("videoUrl", response.body().getUrl());
                            playMovieIntent.putExtra("videoUrl720", response.body().getUrl720());
                            playMovieIntent.putExtra("loginAccount", loginAccount);
                            playButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    startActivity(playMovieIntent);
                                }
                            });
                    }
                }else{
                    playButton.setText("Coming soon");

                }
            }

            @Override
            public void onFailure(Call<VideoModel> call, Throwable t) {
                Log.e("VIDEO TASK", "FAILURE");
            }
        });

        if (movie.getTrailer() != null) {
            playTrailerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Movie_infomation.this, PlayingTrailer.class);
                    intent.putExtra("video_string", movie.getTrailer());
                    startActivity(intent);
                }
            });
        }
    }

    public void initSocialNetwork(){
        if(movie.getExternal_ids().getTiktok_id() == null &&movie.getExternal_ids().getTwitter_id()==null && movie.getExternal_ids().getFacebook_id()==null
        &&movie.getExternal_ids().getYoutube_id() == null && movie.getExternal_ids().getInstagram_id()==null){
            findViewById(R.id.social_link).setVisibility(View.GONE);
        }

        if(movie.getExternal_ids().getTwitter_id()!=null){
            twitter_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent twitter_intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.twitter.com/" + movie.getExternal_ids().getTwitter_id()));
                    startActivity(twitter_intent);
                }
            });
        }else{
            twitter_btn.setVisibility(View.GONE);
        }

        if(movie.getExternal_ids().getFacebook_id()!=null){
            facebook_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent fb_intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/" + movie.getExternal_ids().getFacebook_id()));
                    startActivity(fb_intent);
                }
            });
        }else{
            facebook_btn.setVisibility(View.GONE);
        }

        if(movie.getExternal_ids().getInstagram_id()!=null){
            instagram_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent ins_intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/" + movie.getExternal_ids().getInstagram_id()));
                    startActivity(ins_intent);
                }
            });
        }else{
            instagram_btn.setVisibility(View.GONE);
        }

        if(movie.getExternal_ids().getYoutube_id()!=null){
            youtube_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent youtube_intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/" + movie.getExternal_ids().getYoutube_id()));
                    startActivity(youtube_intent);
                }
            });
        }else{
            youtube_btn.setVisibility(View.GONE);
        }

        if(movie.getExternal_ids().getTiktok_id()!=null){
            tiktok_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent tiktok_intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.tiktok.com/@" + movie.getExternal_ids().getTiktok_id()));
                    startActivity(tiktok_intent);
                }
            });
        }else{
            tiktok_btn.setVisibility(View.GONE);
        }
    }

    public void initFavor() {
        favorButton = findViewById(R.id.favorBtn);
        favorButton.setEnabled(false);
        Call<List<DetailModel>> favorListCall = MyService2.getApi().getFavorListByUserId(Credentials.functionname_detail, loginAccount.getUser_id());
        favorListCall.enqueue(new Callback<List<DetailModel>>() {
            @Override
            public void onResponse(Call<List<DetailModel>> call, Response<List<DetailModel>> response) {
                if (response.code() == 200) {
                    for (DetailModel favor : response.body()) {
                        if (favor.getMovieId() == movieId) {
                            movie.setDuration(favor.getDuration());
                            favorButton.setEnabled(true);
                            return;
                        }
                    }
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