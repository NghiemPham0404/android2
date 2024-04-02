package com.example.movieapp.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.movieapp.AsyncTasks.GetMovieVideo;
import com.example.movieapp.R;

public class PlayingFilm extends AppCompatActivity {

    ExoPlayer player;
    PlayerView styledPlayerView;

    private ImageButton backBtn;
    private int id;
    private String movie_name;
    TextView movie_title_playing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playing_film);
        id = getIntent().getIntExtra("film_id", -1);
        movie_name = getIntent().getStringExtra("movie_name");
        initComponents();


        Toast.makeText(this, ""+id, Toast.LENGTH_SHORT).show();
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
    }
    public void initVideo(){
        styledPlayerView.setPlayer(player);
        new GetMovieVideo("https://script.google.com/macros/s/AKfycbwb6OpcqdD1yJifVIZMeR5x2Ae1R5Ak-V04ASpXUNnkF1IjzbClCW8ZAdC0hoCE6QRp/exec",id, player).execute();
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