package com.example.movieapp.View;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.RadioButton;

import com.example.movieapp.Adapters.FavorAdapter;
import com.example.movieapp.Interfaces.FavorInterface;
import com.example.movieapp.Model.AccountModel;
import com.example.movieapp.Model.DetailModel;
import com.example.movieapp.Model.MovieModel;
import com.example.movieapp.Model.VideoModel;
import com.example.movieapp.R;
import com.example.movieapp.Request.MyService;
import com.example.movieapp.Request.MyService2;
import com.example.movieapp.utils.Credentials;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FavorFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FavorFragment extends Fragment {

    AccountModel loginAccount;
    RecyclerView favorRecyclerView;
    ConstraintLayout loadingScreen;
    FavorAdapter favorAdapter;
    List<MovieModel> fav_movies;

    RadioButton filter_trailer, filter_available, sort_time_btn, sort_rating_btn;
    int sort_time = 0, sort_rating = 0, filter_type= 0;
    Drawable upArrow ;
    Drawable downArrow;

    Drawable starIcon;

    public FavorFragment() {
        // Required empty public constructor
    }

    public static FavorFragment newInstance(AccountModel loginAccount) {
        FavorFragment fragment = new FavorFragment();
        Bundle args = new Bundle();
        args.putParcelable("loginAccount", loginAccount);
        fragment.setArguments(args);
        return fragment;
    }

    FavorInterface favor_click = new FavorInterface() {
        @Override
        public void openMovie(int movieId) {
            Intent openMovieIntent = new Intent(getContext(), Movie_infomation.class);
            openMovieIntent.putExtra("film_id", movieId);
            openMovieIntent.putExtra("loginAccount", loginAccount);
            startActivity(openMovieIntent);
        }

        @Override
        public void playMovie(MovieModel movie, Button button) {
                    if (movie.getVideoUrl()!=null) {
                        button.setBackgroundResource(R.drawable.gradient_corner_bg);
                        button.setText("watch now");
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getContext(), PlayingFilm.class);
                                intent.putExtra("videoUrl", movie.getVideoUrl());
                                intent.putExtra("movie", movie);
                                intent.putExtra("loginAccount", loginAccount);
                                startActivity(intent);
                            }
                        });
                    } else {
                        String trailer = movie.getTrailer();
                        if (trailer != null) {
                            button.setBackgroundResource(R.drawable.play_trailer_btn);
                            button.setText("play trailer");
                            button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(getContext(), PlayingTrailer.class);
                                    intent.putExtra("video_string", trailer);
                                    startActivity(intent);
                                }
                            });
                        } else {
                            button.setText("Unavailable");
                            button.setBackgroundResource(R.drawable.nomal_stroke);
                        }
                    }
        }

        @Override
        public void changeFavorite(int movieId) {
            Call<DetailModel> detailModelCall = MyService2.getApi().addToFavor("detail", loginAccount.getUser_id(), movieId);
            detailModelCall.enqueue(new Callback<DetailModel>() {
                @Override
                public void onResponse(Call<DetailModel> call, Response<DetailModel> response) {
                    if (response.code() == 200) {

                    }
                }

                @Override
                public void onFailure(Call<DetailModel> call, Throwable t) {
                    Log.e("FAVOR TASK", "change favor fail");
                }
            });
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            loginAccount = getArguments().getParcelable("loginAccount");
        }
        fav_movies = new ArrayList<MovieModel>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favor, container, false);
        initComponents(view);
        return view;
    }

    public void initComponents(View view) {
        upArrow = getResources().getDrawable(R.drawable.baseline_keyboard_arrow_up_24);
        downArrow = getResources().getDrawable(R.drawable.baseline_keyboard_arrow_down_24);
        starIcon = getResources().getDrawable(R.drawable.star_icon);

        favorRecyclerView = view.findViewById(R.id.favorRecycleView);
        loadingScreen = view.findViewById(R.id.loadingLayout);
        filter_trailer = view.findViewById(R.id.trailer_filter_btn);
        filter_available = view.findViewById(R.id.available_filter_btn);
        sort_time_btn = view.findViewById(R.id.sort_time_btn);
        sort_rating_btn = view.findViewById(R.id.sort_rating_btn);

        initFeatures();
    }

    public void initFeatures() {
        loadingScreen.setVisibility(View.VISIBLE);
        fav_movies.clear();
        favorAdapter = new FavorAdapter(getContext(), fav_movies, favor_click);
        Call<List<DetailModel>> favorListCall = MyService2.getApi().getFavorListByUserId(Credentials.functionname_detail, loginAccount.getUser_id());
        favorListCall.enqueue(new Callback<List<DetailModel>>() {
            @Override
            public void onResponse(Call<List<DetailModel>> call, Response<List<DetailModel>> response) {
                if (response.code() == 200) {
                    for (DetailModel movie : response.body()) {
                        initListItem(movie.getMovieId(), movie.getDuration(), movie.getMovieUrl(), movie.getTimeFavor());
                    }
                    initList();
                    loadingScreen.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<List<DetailModel>> call, Throwable t) {
                Log.e("FAVOR TASK", t.toString());
            }
        });

        filter_available.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filter_available();
            }
        });

        filter_trailer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    filter_trailer();
                }
            }
        });

        sort_time_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               resetSortButton(sort_rating, sort_rating_btn);
                sort_time_order();
                favorAdapter.notifyDataSetChanged();
            }
        });

        sort_rating_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetSortButton(sort_time, sort_time_btn);
                sort_rating_order();
                favorAdapter.notifyDataSetChanged();
            }
        });
    }

    public void sort_time_order(){
        switch (sort_time) {
            case 0:
                sort_time = 1;
                sort_time_btn.setCompoundDrawablesWithIntrinsicBounds(null, null, downArrow, null);
                break;
            case 1:
                sort_time = 2;
                sort_time_btn.setCompoundDrawablesWithIntrinsicBounds(null, null, upArrow, null);
                break;
            case 2:
                sort_time = 0;
                sort_time_btn.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                break;
        }
        favorAdapter.sort_by_release_date(sort_time);
    }

    public void sort_rating_order(){
        switch (sort_rating) {
            case 0:
                sort_rating = 1;
                sort_rating_btn.setCompoundDrawablesWithIntrinsicBounds(starIcon, null, downArrow, null);
                break;
            case 1:
                sort_rating = 2;
                sort_rating_btn.setCompoundDrawablesWithIntrinsicBounds(starIcon, null,upArrow , null);
                break;
            case 2:
                sort_rating = 0;
                sort_rating_btn.setCompoundDrawablesWithIntrinsicBounds(starIcon, null, null, null);
                break;
        }
        favorAdapter.sort_by_rating(sort_rating);
    }


    public void resetSortButton(int sort ,RadioButton button){
        sort = 0;
       button.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
       button.setChecked(false);
    }

    public void filter_available(){
        List<MovieModel> filter_list = new ArrayList<MovieModel>();
        for(int i = 0 ; i< fav_movies.size(); i++){
            if(fav_movies.get(i).getVideoUrl() != null){
                filter_list.add(fav_movies.get(i));
            }
        }
        favorAdapter = new FavorAdapter(getContext(), filter_list, favor_click);
        favorAdapter.sort_by_rating(sort_rating);
        favorAdapter.sort_by_release_date(sort_time);
        initList();
    }
    public void filter_trailer() {
       List<MovieModel> filter_list = new ArrayList<MovieModel>();
        for(int i = 0 ; i< fav_movies.size(); i++){
            if(fav_movies.get(i).getVideoUrl() == null && fav_movies.get(i).getTrailer()!=null){
                filter_list.add(fav_movies.get(i));
            }
        }
        favorAdapter = new FavorAdapter(getContext(), filter_list, favor_click);
        favorAdapter.sort_by_rating(sort_rating);
        favorAdapter.sort_by_release_date(sort_time);
        initList();
    }

    public void initList() {
        Log.e("ERROR adapter is null : ", " " + (favorAdapter == null));
        favorRecyclerView.setAdapter(favorAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        favorRecyclerView.setLayoutManager(linearLayoutManager);
    }

    public void initListItem(int movieId, String duration, String videoUrl, String favorTime) {
        Call<MovieModel> movieCall = MyService.getMovieApi().searchMovieDetail(movieId, Credentials.API_KEY, "videos");
        movieCall.enqueue(new Callback<MovieModel>() {
            @Override
            public void onResponse(Call<MovieModel> call, Response<MovieModel> response) {
                MovieModel movie = response.body();
                movie.setVideoUrl(videoUrl);
                movie.setDuration(duration);
                movie.setFavorTime(favorTime);
                fav_movies.add(movie);
                favorAdapter.sort_by_rating(0);
                favorAdapter.notifyDataSetChanged();
//                Log.i("Favor movie : " , movie.getTitle());
            }

            @Override
            public void onFailure(Call<MovieModel> call, Throwable t) {
                Log.i("FAVOR TASK", "Fail to add movie : " + t.toString());
            }
        });
    }
}