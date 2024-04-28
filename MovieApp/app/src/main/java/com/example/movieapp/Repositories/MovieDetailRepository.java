package com.example.movieapp.Repositories;

import androidx.lifecycle.LiveData;

import com.example.movieapp.Model.MovieModel;
import com.example.movieapp.Request.MovieDetailApiClient;

public class MovieDetailRepository {
    int id;
    public static  MovieDetailRepository instance;
    MovieDetailApiClient movieDetailApiClient;
    public LiveData<MovieModel> movie;
    public static MovieDetailRepository getInstance(){
        if(instance == null){
            instance = new MovieDetailRepository();
        }
        return instance;
    }

    public MovieDetailRepository(){
        movieDetailApiClient = MovieDetailApiClient.getInstance();
    }

    public void searchMovie(int id){
        this.id = id;
        movieDetailApiClient.searchMovieApi(this.id);
    }
    public LiveData<MovieModel> getMovie(){
        return movieDetailApiClient.getMovie();
    }

}
