package com.example.movieapp.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GestureDetectorCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.PictureInPictureParams;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Rational;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.movieapp.Adapters.FilmAdapter;
import com.example.movieapp.Adapters.ReviewApdater;
import com.example.movieapp.Model.AccountModel;
import com.example.movieapp.Model.DetailModel;
import com.example.movieapp.Model.MovieModel;
import com.example.movieapp.R;
import com.example.movieapp.Request.ImageLoader;
import com.example.movieapp.Request.MyService;
import com.example.movieapp.Request.MyService2;
import com.example.movieapp.Response.DetailResponse;
import com.example.movieapp.Response.MovieSearchResponse;
import com.example.movieapp.utils.Credentials;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;

import java.text.SimpleDateFormat;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlayingFilm extends AppCompatActivity implements View.OnTouchListener, ScaleGestureDetector.OnScaleGestureListener {

    ConstraintLayout layout;
    private static final String PLAYBACK_POSITION_KEY = "playback_position";
    private final boolean playWhenReady = true;

    SimpleExoPlayer player;
    PlayerView playerView;

    TextView totalRating;
    private ImageButton postReviewBtn;
    ImageView avatar;
    EditText reviewBox;
    RatingBar reviewRating;
    TextView movie_title_playing, movie_rating, publish_date, duration;
    private Drawable check_icon;
    private ImageView maximize_btn, pic_in_pic_btn, volume_btn, setting_btn, back_btn;
    private MovieModel movie;
    private int current_id;
    private AccountModel loginAccount;
    private String videoUrl, videoUrl720;
    private RecyclerView recommendRecycler;
    private Button reviewButton;
    private ToggleButton favorButton;
    private boolean full_screen, volume = true;
    private PopupWindow reviewSessionPopup, continuePlaybackPopup, resolutionChangePopup;
    private RecyclerView reviewsRecyclerView;
    private Call<DetailResponse> detailResponseCall;
    private ConstraintLayout error_layout, loadinglayout;
    private View popupReviewView, popupView2, popupView3;
    private FilmAdapter filmAdapter;
    private Call<MovieSearchResponse> recommendationsMovieslCall;
    private TextView btn_360;
    private LinearLayout btn_720;
    public int current_resolution = 720;
    private ReviewApdater reviewApdater;

    // các biến hỗ trợ phóng to màn hình
    View zoom_layout;
    ScaleGestureDetector scaleDetector;
    GestureDetectorCompat gestureDetector;
    private static final float MIN_ZOOM = 1.0f;
    private static final float MAX_ZOOM = 3.0f;
    boolean intLeft, intRight;
    private Display display;
    private Point size;

    private enum Mode {
        NONE,
        DRAG,
        ZOOM
    }

    private Mode mode = Mode.NONE;
    int device_width;
    private int swidth;
    private boolean isEnable = true;
    private float scale = 1.0f;
    private float lastScaleFactor = 0f;
    // khi ngón tay chạm vào màn hình
    private float startX = 0f;
    private float startY = 0f;
    // delta khoản cách giữa chạm và vuốt
    private float dx = 0f;
    private float dy = 0f;
    private float preDx = 0f;
    private float preDy = 0f;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.i("touch", "true");
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                if (scale > MIN_ZOOM) {
                    mode = Mode.DRAG;
                    startX = event.getX() - preDx;
                    startY = event.getY() - preDy;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                isEnable = false;
                if (mode == Mode.DRAG) {
                    dx = event.getX() - startX;
                    dy = event.getY() - startY;
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                mode = Mode.ZOOM;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                mode = Mode.DRAG;
                break;
            case MotionEvent.ACTION_UP:
                mode = Mode.NONE;
                preDx = dx;
                preDy = dy;
        }
        scaleDetector.onTouchEvent(event);
        gestureDetector.onTouchEvent(event);
        if((mode == Mode.DRAG) && scale >= MIN_ZOOM || mode == Mode.ZOOM){
//            zoom_layout.requestDisallowInterceptTouchEvent(true);
            float maxDx = (child().getWidth() - (child().getWidth()/scale)) / 2 * scale;
            float maxDy = (child().getHeight() - (child().getHeight()/scale))/2 * scale;
            dx = Math.min(Math.max(dx, -maxDx), maxDx);
            dy = Math.min(Math.max(dy, -maxDy) ,maxDy);
            applyScaleAndTranslation();
        }
        return false;
    }
    private void applyScaleAndTranslation(){
        child().setScaleX(scale);
        child().setScaleY(scale);
        child().setTranslationX(dx);
        child().setTranslationY(dy);
    }
    private View child(){
        return zoomLayout(0);
    }
    private View zoomLayout(int i){
        return playerView;
    }

    @Override
    public boolean onScale(@NonNull ScaleGestureDetector detector) {
        float scaleFactor = scaleDetector.getScaleFactor();
        if(lastScaleFactor == 0 || (Math.signum(scaleFactor) == Math.signum(lastScaleFactor))){
            scale *= scaleFactor;
            scale = Math.max(MIN_ZOOM, Math.min(scale, MAX_ZOOM));
            lastScaleFactor = scaleFactor;
        }else{
            lastScaleFactor = 0;
        }
        return true;
    }

    @Override
    public boolean onScaleBegin(@NonNull ScaleGestureDetector detector) {
        return true;
    }

    @Override
    public void onScaleEnd(@NonNull ScaleGestureDetector detector) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        check_icon = getResources().getDrawable(R.drawable.check_icon);
        setContentView(R.layout.activity_playing_film);
        setVideoView(getIntent());
        current_id = movie.getId();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(PLAYBACK_POSITION_KEY, player.getCurrentPosition());
    }

    public class GestureDetector extends android.view.GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDoubleTap(@NonNull MotionEvent e) {
            if (e.getX() < (swidth / 2)) {
                intLeft = true;
                intRight = false;
                player.seekTo((player.getCurrentPosition() >= 5000) ? player.getCurrentPosition() - 5000 : 0);
                Toast.makeText(getApplicationContext(), "rewind 5 s", Toast.LENGTH_SHORT);
            } else if (e.getX() > (swidth / 2)) {
                intLeft = false;
                intRight = true;
                player.seekTo((player.getCurrentPosition() < player.getDuration() - 5000) ? player.getCurrentPosition() + 5000 : player.getDuration());
            }
            return super.onDoubleTap(e);
        }

        @Override
        public boolean onSingleTapConfirmed(@NonNull MotionEvent e) {
            if (isEnable) {
                isEnable = false;
            } else {
                isEnable = true;
            }
            return super.onSingleTapConfirmed(e);
        }
    }

    public void initComponent_Potral() {
        movie_title_playing = findViewById(R.id.film_title_playing);
        movie_title_playing.setText(movie.getTitle());


        movie_rating = findViewById(R.id.movie_rating);
        float rate = Math.round(movie.getVote_average() * 100) * 1.0f / 100;
        movie_rating.setText(rate + "");

        publish_date = findViewById(R.id.publish_date_playing);
        try {
            String year = movie.getRelease_date().split("-")[0];
            publish_date.setText("(" + year + ")");
        } catch (Exception ex) {
            publish_date.setText(movie.getRelease_date() + "");
        }

        duration = findViewById(R.id.time_playing);
        duration.setText(movie.getMaxDurationTime());

        TextView recommend_title = findViewById(R.id.relative_movie_film_group).findViewById(R.id.groupTitle);
        recommend_title.setText("Recommendation");

        favorButton = findViewById(R.id.favorBtn_playing);

        Call<List<DetailModel>> favorListCall = MyService2.getApi().getFavorListByUserId(Credentials.functionname_detail, loginAccount.getUser_id());
        favorListCall.enqueue(new Callback<List<DetailModel>>() {
            @Override
            public void onResponse(Call<List<DetailModel>> call, Response<List<DetailModel>> response) {
                if (response.code() == 200) {
                    for (DetailModel favor : response.body()) {
                        if (favor.getMovieId() == movie.getId()) {
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

        initRecommendations();

        reviewButton = findViewById(R.id.rating_btn);
        reviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initReview();
            }
        });
    }

    public void initComponentsLandcaspe() {
        zoom_layout = playerView;
        display = getWindowManager().getDefaultDisplay();
        size = new Point();
        display.getSize(size);
        swidth = size.x;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        device_width = displayMetrics.widthPixels;
        zoom_layout.setOnTouchListener(this);
        scaleDetector = new ScaleGestureDetector(getApplicationContext(), this);
        gestureDetector = new GestureDetectorCompat(getApplicationContext(), new GestureDetector());
    }

    private void initReview() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        popupReviewView = inflater.inflate(R.layout.comment_session, null);

        boolean focusable = true;
        reviewSessionPopup = new PopupWindow(popupReviewView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT, focusable);
        reviewSessionPopup.setAnimationStyle(R.style.PopupAnimation);

        layout = findViewById(R.id.playing_layout);

        loadinglayout = popupReviewView.findViewById(R.id.loadingLayout);
        loadinglayout.setVisibility(View.VISIBLE);

        error_layout = popupReviewView.findViewById(R.id.error_loading);

        totalRating = popupReviewView.findViewById(R.id.total_rating_rv);

        detailResponseCall = MyService2.getApi().getReviewByFilmId("detail", movie.getId());
        detailResponseCall.enqueue(new Callback<DetailResponse>() {
            @Override
            public void onResponse(Call<DetailResponse> call, Response<DetailResponse> response) {
                if (response.code() == 200) {
                    List<DetailModel> detailModels = response.body().getAllReviews();
                    ReviewApdater.DeleteInterface deleteInterface = new ReviewApdater.DeleteInterface() {
                        @Override
                        public void delete() {
                            Call<DetailModel> detailModelCall = MyService2.getApi().deleteReview(Credentials.functionname_delete_detail, loginAccount.getUser_id(), movie.getId());
                            detailModelCall.enqueue(new Callback<DetailModel>() {
                                @Override
                                public void onResponse(Call<DetailModel> call, Response<DetailModel> response) {
                                    Log.i("delete review", "success");
                                    popupReviewView.findViewById(R.id.comment_box).setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onFailure(Call<DetailModel> call, Throwable t) {
                                    Log.i("delete review", "" + t);
                                }
                            });
                        }
                    };
                    reviewApdater = new ReviewApdater(PlayingFilm.this, detailModels, loginAccount.getUser_id(), deleteInterface);
                    reviewApdater.removeEmpty();
                    reviewApdater.notifyDataSetChanged();
                    for (int i = 0; i < detailModels.size(); i++) {
                        if (detailModels.get(i).getUserId() == loginAccount.getUser_id())
                            popupReviewView.findViewById(R.id.comment_box).setVisibility(View.GONE);
                    }
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(PlayingFilm.this);
                    reviewsRecyclerView = popupReviewView.findViewById(R.id.recyclerView);
                    reviewsRecyclerView.setLayoutManager(linearLayoutManager);
                    reviewsRecyclerView.setAdapter(reviewApdater);
                    loadinglayout.setVisibility(View.GONE);
                    error_layout.setVisibility(View.GONE);
                    totalRating.setText("" + response.body().getAllReviews().size());
                }
            }

            @Override
            public void onFailure(Call<DetailResponse> call, Throwable t) {
                Log.e("GET REVIEWs", "FAIL" + t.toString());
                error_layout.setVisibility(View.VISIBLE);
                loadinglayout.setVisibility(View.GONE);
            }
        });
        layout.post(new Runnable() {
            @Override
            public void run() {
                reviewSessionPopup.showAtLocation(layout, Gravity.BOTTOM, 0, 0);
            }
        });

        initUserReviewBox();
    }

    private void initRecommendations() {
        recommendRecycler = findViewById(R.id.relative_movie_film_group).findViewById(R.id.movieGroup_recyclerview);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(PlayingFilm.this, RecyclerView.HORIZONTAL, false);
        recommendRecycler.setLayoutManager(linearLayoutManager);

        recommendationsMovieslCall = MyService.getMovieApi().searchMovieRelativeRecommendation(movie.getId(), Credentials.API_KEY);
        recommendationsMovieslCall.enqueue(new Callback<MovieSearchResponse>() {
            @Override
            public void onResponse(Call<MovieSearchResponse> call, Response<MovieSearchResponse> response) {
                if (response.code() == 200) {
                    List<MovieModel> movieModelList = response.body().getMovies();
                    movieModelList.remove(movie);
                    filmAdapter = new FilmAdapter(movieModelList, PlayingFilm.this, loginAccount);
                    recommendRecycler.setAdapter(filmAdapter);
                    if (filmAdapter == null) {
                        initRecommendationByGenres();
                    } else {
                        if (filmAdapter.getItemCount() == 0) {
                            initRecommendationByGenres();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<MovieSearchResponse> call, Throwable t) {
                Log.e("LOAD RECOMMEND", "FAIL");
            }
        });
    }

    private void initRecommendationByGenres() {
        recommendationsMovieslCall = MyService.getMovieApi().searchMovieRelativeRecommendationByGernes(
                Credentials.API_KEY,
                movie.getGenresIdString()
        );

        recommendationsMovieslCall.enqueue(new Callback<MovieSearchResponse>() {
            @Override
            public void onResponse(Call<MovieSearchResponse> call, Response<MovieSearchResponse> response) {
                if (response.code() == 200) {
                    List<MovieModel> movieModelList = response.body().getMovies();
                    for (int i = 0; i < movieModelList.size(); i++) {
                        if (movieModelList.get(i).getId() == movie.getId()) {
                            movieModelList.remove(movieModelList.get(i));
                        }
                    }
                    filmAdapter = new FilmAdapter(movieModelList, PlayingFilm.this, loginAccount);
                    recommendRecycler.setAdapter(filmAdapter);
                }

            }

            @Override
            public void onFailure(Call<MovieSearchResponse> call, Throwable t) {
                Log.e("LOAD RECOMMEND", "FAIL");
            }
        });
    }

    private void initializePlayer() {
        if (player == null) {
            player = new SimpleExoPlayer.Builder(this).build();
            MediaItem mediaItem;
            if (videoUrl720 != null) {
                mediaItem = MediaItem.fromUri(Uri.parse(videoUrl720));
                current_resolution = 720;
            } else {
                mediaItem = MediaItem.fromUri(Uri.parse(videoUrl));
                current_resolution = 360;
            }
            player.setMediaItem(mediaItem);

            player.setPlayWhenReady(playWhenReady);
            player.prepare();

        }
        playerView.setPlayer(player);

        movie_title_playing = playerView.findViewById(R.id.film_title_playing_2);
        if (full_screen) {
            movie_title_playing.setText(movie.getTitle());
        } else {
            movie_title_playing.setText("");
        }
        initPlayerButton();
    }

    private void reInitPlayer() {
        player.release();
        player = new SimpleExoPlayer.Builder(this).build();
        MediaItem mediaItem;
        if (videoUrl720 != null) {
            mediaItem = MediaItem.fromUri(Uri.parse(videoUrl720));
        } else {
            mediaItem = MediaItem.fromUri(Uri.parse(videoUrl));
        }

        player.setMediaItem(mediaItem);

        player.setPlayWhenReady(playWhenReady);
        player.prepare();

        continuePlayBackPostion();

        playerView.setPlayer(player);

        movie_title_playing = playerView.findViewById(R.id.film_title_playing_2);
        if (full_screen) {
            movie_title_playing.setText(movie.getTitle());
        } else {
            movie_title_playing.setText("");
        }
        initPlayerButton();
    }

    private void switchToFullScreen() {
        setContentView(R.layout.activity_playing_film_landcaspe);
        playerView = findViewById(R.id.playing_film_window);
        initializePlayer();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
       hideSystemUI();
        maximize_btn.setBackgroundResource(R.drawable.exit_maximize_ic);
        pic_in_pic_btn.setVisibility(View.GONE);
        initComponentsLandcaspe();
    }

    private void switchToPortrait() {
        setContentView(R.layout.activity_playing_film);
        playerView = findViewById(R.id.playing_film_window);
        initializePlayer();
        showSystemUI();
        maximize_btn.setBackgroundResource(R.drawable.maximize_ic);
        initComponent_Potral();
        pic_in_pic_btn.setVisibility(View.VISIBLE);
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
    public void onStop() {
        super.onStop();
        player.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        postPlayBackDuration();
    }

    @Override
    public void onBackPressed() {
        if(full_screen){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            full_screen = false;
        }else{
            super.onBackPressed();
            player.release();
        }
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

    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode);
        if (!isInPictureInPictureMode) {
            hidePlayerControl();
        } else {
          showPlayerControl();
        }
    }

    public void initPlayerButton() {
        maximize_btn = playerView.findViewById(R.id.exo_fullscreen_icon);
        pic_in_pic_btn = playerView.findViewById(R.id.exo_pic_in_pip);
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

        pic_in_pic_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PictureInPictureMode();
            }
        });

        volume_btn = playerView.findViewById(R.id.exo_volume);
        volume_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (volume) {
                    volume_btn.setBackgroundResource(R.drawable.volume_off_icon);
                    player.setVolume(0);
                    volume = false;
                } else {
                    volume_btn.setBackgroundResource(R.drawable.volume_up_icon);
                    player.setVolume(1);
                    volume = true;
                }
            }
        });

        setting_btn = playerView.findViewById(R.id.exo_setting);

        setting_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeResolutionBox();
            }
        });

        back_btn = playerView.findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (full_screen) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    full_screen = false;
                } else {
                    onBackPressed();
                }
            }
        });
    }

    public void changeFavorite() {
        Call<DetailModel> changeFavorCall = MyService2.getApi().addToFavor("detail", loginAccount.getUser_id(), movie.getId());
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

    public void initUserReviewBox() {
        avatar = popupReviewView.findViewById(R.id.imageAvatar);
        new ImageLoader().loadAvatar(PlayingFilm.this, loginAccount.getAvatar(), avatar, popupReviewView.findViewById(R.id.avatarText), loginAccount.getUsername());
        reviewRating = popupReviewView.findViewById(R.id.ratingBar_comment_box);
        reviewBox = popupReviewView.findViewById(R.id.commentBox);


        postReviewBtn = popupReviewView.findViewById(R.id.send_comment_box);
        postReviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postReview(reviewRating.getRating(), reviewBox.getText().toString().trim());
            }
        });
    }

    public void postReview(float rating, String review) {
        Call<DetailModel> detailModelCall = MyService2.getApi().addReview(Credentials.functionname_detail, loginAccount.getUser_id(), movie.getId(), rating + "", review);
        detailModelCall.enqueue(new Callback<DetailModel>() {
            @Override
            public void onResponse(Call<DetailModel> call, Response<DetailModel> response) {
                initReview();
            }

            @Override
            public void onFailure(Call<DetailModel> call, Throwable t) {
                Toast.makeText(PlayingFilm.this, "posting review is fail,", Toast.LENGTH_LONG).show();
            }
        });
    }
    public void postPlayBackDuration() {
        String history = new SimpleDateFormat("yyyy/M/dd").format(System.currentTimeMillis());
        long current_position = player.getCurrentPosition();
        String duration = current_position + "-" + history;
        movie.setDuration(duration);
        Call<DetailModel> detailModelCall = MyService2.getApi().postPlayBackDuration(Credentials.functionname_detail, loginAccount.getUser_id(), movie.getId(), duration);
        detailModelCall.enqueue(new Callback<DetailModel>() {
            @Override
            public void onResponse(Call<DetailModel> call, Response<DetailModel> response) {
                if (response.code() == 200) {
                    Toast.makeText(PlayingFilm.this, "Save successfully", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<DetailModel> call, Throwable t) {
                Toast.makeText(PlayingFilm.this, "Save fail", Toast.LENGTH_LONG).show();
            }
        });
    }
    @Override
    public void onUserLeaveHint() {
        enterPictureInPictureMode();
    }
    private void PictureInPictureMode() {
        Rational aspectRatio = new Rational(16, 9);
        PictureInPictureParams.Builder pipBuilder = new PictureInPictureParams.Builder();
        pipBuilder.setAspectRatio(aspectRatio);
        enterPictureInPictureMode(pipBuilder.build());
        if (reviewSessionPopup != null) {
            reviewSessionPopup.dismiss();
        }

        if (resolutionChangePopup != null) {
            reviewSessionPopup.dismiss();
        }

        if (continuePlaybackPopup != null) {
            continuePlaybackPopup.dismiss();
        }
    }
    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setVideoView(intent);
    }
    @SuppressLint("InvalidWakeLockTag")
    public void setVideoView(Intent intent) {
        movie = (MovieModel) intent.getParcelableExtra("movie");
        loginAccount = (AccountModel) intent.getParcelableExtra("loginAccount");
        videoUrl = intent.getStringExtra("videoUrl");
        videoUrl720 = intent.getStringExtra("videoUrl720");

        playerView = findViewById(R.id.playing_film_window);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            full_screen = true;
            switchToFullScreen();
        } else {
            switchToPortrait();
            full_screen = false;
        }

        if (current_id != movie.getId()) {
            reInitPlayer();
            current_id = movie.getId();
        }

        continuePlayBackPostion();
    }
    public void continuePlayBackPostion() {
        if (movie.getPlayBackPositition() <= 1000) return;

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        popupView2 = inflater.inflate(R.layout.continue_playing, null);

        layout = findViewById(R.id.playing_layout);

        boolean focusable = true;
        continuePlaybackPopup = new PopupWindow(popupView2, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT, focusable);
        continuePlaybackPopup.setAnimationStyle(R.style.PopupAnimation);

        TextView text_cp = popupView2.findViewById(R.id.text_cp);
        text_cp.setText(movie.getPlayBackPositionString());

        ImageButton close_btn_cp = popupView2.findViewById(R.id.close_btn_cp);
        close_btn_cp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                continuePlaybackPopup.dismiss();
            }
        });

        Button ok_btn_cp = popupView2.findViewById(R.id.ok_btn_cp);
        ok_btn_cp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                continuePlaybackPopup.dismiss();
                player.seekTo(movie.getPlayBackPositition());
            }
        });
        playerView.post(new Runnable() {
            @Override
            public void run() {
                continuePlaybackPopup.showAtLocation(playerView, Gravity.TOP, 0, 0);
            }
        });
    }
    public void changeResolutionBox() {
        Toast.makeText(getBaseContext(), "setting click", Toast.LENGTH_SHORT).show();
        if (resolutionChangePopup == null) {
            // initial change resolution box
            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            popupView3 = inflater.inflate(R.layout.player_config, null);
            boolean focusable = true;
            resolutionChangePopup = new PopupWindow(popupView3, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT, focusable);

            // btn change resolution btn
            btn_360 = popupView3.findViewById(R.id.btn_360);
            btn_720 = popupView3.findViewById(R.id.btn_720);

            if (videoUrl == null) {
                btn_360.setVisibility(View.GONE);
            } else {
                btn_360.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        changeResolution(360);
                    }
                });
            }

            if (videoUrl720 == null) {
                btn_720.setVisibility(View.GONE);
            } else {
                btn_720.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        changeResolution(720);
                    }
                });
            }

            if (current_resolution == 720) {
                ((TextView) btn_720.findViewById(R.id.btn_720_text)).setTextColor(getColor(R.color.neon_pink));
            } else {
                btn_360.setTextColor(getColor(R.color.neon_pink));
            }
        }
        popupView3.findViewById(R.id.player_config_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resolutionChangePopup.dismiss();
            }
        });
        resolutionChangePopup.showAtLocation(playerView, Gravity.TOP, 0, 0);
        hideSystemUI();
    }
    public void changeResolution(int resolution) {
        if (resolution == 720) {
            if (current_resolution == 720) {
                return;
            } else {
                current_resolution = 720;
                ((TextView) btn_720.findViewById(R.id.btn_720_text)).setTextColor(getColor(R.color.neon_pink));
                btn_360.setTextColor(getColor(R.color.white));
                player.stop();
                long c_position = player.getCurrentPosition();
                MediaItem mediaItem = MediaItem.fromUri(Uri.parse(videoUrl720));
                player.setMediaItem(mediaItem);
                player.setPlayWhenReady(true);
                player.prepare();
                player.seekTo(c_position);
            }
        } else {
            if (current_resolution == 360) {
                return;
            } else {
                current_resolution = 360;
                btn_360.setTextColor(getColor(R.color.neon_pink));
                ((TextView) btn_720.findViewById(R.id.btn_720_text)).setTextColor(getColor(R.color.white));
                player.stop();
                long c_position = player.getCurrentPosition();
                MediaItem mediaItem = MediaItem.fromUri(Uri.parse(videoUrl));
                player.setMediaItem(mediaItem);
                player.setPlayWhenReady(true);
                player.prepare();
                player.seekTo(c_position);
            }
        }
        resolutionChangePopup.dismiss();
    }
    private void hideSystemUI() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // Hide the status bar and navigation bar
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }
    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_VISIBLE);
    }

    private void hidePlayerControl(){
        playerView.findViewById(R.id.player_control).setVisibility(View.GONE);
    }

    private void showPlayerControl(){
        playerView.findViewById(R.id.player_control).setVisibility(View.VISIBLE);
    }
}
