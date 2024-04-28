package com.example.movieapp.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.movieapp.Model.MovieModel;
import com.example.movieapp.Repositories.MovieDetailRepository;

public class MovieViewModel extends ViewModel {
    private MovieDetailRepository movieDetailRepository;

    public MovieViewModel(){
        movieDetailRepository = MovieDetailRepository.getInstance();
    }

    public void searchMovieDetail(int id){
        movieDetailRepository.searchMovie(id);
    }

    public LiveData<MovieModel> getMovie(){
        return movieDetailRepository.getMovie();
    }
}
