package com.example.movieapp.View;


import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.os.Parcelable;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.movieapp.Adapters.FilmAdapter;
import com.example.movieapp.Adapters.FilmSliderAdapter;
import com.example.movieapp.Model.AccountModel;
import com.example.movieapp.Model.MovieModel;
import com.example.movieapp.Model.MoviesGroup;
import com.example.movieapp.R;
import com.example.movieapp.Request.MyService;
import com.example.movieapp.Response.MovieSearchResponse;
import com.example.movieapp.ViewModel.MovieListViewModel;
import com.example.movieapp.utils.Credentials;
import com.example.movieapp.utils.MovieApi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomePage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomePage extends Fragment {

    private static final int REQUEST_CODE_SPEECH_INPUT = 100;
    private static final int PERMISSION_REQUEST_RECORD_AUDIO = 99;

    ViewPager2 movie_slider;
    Button searchBtn, textToSpeechBtn;
    ;
    EditText searchBox;

    LinearLayout divSearch;

    // LIVE data
    private MovieListViewModel movieListSearchViewModel;
    AccountModel loginAccount;
    int page = 1;

    private LinearLayout layout;
    private RecyclerView searchMoviesRecycler, popular_recycler, upcomming_recycler;
    private TextView searchTitleTV;
    private LinearLayout search_film_group;
    private FilmAdapter filmSearchAdapter, film_adapter_popular, film_adapter_upcomming;
    private ScrollView recyclerView_scrollview;
    private int currentPage = 0;
    private Timer timer;
    private Runnable update;
    private Handler handler;

    public HomePage() {
        // Required empty public constructor
    }

    public static HomePage newInstance(AccountModel loginAccount) {
        HomePage fragment = new HomePage();
        Bundle args = new Bundle();
        args.putParcelable("loginAccount", loginAccount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            loginAccount = (AccountModel) getArguments().get("loginAccount");
        }
        if(timer!=null){
            timer.cancel();
        }
        if(handler != null){
            handler.removeCallbacksAndMessages(null);
        }
        if(update!=null){
            update = null;
        }
        movieListSearchViewModel = new ViewModelProvider(this).get(MovieListViewModel.class);
        ObserveAnyChanges();
    }

    public void ObserveAnyChanges() {
        if (movieListSearchViewModel != null) {
            movieListSearchViewModel.getMovies().observe(this, new Observer<List<MovieModel>>() {
                @Override
                public void onChanged(List<MovieModel> movieModels) {
                    if (movieModels != null) {
                        filmSearchAdapter.setMovies(movieModels);
                        filmSearchAdapter.notifyDataSetChanged();

                        String search_title = (movieModels.size() > 1) ? movieModels.size() + "-" + movieListSearchViewModel.getTotalResults() + " results has been found" :
                                0 + " result has been found";
                        searchTitleTV.setText(search_title);
                    }
                }
            });

            movieListSearchViewModel.getFavorMovies().observe(this, new Observer<List<MovieModel>>() {
                @Override
                public void onChanged(List<MovieModel> movieModels) {
                    if(movieModels != null){
                        for (MovieModel movie : movieModels) {
                            Log.i("Movie favor", movie.getTitle());
                        }
                        film_adapter_popular.setMovies(movieModels);
                        film_adapter_popular.notifyDataSetChanged();
                    }
                }
            });

            movieListSearchViewModel.getUpcommingMovies().observe(this, new Observer<List<MovieModel>>() {
                @Override
                public void onChanged(List<MovieModel> movieModels) {
                    if(movieModels != null){
                        for (MovieModel movie : movieModels) {
                            Log.i("Movie upcomming", movie.getTitle());
                        }
                        film_adapter_upcomming.setMovies(movieModels);
                        film_adapter_upcomming.notifyDataSetChanged();
                    }
                }
            });
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_page, container, false);
        initComponents(view);
        configureSearchRecycleView();
        initFeatures();
        return view;
    }

    public void initComponents(View view) {
        movie_slider = view.findViewById(R.id.movie_slider);

        recyclerView_scrollview = view.findViewById(R.id.recyclerView_scrollview);
        searchBtn = view.findViewById(R.id.searchBtn);
        divSearch = view.findViewById(R.id.searchDiv);
        layout = view.findViewById(R.id.home_layout);

        search_film_group = view.findViewById(R.id.search_film_group);
        searchTitleTV = search_film_group.findViewById(R.id.groupTitle);
        searchMoviesRecycler = search_film_group.findViewById(R.id.movieGroup_recyclerview);

        popular_recycler = view.findViewById(R.id.popular_recycler);
        upcomming_recycler = view.findViewById(R.id.upcomming_recycler);
        configureSearchRecycleView();
    }

    public void initFeatures() {
        recyclerView_scrollview.setVisibility(View.VISIBLE);
        search_film_group.setVisibility(View.GONE);

       divSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSearchIntent();
            }
        });

        MovieApi movieApi = MyService.getMovieApi();
        Call<MovieSearchResponse> NowplayingResponseCall =
                movieApi.searchMoviesList(Credentials.BASE_URL
                        + Credentials.NOW_PLAYING, Credentials.API_KEY, 1);

        NowplayingResponseCall.enqueue(new Callback<MovieSearchResponse>() {
            @Override
            public void onResponse(Call<MovieSearchResponse> call, Response<MovieSearchResponse> response) {
                if (response.isSuccessful()) {
                    FilmSliderAdapter filmSliderAdapter = new FilmSliderAdapter(getContext(), response.body().getMovies(), loginAccount);
                    movie_slider.setAdapter(filmSliderAdapter);
                }
            }

            @Override
            public void onFailure(Call<MovieSearchResponse> call, Throwable t) {
                Log.e("Load nowplaying", "fail");
            }
        });
        movieListSearchViewModel.searchFavorMovieApi(1);
        movieListSearchViewModel.searchUpcommingMovieApi(1);
        startAutoScroll();
    }

    public void searchMovie(String query, int page) {
        recyclerView_scrollview.setVisibility(View.GONE);
        search_film_group.setVisibility(View.VISIBLE);
        movieListSearchViewModel.searchMovieApi(query, page);
    }

    public void goToSearchIntent(){
        Intent intent = new Intent(getContext(), SearchPage.class);
        intent.putExtra("loginAccount", (Parcelable) loginAccount);
        getContext().startActivity(intent);
        getActivity().overridePendingTransition(0, 0);
    }

    public void configureSearchRecycleView() {
        if (searchMoviesRecycler != null) {
            searchMoviesRecycler.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

            filmSearchAdapter = new FilmAdapter(getContext(), loginAccount);
            searchMoviesRecycler.setAdapter(filmSearchAdapter);

            searchMoviesRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    if (!searchMoviesRecycler.canScrollVertically(1)) {
                        movieListSearchViewModel.searchMovieApiNextPage();
                    }
                }
            });
        }

        if (popular_recycler != null) {
            popular_recycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

            film_adapter_popular = new FilmAdapter(getContext(), loginAccount);
            popular_recycler.setAdapter(film_adapter_popular);

            popular_recycler.setOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    if(!popular_recycler.canScrollHorizontally(1)){
                        movieListSearchViewModel.searchFavorMovieApiNextPage();
                    }
                }
            });
        }

        if(upcomming_recycler != null){
            upcomming_recycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

            film_adapter_upcomming = new FilmAdapter(getContext(), loginAccount);
            upcomming_recycler.setAdapter(film_adapter_upcomming);
        }
    }
    private void startAutoScroll() {
        movie_slider.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                currentPage = position;
            }
        });

        handler = new Handler();
         update = new Runnable() {
            public void run() {
                if(movie_slider.getAdapter()!=null){
                    if (currentPage == movie_slider.getAdapter().getItemCount()) {
                        currentPage = 0;
                    }
                    movie_slider.setCurrentItem(currentPage++, true);
                }
            }
        };

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(update);
            }
        }, 5000, 3000);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopTimer();
    }
    public void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopTimer();
    }
}