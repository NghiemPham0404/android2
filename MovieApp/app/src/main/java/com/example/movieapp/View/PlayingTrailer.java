package com.example.movieapp.View;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.movieapp.R;

public class PlayingTrailer extends AppCompatActivity {

    WebView trailer_window;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playing_trailer);
        String video_string = getIntent().getStringExtra("video_string");
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

        trailer_window.setWebViewClient(new WebViewClient());

    }
}