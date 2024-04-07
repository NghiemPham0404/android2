package com.example.movieapp.utils;

import com.example.movieapp.Model.DetailModel;
import com.example.movieapp.Model.LoginModel;
import com.example.movieapp.Model.VideoModel;
import com.example.movieapp.Response.DetailResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ManagerApi {

    // login with email and password
    @POST(Credentials.login_url)
    Call<LoginModel> loginWithAccount(
      @Query("email") String email,
      @Query("password") String password
    );

    @POST(Credentials.login_url)
    Call<LoginModel> loginWithGoogle(
            @Query("google_id") String google_id
    );

    // signup with email, password, username
    @POST(Credentials.signup_url)
    Call<LoginModel> signUpWithInfo(
            @Query("email") String email,
            @Query("password") String password,
            @Query("username") String username
    );

    // get film video from id
    @POST(Credentials.video_url)
    Call<VideoModel> getMovieVideo(
            @Query("functionname") String functionname,
            @Query("movieId") int movieId
    );

    // Add to favor
    @POST(Credentials.favor_url)
    Call<DetailModel> addToFavor(
            @Query("functionname") String functionname,
            @Query("userId") String userId,
            @Query("movieId") int movieId
    );

    @POST(Credentials.favor_url)
    Call<List<DetailModel>> getFavorListByUserId(
            @Query("functionname") String functionname,
            @Query("userId") String userId
    );

    //Add to Review
    @POST(Credentials.favor_url)
    Call<DetailModel> addReview(
            @Query("functionname") String functionname,
            @Query("userId") String userId,
            @Query("movieId") int movieId,
            @Query("rating") float rating,
            @Query("review") String review
    );

    @POST(Credentials.favor_url)
    Call<DetailResponse>getReviewByFilmId(
            @Query("functionname") String functionname,
            @Query("movieId") int movieId
    );

    // set duration
    @POST(Credentials.favor_url)
    Call<DetailModel> setDuration(
            @Query("functionname") String functionname,
            @Query("userId") String userId,
            @Query("movieId") int movieId,
            @Query("duration") String current_duration
    );
}
