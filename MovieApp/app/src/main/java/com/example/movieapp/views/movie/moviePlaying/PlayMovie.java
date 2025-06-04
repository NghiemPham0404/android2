package com.example.movieapp.views.movie.moviePlaying;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.DisplayCutoutCompat;
import androidx.core.view.GestureDetectorCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.PictureInPictureParams;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Rational;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.movieapp.ViewModel.MovieViewModel;
import com.example.movieapp.ViewModel.UserViewModel;
import com.example.movieapp.data.Model.AccountModel;
import com.example.movieapp.data.Model.DetailModel;
import com.example.movieapp.data.Model.MovieModel;
import com.example.movieapp.R;
import com.example.movieapp.utils.Credentials;

import java.text.SimpleDateFormat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlayMovie extends AppCompatActivity implements ScaleGestureDetector.OnScaleGestureListener, View.OnTouchListener {
    private MovieViewModel movieViewModel;

    // variables
    private int current_id = -1,  currentResolution = 720;
    boolean isFullScreen = false, isShowControls = true, isMute = false;
    String videoUrl, videoUrl720;
    MovieModel movie;
    private int currentPosition = 0;
    private static final String POSITION_KEY = "position";
    private AnimatorSet animatorSet1, animatorSet2;
    // variables for pinch to zoom feature
    ScaleGestureDetector scaleDetector;
    GestureDetectorCompat gestureDetector;
    private static final float MIN_ZOOM = 1.0f;
    private static final float MAX_ZOOM = 2.0f;
    boolean intLeft, intRight;
    private Display display;
    private Point size;
    private Handler handler;
    private Runnable runnable2, runnable1;


    private enum Mode {
        NONE,
        DRAG,
        ZOOM
    }
    private PlayMovie.Mode mode = PlayMovie.Mode.NONE;
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
    // components
    TextView movieTitle, currentTime, endTime, btn360;
    ImageButton backBtn, playBtn, stopBtn, volumeBtn, settingBtn, picInPicBtn, fullScreenButton;
    VideoView moviePlayer; MediaPlayer mediaPlayer;
    SeekBar movieSeekBar; ProgressBar loading;
    LinearLayout layout1, layout2, layout3, btn720;
    RelativeLayout zoom_layout;
    ConstraintLayout rewindBg, forwardBg, layout;
    private View popupView2, popupView3;;
    private PopupWindow continuePlaybackPopup, resolutionChangePopup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Make the activity fullscreen
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_play_movie);
        intoEdgeToEdgeMode();
        movieViewModel = new ViewModelProvider(this).get(MovieViewModel.class);

        movie = getIntent().getParcelableExtra("movie");

        videoUrl = getIntent().getStringExtra("videoUrl");
        videoUrl720 = getIntent().getStringExtra("videoUrl720");

        if (current_id != movie.getId()) {
            current_id = movie.getId();
            initComponents();
            initFeatures();
        }
    }

    //TODO : binding to the components
    private void initComponents() {
        // movieTitle
        movieTitle = findViewById(R.id.movie_title);

        // buttons
        backBtn = findViewById(R.id.back_btn);
        playBtn = findViewById(R.id.playBtn);
        stopBtn = findViewById(R.id.pauseBtn);
        volumeBtn = findViewById(R.id.exo_volume);
        fullScreenButton = findViewById(R.id.exo_fullscreen);
        picInPicBtn = findViewById(R.id.exo_pic_in_pip);
        settingBtn = findViewById(R.id.exo_setting);

        // player
        moviePlayer = findViewById(R.id.moviePlayer);

        // seekbar
        movieSeekBar = findViewById(R.id.movieSeekBar);
        currentTime = findViewById(R.id.exo_position);
        endTime = findViewById(R.id.exo_duration);

        // layout binding
        layout1 = findViewById(R.id.layout_1);
        layout2 = findViewById(R.id.layout_2);
        layout3 = findViewById(R.id.layout_3);
        zoom_layout = findViewById(R.id.zoom_layout);
        display = getWindowManager().getDefaultDisplay();
        rewindBg = findViewById(R.id.rewind_bg);
        forwardBg = findViewById(R.id.forward_bg);
        layout = findViewById(R.id.layout);

        loading = findViewById(R.id.loading);
    }

    //TODO : initialize features
    private void initFeatures() {
        movieTitle.setText(movie.getTitle());
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeVideoState(false);
            }
        });
        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeVideoState(true);
            }
        });

        animatorSet1 = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.forward_rewind_animation);
        animatorSet1.setTarget(rewindBg);

        animatorSet2= (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.forward_rewind_animation);
        animatorSet2.setTarget(forwardBg);

        // click screen
        zoom_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("click", "true");

            }
        });
        fullScreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isFullScreen){
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }else{
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }
            }
        });
        // pinch to zoom
        size = new Point();
        display.getSize(size);
        swidth = size.x;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        device_width = displayMetrics.widthPixels;
        zoom_layout.setOnTouchListener(this);
        scaleDetector = new ScaleGestureDetector(getApplicationContext(), this);
        gestureDetector = new GestureDetectorCompat(getApplicationContext(), new GestureDetector());

        // handler support automatic hide controls and update seekbar progress
        handler = new Handler();
        //enter pip
        picInPicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPictureInPictureMode();
            }
        });
        //open change resolution popup
        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeResolutionBox();
            }
        });
        initPlayer();
        showControls();
    }

    private class GestureDetector extends android.view.GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDoubleTap(@NonNull MotionEvent e) {
            if (e.getX() < (swidth / 2)) {
                intLeft = true;
                intRight = false;
                seekTo(moviePlayer.getCurrentPosition() - 10000);
                animatorSet1.start();
            } else if (e.getX() > (swidth / 2)) {
                intLeft = false;
                intRight = true;
                seekTo(moviePlayer.getCurrentPosition() + 10000);
                animatorSet2.start();
            }
            return super.onDoubleTap(e);
        }

        @Override
        public boolean onSingleTapConfirmed(@NonNull MotionEvent e) {
            if (isShowControls) {
                hideControls();
            } else {
                showControls();
            }
            return super.onSingleTapConfirmed(e);
        }
    }

    //TODO : create movie player
    public void initPlayer() {
        if(videoUrl720!=null){
            moviePlayer.setVideoPath(videoUrl720);
        }else{
            moviePlayer.setVideoPath(videoUrl);
        }

        moviePlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mediaPlayer = mp;
                mp.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                    @Override
                    public void onSeekComplete(MediaPlayer mp) {
                        mp.start();
                        onLoadingComplete(true);
                    }
                });
                mp.seekTo(currentPosition, MediaPlayer.SEEK_CLOSEST);
                mp.start();
                onLoadingComplete(true);
                volumeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(isMute){
                            changeVolumeState(mp);
                        }else{
                            changeVolumeState(mp);
                        }
                    }
                });
                initSeekBarProgress();
                initSeekBar();
            }
        });
        moviePlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                    changeVideoState(false);
                    return true;
                }
                return false;
            }
        });
        // Listener for when the video is paused
        moviePlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // Video playback completed
                changeVideoState(true);
            }
        });
        continuePlayBackPostion();
    }

    private void onLoadingComplete(boolean isComplete) {
        if(isComplete){
            loading.setVisibility(View.GONE);
            stopBtn.setVisibility(View.VISIBLE);
        }else{
            loading.setVisibility(View.VISIBLE);
            stopBtn.setVisibility(View.GONE);
        }
    }

    //TODO : seekbar progess initialize
    public void initSeekBar(){
        int maxDuration = moviePlayer.getDuration();
        movieSeekBar.setMin(0);
        movieSeekBar.setMax(maxDuration);
        endTime.setText(fromIntToHourString(maxDuration));
        movieSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    seekTo(Math.min(progress, moviePlayer.getDuration()));
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if(runnable2 !=null){
                    handler.removeCallbacks(runnable2);
                }
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(runnable2 !=null){
                    handler.postDelayed(runnable2, 5000);
                }
            }
        });
    }

    // TODO : Convert int to hour, using to covert duration of video view to string and display it on the textviews
    public String fromIntToHourString(int ms){
        String time;
        int x, seconds, minutes, hours;
        x = ms / 1000;
        seconds = x % 60;
        x /= 60;
        minutes = x % 60;
        x /= 60;
        hours = x % 24;
        if (hours != 0)
            time = String.format("%02d", hours) + ":" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds);
        else time = String.format("%02d", minutes) + ":" + String.format("%02d", seconds);
        return time;
    }

    //TODO : seekbar progress update
    public void initSeekBarProgress(){
        runnable1 = new Runnable() {
            @Override
            public void run() {
                if (moviePlayer.getDuration()>0){
                    int currentPosition = moviePlayer.getCurrentPosition();
                    movieSeekBar.setProgress(currentPosition);
                    currentTime.setText(fromIntToHourString(currentPosition));
                    handler.postDelayed(this,0);
                }
            }
        };
        handler.postDelayed(runnable1,500);
    }

    // TODO : forward or rewind video
    public void seekTo(int next) {
        if(runnable2!=null){
            handler.removeCallbacks(runnable2);
            handler.postDelayed(runnable2, 5000);
        }
        mediaPlayer.seekTo(next, MediaPlayer.SEEK_CLOSEST);
        moviePlayer.pause();
        onLoadingComplete(false);

        if(runnable1!=null){
            handler.removeCallbacks(runnable1);
            handler.postDelayed(runnable1, 0);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
//        Log.i("touch", "true");
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                if (scale > MIN_ZOOM) {
                    mode = PlayMovie.Mode.DRAG;
                    startX = event.getX() - preDx;
                    startY = event.getY() - preDy;
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                mode = PlayMovie.Mode.ZOOM;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                mode = PlayMovie.Mode.DRAG;
                break;
            case MotionEvent.ACTION_UP:
                mode = PlayMovie.Mode.NONE;
                preDx = dx;
                preDy = dy;
        }
        scaleDetector.onTouchEvent(event);
        gestureDetector.onTouchEvent(event);
        if ((mode == PlayMovie.Mode.DRAG) && scale >= MIN_ZOOM || mode == PlayMovie.Mode.ZOOM) {
            zoom_layout.requestDisallowInterceptTouchEvent(true);
            float maxDx = (child().getWidth() - (child().getWidth() / scale)) / 2 * scale;
            float maxDy = (child().getHeight() - (child().getHeight() / scale)) / 2 * scale;
            dx = Math.min(Math.max(dx, -maxDx), maxDx);
            dy = Math.min(Math.max(dy, -maxDy), maxDy);
            applyScaleAndTranslation();
        }
        return false;
    }

    private void applyScaleAndTranslation() {
        child().setScaleX(scale);
        child().setScaleY(scale);
        child().setTranslationX(dx);
        child().setTranslationY(dy);
    }

    private View child() {
        return zoomLayout();
    }

    private View zoomLayout() {
        return moviePlayer;
    }

    @Override
    public boolean onScale(@NonNull ScaleGestureDetector detector) {
        float scaleFactor = scaleDetector.getScaleFactor();
        if (lastScaleFactor == 0 || (Math.signum(scaleFactor) == Math.signum(lastScaleFactor))) {
            scale *= scaleFactor;
            scale = Math.max(MIN_ZOOM, Math.min(scale, MAX_ZOOM));
            lastScaleFactor = scaleFactor;
        } else {
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
    //TODO : hide controls
    public void hideControls() {
        layout1.setVisibility(View.INVISIBLE);
        layout2.setVisibility(View.INVISIBLE);
        layout3.setVisibility(View.INVISIBLE);
        movieSeekBar.setVisibility(View.INVISIBLE);

        isShowControls = false;
    }

    //TODO : show controls
    public void showControls() {
        layout1.setVisibility(View.VISIBLE);
        layout2.setVisibility(View.VISIBLE);
        layout3.setVisibility(View.VISIBLE);
        movieSeekBar.setVisibility(View.VISIBLE);
        isShowControls = true;
        runnable2 = new Runnable() {
            @Override
            public void run() {
                if (isShowControls){
                    hideControls();
                }
            }
        };
        handler.postDelayed(runnable2,5000);
    }


    // TODO : start or stop video
    public void changeVideoState(boolean isPlaying) {
        if (isPlaying) {
            moviePlayer.pause();
            playBtn.setVisibility(View.VISIBLE);
            stopBtn.setVisibility(View.GONE);
        } else {
            moviePlayer.start();
            stopBtn.setVisibility(View.VISIBLE);
            playBtn.setVisibility(View.GONE);
        }
    }

    //TODO : change volume state from enable to mute and reverse
    public void changeVolumeState(MediaPlayer mediaPlayer){
        if(!isMute){
            volumeBtn.setImageDrawable(getDrawable(R.drawable.volume_off_icon));
            mediaPlayer.setVolume(0,0);
            isMute = true;
        }else{
            volumeBtn.setImageDrawable(getDrawable(R.drawable.volume_up_icon));
            mediaPlayer.setVolume(1,1);
            isMute = false;
        }
    }

    //TODO : change the swidth when configuration changed
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            isFullScreen = true;
            fullScreenButton.setImageDrawable(getDrawable(R.drawable.exit_maximize_ic));
            swidth = size.y;
        }else{
            isFullScreen = false;
            fullScreenButton.setImageDrawable(getDrawable(R.drawable.maximize_ic));
            swidth = size.x;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(POSITION_KEY, currentPosition);
    }

    @Override
    protected void onPause() {
        super.onPause();
        currentPosition = moviePlayer.getCurrentPosition();
        moviePlayer.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        moviePlayer.seekTo(currentPosition);
        moviePlayer.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        postPlayBackDuration();
    }

    @Override
    public void onBackPressed() {
        if(isFullScreen){
            fullScreenButton.callOnClick();
        }else{
            super.onBackPressed();
        }
    }

    //TODO : make display screen fit to cutout device
    public void intoEdgeToEdgeMode(){
        // padding according to cutout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.player_control), (view, insets) -> {
            WindowInsetsCompat insetsCompat = WindowInsetsCompat.toWindowInsetsCompat(insets.toWindowInsets());
            DisplayCutoutCompat cutout = insetsCompat.getDisplayCutout();

            if (cutout != null) {
                // Get the safe insets
                int left = cutout.getSafeInsetLeft();
                int top = cutout.getSafeInsetTop();
                int right = cutout.getSafeInsetRight();
                int bottom = cutout.getSafeInsetBottom();

                // Apply padding to the root view
                view.setPadding(left, top, right, bottom);
            }
            return insets;
        });

        //full screen request
        final Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LOW_PROFILE |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }
    // TODO : help users continue to play the current position they left
    public void continuePlayBackPostion() {
        if (movie.getPlayBackPositition() <= 1000) return;

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        popupView2 = inflater.inflate(R.layout.continue_playing, null);

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
                seekTo((int)movie.getPlayBackPositition());
            }
        });
        layout.post(new Runnable() {
            @Override
            public void run() {
                continuePlaybackPopup.showAtLocation(layout, Gravity.TOP, 0, 0);
            }
        });
    }

    //TODO : post the play back duration when user exit app
    public void postPlayBackDuration() {
        String history = new SimpleDateFormat("yyyy/M/dd").format(System.currentTimeMillis());
        int current_position = movieSeekBar.getProgress();
        String duration = current_position + "-" + history;
        Log.i("movieapp", duration);
        movie.setDuration(duration);
        movieViewModel.postPlayingProgress(movie.getId(), duration);
    }

    //TODO : into Picture in picture mode
    @Override
    public void onUserLeaveHint() {
        onPictureInPictureMode();
    }
    private void onPictureInPictureMode() {
        Rational aspectRatio = new Rational(16, 9);
        PictureInPictureParams.Builder pipBuilder = new PictureInPictureParams.Builder();
        pipBuilder.setAspectRatio(aspectRatio);
        enterPictureInPictureMode(pipBuilder.build());
        if (continuePlaybackPopup != null) {
            continuePlaybackPopup.dismiss();
        }
    }
    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode);
        if(isInPictureInPictureMode) {
            hideControls();
            moviePlayer.start();
        }else {
            currentPosition = moviePlayer.getCurrentPosition();
            showControls();
        }
    }

    //TODO : handle when open a new intent with this activity while in picture in picture mode
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        movie = intent.getParcelableExtra("movie");
        videoUrl = intent.getStringExtra("videoUrl");
        videoUrl720 = intent.getStringExtra("videoUrl720");

        if (current_id != movie.getId()) {
            current_id = movie.getId();
            initComponents();
            initFeatures();
        }
    }

    //TODO : initialize popup to change Resolution
    public void changeResolutionBox() {
        Toast.makeText(getBaseContext(), "setting click", Toast.LENGTH_SHORT).show();
        if (resolutionChangePopup == null) {
            // initial change resolution box
            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            popupView3 = inflater.inflate(R.layout.player_config, null);
            boolean focusable = true;
            resolutionChangePopup = new PopupWindow(popupView3, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT, focusable);

            // btn change resolution btn
            btn360 = popupView3.findViewById(R.id.btn_360);
            btn720 = popupView3.findViewById(R.id.btn_720);

            if (videoUrl == null) {
                btn360.setVisibility(View.GONE);
            } else {
                btn360.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        changeResolution(360);
                    }
                });
            }

            if (videoUrl720 == null) {
                btn720.setVisibility(View.GONE);
            } else {
                btn720.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        changeResolution(720);
                    }
                });
            }

            if (currentResolution == 720) {
                ((TextView) btn720.findViewById(R.id.btn_720_text)).setTextColor(getColor(R.color.neon_pink));
            } else {
                btn360.setTextColor(getColor(R.color.neon_pink));
            }
        }
        popupView3.findViewById(R.id.player_config_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resolutionChangePopup.dismiss();
            }
        });
        resolutionChangePopup.showAtLocation(moviePlayer, Gravity.TOP, 0, 0);
    }
    public void changeResolution(int resolution) {
        if (resolution == 720) {
            if (currentResolution == 720) {
                return;
            } else {
                currentResolution = 720;
                ((TextView) btn720.findViewById(R.id.btn_720_text)).setTextColor(getColor(R.color.neon_pink));
                btn360.setTextColor(getColor(R.color.white));
                moviePlayer.pause();
                currentPosition = moviePlayer.getCurrentPosition();
                moviePlayer.setVideoURI(Uri.parse(videoUrl720));
            }
        } else {
            if (currentResolution == 360) {
                return;
            } else {
                currentResolution = 360;
                btn360.setTextColor(getColor(R.color.neon_pink));
                ((TextView) btn720.findViewById(R.id.btn_720_text)).setTextColor(getColor(R.color.white));
                moviePlayer.pause();
                currentPosition = moviePlayer.getCurrentPosition();
                moviePlayer.setVideoURI(Uri.parse(videoUrl));
            }
        }
        resolutionChangePopup.dismiss();
    }
}