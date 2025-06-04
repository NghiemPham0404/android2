package com.example.movieapp.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.movieapp.data.Model.MovieModel;
import com.example.movieapp.data.Repositories.MovieRepository;
import com.example.movieapp.data.Response.MovieListResponse;

import java.util.List;

// TODO : this class is ViewModel class for MovieListResponse
public class MovieListViewModel extends ViewModel {

    private static MovieRepository movieRepository;
    // Constructor
    public MovieListViewModel() {
        movieRepository = MovieRepository.getInstance();
    }

    public LiveData<MovieListResponse> getSearchMovies(){return movieRepository.getSearchMovies();}
    public LiveData<MovieListResponse> getNowPlayingMovies(){return movieRepository.getNowPlayingMovies();}
    public LiveData<MovieListResponse> getPopularMovies(){return movieRepository.getPopularMovies();}
    public LiveData<MovieListResponse> getUpComingMovies(){return movieRepository.getUpcomingMovies();}
    public LiveData<MovieListResponse> getDiscoverMovies(){return  movieRepository.getDiscoverMovies();}

    public void requestSearchMovie(String query, int pageNumber){
        movieRepository.requestSearchMovie(query, pageNumber);
    }

    public void requestSearchMovieNextPage(){
        movieRepository.requestSearchMoviesNext();
    }

    public void requestNowPlayingMovie(int pageNumber) {
        movieRepository.requestNowPlayingMovie(pageNumber);
    }
    public void requestNowPlayingMovieNextPage(){
        movieRepository.requestNowPlayingMovieNext();
    }

    public void requestPopularMovies(int pageNumber){
        movieRepository.requestPopularMovie(pageNumber);
    }

    public void requestPopularMoviesNextPage(){
        movieRepository.requestPopularMovieNext();
    }

    public void requestUpComingMovies(int pageNumber){
        movieRepository.requestUpComingMovie(pageNumber);
    }

    public void requestUpcomingMoviesNextPage(){
        movieRepository.requestUpcomingMoviesNext();
    }

    public void requestDiscoverMovies(String with_genres, String with_origin_country , int year, int pageNumber){
        movieRepository.discoverMovies(with_genres, with_origin_country, year, pageNumber);
    }

    public void discoverMoviesNext() {
        movieRepository.discoverMoviesNext();
    }
}
