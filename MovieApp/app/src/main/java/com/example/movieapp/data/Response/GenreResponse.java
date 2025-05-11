package com.example.movieapp.data.Response;

import com.example.movieapp.data.Model.MovieModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GenreResponse {

    @SerializedName("genres")
    @Expose
    List<MovieModel.Genre> genres;

    public  List<MovieModel.Genre> getGenres(){
        return genres;
    }
}
