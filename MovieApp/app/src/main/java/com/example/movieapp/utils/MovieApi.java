package com.example.movieapp.utils;

import com.example.movieapp.Response.CastResponse;
import com.example.movieapp.Response.MovieSearchResponse;
import com.example.movieapp.Response.VideoResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface MovieApi {


    //tìm kiếm phim
    //normal searching link : https://api.themoviedb.org/3/search/movie?query= + {movie_name} + &api_key=cf32372af846ed46863011b283bdcba1
    @GET
    Call<MovieSearchResponse> searchMovie(
            @Url() String url,
        @Query("api_key") String key,
        @Query("query") String query,
        @Query("page") String page
        );


    // now_playing : https://api.themoviedb.org/3/movie/now_playing?api_key=cf32372af846ed46863011b283bdcba1
    @GET
    Call<MovieSearchResponse> searchMoviesList(
            @Url()  String url,
            @Query("api_key") String key
    );

    @GET
    Call<MovieSearchResponse> searchMoviesList(
            @Url()  String url,
            @Query("api_key") String key,
            @Query("language") String language
    );


    //https://api.themoviedb.org/3/movie/299536/credits?api_key=cf32372af846ed46863011b283bdcba1
    @GET
    Call<CastResponse> searchCastByFilmID(
            @Url()  String url,
            @Query("api_key") String key
    );

    //https://api.themoviedb.org/3/movie/6/videos?api_key=cf32372af846ed46863011b283bdcba1
    @GET
    Call<VideoResponse> searchVideoByFilmID(
            @Url()  String url,
            @Query("api_key") String key
    );
}
