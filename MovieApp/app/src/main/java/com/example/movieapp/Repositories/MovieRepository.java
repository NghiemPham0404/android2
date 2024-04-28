package com.example.movieapp.Repositories;

import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.movieapp.Model.MovieModel;
import com.example.movieapp.Request.MovieApiClient;

import java.util.List;

public class MovieRepository {
    private static MovieRepository instance;
    private MovieApiClient movieApiClient;
    private String query;
    private int pageNumber, popularPageNumber, upcommingPageNumber;

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
    public  LiveData<List<MovieModel>> getFavorMovies(){return  movieApiClient.getPopularMovies();}

    public LiveData<List<MovieModel>> getUpcommingMovies(){return  movieApiClient.getUpcommingMovies();}

    public void searchMovieApi(String query, int pageNumber){
        this.query = query;
        this.pageNumber = pageNumber;
        movieApiClient.searchMovieApi(query, pageNumber);
    }

    public void searchMovieApiNextPage(){
        searchMovieApi(query, pageNumber+1);
    }

    public void searchMovieApi(int list_type, int page){
        Log.i("movielist","finding2");
        switch (list_type){
            case  2:
                popularPageNumber = page;
                movieApiClient.searchFavorMovieApi(popularPageNumber);
                break;
            case 4:
                upcommingPageNumber = page;
                movieApiClient.searchUpcommingMovieApi(upcommingPageNumber);
                break;
        }
    }

    public void searchPopularMovieApiNextPage(){
        movieApiClient.searchFavorMovieApi(++popularPageNumber);
    }

    public void searchUpcommingMovieApiNextPage(){
        movieApiClient.searchUpcommingMovieApi(++upcommingPageNumber);
    }

    public void discoverMovieApi(String genre_str, String country ,int year, int pageNumber){
        movieApiClient.discoverMovieApi(genre_str, country, year, pageNumber);
    }

    public int getTotalResults(){
        return movieApiClient.getTotalResults();
    }
}
