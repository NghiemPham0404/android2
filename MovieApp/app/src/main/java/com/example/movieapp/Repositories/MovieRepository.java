package com.example.movieapp.Repositories;

import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.movieapp.Model.MovieModel;
import com.example.movieapp.Request.MovieApiClient;

import java.util.List;

public class MovieRepository {
    private static MovieRepository instance;
    private MovieApiClient movieApiClient;
    private String query, with_genres, with_origin_country;
    private int pageNumber, popularPageNumber, upcommingPageNumber, year;

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
    public LiveData<List<MovieModel>> getDicorverMovies(){return  movieApiClient.getDiscoverMovies();}

    public void searchMovieApi(String query, int pageNumber){
        this.query = query;
        this.pageNumber = pageNumber;
        movieApiClient.searchMovieApi(query, pageNumber);
    }

    public void searchMovieApiNextPage(){
        searchMovieApi(query, ++pageNumber);
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

    public void discoverMovieApi(String with_genres, String with_origin_country ,int year, int pageNumber){
        this.with_genres = with_genres;
        this.with_origin_country = with_origin_country;
        this.year = year;
        this.pageNumber = pageNumber;
        movieApiClient.discoverMovieApi(with_genres, with_origin_country, year, pageNumber);
    }
    public void discoverMovieApiNext() {
        movieApiClient.discoverMovieApi(with_genres, with_origin_country, year, ++pageNumber);
    }
    public int getTotalResults(){
        return movieApiClient.getTotalResults();
    }
}
