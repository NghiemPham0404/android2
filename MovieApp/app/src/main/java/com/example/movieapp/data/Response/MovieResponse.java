package com.example.movieapp.data.Response;
import com.example.movieapp.data.Model.MovieModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

// class này xử lý kết quả trả về của 1 movie
public class MovieResponse {
    // tìm thẻ result khi truy vấn và trả về một movie
    @SerializedName("results")
    @Expose
    private MovieModel movieModel;

    public MovieModel getMovie(){
        return movieModel;
    }

    @Override
    public String toString(){
        return "MovieResponse={" +"movie="+movieModel + "}";
    }
}
