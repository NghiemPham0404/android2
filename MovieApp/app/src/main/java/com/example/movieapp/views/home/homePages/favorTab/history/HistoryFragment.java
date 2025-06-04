package com.example.movieapp.views.home.homePages.favorTab.history;

import android.content.Intent;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.movieapp.Adapters.HistoryAdapter;
import com.example.movieapp.Interfaces.FavorInterface;
import com.example.movieapp.ViewModel.InteractionsListViewModel;
import com.example.movieapp.ViewModel.UserViewModel;
import com.example.movieapp.data.Model.DTO.InteractionDTO.*;
import com.example.movieapp.data.Model.user.UserDTO.*;
import com.example.movieapp.views.movie.MovieInformation;
import com.example.movieapp.views.movie.moviePlaying.PlayMovie;
import com.example.movieapp.views.movie.trailerPlaying.PlayingTrailer;
import com.example.movieapp.data.Model.MovieModel;
import com.example.movieapp.R;
import com.example.movieapp.ViewModel.MovieViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistoryFragment extends Fragment {

    UserInfo loginAccount;
    MovieViewModel movieViewModel;
    InteractionsListViewModel interactionsListViewModel;
    private RecyclerView historyRecyclerView;
    private ConstraintLayout loadingScreen;

    List<InteractionDAOExtended> favHisMovies = new ArrayList<InteractionDAOExtended>();
    HistoryAdapter historyAdapter;
    private UserViewModel userViewModel;

    public HistoryFragment() {
        // Required empty public constructor
    }

    public static HistoryFragment newInstance() {
        return new HistoryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        movieViewModel = new ViewModelProvider(this).get(MovieViewModel.class);
        interactionsListViewModel = new ViewModelProvider(this).get(InteractionsListViewModel.class);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        initFeatures();
    }
    public void ObserveAnyChange() {
        interactionsListViewModel.getHistoryMovie().observe(getViewLifecycleOwner(), (favoriteMovies) -> {
            loadingScreen.setVisibility(View.GONE);
            favHisMovies = favoriteMovies.getData();
            historyAdapter.setMovies(favHisMovies);
            Log.i("HISTORY TASK", "history list size : " + favHisMovies.size());
        });
        userViewModel.getLoginAccount().observe(getViewLifecycleOwner(), userInfo -> {
            loginAccount = userInfo;
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
        ObserveAnyChange();
        return view;
    }
    public void onConfigureRecycler() {
        historyAdapter = new HistoryAdapter(getContext(), favHisMovies, favor_click);
        historyRecyclerView.setAdapter(historyAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        historyRecyclerView.setLayoutManager(linearLayoutManager);
    }
    public void initComponents(View view) {
        historyRecyclerView = view.findViewById(R.id.historyRecycleView);
        loadingScreen = view.findViewById(R.id.loadingLayout);
    }

    public void initFeatures() {
       interactionsListViewModel.requestGetHistoryMovies(1);
    }


    FavorInterface favor_click = new FavorInterface() {
        @Override
        public void openMovie(int movieId) {
            Intent openMovieIntent = new Intent(getContext(), MovieInformation.class);
            openMovieIntent.putExtra("film_id", movieId);
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
                        Intent intent = new Intent(getContext(), PlayMovie.class);
                        intent.putExtra("videoUrl", movie.getVideoUrl());
                        intent.putExtra("videoUrl720", movie.getVideoUrl());
                        intent.putExtra("movie", movie);
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
                    button.setText("Unavailable");
                    button.setBackgroundResource(R.drawable.nomal_stroke);
                }
            }
        }

        @Override
        public void changeFavorite(int movieId) {
            movieViewModel.changeFavor(movieId);
        }
    };
}