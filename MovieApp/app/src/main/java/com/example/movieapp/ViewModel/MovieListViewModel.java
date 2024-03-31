package com.example.movieapp.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.movieapp.Model.MovieModel;
import com.example.movieapp.Repositories.MovieRepository;

import java.util.List;

public class MovieListViewModel extends ViewModel {

    // this class is used for Movie List with ViewModel
    // View Model làm cho app không bị rối loạn dữ liệu khi tiến hành công việc như đổi phong chữ, xoay màn hình

    private MovieRepository movieRepository;
    // Constructor
    public MovieListViewModel() {
        movieRepository = MovieRepository.getInstance();
    }

    public LiveData<List<MovieModel>> getMovies(){
        return movieRepository.getMovies();
    }

    public void searchMovieApi(String query, int pageNumber){
        movieRepository.searchMovieApi(query, pageNumber);
    }

    public void searchMovieApi(int list_type, int pageNumber){
        movieRepository.searchMovieApi(list_type, pageNumber);
    }
}
