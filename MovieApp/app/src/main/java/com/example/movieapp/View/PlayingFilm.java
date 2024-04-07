package com.example.movieapp.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.movieapp.Adapters.FilmAdapter;
import com.example.movieapp.Adapters.ReviewApdater;
import com.example.movieapp.Model.DetailModel;
import com.example.movieapp.R;
import com.example.movieapp.Request.MyService;
import com.example.movieapp.Request.MyService2;
import com.example.movieapp.Response.DetailResponse;
import com.example.movieapp.Response.MovieSearchResponse;
import com.example.movieapp.utils.Credentials;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlayingFilm extends AppCompatActivity {

    ConstraintLayout layout;
    private static final String PLAYBACK_POSITION_KEY = "playback_position";
    private boolean playWhenReady = true;
    private int currentWindow = 0;
    private long playbackPosition = 0;
    private PowerManager.WakeLock wakeLock;

    SimpleExoPlayer player;
    PlayerView playerView;

    private ImageButton backBtn;
    TextView movie_title_playing;

    private ImageView maximize_btn;
    private int id;
    private String userId;
    private String movie_name;
    private String duration;
    private String videoUrl;
    private RecyclerView recommendRecycler;
    private Button reviewButton;
    private ToggleButton favorButton;

    private int current_duration;
    private boolean full_screen;
    private PopupWindow reviewSessionPopup;
    private RecyclerView reviewsRecyclerView;

    @SuppressLint("InvalidWakeLockTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playing_film);
        id = getIntent().getIntExtra("film_id", -1);
        userId = getIntent().getStringExtra("userId");
        videoUrl = getIntent().getStringExtra("videoUrl");
        movie_name = getIntent().getStringExtra("movie_name");
        duration = getIntent().getStringExtra("duration");

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "MovieApp tag");
        wakeLock.acquire();

        playerView = findViewById(R.id.playing_film_window);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            full_screen = true;
            switchToFullScreen();
        } else {
            switchToPortrait();
            full_screen = false;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(PLAYBACK_POSITION_KEY, player.getCurrentPosition());
    }


    public void initComponent_Potral(){
        backBtn = findViewById(R.id.backBtn_playing);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        movie_title_playing = findViewById(R.id.film_title_playing);
        movie_title_playing.setText(movie_name);

        TextView recommend_title = findViewById(R.id.relative_movie_film_group).findViewById(R.id.groupTitle);
        recommend_title.setText("Recommend");

        favorButton = findViewById(R.id.favorBtn_playing);
        favorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFavorite();
            }
        });

        initRecommendations();

        reviewButton = findViewById(R.id.rating_btn);
        reviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initReview();
            }
        });
    }
    private void initReview(){
        if(reviewSessionPopup==null){
            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            View popupView = inflater.inflate(R.layout.comment_session, null);

            boolean focusable = true;
            reviewSessionPopup = new PopupWindow(popupView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT, focusable);

            layout = findViewById(R.id.playing_layout);
            reviewSessionPopup.setAnimationStyle(R.anim.review_popup_animation);

            Call<DetailResponse> detailResponseCall = MyService2.getApi().getReviewByFilmId("detail", id);
            detailResponseCall.enqueue(new Callback<DetailResponse>() {
                @Override
                public void onResponse(Call<DetailResponse> call, Response<DetailResponse> response) {
                    if(response.code()==200){
                        List<DetailModel> detailModels = response.body().getAllReviews();
                        ReviewApdater reviewApdater = new ReviewApdater(PlayingFilm.this, detailModels);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(PlayingFilm.this);
                        reviewsRecyclerView =  popupView.findViewById(R.id.recyclerView);
                        reviewsRecyclerView.setLayoutManager(linearLayoutManager);
                        reviewsRecyclerView.setAdapter(reviewApdater);
                    }
                }

                @Override
                public void onFailure(Call<DetailResponse> call, Throwable t) {
                    Log.e("GET REVIEWs" , "FAIL");
                }
            });
            layout.post(new Runnable() {
                @Override
                public void run() {
                    reviewSessionPopup.showAtLocation(layout, Gravity.BOTTOM, 0, 0);
                }
            });
        }else{
            reviewSessionPopup.showAtLocation(layout, Gravity.BOTTOM, 0, 0);
        }
    }

    private void initRecommendations(){
        recommendRecycler = findViewById(R.id.relative_movie_film_group).findViewById(R.id.movieGroup_recyclerview);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(PlayingFilm.this,RecyclerView.HORIZONTAL, false);
        recommendRecycler.setLayoutManager(linearLayoutManager);


        Call<MovieSearchResponse> recommendationsMovieslCall = MyService.getMovieApi().searchMovieRelativeRecommendation(id, Credentials.API_KEY);
        recommendationsMovieslCall.enqueue(new Callback<MovieSearchResponse>() {
            @Override
            public void onResponse(Call<MovieSearchResponse> call, Response<MovieSearchResponse> response) {
                FilmAdapter filmAdapter = new FilmAdapter(response.body().getMovies(), PlayingFilm.this);
                filmAdapter.setUserId(userId);
                recommendRecycler.setAdapter(filmAdapter);
            }

            @Override
            public void onFailure(Call<MovieSearchResponse> call, Throwable t) {
                Log.e("LOAD RECOMMEND", "FAIL");
            }
        });
    }

    private void initializePlayer() {
        if(player == null){
            player = new SimpleExoPlayer.Builder(this).build();
            MediaItem mediaItem = MediaItem.fromUri(Uri.parse(videoUrl));
            player.setMediaItem(mediaItem);

            player.setPlayWhenReady(playWhenReady);
            player.seekTo(currentWindow, playbackPosition);
            player.prepare();
        }
        playerView.setPlayer(player);
        initPlayerButton();
    }

    private void switchToFullScreen() {
        setContentView(R.layout.activity_playing_film_landcaspe);
        playerView = findViewById(R.id.playing_film_window);
        initializePlayer();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if(getSupportActionBar()!=null){
            getSupportActionBar().hide();
        }
        if(getWindow().getDecorView()!=null){
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
        maximize_btn.setBackgroundResource(R.drawable.exit_maximize_ic);
    }

    private void switchToPortrait() {
        setContentView(R.layout.activity_playing_film);
        playerView = findViewById(R.id.playing_film_window);
        initializePlayer();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if(getSupportActionBar()!=null){
            getSupportActionBar().show();
        }
        maximize_btn.setBackgroundResource(R.drawable.maximize_ic);
        initComponent_Potral();
    }


    @Override
    public void onStart() {
        super.onStart();
        initializePlayer();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (player == null) {
            initializePlayer();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
        }
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        player.release();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            full_screen = true;
            switchToFullScreen();
        } else {
            switchToPortrait();
            full_screen = false;
        }
    }

    public void initPlayerButton(){
        maximize_btn = playerView.findViewById(R.id.exo_fullscreen_icon);
        maximize_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (full_screen) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    full_screen = false;
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    full_screen = true;
                }
            }
        });
    }
    public void changeFavorite() {
        Call<DetailModel> changeFavorCall = MyService2.getApi().addToFavor("detail", userId, id);
        changeFavorCall.enqueue(new Callback<DetailModel>() {
            @Override
            public void onResponse(Call<DetailModel> call, Response<DetailModel> response) {
                if (response.code() == 200) {

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
