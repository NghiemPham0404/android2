package com.example.movieapp.View;

import android.content.Intent;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.movieapp.Adapters.HistoryAdapter;
import com.example.movieapp.Interfaces.FavorInterface;
import com.example.movieapp.Model.AccountModel;
import com.example.movieapp.Model.DetailModel;
import com.example.movieapp.Model.MovieModel;
import com.example.movieapp.R;
import com.example.movieapp.Request.MyService;
import com.example.movieapp.Request.MyService2;
import com.example.movieapp.utils.Credentials;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistoryFragment extends Fragment {

    AccountModel loginAccount;
    private RecyclerView historyRecyclerView;
    private ConstraintLayout loadingScreen;

    public List<MovieModel> movies;
    HistoryAdapter historyAdapter;

    public HistoryFragment() {
        // Required empty public constructor
    }

    FavorInterface favor_click = new FavorInterface() {
        @Override
        public void openMovie(int movieId) {
            Intent openMovieIntent = new Intent(getContext(), Movie_infomation.class);
            openMovieIntent.putExtra("film_id", movieId);
            openMovieIntent.putExtra("loginAccount", (Parcelable) loginAccount);
            startActivity(openMovieIntent);
        }

        @Override
        public void playMovie(MovieModel movie, Button button) {
            if (movie.getVideoUrl() != null) {
                button.setBackgroundResource(R.drawable.gradient_corner_bg);
                button.setText("watch now");
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), PlayingFilm.class);
                        intent.putExtra("videoUrl", movie.getVideoUrl());
                        intent.putExtra("movie", movie);
                        intent.putExtra("loginAccount", (Parcelable) loginAccount);
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
                    button.setBackgroundResource(R.drawable.nomal_stroke);
                    button.setText("Unavailable");
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

    public static HistoryFragment newInstance(AccountModel loginAccount) {
        HistoryFragment fragment = new HistoryFragment();
        Bundle args = new Bundle();
        args.putParcelable("loginAccount", loginAccount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            loginAccount = getArguments().getParcelable("loginAccount");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initFeatures();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history_page, container, false);
        initComponents(view);
        return view;
    }

    public void initComponents(View view) {
        historyRecyclerView = view.findViewById(R.id.historyRecycleView);
        loadingScreen = view.findViewById(R.id.loadingLayout);
    }

    public void initFeatures() {
        loadingScreen.setVisibility(View.VISIBLE);
        movies = new ArrayList<MovieModel>();

        Call<List<DetailModel>> favorListCall = MyService2.getApi().getFavorListByUserId(Credentials.functionname_detail, loginAccount.getUser_id());
        favorListCall.enqueue(new Callback<List<DetailModel>>() {
            @Override
            public void onResponse(Call<List<DetailModel>> call, Response<List<DetailModel>> response) {
                if (response.code() == 200) {
                    for (DetailModel movie : response.body()) {
                        initListItem(movie.getMovieId(), movie.getDuration(), movie.getUrl(), movie.getTimeFavor());
                    }
                }
            }

            @Override
            public void onFailure(Call<List<DetailModel>> call, Throwable t) {
                Log.e("HISTORY TASK", t.toString());
            }
        });
    }

    public void initList() {
        Log.e("ERROR adapter is null : ", " " + (historyAdapter == null));
        historyRecyclerView.setAdapter(historyAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        historyRecyclerView.setLayoutManager(linearLayoutManager);
        Log.i("HISTORY TASK", "history list size : " + historyAdapter.history_list.size());
        Log.i("HISTORY TASK", "movies list size : " + historyAdapter.movies_lists.size());
        Log.i("HISTORY TASK", "movies size : " + historyAdapter.movies.size());
    }

    public void initListItem(int movieId, String duration, String videoUrl, String favorTime) {
        Call<MovieModel> movieCall = MyService.getMovieApi().searchMovieDetail(movieId, Credentials.API_KEY, "videos");
        movieCall.enqueue(new Callback<MovieModel>() {
            @Override
            public void onResponse(Call<MovieModel> call, Response<MovieModel> response) {
                MovieModel movie = response.body();
                movie.setDuration(duration);
                movie.setVideoUrl(videoUrl);
                movie.setFavorTime(favorTime);
                movies.add(movie);
                Log.i("Favor movie : ", movie.getTitle());
                historyAdapter = new HistoryAdapter(getContext(), movies, favor_click);
                initList();
                loadingScreen.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<MovieModel> call, Throwable t) {
                Log.i("FAVOR TASK", "Fail to add movie : " + t.toString());
            }
        });
    }


}