package com.example.movieapp.View;

import android.content.Intent;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.movieapp.Adapters.FavorAdapter;
import com.example.movieapp.Adapters.HistoryAdapter;
import com.example.movieapp.Interfaces.FavorInterface;
import com.example.movieapp.Model.AccountModel;
import com.example.movieapp.Model.DetailModel;
import com.example.movieapp.Model.MovieModel;
import com.example.movieapp.R;
import com.example.movieapp.Request.MyService;
import com.example.movieapp.Request.MyService2;
import com.example.movieapp.ViewModel.MovieViewModel;
import com.example.movieapp.utils.Credentials;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
    MovieViewModel movieViewModel;
    private RecyclerView historyRecyclerView;
    private ConstraintLayout loadingScreen;

    List<DetailModel> favHisMovies;
    public List<MovieModel> movies;
    HistoryAdapter historyAdapter;

    public HistoryFragment() {
        // Required empty public constructor
    }

    FavorInterface favor_click = new FavorInterface() {
        @Override
        public void openMovie(int movieId) {
            MovieInteraction.openMovieInformation(getContext(), movieId, loginAccount);
        }

        @Override
        public void playMovie(MovieModel movie, Button button) {
            if (movie.getVideoUrl() != null) {
                button.setBackgroundResource(R.drawable.gradient_corner_bg);
                button.setText("watch now");
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), PlayMovie.class);
                        intent.putExtra("videoUrl", movie.getVideoUrl());
                        intent.putExtra("movie", movie);
                        intent.putExtra("loginAccount", (Parcelable) loginAccount);
                        startActivity(intent);
                    }
                });
            } else {
                String trailer = movie.getTrailer();
                if (trailer != null) {
                    button.setBackgroundResource(R.drawable.play_trailer_btn_2);
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
        movieViewModel = new ViewModelProvider(this).get(MovieViewModel.class);
        ObserveAnyChange();
        movies = new ArrayList<MovieModel>();
    }
    public void ObserveAnyChange(){
        movieViewModel.getHistoryMovies().observe(this, new Observer<List<DetailModel>>() {
            @Override
            public void onChanged(List<DetailModel> detailModels) {
                favHisMovies = detailModels;
                initHistoryList();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history_page, container, false);
        initComponents(view);
        onConfigureRecycler();
        initFeatures();
        return view;
    }
    public void onConfigureRecycler() {
        historyAdapter = new HistoryAdapter(getContext(), movies, favor_click);
        Log.e("ERROR adapter is null : ", " " + (historyAdapter == null));
        historyRecyclerView.setAdapter(historyAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        historyRecyclerView.setLayoutManager(linearLayoutManager);
        Log.i("HISTORY TASK", "history list size : " + historyAdapter.history_list.size());
        Log.i("HISTORY TASK", "movies list size : " + historyAdapter.movies_lists.size());
        Log.i("HISTORY TASK", "movies size : " + historyAdapter.movies.size());
    }
    public void initComponents(View view) {
        historyRecyclerView = view.findViewById(R.id.historyRecycleView);
        loadingScreen = view.findViewById(R.id.loadingLayout);
    }

    public void initFeatures() {
       movieViewModel.searchHistoryMovies(loginAccount.getUser_id());
    }

    public void initHistoryList() {
        loadingScreen.setVisibility(View.VISIBLE);
        movies.clear();
        if(favHisMovies.size()==0){
            loadingScreen.setVisibility(View.GONE);
        }
        for (DetailModel movie : favHisMovies) {
            initListItem(movie.getMovieId(), movie.getDuration(), movie.getUrl(), movie.getUrl720(), movie.getTimeFavor());
        }
    }

    public void initListItem(int movieId, String duration, String videoUrl,String videoUrl720, String favorTime) {
        Call<MovieModel> movieCall = MyService.getMovieApi().searchMovieDetail(movieId, Credentials.API_KEY, "videos", Locale.getDefault().getLanguage());
        movieCall.enqueue(new Callback<MovieModel>() {
            @Override
            public void onResponse(Call<MovieModel> call, Response<MovieModel> response) {
                MovieModel movie = response.body();
                movie.setVideoUrl(videoUrl);
                movie.setVideoUrl720(videoUrl720);
                movie.setDuration(duration);
                movie.setFavorTime(favorTime);
                movies.add(movie);
                if(movies.size() == favHisMovies.size()){
                    historyAdapter.setHistoryMovies(movies);
                    historyAdapter.notifyDataSetChanged();
                    loadingScreen.setVisibility(View.GONE);
                }
//                Log.i("Favor movie : " , movie.getTitle());
            }

            @Override
            public void onFailure(Call<MovieModel> call, Throwable t) {
                Log.i("FAVOR TASK", "Fail to add movie : " + t.toString());
            }
        });
    }
}