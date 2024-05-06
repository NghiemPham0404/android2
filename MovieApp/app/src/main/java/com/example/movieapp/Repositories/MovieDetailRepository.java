package com.example.movieapp.Repositories;

import androidx.lifecycle.LiveData;

import com.example.movieapp.Model.DetailModel;
import com.example.movieapp.Model.MovieModel;
import com.example.movieapp.Request.MovieDetailApiClient;
import com.example.movieapp.utils.Credentials;

import java.util.List;

public class MovieDetailRepository {
    int id;
    String user_id;
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

    public void searchMovieDetail(int id, String user_id){
        this.id = id;
        this.user_id = user_id;
        movieDetailApiClient.searchMovieDetailApi(id, user_id);
    }
    public LiveData<DetailModel> getMovieDetail(){
        return movieDetailApiClient.getMovieDetail();
    }
    public void changeFavor(){
        movieDetailApiClient.changeFavor(id, user_id);
    }

    public void getReviews(int id){
        this.id = id;
        movieDetailApiClient.getReviews(id);
    }
    public LiveData<List<DetailModel>> getMovieReviews(){
        return movieDetailApiClient.getMovieReviews();
    }

    public void deleteReview(){
        movieDetailApiClient.deleteReview(id, user_id);
    }
    public void postReview(int id, String user_id, String rating, String review){
        movieDetailApiClient.postReview(id, user_id, rating, review);
    }
    public void searchFavHisMovies(String user_id){
        movieDetailApiClient.searchFavHisMovies(user_id);
    }
    public void searchHistoryMovies(String user_id){
        movieDetailApiClient.searchHisMovies(user_id);
    }
    public LiveData<List<DetailModel>> getFavHisMovies(){
        return movieDetailApiClient.getFavHisMovies();
    }
}
