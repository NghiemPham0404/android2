package com.example.movieapp.View;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.movieapp.Adapters.FilmAdapter;
import com.example.movieapp.Model.MovieModel;
import com.example.movieapp.R;
import com.example.movieapp.utils.Credentials;

public class Movie_infomation extends AppCompatActivity {

    TextView film_title, film_overview;
    private ImageView poster_image, backdrop_image;
    ImageButton backBtn;

    MovieModel movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_film_infomation);
        MovieModel passMovie = getIntent().getParcelableExtra("movie");

        if (passMovie != null) {
            // Use the retrieved object
            movie = passMovie;
        }

        init();

    }

    public void init(){
        poster_image = findViewById(R.id.filmImage);
        backdrop_image = findViewById(R.id.backdropImage);
        backBtn = findViewById(R.id.backBtn_info);
        film_title = findViewById(R.id.film_title_info);
        film_overview = findViewById(R.id.film_overview);
        initComponent();
    }

    public void initComponent(){
        new FilmAdapter.DownloadImageTask(poster_image).execute(Credentials.BASE_IMAGE_URL + movie.getPoster_path());
        new FilmAdapter.DownloadImageTask(backdrop_image).execute(Credentials.BASE_IMAGE_URL + movie.getBackgrop_path());
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


}