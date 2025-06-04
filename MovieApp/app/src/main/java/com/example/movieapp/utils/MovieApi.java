package com.example.movieapp.utils;

import com.example.movieapp.data.Model.PersonModel;
import com.example.movieapp.data.Model.CountryModel;
import com.example.movieapp.data.Model.MovieModel;
import com.example.movieapp.data.Response.CastResponse;
import com.example.movieapp.data.Response.GenreResponse;
import com.example.movieapp.data.Response.MovieDetailListResponse;
import com.example.movieapp.data.Response.MovieListResponse;
import com.example.movieapp.data.Response.PeopleResponse;
import com.example.movieapp.data.Response.VideoResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface MovieApi {


    //TODO : TÌM KIẾM PHIM
    @GET("3/search/movie")
    Call<MovieListResponse> searchMovie(
        @Query("query") String query,
        @Query("page") int page,
        @Query("language") String language
        );


    @GET
    Call<MovieListResponse> searchMoviesList(
            @Url()  String url,
            @Query("page") int page,
            @Query("language") String language
    );

    //TODO : TÌM KIẾM NGƯỜI THAM GIA PHIM
    @GET("3/movie/{movie_id}/credits")
    Call<CastResponse> searchCastByFilmID(
            @Path("movie_id") int movie_id
    );

    @GET("3/person/{person_id}") // get person information
    Call<PersonModel> searchPersonByID(
            @Path("person_id") int person_id,
            @Query("append_to_response") String append_to_response,
            @Query("language") String language
    );


    @GET("3/search/person")
    Call<PeopleResponse> searchPerson(
            @Query("query") String query,
            @Query("page") int page,
            @Query("language") String language
    );

    @GET("3/movie/{movie_id}/videos")
    Call<VideoResponse> searchVideoByFilmID(
            @Path("movie_id") int movie_id
    );

    @GET("3/movie/{movie_id}")
    Call<MovieModel> searchMovieDetail(
           @Path("movie_id") int movie_id
    );
    @GET("3/movie/{movie_id}")
    Call<MovieModel> searchMovieDetail(
            @Path("movie_id") int movie_id,
            @Query("append_to_response") String append_to_response,
            @Query("language") String language
    );

    @GET("3/movie/{movie_id}")
    Call<MovieDetailListResponse> searchMovieDetailList(
            @Path("list_id") int movie_id,
            @Query("append_to_response") String append_to_response,
            @Query("language") String language
    );

    @GET("3/movie/{movie_id}/recommendations")
    Call<MovieListResponse> searchMovieRelativeRecommendation(
            @Path("movie_id") int movie_id
    );

    @GET("3/discover/movie")
    Call<MovieListResponse> searchMovieRelativeRecommendationByGernes(
            @Query("api_key") String key,
            @Query("with_genres") String with_genres
    );

    // DISCOVER MOVIE

    //https://api.themoviedb.org/3/genre/movie/list
    @GET("3/genre/movie/list")
    Call<GenreResponse> getAllGenre(
            @Query("language") String language
    );

    @GET("3/configuration/countries")
    Call<List<CountryModel>> getAllRegion(
            @Query("language") String language
    );

    @GET("3/discover/movie")
    Call<MovieListResponse> discoverMovie(
            @Query("with_genres") String with_genres,
            @Query("with_origin_country") String with_origin_country,
            @Query("year") int year,
            @Query("page") int page,
            @Query("language") String language
    );

    @GET("3/movie/now_playing")
    Call<MovieListResponse> getNowPlayingMovie(
            @Query("page") int page
    );
    @GET("3/movie/popular")
    Call<MovieListResponse> getPopularMovie(
            @Query("page") int page
    );
    @GET("3/movie/upcoming")
    Call<MovieListResponse> getUpComingMovie(
            @Query("page") int page
    );
}
