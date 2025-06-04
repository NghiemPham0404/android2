package com.example.movieapp.views.movie;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
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
import com.example.movieapp.ViewModel.UserViewModel;
import com.example.movieapp.data.Model.DTO.InteractionDTO.*;
import com.example.movieapp.data.Model.PersonModel;
import com.example.movieapp.data.Model.MovieModel;
import com.example.movieapp.R;
import com.example.movieapp.Request.ImageLoader;
import com.example.movieapp.Services.DownloadReceiver;
import com.example.movieapp.ViewModel.MovieViewModel;
import com.example.movieapp.data.Model.user.UserDTO;
import com.example.movieapp.utils.Credentials;
import com.example.movieapp.views.MovieInteraction;
import com.example.movieapp.views.movie.moviePlaying.PlayMovie;
import com.example.movieapp.views.movie.trailerPlaying.PlayingTrailer;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.io.File;
import java.util.List;

public class MovieInformation extends AppCompatActivity implements LoadingActivity {
    private int movie_id;
    private MovieModel movie;
    private UserDTO.UserInfo loginAccount;
    private InteractionDAOExtended movie_detail;
    private List<InteractionDAOReview> movie_reviews;

    private MovieViewModel movie_view_model;
    private UserViewModel userViewModel;


    private String url360, url720;
    private DownloadReceiver download_receiver;
    private TextView film_title, film_overview, rating, time_info, date_info, country_info;
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
    private ChipGroup genres_group;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_film_infomation);
        initComponent();
        initViewModels();

        movie_id = getIntent().getIntExtra("film_id", -1);
    }

    @Override
    protected void onStart(){
        super.onStart();
        requestMovieData();
        ObserveAnyChange();
    }

    private void initViewModels() {
        movie_view_model = new ViewModelProvider(this).get(MovieViewModel.class);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
    }


    public void ObserveAnyChange() {
        if(userViewModel != null){
            userViewModel.getLoginAccount().observe(
                    this, userInfo -> {
                        loginAccount = userInfo;
                    }
            );
        }

        if (movie_view_model != null) {
            movie_view_model.getMovie().observe(this, movieModel -> {
                movie = movieModel;
                initDetails();
                movie_view_model.getDetailMovie().observe(this, detailModel -> {
                    if(detailModel != null){
                        movie_detail = detailModel;
                        initInteractions();
                    }else {
                        play_button.setText("Coming soon");
                        hideInteractionButtons();
                    }
                });

                movie_view_model.getMovieReviews().observe(this, reviews -> {
                    if(reviews!=null){
                        movie_reviews = reviews.getData();
                        initReview();
                    }
                });
            });
        }
        turnOffLoadingScreen();
    }
    public void initComponent() {
        layout = findViewById(R.id.info_layout);

        loading_screen = findViewById(R.id.loadingLayout);
        favor_btn = findViewById(R.id.favorBtn);
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
        genres_group = findViewById(R.id.genres_group);
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
    public void requestMovieData() {
        movie_view_model.getMovie(movie_id);
        movie_view_model.getMovieInteration(movie_id);
        movie_view_model.getReviews(movie_id);
    }

    private void initCast() {
        List<PersonModel> castModels = movie.getCredits().getCast();
        cast_adapter = new CastAdapter(MovieInformation.this, castModels);
    }

    private void initCrew() {
        List<PersonModel> crewModels = movie.getCredits().getCrew();
        crew_adapter = new CastAdapter(MovieInformation.this, crewModels);
    }


    private void initDetails() {
        initCastCrew();
        initSocialNetwork();
        rating.setText(movie.getRating());
        date_info.setText("(" + movie.getPublishDate() + ")");
        time_info.setText(movie.getMaxDurationTime());
        country_info.setText(movie.getProductionCountry());
        initGenres();
        new ImageLoader().loadImageIntoImageView(MovieInformation.this, Credentials.BASE_IMAGE_URL + movie.getPoster_path(), poster_image, findViewById(R.id.shimmerLayout_info));
        new ImageLoader().loadImageIntoImageView(MovieInformation.this, Credentials.BASE_IMAGE_URL + movie.getPoster_path(), background_image);

        film_title.setText(movie.getTitle());
        film_overview.setText(movie.getOverview());
        back_btn.setOnClickListener(v -> onBackPressed());

        if (movie.getTrailer() != null) {
            Log.i("MOVIE TRAILER", movie.getTrailer());
            play_trailer_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MovieInformation.this, PlayingTrailer.class);
                    intent.putExtra("video_string", movie.getTrailer());
                    startActivity(intent);
                }
            });
        }else{
            Log.e("MOVIE TRAILER", "NOT EXIST");
        }
    }

    private void initGenres() {
        List<MovieModel.Genre> genres = movie.getGenres();
        genres_group.removeAllViews();
        for(int i = 0; i<genres.size(); i++){
            Chip chip = new Chip(this);
            chip.setCheckable(false);
            chip.setTextColor(getResources().getColor(R.color.white));
            chip.setChipBackgroundColor(ColorStateList.valueOf(getResources().getColor(R.color.black)));
            chip.setChipStrokeColor(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            chip.setChipStrokeWidth(1.0f);
            chip.setText(genres.get(i).getName());
            chip.setId(genres.get(i).getId());
            genres_group.addView(chip);
        }
    }

    private void showReviewPopup() {
        if(review_session_popup !=null){
            layout.post(() -> review_session_popup.showAtLocation(layout, Gravity.BOTTOM, 0, 0));
        }
    }

    private void initCastCrew() {
        initCast();
        initCrew();
        cast_crew_recycler_view.setAdapter(cast_adapter);
        cast_crew_recycler_view.setLayoutManager(new LinearLayoutManager(MovieInformation.this, LinearLayoutManager.HORIZONTAL, false));
        cast_chip.setOnClickListener(v -> {
            crew_chip.setChecked(false);
            cast_crew_recycler_view.setAdapter(cast_adapter);
        });

        crew_chip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cast_chip.setChecked(false);
                cast_crew_recycler_view.setAdapter(crew_adapter);
            }
        });
    }

    private void initInteractions() {
        Log.i("INIT VIDEO", ""+(movie_detail==null));
        //favor button
        if (movie_detail != null) {
            initFavor(movie_detail.isFavor());
            play_button.setText("Watch now");
            play_button.setEnabled(true);
            Intent playMovieIntent = new Intent(MovieInformation.this, PlayMovie.class);
            playMovieIntent.putExtra("movie", movie);
            url360 = movie_detail.getUrl();
            playMovieIntent.putExtra("videoUrl", movie_detail.getUrl());
            url720 = movie_detail.getUrl();
            playMovieIntent.putExtra("videoUrl720", movie_detail.getUrl());
            play_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(playMovieIntent);
                }
            });
            review_btn.setOnClickListener(v -> showReviewPopup());
            initDownloadButton();
            movie.setDuration(movie_detail.getPlay_progress());
            showInteractionButtons();
        }else{
            hideInteractionButtons();
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
                    Intent playMovieIntent = new Intent(MovieInformation.this, PlayMovie.class);
                    playMovieIntent.putExtra("movie", movie);
                    playMovieIntent.putExtra("videoUrl720", movie_detail.getUrl());
                    startActivity(playMovieIntent);
                }
            });
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
        if (movie.getExternal_ids() == null) {
            findViewById(R.id.social_link).setVisibility(View.GONE);
            return;
        }

        if (movie.getExternal_ids().getTwitter_id() != null) {
            twitter_btn.setOnClickListener(v -> {
                Intent twitter_intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.twitter.com/" + movie.getExternal_ids().getTwitter_id()));
                startActivity(twitter_intent);
            });
        } else {
            twitter_btn.setVisibility(View.GONE);
        }

        if (movie.getExternal_ids().getFacebook_id() != null) {
            facebook_btn.setOnClickListener(v -> {
                Intent fb_intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/" + movie.getExternal_ids().getFacebook_id()));
                startActivity(fb_intent);
            });
        } else {
            facebook_btn.setVisibility(View.GONE);
        }

        if (movie.getExternal_ids().getInstagram_id() != null) {
            instagram_btn.setOnClickListener(v -> {
                Intent ins_intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/" + movie.getExternal_ids().getInstagram_id()));
                startActivity(ins_intent);
            });
        } else {
            instagram_btn.setVisibility(View.GONE);
        }

        if (movie.getExternal_ids().getYoutube_id() != null) {
            youtube_btn.setOnClickListener(v -> {
                Intent youtube_intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/" + movie.getExternal_ids().getYoutube_id()));
                startActivity(youtube_intent);
            });
        } else {
            youtube_btn.setVisibility(View.GONE);
        }

        if (movie.getExternal_ids().getTiktok_id() != null) {
            tiktok_btn.setOnClickListener(v -> {
                Intent tiktok_intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.tiktok.com/@" + movie.getExternal_ids().getTiktok_id()));
                startActivity(tiktok_intent);
            });
        } else {
            tiktok_btn.setVisibility(View.GONE);
        }
    }

    public void initFavor(boolean isfavor) {
        favor_btn.setChecked(isfavor);
        if (isfavor) {
            MovieInteraction.subcribeToNotification(movie_id);
        } else {
            MovieInteraction.unsubcribeToNotification(movie_id);
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
                    movie_view_model.delete_review(movie_id);
                    review_session_popup_view.findViewById(R.id.comment_box).setVisibility(View.VISIBLE);
                    totalRating.setText("" +movie_reviews.size());
                }
            };

            if (movie_reviews != null) {
                reviewApdater = new ReviewApdater(MovieInformation.this, movie_reviews, loginAccount.getUser_id(), deleteInterface);
                reviewApdater.removeEmpty();
                for (int i = 0; i < movie_reviews.size(); i++) {
                    if (movie_reviews.get(i).getUser_id() == loginAccount.getUser_id()) {
                        review_session_popup_view.findViewById(R.id.comment_box).setVisibility(View.GONE);
                    }
                }
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MovieInformation.this);
                reviewsRecyclerView = review_session_popup_view.findViewById(R.id.recyclerView);
                reviewsRecyclerView.setLayoutManager(linearLayoutManager);
                reviewsRecyclerView.setAdapter(reviewApdater);
                error_layout.setVisibility(View.GONE);
            }
            rating_bar = review_session_popup_view.findViewById(R.id.comment_box).findViewById(R.id.ratingBar_comment_box);
            review_text = review_session_popup_view.findViewById(R.id.comment_box).findViewById(R.id.commentBox);
            send_btn = review_session_popup_view.findViewById(R.id.send_comment_box);
            ImageView avatar_image = review_session_popup_view.findViewById(R.id.imageAvatar);

            new ImageLoader().loadAvatar(this, loginAccount.getAvatar(), avatar_image, loginAccount.getName());
            send_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    float rating = rating_bar.getRating();
                    String review = review_text.getText().toString().trim();
                    if(rating>0)
                        postReview(rating, review);
                    else
                        Toast.makeText(MovieInformation.this, "Please rate the movie", Toast.LENGTH_SHORT).show();
                }
            });

            loading_recommend_layout.setVisibility(View.GONE);
            totalRating.setText("" + movie_reviews.size());
        }else{
            ReviewApdater.DeleteInterface deleteInterface = () -> {
                movie_view_model.delete_review(movie_id);
                review_session_popup_view.findViewById(R.id.comment_box).setVisibility(View.VISIBLE);
                totalRating.setText("" +movie_reviews.size());
            };

            if (movie_reviews != null) {
                reviewApdater = new ReviewApdater(MovieInformation.this, movie_reviews, loginAccount.getUser_id(), deleteInterface);
                reviewApdater.removeEmpty();
                reviewApdater.notifyDataSetChanged();

                for (int i = 0; i < movie_reviews.size(); i++) {
                    if (movie_reviews.get(i).getUser_id() == loginAccount.getUser_id()) {
                        review_session_popup_view.findViewById(R.id.comment_box).setVisibility(View.GONE);
                    }
                }
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MovieInformation.this);
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
        movie_view_model.changeFavor(movie_id);
    }
    public void postReview(float rating, String review) {
        loading_recommend_layout.setVisibility(View.VISIBLE);
        movie_view_model.post_review(movie.getId(), rating, review);
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
    public void hideInteractionButtons(){
       displayInteractionButtons(false);
    }

    public void showInteractionButtons(){
        displayInteractionButtons(true);
    }

    public void displayInteractionButtons(boolean isVisible){
        int visibility = isVisible ? View.VISIBLE : View.GONE;
        download_btn.setVisibility(visibility);
        review_btn.setVisibility(visibility);
        favor_btn.setVisibility(visibility);
    }
}