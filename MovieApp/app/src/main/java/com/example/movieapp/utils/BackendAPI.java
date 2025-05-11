package com.example.movieapp.utils;

import com.example.movieapp.data.Model.auth.AuthDTO.*;
import com.example.movieapp.data.Model.auth.AuthResponse;
import com.example.movieapp.data.Model.DTO.InteractionDTO.*;
import com.example.movieapp.data.Model.user.UserDTO.*;
import com.example.movieapp.data.Response.ListResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface BackendAPI {
    // TODO : login with email and password
    @POST("/auth/login")
    Call<AuthResponse> loginWithAccount(
            @Body LoginModel loginModel
    );

    // signup with email, password, username
    @POST("/auth/signup")
    Call<AuthResponse> signUpWithInfo(
            @Body AccountCreate accountCreate
    );

    // TODO : login with google
    @POST("/auth/login-google")
    Call<AuthResponse> loginWithGoogle(
            @Body LoginModelGoogle loginModelGoogle
    );

    // TODO : signup with google
    @POST("/auth/signup-google")
    Call<AuthResponse> signupWithGoogle(
            @Body AccountCreateGoogle accountCreateGoogle
    );

    // TODO : login with facebook
    @POST("/auth/login-facebook")
    Call<AuthResponse> loginWithFacebook(
            @Body LoginModelFacebook loginModelFacebook
    );

    // TODO : sign up with facebook
    @POST("/auth/signup-facebook")
    Call<AuthResponse> signUpWithFacebook(
            @Body AccountCreateFacebook accountCreateFacebook
    );

    // TODO : get current user information
    @GET("/auth/info")
    Call<UserInfo> getCurrentUser();

    // TODO : update user infomation
    @PUT("/api/users/{id}")
    Call<UserInfo> updateUser(
            @Path("id")int id,
            @Body UserUpdate userUpdate
    );

    // TODO : get movie from id
    @GET("/api/movies/{id}")
    Call<InteractionDAOExtended> getMovieInteration(
            @Path("id") int id
    );

    // TODO : add movie to favorite list
    @POST("/api/movies/{id}/favorite-movies")
    Call<InteractionDAOExtended> addToFavoriteMovies(
            @Path("id") int id
    );

    // TODO : get favorites movie of user
    @GET("api/movies/favorite-movies")
    Call<ListResponse<InteractionDAOExtended>> getFavorMovies(
           @Query("page") int page
    );

    // TODO : add review to a movie
    @POST("api/movies/{id}/reviews")
    Call<InteractionDAOExtended> addMovieReview(
            @Path("id") int id,
            @Body InteractionDAOReviewAdd interactionDAOReviewAdd
    );

    // TODO : get reviews of a movie
    @GET("api/movies/{id}/reviews")
    Call<ListResponse<InteractionDAOReview>>getMovieReviews(
            @Path("id") int id
    );

    // TODO : delete review of a movie
    @DELETE("/api/movies/{id}/reviews")
    Call<String> deleteReview(
            @Path("id") int id
    );

    // TODO : set play-progress of a movie
    @POST("/api/movies/{id}/play-progress-movies")
    Call<InteractionDAOExtended> addPlayingProgressMovies(
            @Path("id") int id,
            @Query("play_progress") String play_progress
    );

    // TODO : get in-play-progress movies of a user
    @GET("/api/movies/play-progress-movies")
    Call<ListResponse<InteractionDAOExtended>> getPlayingProgressMovies(
            @Query("page") int page
    );
}
