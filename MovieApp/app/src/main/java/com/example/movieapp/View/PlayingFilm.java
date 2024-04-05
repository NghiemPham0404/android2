package com.example.movieapp.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.movieapp.AsyncTasks.GetMovieVideo;
import com.example.movieapp.R;
import com.example.movieapp.Request.MyService2;
import com.example.movieapp.utils.Credentials;

import retrofit2.Call;

public class PlayingFilm extends AppCompatActivity {

    ExoPlayer player;
    PlayerView styledPlayerView;

    private ImageButton backBtn;
    private int id;
    private String movie_name;
    private String duration;
    TextView movie_title_playing;

    private String videoUrl;
    private View comment_box_layout_1, comment_box_layout_2;
    private EditText comment_box_1, comment_box_2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playing_film);
        id = getIntent().getIntExtra("film_id", -1);
        videoUrl = getIntent().getStringExtra("videoUrl");
        movie_name = getIntent().getStringExtra("movie_name");
        duration = getIntent().getStringExtra("duration");
//        Toast.makeText(this, duration, Toast.LENGTH_SHORT).show();
        initComponents();


//        Toast.makeText(this, ""+id, Toast.LENGTH_SHORT).show();
    }

    public void initComponents(){
            player = new ExoPlayer.Builder(PlayingFilm.this).build();
            styledPlayerView = findViewById(R.id.playing_film_window);
            backBtn = findViewById(R.id.backBtn_playing);
            backBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
            movie_title_playing = findViewById(R.id.film_title_playing);
            movie_title_playing.setText(movie_name);
            Log.i("movie id",id+"");
            initVideo();

            comment_box_layout_1 = findViewById(R.id.comment_box);
            comment_box_layout_2 = findViewById(R.id.comment_box_focus);
            comment_box_1 = comment_box_layout_1.findViewById(R.id.commentBox);
            comment_box_2 = comment_box_layout_2.findViewById(R.id.commentBox);

            comment_box_1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(hasFocus){
                        comment_box_layout_1.setVisibility(View.GONE);
                        comment_box_2.requestFocus();
                        comment_box_layout_2.setVisibility(View.VISIBLE);
                    }
                }
            });

            comment_box_2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(!hasFocus){
                        comment_box_layout_1.setVisibility(View.VISIBLE);
                        comment_box_1.setText(comment_box_2.getText().toString());
                        comment_box_layout_2.setVisibility(View.GONE);
                    }
                }
            });

            comment_box_2.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    comment_box_2.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(comment_box_2.getWindowToken(), 0);
                    return true; // Consume the event
                }
                return false; // Let the system handle other key events
            }
        });
    }
    public void initVideo(){
        MediaItem mediaItem = MediaItem.fromUri(videoUrl);
        styledPlayerView.setPlayer(player);

//        new GetMovieVideo(id, player).execute();
        player.setMediaItem(mediaItem);
        player.prepare();
        player.setPlayWhenReady(true);
    }

    public void initRelative(){

    }

    public void rating_and_comments(){

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.activity_playing_film);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            setContentView(R.layout.activity_playing_film);
        }
        initComponents();
    }

}