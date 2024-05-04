package com.example.movieapp.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.movieapp.Model.DetailModel;
import com.example.movieapp.Model.MovieModel;
import com.example.movieapp.Repositories.MovieDetailRepository;

import java.util.List;

public class MovieViewModel extends ViewModel {
    private MovieDetailRepository movieDetailRepository;

    public MovieViewModel(){
        movieDetailRepository = MovieDetailRepository.getInstance();
    }

    public void searchMovie(int id){
        movieDetailRepository.searchMovie(id);
    }

    public LiveData<MovieModel> getMovie(){
        return movieDetailRepository.getMovie();
    }
    public void searchMovieDetail(int id, String user_id){
        movieDetailRepository.searchMovieDetail(id,user_id);
    }
    public LiveData<DetailModel> getDetailMovie(){
        return movieDetailRepository.getMovieDetail();
    }
    public void changeFavor(){
        movieDetailRepository.changeFavor();
    }
    public void getReviews(int id){
        movieDetailRepository.getReviews(id);
    }
    public LiveData<List<DetailModel>> getMovieReviews(){
       return movieDetailRepository.getMovieReviews();
    }
    public void delete_review(){
        movieDetailRepository.deleteReview();
    }
    public void post_review(int id, String user_id, String rating, String review){movieDetailRepository.postReview(id, user_id, rating, review);}
    public  void searchFavorMovies(String user_id){
        movieDetailRepository.searchFavHisMovies(user_id);
    }
    public LiveData<List<DetailModel>> getFavorMovies(){
        return movieDetailRepository.getFavHisMovies();
    }
}
