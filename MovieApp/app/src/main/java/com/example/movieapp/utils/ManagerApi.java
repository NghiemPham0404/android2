package com.example.movieapp.utils;

import com.example.movieapp.Model.AccountModel;
import com.example.movieapp.Model.DetailModel;
import com.example.movieapp.Model.LoginModel;
import com.example.movieapp.Model.VideoModel;
import com.example.movieapp.Response.DetailResponse;

import java.io.File;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ManagerApi {

    // login with email and password
    @POST(Credentials.manage_url)
    Call<LoginModel> loginWithAccount(
            @Query("functionname") String functionname,
      @Query("email") String email,
      @Query("password") String password
    );

    @POST(Credentials.manage_url)
    Call<LoginModel> loginWithGoogle(
            @Query("functionname") String functionname,
            @Query("google_id") String google_id
    );

    @POST(Credentials.manage_url)
    Call<LoginModel> regisWithGoogle(
            @Query("functionname") String functionname,
            @Query("google_id") String google_id,
            @Query("email") String email,
            @Query("username") String username,
            @Query("avatar") String avatar
    );

    @POST(Credentials.manage_url)
    Call<LoginModel> loginWithFacebook(
            @Query("functionname") String functionname,
            @Query("facebook_id") String facebook_id
    );

    @POST(Credentials.manage_url)
    Call<LoginModel> regisWithFacebook(
            @Query("functionname") String functionname,
            @Query("facebook_id") String facebook_id,
            @Query("username") String username,
            @Query("avatar") String avatar
    );

    // signup with email, password, username
    @POST(Credentials.manage_url)
    Call<LoginModel> signUpWithInfo(
            @Query("functionname") String functionname,
            @Query("email") String email,
            @Query("password") String password,
            @Query("username") String username
    );

    // get film video from id
    @POST(Credentials.manage_url)
    Call<DetailModel> getMovieVideo(
            @Query("functionname") String functionname,
            @Query("movieId") int movieId,
            @Query("userId") String userId
    );

    // Add to favor
    @POST(Credentials.manage_url)
    Call<DetailModel> addToFavor(
            @Query("functionname") String functionname,
            @Query("userId") String userId,
            @Query("movieId") int movieId
    );

    @POST(Credentials.manage_url)
    Call<List<DetailModel>> getFavorListByUserId(
            @Query("functionname") String functionname,
            @Query("userId") String userId
    );

    //Add to Review
    @POST(Credentials.manage_url)
    Call<DetailModel> addReview(
            @Query("functionname") String functionname,
            @Query("userId") String userId,
            @Query("movieId") int movieId,
            @Query("rating") String rating,
            @Query("review") String review
    );

    @POST(Credentials.manage_url)
    Call<DetailResponse>getReviewByFilmId(
            @Query("functionname") String functionname,
            @Query("movieId") int movieId
    );

    // set duration
    @POST(Credentials.manage_url)
    Call<DetailModel> postPlayBackDuration(
            @Query("functionname") String functionname,
            @Query("userId") String userId,
            @Query("movieId") int movieId,
            @Query("duration") String current_duration
    );

    // config user
    @POST(Credentials.manage_url)
    Call<AccountModel>configureUserInfo(
            @Query("functionname") String functionname,
            @Query("userId") String userId,
            @Query("username") String username,
            @Query("email") String email,
            @Query("password") String password,
            @Query("sms") String sms,
            @Query("google_id") String google_id,
            @Query("facebook_id") String facebook_id,
            @Query("avatar") String avatar
    );

    @POST(Credentials.manage_url)
    Call<DetailModel> deleteReview(
            @Query("functionname") String functionname,
            @Query("userId") String userId,
            @Query("movieId") int movieId);
}
