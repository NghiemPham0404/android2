package com.example.movieapp.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.movieapp.Adapters.CastAdapter;
import com.example.movieapp.Adapters.FilmAdapter;
import com.example.movieapp.Model.CastModel;
import com.example.movieapp.Model.MovieModel;
import com.example.movieapp.Model.VideoModel;
import com.example.movieapp.R;
import com.example.movieapp.Request.MyService;
import com.example.movieapp.Response.CastResponse;
import com.example.movieapp.Response.VideoResponse;
import com.example.movieapp.utils.Credentials;
import com.example.movieapp.utils.MovieApi;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Movie_infomation extends AppCompatActivity {

    TextView film_title, film_overview;
    private ImageView poster_image;
    ImageButton backBtn;

    MovieModel movie;

    RecyclerView castRecyclerView;
    private MovieApi movieApi;

    private WebView trailer_webView;


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
        backBtn = findViewById(R.id.backBtn_info);
        film_title = findViewById(R.id.film_title_info);
        film_overview = findViewById(R.id.film_overview);
        castRecyclerView = findViewById(R.id.castRecyclerView);
        trailer_webView = findViewById(R.id.trailer_webview);
        initComponent();
    }

    public void initComponent(){

        new FilmAdapter.DownloadImageTask(poster_image).execute(Credentials.BASE_IMAGE_URL + movie.getPoster_path());
        this.film_title.setText(this.movie.getTitle());
        this.film_overview.setText(this.movie.getOverriew());
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        
        initDetails();
        initCast();
        initTrailers();
    }

    private void initTrailers() {
        Call<VideoResponse> videoResponseCall = movieApi.searchVideoByFilmID(
                "https://api.themoviedb.org/3/movie/"+movie.getId()+"/videos",
                Credentials.API_KEY
        );
        videoResponseCall.enqueue(new Callback<VideoResponse>() {
            @Override
            public void onResponse(Call<VideoResponse> call, Response<VideoResponse> response) {
                if(response.code() == 200){
                    List<VideoModel> videoModels = response.body().getVideoModels();
                    if(videoModels.size() == 0){
                        trailer_webView.setVisibility(View.GONE);
                    }else{
                        boolean found = false;
                        for(int i = 0; i<videoModels.size(); i++){
                            if(videoModels.get(i).getType().equalsIgnoreCase("Trailer")){
                                String video = "<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/"+videoModels.get(i).getKey()+"\" title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\" allowfullscreen=\"true\"></iframe>";
//                                String video = "<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/V2KCAfHjySQ?si=g_u_dyntOUg64ANT\" title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\" allowfullscreen></iframe>";
                                trailer_webView.loadData(video, "text/html","utf-8");
                                trailer_webView.getSettings().setJavaScriptEnabled(true);
                                trailer_webView.setWebChromeClient(new WebChromeClient());
                            }
                        }
                        if(found == false){

                        }
                    }
                }

            }

            @Override
            public void onFailure(Call<VideoResponse> call, Throwable t) {
                Toast.makeText(Movie_infomation.this, "failure trailers", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initCast() {
        movieApi = MyService.getMovieApi();
        Call<CastResponse> castResponseCall = movieApi.searchCastByFilmID(
               "https://api.themoviedb.org/3/movie/"+movie.getId()+"/credits",
                Credentials.API_KEY
        );
        castResponseCall.enqueue(new Callback<CastResponse>() {
            @Override
            public void onResponse(Call<CastResponse> call, Response<CastResponse> response) {
                List<CastModel> castModels = response.body().getCastModels();
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(Movie_infomation.this, LinearLayoutManager.HORIZONTAL, false);
                CastAdapter castAdapter = new CastAdapter(Movie_infomation.this, castModels);
                castRecyclerView.setAdapter(castAdapter);
                castRecyclerView.setLayoutManager(linearLayoutManager);
            }

            @Override
            public void onFailure(Call<CastResponse> call, Throwable t) {
                Toast.makeText(Movie_infomation.this, "failure cast", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initDetails() {
    }


}