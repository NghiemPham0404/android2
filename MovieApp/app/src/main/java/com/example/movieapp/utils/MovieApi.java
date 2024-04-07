package com.example.movieapp.utils;

import com.example.movieapp.Model.CastModel;
import com.example.movieapp.Model.ExternalLinkModel;
import com.example.movieapp.Model.MovieModel;
import com.example.movieapp.Response.CastResponse;
import com.example.movieapp.Response.CreditResponse;
import com.example.movieapp.Response.MovieResponse;
import com.example.movieapp.Response.MovieSearchResponse;
import com.example.movieapp.Response.VideoResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface MovieApi {


    //TODO : TÌM KIẾM PHIM
    //normal searching link : https://api.themoviedb.org/3/search/movie?query= + {movie_name} + &api_key=cf32372af846ed46863011b283bdcba1
    @GET(Credentials.SEARCH_MOVIE_URL)
    Call<MovieSearchResponse> searchMovie(
        @Query("api_key") String key,
        @Query("query") String query,
        @Query("page") int page
        );


    // now_playing : https://api.themoviedb.org/3/movie/now_playing?api_key=cf32372af846ed46863011b283bdcba1
    @GET
    Call<MovieSearchResponse> searchMoviesList(
            @Url()  String url,
            @Query("api_key") String key,
            @Query("page") int page
    );

    @GET
    Call<MovieSearchResponse> searchMoviesList(
            @Url()  String url,
            @Query("api_key") String key,
            @Query("language") String language
    );

    //https://api.themoviedb.org/3/genre/movie/list
    //https://api.themoviedb.org/3/discover/movie


    //TODO : TÌM KIẾM NGƯỜI THAM GIA PHIM
    //https://api.themoviedb.org/3/movie/299536/credits?api_key=cf32372af846ed46863011b283bdcba1
    @GET("3/movie/{movie_id}/credits")
    Call<CastResponse> searchCastByFilmID(
            @Path("movie_id") int movie_id,
            @Query("api_key") String key
    );

    @GET("3/person/{person_id}") // get person infomation
    Call<CastModel> searchPersonByID(
            @Path("person_id") int person_id,
            @Query("api_key") String key
    );

    // Person Credits by id
    @GET("3/person/{person_id}/movie_credits")
    Call<CreditResponse> searchPersonCreditByID(
            @Path("person_id") int person_id,
            @Query("api_key") String key
    );

    // External Link by id
    @GET("3/person/{person_id}/external_ids")
    Call<ExternalLinkModel> searchPersonExternalIdByID(
            @Path("person_id") int person_id,
            @Query("api_key") String key
    );

    //https://api.themoviedb.org/3/search/person
    @GET
    Call<CastResponse> searchPerson(
            @Query("query") String query,
            @Query("api_key") String key,
            @Query("page") int page
    );

    // EXTERNAL LINK : https://api.themoviedb.org/3/person/1172108/external_ids?api_key=cf32372af846ed46863011b283bdcba1



    //https://api.themoviedb.org/3/movie/6/videos?api_key=cf32372af846ed46863011b283bdcba1
    @GET("3/movie/{movie_id}/videos")
    Call<VideoResponse> searchVideoByFilmID(
            @Path("movie_id") int movie_id,
            @Query("api_key") String key
    );

    @GET("3/movie/{movie_id}")
    Call<MovieModel> searchMovieDetail(
           @Path("movie_id") int movie_id,
            @Query("api_key") String key
    );
    @GET("3/movie/{movie_id}")
    Call<MovieModel> searchMovieDetail(
            @Path("movie_id") int movie_id,
            @Query("api_key") String key,
            @Query("append_to_response") String append_to_ressponse
    );

    @GET("3/movie/{movie_id}/recommendations")
    Call<MovieSearchResponse> searchMovieRelativeRecommendation(
            @Path("movie_id") int movie_id,
            @Query("api_key") String key
    );
}
