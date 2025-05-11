package com.example.movieapp.data.Repositories;

import android.content.Context;
import android.util.Log;


import androidx.lifecycle.MutableLiveData;

import com.example.movieapp.Request.BackendService;
import com.example.movieapp.Request.MyService;
import com.example.movieapp.data.Model.DTO.InteractionDTO.*;
import com.example.movieapp.data.Model.MovieModel;
import com.example.movieapp.data.Response.ListResponse;
import com.example.movieapp.utils.BackendAPI;
import com.example.movieapp.utils.Credentials;

import java.util.Locale;
import java.util.Objects;

import lombok.Getter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieRepo{

    private static MovieRepo instance;
    @Getter
    private final MutableLiveData<MovieModel> movie;
    @Getter
    private final MutableLiveData<InteractionDAOExtended> interaction;
    @Getter
    private final MutableLiveData<InteractionDAOReview> userReview;
    @Getter
    private final MutableLiveData<ListResponse<InteractionDAOReview>> reviews;

    private final BackendAPI backendAPI;

    public static MovieRepo getInstance(Context context){
        if(instance == null){
            instance = new MovieRepo(context);
        }
        return instance;
    }

    public MovieRepo(Context context){
        backendAPI = BackendService.getAuthBackendRetrofit(context).create(BackendAPI.class);
        movie = new MutableLiveData<>();
        interaction = new MutableLiveData<>();
        userReview = new MutableLiveData<>();
        reviews = new MutableLiveData<>();
    }

    public void requestGetMovie(int id){
        Call<MovieModel> movieModelCall = MyService.getMovieApi()
                .searchMovieDetail(id,
                        Credentials.API_KEY,
                        "credits,videos,external_ids",
                        Locale.getDefault().getLanguage());
        movieModelCall.enqueue(new Callback<MovieModel>() {
            @Override
            public void onResponse(Call<MovieModel> call, Response<MovieModel> response) {
                if(response.isSuccessful()){
                    movie.postValue(response.body());
                    Log.e("GET MOVIE", "SUCCESS"+response.body().getId());
                }else{
                    Log.e("GET MOVIE", Objects.requireNonNull(response.errorBody().toString()));
                }
            }

            @Override
            public void onFailure(Call<MovieModel> call, Throwable t) {
                Log.e("GET MOVIE", Objects.requireNonNull(t.getMessage()));
            }
        });
    }

    public void requestGetInteraction(int id){
        Call<InteractionDAOExtended> movieInteractionCall = backendAPI.getMovieInteration(id);
        movieInteractionCall.enqueue(new Callback<InteractionDAOExtended>() {
            @Override
            public void onResponse(Call<InteractionDAOExtended> call,
                                   Response<InteractionDAOExtended> response) {
                if(response.isSuccessful()){
                    interaction.postValue(response.body());
                    Log.i("GET MOVIE INTERACTION", response.body().isFavor()+"");
                }else{
                    Log.e("GET MOVIE INTERACTION 1",
                            Objects.requireNonNull(response.errorBody().toString()));
                    interaction.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<InteractionDAOExtended> call, Throwable t) {
                Log.e("GET MOVIE INTERACTION 2", Objects.requireNonNull(t.getMessage()));
            }
        });
    }

    public void requestAddToFavoriteMovies(int id){
        Call<InteractionDAOExtended> movieInterationCall = backendAPI.addToFavoriteMovies(id);
        movieInterationCall.enqueue(new Callback<InteractionDAOExtended>() {
            @Override
            public void onResponse(Call<InteractionDAOExtended> call,
                                   Response<InteractionDAOExtended> response) {
                if(response.isSuccessful()){
                    interaction.postValue(response.body());
                }else{
                    Log.e("ADD TO FAVOR",
                            Objects.requireNonNull(response.errorBody().toString()));
                }
            }

            @Override
            public void onFailure(Call<InteractionDAOExtended> call, Throwable t) {
                Log.e("ADD TO FAVOR", Objects.requireNonNull(t.getMessage()));
            }
        });
    }
    public void requestAddMovieReview(int id, InteractionDAOReviewAdd interactionDAOReviewAdd){
        Call<InteractionDAOExtended> movieReviewCall =
                backendAPI.addMovieReview(id, interactionDAOReviewAdd);
        movieReviewCall.enqueue(new Callback<InteractionDAOExtended>() {
            @Override
            public void onResponse(Call<InteractionDAOExtended> call,
                                   Response<InteractionDAOExtended> response) {
                if(response.isSuccessful()){
                    interaction.postValue(response.body());
                }else{
                    Log.e("ADD MOVIE REVIEW",
                            Objects.requireNonNull(response.errorBody().toString()));
                }
            }

            @Override
            public void onFailure(Call<InteractionDAOExtended> call, Throwable t) {
                Log.e("ADD MOVIE REVIEW", Objects.requireNonNull(t.getMessage()));
            }
        });
    }
    public void requestDeleteMovieReview(int id){
        Call<String> movieReviewCall = backendAPI.deleteReview(id);
        movieReviewCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.isSuccessful()){
                    Log.i("DELETE MOVIE REVIEW", "success");
                }else{
                    Log.e("DELETE MOVIE REVIEW",
                            Objects.requireNonNull(response.errorBody().toString()));
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("DELETE MOVIE REVIEW", Objects.requireNonNull(t.getMessage()));
            }
        });
    }
    public void requestGetMovieReviews(int id){
        Call<ListResponse<InteractionDAOReview>> reviewsCall = backendAPI.getMovieReviews(id);
        reviewsCall.enqueue(new Callback<ListResponse<InteractionDAOReview>>() {
            @Override
            public void onResponse(Call<ListResponse<InteractionDAOReview>> call,
                                   Response<ListResponse<InteractionDAOReview>> response) {
                if (response.isSuccessful()) {
                    reviews.postValue(response.body());
                }else{
                    Log.e("GET MOVIE REVIEWS",
                            Objects.requireNonNull(response.errorBody().toString()));
                }
            }

            @Override
            public void onFailure(Call<ListResponse<InteractionDAOReview>> call, Throwable t) {
                Log.e("GET MOVIE REVIEWS", Objects.requireNonNull(t.getMessage()));
            }
        });
    }

    public void requestAddPlayingProgressMovies(int id, String play_progress){
        Call<InteractionDAOExtended> movieReviewCall =
                backendAPI.addPlayingProgressMovies(id, play_progress);
        movieReviewCall.enqueue(new Callback<InteractionDAOExtended>() {
            @Override
            public void onResponse(Call<InteractionDAOExtended> call,
                                   Response<InteractionDAOExtended> response) {
                if(response.isSuccessful()){
                    interaction.postValue(response.body());
                }else{
                    Log.e("ADD MOVIE PLAY-PROGRESS",
                            Objects.requireNonNull(response.errorBody().toString()));
                }
            }

            @Override
            public void onFailure(Call<InteractionDAOExtended> call, Throwable t) {
                Log.e("ADD MOVIE PLAY-PROGRESS", Objects.requireNonNull(t.getMessage()));
            }
        });
    }

}
