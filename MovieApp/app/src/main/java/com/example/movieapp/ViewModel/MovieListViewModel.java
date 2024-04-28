package com.example.movieapp.ViewModel;

import android.util.Log;

import androidx.lifecycle.LiveData;
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
    public LiveData<List<MovieModel>> getFavorMovies(){return movieRepository.getFavorMovies();}

    public LiveData<List<MovieModel>> getUpcommingMovies(){return movieRepository.getUpcommingMovies();}


    public int getTotalResults(){
        return movieRepository.getTotalResults();
    }

    public void searchMovieApi(String query, int pageNumber){
        movieRepository.searchMovieApi(query, pageNumber);
    }

    // Tìm kiếm tiếp theo
    public void searchMovieApiNextPage(){
        movieRepository.searchMovieApiNextPage();
    }

    public void searchFavorMovieApi(int pageNumber){
        Log.i("movielist","finding");
        movieRepository.searchMovieApi(2, pageNumber);
    }

    public void searchFavorMovieApiNextPage(){
        Log.i("movielist","finding");
        movieRepository.searchPopularMovieApiNextPage();
    }

    public void searchUpcommingMovieApi(int pageNumber){
        Log.i("movielist","finding");
        movieRepository.searchMovieApi(4, pageNumber);
    }

    public void searchUpcommingMovieApiNextPage(){
        Log.i("movielist","finding");
        movieRepository.searchUpcommingMovieApiNextPage();
    }

    public void discoverMovieApi(String genre_str, String country ,int year, int pageNumber){
        movieRepository.discoverMovieApi(genre_str, country, year, pageNumber);
    }
}
