package com.example.movieapp.ViewModel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.movieapp.data.Model.DTO.InteractionDTO.*;
import com.example.movieapp.data.Model.DetailModel;
import com.example.movieapp.data.Model.MovieModel;
import com.example.movieapp.data.Repositories.MovieRepo;
import com.example.movieapp.data.Response.ListResponse;

import java.util.List;

public class MovieViewModel extends AndroidViewModel {
    private MovieRepo movieRepo;

    public MovieViewModel(Application application){
        super(application);
        movieRepo = MovieRepo.getInstance(application.getApplicationContext());
    }

    public void getMovie(int id){
        movieRepo.requestGetMovie(id);
    }

    public LiveData<MovieModel> getMovie(){
        return movieRepo.getMovie();
    }
    public void getMovieInteration(int id){
       movieRepo.requestGetInteraction(id);
    }
    public LiveData<InteractionDAOExtended> getDetailMovie(){
        return movieRepo.getInteraction();
    }
    public void changeFavor(int id){
        movieRepo.requestAddToFavoriteMovies(id);
    }
    public void getReviews(int id){
        movieRepo.requestGetMovieReviews(id);
    }
    public LiveData<ListResponse<InteractionDAOReview>> getMovieReviews(){
       return movieRepo.getReviews();
    }
    public void delete_review(int id){
        movieRepo.requestDeleteMovieReview(id);
    }
    public void post_review(int id, float rating, String review){
        InteractionDAOReviewAdd interactionDAOReviewAdd = new InteractionDAOReviewAdd();
        interactionDAOReviewAdd.setRating(rating);
        interactionDAOReviewAdd.setReview(review);
    }

    public void postPlayingProgress(int it, String playingProgress){
        movieRepo.requestAddPlayingProgressMovies(it, playingProgress);
    }

//    public  void searchFavorMovies(String user_id){
//        movieDetailRepository.searchFavHisMovies(user_id);
//    }
//    public void searchHistoryMovies(String user_id){
//        movieDetailRepository.searchHistoryMovies(user_id);
//    }
//    public LiveData<List<DetailModel>> getFavorMovies(){
//        return movieDetailRepository.getFavHisMovies();
//    }
//    public LiveData<List<DetailModel>> getHistoryMovies(){
//        return movieDetailRepository.getHistoryMovies();
//    }
}
