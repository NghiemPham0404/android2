package com.example.movieapp.data.Response;

import com.example.movieapp.data.Model.MovieModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.Getter;

@Getter
public class MovieDetailListResponse {
    @SerializedName("item")
    @Expose
    public List<MovieModel> movies;
}
