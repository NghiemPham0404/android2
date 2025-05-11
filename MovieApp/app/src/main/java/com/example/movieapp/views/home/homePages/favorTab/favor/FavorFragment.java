package com.example.movieapp.views.home.homePages.favorTab.favor;

import android.content.Intent;
import android.graphics.drawable.Drawable;
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

import com.example.movieapp.Adapters.FavorAdapter;
import com.example.movieapp.Interfaces.FavorInterface;
import com.example.movieapp.ViewModel.InteractionsListViewModel;
import com.example.movieapp.ViewModel.UserViewModel;
import com.example.movieapp.data.Model.DTO.InteractionDTO.*;
import com.example.movieapp.data.Model.user.UserDTO;
import com.example.movieapp.views.movie.MovieInformation;
import com.example.movieapp.views.movie.moviePlaying.PlayMovie;
import com.example.movieapp.views.movie.trailerPlaying.PlayingTrailer;
import com.example.movieapp.data.Model.MovieModel;
import com.example.movieapp.R;
import com.example.movieapp.ViewModel.MovieViewModel;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FavorFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FavorFragment extends Fragment {

    UserDTO.UserInfo loginAccount;
    MovieViewModel movieViewModel;
    InteractionsListViewModel interactionsViewModel;
    RecyclerView favorRecyclerView;
    ConstraintLayout loadingScreen;
    FavorAdapter favorAdapter;
    List<InteractionDAOExtended> favHisMovies;
    Drawable upArrow;
    Drawable downArrow;
    Drawable starIcon;
    private UserViewModel userViewModel;

    public FavorFragment() {
        // Required empty public constructor
    }

    public static FavorFragment newInstance() {
        return new FavorFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        movieViewModel = new ViewModelProvider(this).get(MovieViewModel.class);
        interactionsViewModel = new ViewModelProvider(this).get(InteractionsListViewModel.class);
        initFeatures();
        ObserveAnyChange();
    }

    public void ObserveAnyChange() {
        if(interactionsViewModel!=null){
            interactionsViewModel.getFavoritesMovie().observe(this, (favoriteMovies) -> {
                favHisMovies = favoriteMovies.getData();
                if (favHisMovies != null) {
                    loadingScreen.setVisibility(View.GONE);
                    onConfigureRecycler();
                    if (!favHisMovies.isEmpty()) {
                        for (InteractionDAOExtended movie : favHisMovies) {
                            Log.i("Movie favor", movie.getMovie_id() + "");
                        }
                    } else {
                        Log.i("Movie favor", "leng = 0");
                    }
                } else {
                    Log.i("Movie favor", "null");
                }
            });
        }
        if(userViewModel!=null){
            userViewModel.getLoginedAccount().observe(this, userInfo -> {
                Log.i("User is NULL  2 ?",(userInfo == null)+" ??");
                loginAccount = userInfo;
            });
        }else{
            Log.i("User is NULL  3 ?"," true");
        }
    }

    public void onConfigureRecycler() {
        favorAdapter = new FavorAdapter(getContext(), favHisMovies, favor_click);
        favorRecyclerView.setAdapter(favorAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        favorRecyclerView.setLayoutManager(linearLayoutManager);
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
    }

    public void initFeatures() {
        userViewModel.requestLoginedAccount();
        Log.i("User is NULL  2 ? :",(loginAccount == null)+" ??");
        interactionsViewModel.requestGetFavoriteMovies(0);
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