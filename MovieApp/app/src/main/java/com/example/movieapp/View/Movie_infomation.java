package com.example.movieapp.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.movieapp.Adapters.CastAdapter;
import com.example.movieapp.Adapters.ReviewApdater;
import com.example.movieapp.Model.AccountModel;
import com.example.movieapp.Model.CastModel;
import com.example.movieapp.Model.DetailModel;
import com.example.movieapp.Model.MovieModel;
import com.example.movieapp.R;
import com.example.movieapp.Request.ImageLoader;
import com.example.movieapp.Request.MyService;
import com.example.movieapp.Request.MyService2;
import com.example.movieapp.Response.DetailResponse;
import com.example.movieapp.Services.DownloadReceiver;
import com.example.movieapp.ViewModel.MovieViewModel;
import com.example.movieapp.utils.Credentials;
import com.example.movieapp.utils.MovieApi;
import com.google.android.material.chip.Chip;

import java.io.File;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Movie_infomation extends AppCompatActivity {
    DownloadReceiver downloadReceiver;
    TextView film_title, film_overview, rating, genres_info, time_info, date_info, country_info;
    private ImageView poster_image;
    ImageButton backBtn;
    Button playButton, playTrailerButton;
    Button download_btn, review_btn;
    ToggleButton favorButton;
    ImageButton facebook_btn, twitter_btn, youtube_btn, instagram_btn, tiktok_btn;
    MovieModel movie;
    MovieViewModel movieViewModel;
    int movieId;

    private AccountModel loginAccount;
    RecyclerView cast_crew_recycler_view;
    private MovieApi movieApi;
    private ImageView imageView2;

    Call<DetailModel> changeFavorCall;
    private CastAdapter castAdapter, crewAdapter;
    Chip crewChip, castChip;
    private String url360, url720;
    PopupWindow reviewSessionPopup;
    private View popupReviewView;
    private ReviewApdater reviewApdater;
    private RecyclerView reviewsRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_film_infomation);
        movieId = getIntent().getIntExtra("film_id", -1);
        loginAccount = (AccountModel) getIntent().getParcelableExtra("loginAccount");
        movieViewModel = new ViewModelProvider(this).get(MovieViewModel.class);
        ObserveAnyChange();
        init();
    }

    public void ObserveAnyChange() {
        if (movieViewModel != null) {
            movieViewModel.getMovie().observe(this, new Observer<MovieModel>() {
                @Override
                public void onChanged(MovieModel movieModel) {
                    movie = movieModel;
                }
            });
        }
    }

    public void init() {
        movieViewModel.searchMovieDetail(movieId);
        initComponent();
        initDetails();
    }

    public void initComponent() {
        movieApi = MyService.getMovieApi();
        poster_image = findViewById(R.id.filmImage);
        backBtn = findViewById(R.id.backBtn_info);
        film_title = findViewById(R.id.film_title_info);
        film_overview = findViewById(R.id.film_overview);
        cast_crew_recycler_view = findViewById(R.id.castRecyclerView);
        castChip = findViewById(R.id.cast_chip);
        crewChip = findViewById(R.id.crew_chip);

        rating = findViewById(R.id.movie_rating);
        date_info = findViewById(R.id.publish_date_info);
        time_info = findViewById(R.id.time_info);
        genres_info = findViewById(R.id.genre_info);
        country_info = findViewById(R.id.country_info);
        imageView2 = findViewById(R.id.imageView2);
        playButton = findViewById(R.id.playBtn_info);
        playTrailerButton = findViewById(R.id.play_trailer_btn);
        download_btn = findViewById(R.id.download_btn);
        review_btn = findViewById(R.id.review_btn);

        twitter_btn = findViewById(R.id.twitter_x_info);
        facebook_btn = findViewById(R.id.facebook_info);
        youtube_btn = findViewById(R.id.youtube_info);
        instagram_btn = findViewById(R.id.instagram_info);
        tiktok_btn = findViewById(R.id.tiktok_info);


        downloadReceiver = new DownloadReceiver(download_btn);
    }

    private void initCast() {
        List<CastModel> castModels = movie.getCredits().getCast();
        castAdapter = new CastAdapter(Movie_infomation.this, castModels, loginAccount);
    }

    private void initCrew() {
        List<CastModel> crewModels = movie.getCredits().getCrew();
        crewAdapter = new CastAdapter(Movie_infomation.this, crewModels, loginAccount);
    }


    private void initDetails() {
        Call<MovieModel> detailsResponseCall = movieApi.searchMovieDetail(
                movieId,
                Credentials.API_KEY,
                "credits,videos,external_ids"
        );

        detailsResponseCall.enqueue(new Callback<MovieModel>() {
            @Override
            public void onResponse(Call<MovieModel> call, Response<MovieModel> response) {
                if (response.code() == 200) {
                    movie = response.body();
                    initCastCrew();
                    initVideo();
                    initSocialNetwork();
//                        Log.i("TAG DETAIL", movie.getGenres().get(0).getName());

                    float rate = Math.round(movie.getVote_average() * 100) * 1.0f / 100;
                    rating.setText(rate + "");
                    try {
                        String year = movie.getRelease_date().split("-")[0];
                        date_info.setText("(" + year + ")");
                    } catch (Exception ex) {
                        date_info.setText(movie.getRelease_date() + "");
                    }
                    time_info.setText(movie.getMaxDurationTime());
                    country_info.setText(movie.getProductionCountry());
                    genres_info.setText(movie.getGenresString());
                    new ImageLoader().loadImageIntoImageView(Movie_infomation.this, Credentials.BASE_IMAGE_URL + movie.getPoster_path(), poster_image, findViewById(R.id.shimmerLayout_info));
                    new ImageLoader().loadImageIntoImageView(Movie_infomation.this, Credentials.BASE_IMAGE_URL + movie.getPoster_path(), imageView2);
                    initVideo();

                    film_title.setText(movie.getTitle());
                    film_overview.setText(movie.getOverriew());
                    backBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onBackPressed();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<MovieModel> call, Throwable t) {
                Toast.makeText(getBaseContext(), "Failure Details", Toast.LENGTH_SHORT).show();
            }
        });

        review_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initReview();
            }
        });
    }

    private void initCastCrew() {
        initCast();
        initCrew();
        cast_crew_recycler_view.setAdapter(castAdapter);
        cast_crew_recycler_view.setLayoutManager(new LinearLayoutManager(Movie_infomation.this, LinearLayoutManager.HORIZONTAL, false));
        castChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crewChip.setChecked(false);
                cast_crew_recycler_view.setAdapter(castAdapter);
            }
        });

        crewChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                castChip.setChecked(false);
                cast_crew_recycler_view.setAdapter(crewAdapter);
            }
        });
    }

    private void initVideo() {
        playButton.setEnabled(false);
        playButton.setText("Loading...");

        favorButton = findViewById(R.id.favorBtn);
        favorButton.setEnabled(false);
        Call<DetailModel> videoModelCall = MyService2.getApi().getMovieVideo(Credentials.functionname_video, movieId, loginAccount.getUser_id());
        videoModelCall.enqueue(new Callback<DetailModel>() {
            @Override
            public void onResponse(Call<DetailModel> call, Response<DetailModel> response) {
                if (response.code() == 200) {
                    //favor button
                    initFavor(response.body().isFavor());
                    if (response.body().getUrl() != null || response.body().getUrl720() != null) {
                        playButton.setText("Watch now");
                        playButton.setEnabled(true);
                        Intent playMovieIntent = new Intent(Movie_infomation.this, PlayingFilm.class);
                        playMovieIntent.putExtra("movie", movie);

                        url360 = response.body().getUrl();
                        playMovieIntent.putExtra("videoUrl", response.body().getUrl());

                        url720 = response.body().getUrl720();
                        playMovieIntent.putExtra("videoUrl720", response.body().getUrl720());

                        playMovieIntent.putExtra("loginAccount", (Parcelable) loginAccount);
                        playButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(playMovieIntent);
                            }
                        });

                        initDownloadButton();
                        movie.setDuration(response.body().getDuration());
                    } else {
                        playButton.setText("Coming soon");
                    }
                } else {
                    playButton.setText("Coming soon");
                    download_btn.setEnabled(false);
                }
                findViewById(R.id.loadingLayout).setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<DetailModel> call, Throwable t) {
                Log.e("VIDEO TASK", "FAILURE");
                findViewById(R.id.loadingLayout).setVisibility(View.GONE);
            }
        });

        if (movie.getTrailer() != null) {
            playTrailerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Movie_infomation.this, PlayingTrailer.class);
                    intent.putExtra("video_string", movie.getTrailer());
                    startActivity(intent);
                }
            });
        }
    }

    private void initDownloadButton() {
        if(url360 == null && url720==null){
            download_btn.setVisibility(View.GONE);
        }

        String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/MovieApp/" + movie.getId() + ".mp4";
        if(new File(filePath).exists()){
            download_btn.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.download_done_icon), null,null);
            download_btn.setEnabled(false);
            return;
        }

        // download btn
        if (url720 != null) {
            download_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    downloadMovie(url720);
                }
            });
        } else {
            download_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    downloadMovie(url360);
                }
            });
        }
    }

    public void initSocialNetwork() {
        if (movie.getExternal_ids().getTiktok_id() == null && movie.getExternal_ids().getTwitter_id() == null && movie.getExternal_ids().getFacebook_id() == null
                && movie.getExternal_ids().getYoutube_id() == null && movie.getExternal_ids().getInstagram_id() == null) {
            findViewById(R.id.social_link).setVisibility(View.GONE);
        }

        if (movie.getExternal_ids().getTwitter_id() != null) {
            twitter_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent twitter_intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.twitter.com/" + movie.getExternal_ids().getTwitter_id()));
                    startActivity(twitter_intent);
                }
            });
        } else {
            twitter_btn.setVisibility(View.GONE);
        }

        if (movie.getExternal_ids().getFacebook_id() != null) {
            facebook_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent fb_intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/" + movie.getExternal_ids().getFacebook_id()));
                    startActivity(fb_intent);
                }
            });
        } else {
            facebook_btn.setVisibility(View.GONE);
        }

        if (movie.getExternal_ids().getInstagram_id() != null) {
            instagram_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent ins_intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/" + movie.getExternal_ids().getInstagram_id()));
                    startActivity(ins_intent);
                }
            });
        } else {
            instagram_btn.setVisibility(View.GONE);
        }

        if (movie.getExternal_ids().getYoutube_id() != null) {
            youtube_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent youtube_intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/" + movie.getExternal_ids().getYoutube_id()));
                    startActivity(youtube_intent);
                }
            });
        } else {
            youtube_btn.setVisibility(View.GONE);
        }

        if (movie.getExternal_ids().getTiktok_id() != null) {
            tiktok_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent tiktok_intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.tiktok.com/@" + movie.getExternal_ids().getTiktok_id()));
                    startActivity(tiktok_intent);
                }
            });
        } else {
            tiktok_btn.setVisibility(View.GONE);
        }
    }

    public void initFavor(boolean isfavor) {
        favorButton.setChecked(isfavor);
        favorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFavorite();
            }
        });
        favorButton.setEnabled(true);
    }

    public void initReview() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        popupReviewView = inflater.inflate(R.layout.comment_session, null);

        boolean focusable = true;
        reviewSessionPopup = new PopupWindow(popupReviewView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT, focusable);
        reviewSessionPopup.setAnimationStyle(R.style.PopupAnimation);

        ConstraintLayout loadinglayout = popupReviewView.findViewById(R.id.loadingLayout);
        loadinglayout.setVisibility(View.VISIBLE);

        ConstraintLayout error_layout = popupReviewView.findViewById(R.id.error_loading);

        TextView totalRating = popupReviewView.findViewById(R.id.total_rating_rv);

        Call<DetailResponse> detailResponseCall = MyService2.getApi().getReviewByFilmId("detail", movie.getId());
        detailResponseCall.enqueue(new Callback<DetailResponse>() {
            @Override
            public void onResponse(Call<DetailResponse> call, Response<DetailResponse> response) {
                if(response.code()==200){
                    List<DetailModel> detailModels = response.body().getAllReviews();

                    // Không cho review lần 2
                    for(int i =0 ;i <detailModels.size(); i++){
                        int num = i;
                        if(detailModels.get(i).getUserId().equalsIgnoreCase(loginAccount.getUser_id())){
                            popupReviewView.findViewById(R.id.comment_box).setVisibility(View.GONE);

                            ReviewApdater.DeleteInterface deleteInterface = new ReviewApdater.DeleteInterface() {
                                @Override
                                public void delete() {
                                    detailModels.remove(num);
                                    reviewApdater.notifyDataSetChanged();
                                    Call<DetailModel> detailModelCall = MyService2.getApi().addReview(Credentials.functionname_detail,  loginAccount.getUser_id(), movie.getId(), "0.0", " ");
                                    detailModelCall.enqueue(new Callback<DetailModel>() {
                                        @Override
                                        public void onResponse(Call<DetailModel> call, Response<DetailModel> response) {
                                            Log.i("delete review", "success");
                                            popupReviewView.findViewById(R.id.comment_box).setVisibility(View.VISIBLE);
                                        }

                                        @Override
                                        public void onFailure(Call<DetailModel> call, Throwable t) {
                                            Log.i("delete review", ""+t);
                                        }
                                    });
                                }
                            };
                            reviewApdater = new ReviewApdater(Movie_infomation.this, detailModels, loginAccount.getUser_id(), deleteInterface);
                        }
                    }
                    reviewApdater.removeEmpty();
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(Movie_infomation.this);
                    reviewsRecyclerView =  popupReviewView.findViewById(R.id.recyclerView);
                    reviewsRecyclerView.setLayoutManager(linearLayoutManager);
                    reviewsRecyclerView.setAdapter(reviewApdater);
                    loadinglayout.setVisibility(View.GONE);
                    error_layout.setVisibility(View.GONE);
                    totalRating.setText(""+response.body().getAllReviews().size());
                }
            }



            @Override
            public void onFailure(Call<DetailResponse> call, Throwable t) {
                Log.e("GET REVIEWs" , "FAIL" + t.toString());
                error_layout.setVisibility(View.VISIBLE);
                loadinglayout.setVisibility(View.GONE);
            }
        });

        popupReviewView.findViewById(R.id.comment_box).setVisibility(View.GONE);
       findViewById(R.id.info_layout).post(new Runnable() {
            @Override
            public void run() {
                reviewSessionPopup.showAtLocation(findViewById(R.id.info_layout), Gravity.BOTTOM, 0, 0);
            }
        });
    }

    public void changeFavorite() {
        changeFavorCall = MyService2.getApi().addToFavor("detail", loginAccount.getUser_id(), movieId);
        changeFavorCall.enqueue(new Callback<DetailModel>() {
            @Override
            public void onResponse(Call<DetailModel> call, Response<DetailModel> response) {
                if (response.code() == 200) {
                    changeFavorCall = null;
                }
            }

            @Override
            public void onFailure(Call<DetailModel> call, Throwable t) {
                Log.e("FAVOR TASK", "change favor fail");
                favorButton.setEnabled(true);
            }
        });
    }

    public void downloadMovie(String url) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setTitle(movie.getTitle());
        request.setDescription(movie.getTitle() + "Downloaded Movie : " + movie.getTitle());
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "/MovieApp/" + movie.getId() + ".mp4");

        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        downloadManager.enqueue(request);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(downloadReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(downloadReceiver);
    }
}