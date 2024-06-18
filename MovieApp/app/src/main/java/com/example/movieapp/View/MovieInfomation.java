package com.example.movieapp.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.movieapp.Adapters.CastAdapter;
import com.example.movieapp.Adapters.ReviewApdater;
import com.example.movieapp.Interfaces.LoadingActivity;
import com.example.movieapp.Model.AccountModel;
import com.example.movieapp.Model.PersonModel;
import com.example.movieapp.Model.DetailModel;
import com.example.movieapp.Model.MovieModel;
import com.example.movieapp.R;
import com.example.movieapp.Request.ImageLoader;
import com.example.movieapp.Services.DownloadReceiver;
import com.example.movieapp.ViewModel.MovieViewModel;
import com.example.movieapp.utils.Credentials;
import com.google.android.material.chip.Chip;

import java.io.File;
import java.util.List;

public class MovieInfomation extends AppCompatActivity implements LoadingActivity {
    private int movie_id;
    private MovieModel movie;
    private DetailModel movie_detail;
    private List<DetailModel> movie_reviews;
    private MovieViewModel movie_view_model;
    private String url360, url720;
    private AccountModel login_account;
    private DownloadReceiver download_receiver;
    private TextView film_title, film_overview, rating, genres_info, time_info, date_info, country_info;
    private ImageView poster_image;
    private ImageButton back_btn,facebook_btn, twitter_btn, youtube_btn, instagram_btn, tiktok_btn, send_btn;
    private Button play_button, play_trailer_btn,download_btn, review_btn;
    private ToggleButton favor_btn;
    private RecyclerView cast_crew_recycler_view,reviewsRecyclerView;
    private ImageView background_image;
    private CastAdapter cast_adapter, crew_adapter;
    private Chip crew_chip, cast_chip;
    private PopupWindow review_session_popup;
    private View review_session_popup_view;
    private ReviewApdater reviewApdater;
    private RatingBar rating_bar;
    private EditText review_text;
    private ConstraintLayout loading_screen, layout;
    private TextView totalRating;
    private ConstraintLayout loading_recommend_layout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_film_infomation);
        movie_id = getIntent().getIntExtra("film_id", -1);
        login_account = (AccountModel) getIntent().getParcelableExtra("loginAccount");
        initComponent();
        movie_view_model = new ViewModelProvider(this).get(MovieViewModel.class);
        ObserveAnyChange();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initFeatures();
    }

    public void ObserveAnyChange() {
        if (movie_view_model != null) {
            movie_view_model.getMovie().observe(this, new Observer<MovieModel>() {
                @Override
                public void onChanged(MovieModel movieModel) {
                    movie = movieModel;
                    initDetails();
                }
            });

            movie_view_model.getDetailMovie().observe(this, new Observer<DetailModel>() {
                @Override
                public void onChanged(DetailModel detailModel) {
                    if (movie_id == detailModel.getMovieId()) {
                        movie_detail = detailModel;
                        initVideo();
                    }
                }
            });

            movie_view_model.getMovieReviews().observe(this, new Observer<List<DetailModel>>() {
                @Override
                public void onChanged(List<DetailModel> detailModels) {
                    movie_reviews = detailModels;
                    initReview();
                }
            });
        }
    }
    public void initComponent() {
        layout = findViewById(R.id.info_layout);

        loading_screen = findViewById(R.id.loadingLayout);
        poster_image = findViewById(R.id.filmImage);
        back_btn = findViewById(R.id.backBtn_info);
        film_title = findViewById(R.id.film_title_info);
        film_overview = findViewById(R.id.film_overview);
        cast_crew_recycler_view = findViewById(R.id.castRecyclerView);
        cast_chip = findViewById(R.id.cast_chip);
        crew_chip = findViewById(R.id.crew_chip);

        rating = findViewById(R.id.movie_rating);
        date_info = findViewById(R.id.publish_date_info);
        time_info = findViewById(R.id.time_info);
        genres_info = findViewById(R.id.genre_info);
        country_info = findViewById(R.id.country_info);
        background_image = findViewById(R.id.imageView2);
        play_button = findViewById(R.id.playBtn_info);
        play_trailer_btn = findViewById(R.id.play_trailer_btn);
        download_btn = findViewById(R.id.download_btn);
        review_btn = findViewById(R.id.review_btn);

        twitter_btn = findViewById(R.id.twitter_x_info);
        facebook_btn = findViewById(R.id.facebook_info);
        youtube_btn = findViewById(R.id.youtube_info);
        instagram_btn = findViewById(R.id.instagram_info);
        tiktok_btn = findViewById(R.id.tiktok_info);
    }
    public void initFeatures() {
        turnOnLoadingScreen();
        movie_view_model.searchMovie(movie_id);
        movie_view_model.searchMovieDetail(movie_id, login_account.getUser_id());
        movie_view_model.getReviews(movie_id);
    }

    private void initCast() {
        List<PersonModel> castModels = movie.getCredits().getCast();
        cast_adapter = new CastAdapter(MovieInfomation.this, castModels, login_account);
    }

    private void initCrew() {
        List<PersonModel> crewModels = movie.getCredits().getCrew();
        crew_adapter = new CastAdapter(MovieInfomation.this, crewModels, login_account);
    }


    private void initDetails() {
        initCastCrew();
        initSocialNetwork();
        rating.setText(movie.getRating());
        date_info.setText("(" + movie.getPublishDate() + ")");
        time_info.setText(movie.getMaxDurationTime());
        country_info.setText(movie.getProductionCountry());
        genres_info.setText(movie.getGenresString());
        new ImageLoader().loadImageIntoImageView(MovieInfomation.this, Credentials.BASE_IMAGE_URL + movie.getPoster_path(), poster_image, findViewById(R.id.shimmerLayout_info));
        new ImageLoader().loadImageIntoImageView(MovieInfomation.this, Credentials.BASE_IMAGE_URL + movie.getPoster_path(), background_image);

        film_title.setText(movie.getTitle());
        film_overview.setText(movie.getOverview());
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        review_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showReviewPopup();
            }
        });
    }

    private void showReviewPopup() {
        if(review_session_popup !=null){
            layout.post(new Runnable() {
                @Override
                public void run() {
                    review_session_popup.showAtLocation(layout, Gravity.BOTTOM, 0, 0);
                }
            });
        }
    }

    private void initCastCrew() {
        initCast();
        initCrew();
        cast_crew_recycler_view.setAdapter(cast_adapter);
        cast_crew_recycler_view.setLayoutManager(new LinearLayoutManager(MovieInfomation.this, LinearLayoutManager.HORIZONTAL, false));
        cast_chip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crew_chip.setChecked(false);
                cast_crew_recycler_view.setAdapter(cast_adapter);
            }
        });

        crew_chip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cast_chip.setChecked(false);
                cast_crew_recycler_view.setAdapter(crew_adapter);
            }
        });
    }

    private void initVideo() {
        play_button.setEnabled(false);
        play_button.setText("Loading...");
        favor_btn = findViewById(R.id.favorBtn);
        favor_btn.setEnabled(false);
        //favor button
        initFavor(movie_detail.isFavor());
        if (movie_detail.getUrl() != null || movie_detail.getUrl720() != null) {
            play_button.setText("Watch now");
            play_button.setEnabled(true);
            Intent playMovieIntent = new Intent(MovieInfomation.this, PlayMovie.class);
            playMovieIntent.putExtra("movie", movie);
            url360 = movie_detail.getUrl();
            playMovieIntent.putExtra("videoUrl", movie_detail.getUrl());
            url720 = movie_detail.getUrl720();
            playMovieIntent.putExtra("videoUrl720", movie_detail.getUrl720());
            playMovieIntent.putExtra("loginAccount", (Parcelable) login_account);
            play_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(playMovieIntent);
                }
            });

            initDownloadButton();
            movie.setDuration(movie_detail.getDuration());
        } else {
            download_btn.setVisibility(View.GONE);
            play_button.setText("Coming soon");
        }


        if (movie.getTrailer() != null) {
            play_trailer_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MovieInfomation.this, PlayingTrailer.class);
                    intent.putExtra("video_string", movie.getTrailer());
                    startActivity(intent);
                }
            });
        }
        turnOffLoadingScreen();
    }

    private void initDownloadButton() {
        if (url360 == null && url720 == null) {
            download_btn.setVisibility(View.GONE);
            return;
        }
        String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/MovieApp/" + movie.getId() + ".mp4";
        if (new File(filePath).exists()) {
            download_btn.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.download_done_icon), null, null);
            download_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent playMovieIntent = new Intent(MovieInfomation.this, PlayingFilm.class);
                    playMovieIntent.putExtra("movie", movie);
                    playMovieIntent.putExtra("videoUrl720", movie_detail.getUrl720());
                    playMovieIntent.putExtra("loginAccount", (Parcelable) login_account);
                    startActivity(playMovieIntent);
                }
            });
            return;
        }else if (url720 != null) {
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
        favor_btn.setChecked(isfavor);
        if (isfavor) {
            MovieInteraction.subcribeToNotification(movie.getId());
        } else {
            MovieInteraction.unsubcribeToNotification(movie.getId());
        }
        favor_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFavorite();
            }
        });
        favor_btn.setText(getResources().getText(R.string.add_favor));
        favor_btn.setEnabled(true);
    }

    public void initReview() {
        if(review_session_popup == null) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            review_session_popup_view = inflater.inflate(R.layout.comment_session, null);

            boolean focusable = true;
            review_session_popup = new PopupWindow(review_session_popup_view, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT, focusable);
            review_session_popup.setAnimationStyle(R.style.PopupAnimation);

            loading_recommend_layout = review_session_popup_view.findViewById(R.id.loadingLayout);
            loading_recommend_layout.setVisibility(View.VISIBLE);

            ConstraintLayout error_layout = review_session_popup_view.findViewById(R.id.error_loading);

            totalRating = review_session_popup_view.findViewById(R.id.total_rating_rv);

            ReviewApdater.DeleteInterface deleteInterface = new ReviewApdater.DeleteInterface() {
                @Override
                public void delete() {
                    movie_view_model.delete_review();
                    review_session_popup_view.findViewById(R.id.comment_box).setVisibility(View.VISIBLE);
                    totalRating.setText("" +movie_reviews.size());
                }
            };

            if (movie_reviews != null) {
                reviewApdater = new ReviewApdater(MovieInfomation.this, movie_reviews, login_account.getUser_id(), deleteInterface);
                reviewApdater.removeEmpty();
                reviewApdater.notifyDataSetChanged();
                for (int i = 0; i < movie_reviews.size(); i++) {
                    if (movie_reviews.get(i).getUserId().equalsIgnoreCase(login_account.getUser_id())) {
                        review_session_popup_view.findViewById(R.id.comment_box).setVisibility(View.GONE);
                    }
                }
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MovieInfomation.this);
                reviewsRecyclerView = review_session_popup_view.findViewById(R.id.recyclerView);
                reviewsRecyclerView.setLayoutManager(linearLayoutManager);
                reviewsRecyclerView.setAdapter(reviewApdater);
                reviewApdater.notifyDataSetChanged();
                error_layout.setVisibility(View.GONE);
            }
            rating_bar = review_session_popup_view.findViewById(R.id.comment_box).findViewById(R.id.ratingBar_comment_box);
            review_text = review_session_popup_view.findViewById(R.id.comment_box).findViewById(R.id.commentBox);
            send_btn = review_session_popup_view.findViewById(R.id.send_comment_box);
            ImageView avatar_image = review_session_popup_view.findViewById(R.id.imageAvatar);
            TextView avatar_text = review_session_popup_view.findViewById(R.id.avatarText);

            new ImageLoader().loadAvatar(this, login_account.getAvatar(), avatar_image, avatar_text, login_account.getUsername());
            send_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    float rating = rating_bar.getRating();
                    String review = review_text.getText().toString().trim();
                    if(rating>0)
                        postReview(rating, review);
                    else
                        Toast.makeText(MovieInfomation.this, "Please rate the movie", Toast.LENGTH_SHORT).show();
                }
            });

            loading_recommend_layout.setVisibility(View.GONE);
            totalRating.setText("" + movie_reviews.size());
        }else{
            ReviewApdater.DeleteInterface deleteInterface = new ReviewApdater.DeleteInterface() {
                @Override
                public void delete() {
                    movie_view_model.delete_review();
                    review_session_popup_view.findViewById(R.id.comment_box).setVisibility(View.VISIBLE);
                    totalRating.setText("" +movie_reviews.size());
                }
            };

            if (movie_reviews != null) {
                reviewApdater = new ReviewApdater(MovieInfomation.this, movie_reviews, login_account.getUser_id(), deleteInterface);
                reviewApdater.removeEmpty();
                reviewApdater.notifyDataSetChanged();
                for (int i = 0; i < movie_reviews.size(); i++) {
                    if (movie_reviews.get(i).getUserId().equalsIgnoreCase(login_account.getUser_id())) {
                        review_session_popup_view.findViewById(R.id.comment_box).setVisibility(View.GONE);
                    }
                }
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MovieInfomation.this);
                reviewsRecyclerView = review_session_popup_view.findViewById(R.id.recyclerView);
                reviewsRecyclerView.setLayoutManager(linearLayoutManager);
                reviewsRecyclerView.setAdapter(reviewApdater);
                reviewApdater.notifyDataSetChanged();
                ConstraintLayout error_layout = review_session_popup_view.findViewById(R.id.error_loading);
                error_layout.setVisibility(View.GONE);

                loading_recommend_layout.setVisibility(View.GONE);
                totalRating.setText("" + movie_reviews.size());
            }
        }
    }

    public void changeFavorite() {
        movie_view_model.changeFavor();
    }
    public void postReview(float rating, String review) {
        loading_recommend_layout.setVisibility(View.VISIBLE);
        movie_view_model.post_review(movie.getId(), login_account.getUser_id(), rating+"", review);
        review_session_popup_view.findViewById(R.id.comment_box).setVisibility(View.GONE);
    }
    public void downloadMovie(String url) {
        download_receiver = new DownloadReceiver(download_btn, this);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setTitle(movie.getTitle());
        request.setDescription(movie.getTitle() + "Downloaded Movie : " + movie.getTitle());
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "/MovieApp/" + movie.getId() + ".mp4");

        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        downloadManager.enqueue(request);
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(download_receiver, filter);
    }

    public void turnOnLoadingScreen(){
        loading_screen.setVisibility(View.VISIBLE);
        setViewAndChildrenEnabled(layout, false);
    }
    public void turnOffLoadingScreen(){
        loading_screen.setVisibility(View.GONE);
        setViewAndChildrenEnabled(layout, true);
    }
}