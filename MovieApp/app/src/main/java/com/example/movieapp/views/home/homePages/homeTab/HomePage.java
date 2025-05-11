package com.example.movieapp.views.home.homePages.homeTab;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.movieapp.Adapters.FilmAdapter;
import com.example.movieapp.Adapters.FilmSliderAdapter;
import com.example.movieapp.ViewModel.UserViewModel;
import com.example.movieapp.data.Model.user.UserDTO.*;
import com.example.movieapp.views.SearchPage;
import com.example.movieapp.data.Model.MovieModel;
import com.example.movieapp.R;
import com.example.movieapp.data.Response.MovieListResponse;
import com.example.movieapp.ViewModel.MovieListViewModel;

import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomePage extends Fragment {
    ViewPager2 movie_slider;
    Button searchBtn;

    LinearLayout divSearch;

    // LIVE data
    private MovieListViewModel movieListSearchViewModel;
    private UserViewModel userViewModel;

    UserInfo loginAccount;

    private RecyclerView  popular_recycler, upcomming_recycler;
    private FilmAdapter film_adapter_popular, film_adapter_upcomming;
    private ScrollView recyclerView_scrollview;
    private int currentPage = 0;
    private Timer timer;
    private Runnable update;
    private Handler handler;

    public HomePage() {
        // Required empty public constructor
    }

    public static HomePage newInstance() {
        return new HomePage();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
    }

    public void ObserveAnyChanges() {

        if (movieListSearchViewModel != null) {
            movieListSearchViewModel.getNowPlayingMovies().observe(getViewLifecycleOwner(), movieListResponse -> {
                if (movieListResponse != null) {
                    FilmSliderAdapter filmSliderAdapter = new FilmSliderAdapter(getContext(), movieListResponse.getMovies());

                    movie_slider.setAdapter(filmSliderAdapter);
                    movie_slider.setCurrentItem(1);
                    movie_slider.setOffscreenPageLimit(3);
                    movie_slider.setClipChildren(false);
                    movie_slider.setClipToPadding(false);
                    movie_slider.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

                    CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
                    compositePageTransformer.addTransformer(new MarginPageTransformer(40));
                    compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
                        @Override
                        public void transformPage(@NonNull View page, float position) {
                            float r = 1 - Math.abs(position);
                            page.setScaleY(0.85f + r*0.15f);
                        }
                    });

                    movie_slider.setPageTransformer(compositePageTransformer);
                }
            });

            movieListSearchViewModel.getPopularMovies().observe(getViewLifecycleOwner(), movieListResponse -> {
                if(movieListResponse != null){
                    film_adapter_popular.setMovies(movieListResponse.getMovies());
                }
            });

            movieListSearchViewModel.getUpComingMovies().observe(getViewLifecycleOwner(), movieListResponse -> {
                if(movieListResponse != null){
                    for (MovieModel movie : movieListResponse.getMovies()) {
                        Log.i("Movie upcomming", movie.getTitle());
                    }
                    film_adapter_upcomming.setMovies(movieListResponse.getMovies());
                }
            });
        }

        if(userViewModel!=null){
            userViewModel.getLoginedAccount().observe(getViewLifecycleOwner(), currentAccount ->{
                if(currentAccount != null){
                    loginAccount = currentAccount;
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
        ObserveAnyChanges();
        return view;
    }

    public void initComponents(View view) {
        movie_slider = view.findViewById(R.id.movie_slider);

        recyclerView_scrollview = view.findViewById(R.id.recyclerView_scrollview);
        searchBtn = view.findViewById(R.id.searchBtn);
        divSearch = view.findViewById(R.id.searchDiv);

        popular_recycler = view.findViewById(R.id.popular_recycler);
        upcomming_recycler = view.findViewById(R.id.upcomming_recycler);
    }

    public void initFeatures() {
        recyclerView_scrollview.setVisibility(View.VISIBLE);

       divSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSearchIntent();
            }
        });


       movieListSearchViewModel.requestNowPlayingMovie(1);
       movieListSearchViewModel.requestNowPlayingMovie(1);
       movieListSearchViewModel.requestPopularMovies(1);
       movieListSearchViewModel.requestUpComingMovies(1);
       startAutoScroll();
    }

    public void goToSearchIntent(){
        Intent intent = new Intent(getContext(), SearchPage.class);
        intent.putExtra("loginAccount", (Parcelable) loginAccount);
        getContext().startActivity(intent);
        getActivity().overridePendingTransition(0, 0);
    }

    public void configureSearchRecycleView() {
        if (popular_recycler != null) {
            popular_recycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

            film_adapter_popular = new FilmAdapter(getContext());
            popular_recycler.setAdapter(film_adapter_popular);

            popular_recycler.setOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    if(!popular_recycler.canScrollHorizontally(1)){
                        movieListSearchViewModel.requestPopularMoviesNextPage();
                    }
                }
            });
        }

        if(upcomming_recycler != null){
            upcomming_recycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

            film_adapter_upcomming = new FilmAdapter(getContext());
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