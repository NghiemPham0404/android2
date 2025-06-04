package com.example.movieapp.views.movie.trailerPlaying;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.example.movieapp.R;

public class PlayingTrailer extends AppCompatActivity {

    WebView trailer_window;
    private String video_string;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playing_trailer);
       video_string = getIntent().getStringExtra("video_string");
       initPlayer();
    }

    public void initPlayer(){
        String html_string = "<!DOCTYPE html>\n" +
                "<html lang=\"en\" style=\"margin: 0; width: 100%; height: 100%\">\n" +
                "  <head>\n" +
                "    <meta charset=\"UTF-8\" />\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />\n" +
                "    <title>Document</title>\n" +
                "  </head>\n" +
                "  <body style=\"margin: 0; width: 100%; height: 100%\">\n" +
                video_string+
                "  </body>\n" +
                "</html>\n";
           trailer_window = findViewById(R.id.trailer_window);
            trailer_window.loadData(html_string, "text/html","utf-8");
            trailer_window.getSettings().setJavaScriptEnabled(true);

            trailer_window.setWebChromeClient(new WebChromeClient() {
                @Override
                public void onShowCustomView(View view, CustomViewCallback callback) {
                    super.onShowCustomView(view, callback);
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }

                @Override
                public void onHideCustomView() {
                    super.onHideCustomView();
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
            });
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        initPlayer();
    }
}