package com.example.movieapp.data.Repositories;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.movieapp.Request.MyService;
import com.example.movieapp.data.Response.MovieListResponse;
import com.example.movieapp.utils.Credentials;
import com.example.movieapp.utils.MovieApi;

import java.util.Locale;
import java.util.Objects;

import lombok.Getter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieRepository {
    private static MovieRepository instance;

    @Getter
    final MutableLiveData<MovieListResponse> nowPlayingMovies,
            popularMovies,
            UpcomingMovies,
            discoverMovies,
            searchMovies;
    private final MovieApi movieApi;
    // query for searching movie
    private String query="";
    // parameter for discovery
    private String with_gernes="", origin_country="";
    private int year=0;

    public static MovieRepository getInstance() {
        if (instance == null) {
            instance = new MovieRepository();
        }
        return instance;
    }

    public MovieRepository() {
        movieApi = MyService.getMovieApi();

        nowPlayingMovies = new MutableLiveData<>();
        popularMovies = new MutableLiveData<>();
        UpcomingMovies = new MutableLiveData<>();
        discoverMovies = new MutableLiveData<>();
        searchMovies = new MutableLiveData<>();
    }

    public void requestSearchMovie(String query, int page) {
        if(!this.query.equalsIgnoreCase(query)){
            searchMovies.setValue(null);
            this.query = query;
        }
        Call<MovieListResponse> call = movieApi.searchMovie(query, page, Locale.getDefault().getLanguage());
        callToMutableMovieList(call, searchMovies, "SEARCH MOVIE PAGE : " + page);
    }

    public void requestSearchMoviesNext() {
        if (searchMovies.getValue() == null) requestSearchMovie(query, 1);
        if (searchMovies.getValue().getPage() < searchMovies.getValue().getTotal_pages()) {
            int nextPage = searchMovies.getValue().getPage() + 1;
            requestSearchMovie(query, nextPage);
        }
    }

    public void requestNowPlayingMovie(int page) {
        Call<MovieListResponse> call = movieApi.getNowPlayingMovie(page);
        callToMutableMovieList(call, nowPlayingMovies, "GET NOW PLAYING MOVIE PAGE : " + page);
    }

    public void requestNowPlayingMovieNext() {
        if (nowPlayingMovies.getValue() == null) {
            requestNowPlayingMovie(1);
        } else {
            if (nowPlayingMovies.getValue().getPage() < nowPlayingMovies.getValue().getTotal_pages()) {
                int nextPage = nowPlayingMovies.getValue().getPage() + 1;
                requestNowPlayingMovie(nextPage);
            }
        }
    }

    public void requestPopularMovie(int pageNumber) {
        Call<MovieListResponse> call = movieApi.getPopularMovie( pageNumber);
        callToMutableMovieList(call, popularMovies, "GET POPULAR MOVIE PAGE : " + pageNumber);
    }

    public void requestPopularMovieNext() {
        if (popularMovies.getValue() == null) {
            requestPopularMovie(1);
        } else {
            if (popularMovies.getValue().getPage() < popularMovies.getValue().getTotal_pages()) {
                int nextPage = popularMovies.getValue().getPage() + 1;
                requestPopularMovie(nextPage);
            }
        }
    }

    public void requestUpComingMovie(int pageNumber) {
        Call<MovieListResponse> call = movieApi.getUpComingMovie(pageNumber);
        callToMutableMovieList(call, UpcomingMovies, "GET UPCOMING MOVIE PAGE : " + pageNumber);
    }

    public void requestUpcomingMoviesNext() {
        if (UpcomingMovies.getValue() == null) {
            requestUpComingMovie(1);
        } else {
            if (UpcomingMovies.getValue().getPage() < UpcomingMovies.getValue().getTotal_pages()) {
                int nextPage = UpcomingMovies.getValue().getPage() + 1;
                requestUpComingMovie(nextPage);
            }
        }
    }

    public void discoverMovies(String with_genres, String with_origin_country, int year, int pageNumber) {
        if (!this.with_gernes.equalsIgnoreCase(with_genres) ||
                !this.origin_country.equalsIgnoreCase(with_origin_country) ||
                this.year != year) {
            discoverMovies.setValue(null);
            this.with_gernes = with_genres;
            this.origin_country = with_origin_country;
            this.year = year;
        }
        Call<MovieListResponse> call = movieApi.discoverMovie(
                with_genres,
                with_origin_country,
                year,
                pageNumber,
                Locale.getDefault().getLanguage());
        callToMutableMovieList(call, discoverMovies, "DISCOVER MOVIE PAGE : " + pageNumber);
    }

    public void discoverMoviesNext() {
        if (discoverMovies.getValue() == null) discoverMovies(null, null, 0, 1);
    }

    public void callToMutableMovieList(Call<MovieListResponse> call,
                                       MutableLiveData<MovieListResponse> mutableLiveData,
                                       String debugTAG) {
        call.enqueue(new Callback<MovieListResponse>() {
            @Override
            public void onResponse(Call<MovieListResponse> call, Response<MovieListResponse> response) {
                if (response.isSuccessful()) {
                    MovieListResponse currentMovies = mutableLiveData.getValue();
                    if (currentMovies != null) {
                        currentMovies.getMovies().addAll(response.body().getMovies());
                        currentMovies.setPage(response.body().getPage());
                        mutableLiveData.postValue(currentMovies);
                    } else {
                        mutableLiveData.postValue(response.body());
                    }
                } else {
                    Log.e(debugTAG, Objects.requireNonNull(response.raw().toString()));
                }
            }

            @Override
            public void onFailure(Call<MovieListResponse> call, Throwable t) {
                Log.e(debugTAG, Objects.requireNonNull(t.getMessage()));
            }
        });
    }
}
