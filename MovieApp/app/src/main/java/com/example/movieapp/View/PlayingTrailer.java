package com.example.movieapp.View;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.example.movieapp.R;

public class PlayingTrailer extends AppCompatActivity {

    WebView trailer_window;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playing_trailer);

        String video_string = getIntent().getStringExtra("video_string");
        trailer_window = findViewById(R.id.trailer_window);
        trailer_window.loadData(video_string, "text/html","utf-8");
        trailer_window.getSettings().setJavaScriptEnabled(true);
        trailer_window.setWebChromeClient(new WebChromeClient());
    }
}