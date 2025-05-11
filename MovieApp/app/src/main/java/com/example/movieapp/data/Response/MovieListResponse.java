package com.example.movieapp.data.Response;

import androidx.annotation.NonNull;

import com.example.movieapp.data.Model.MovieModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Lớp này xử lý kết quả trả về của 1 dãy movie
@NoArgsConstructor
@Getter
public class MovieListResponse {
    @SerializedName("total_results")
    @Expose
    public int total_results;

    @SerializedName("results")
    @Expose
    public List<MovieModel> movies;

    @Setter
    @SerializedName("page")
    @Expose
    public int page;

    @SerializedName("total_pages")
    @Expose
    public int total_pages;


    @NonNull
    @Override
    public String toString() {
        return "MovieSearchResponse={"
                +"total_count="+total_results
                +", movies = "+movies
                +"}";
    }
}
