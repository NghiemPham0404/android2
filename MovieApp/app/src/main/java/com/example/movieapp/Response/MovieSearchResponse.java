package com.example.movieapp.Response;

import androidx.annotation.NonNull;

import com.example.movieapp.Model.MovieModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

// Lớp này xử lý kết quả trả về của 1 dãy movie
public class MovieSearchResponse {
    @SerializedName("total_results")
    @Expose
    public int total_results;

    @SerializedName("results")
    @Expose
    public List<MovieModel> movies;

    public int getTotalCount(){
        return total_results;
    }

    public List<MovieModel> getMovies(){
        return movies;
    }

    @NonNull
    @Override
    public String toString() {
        return "MovieSearchResponse={"
                +"total_count="+total_results
                +", movies = "+movies
                +"}";
    }
}
