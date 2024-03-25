package com.example.movieapp.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

import android.content.res.Configuration;
import android.os.Bundle;

import com.example.movieapp.R;

public class PlayingFilm extends AppCompatActivity {

    ExoPlayer player;
    PlayerView styledPlayerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playing_film);
        initComponents();
    }

    public void initComponents(){
            player = new ExoPlayer.Builder(PlayingFilm.this).build();
            styledPlayerView = findViewById(R.id.playing_film_window);

            initVideo();
    }
    public void initVideo(){
        styledPlayerView.setPlayer(player);

        String videoUrl ="https://www.dropbox.com/scl/fi/g2skprihuaw2h6p50zvgf/Kore-Nani-Neko.mp4";
        MediaItem mediaItem = MediaItem.fromUri(videoUrl);

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