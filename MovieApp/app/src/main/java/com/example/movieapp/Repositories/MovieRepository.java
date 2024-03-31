package com.example.movieapp.Repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.movieapp.Model.MovieModel;
import com.example.movieapp.Request.MovieApiClient;

import java.util.List;

public class MovieRepository {
    private static MovieRepository instance;

    private MovieApiClient movieApiClient;

    // LiveData

    public static  MovieRepository getInstance(){
        if(instance == null){
          instance = new MovieRepository();
        }
        return instance;
    }

    public MovieRepository(){
        movieApiClient =MovieApiClient.getInstance();
    }

    public LiveData<List<MovieModel>> getMovies(){
        return movieApiClient.getMovies();
    }

    public void searchMovieApi(String query, int pageNumber){
        movieApiClient.searchMovieApi(query, pageNumber);
    }

    public void searchMovieApi(int list_type, int page){
        movieApiClient.searchMovieApi(list_type, page);
    }
}
