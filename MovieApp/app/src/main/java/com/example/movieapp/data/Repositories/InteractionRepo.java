package com.example.movieapp.data.Repositories;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.movieapp.Request.BackendService;
import com.example.movieapp.data.Model.DTO.InteractionDTO.*;
import com.example.movieapp.data.Response.ListResponse;
import com.example.movieapp.utils.BackendAPI;

import java.util.Objects;

import lombok.Getter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InteractionRepo {
    private final BackendAPI backendAPI;

    private static InteractionRepo instance;

    /**
     * MutableLiveData that contains the in progress playing movies (history movie list of user)
     * */
    @Getter
    private MutableLiveData<ListResponse<InteractionDAOExtended>> historyMovies = new MutableLiveData<>();

    /**
     * MutableLiveData that contains the favorite movies of user
     * */
    @Getter
    private MutableLiveData<ListResponse<InteractionDAOExtended>> favoriteMovies = new MutableLiveData<>();

    public InteractionRepo getInstance(Context context) {
        if(instance == null){
            instance = new InteractionRepo(context);
        }
        return instance;
    }
    public InteractionRepo(Context context){
        backendAPI = BackendService.getAuthBackendRetrofit(context).create(BackendAPI.class);
    }

    public void requestGetHistoryMovies(int page){
        Call<ListResponse<InteractionDAOExtended>> historyMoviesCall = backendAPI.getPlayingProgressMovies(page);
        historyMoviesCall.enqueue(new Callback<ListResponse<InteractionDAOExtended>>() {
            @Override
            public void onResponse(Call<ListResponse<InteractionDAOExtended>> call, Response<ListResponse<InteractionDAOExtended>> response) {
                if(response.isSuccessful()){
//                    if(historyMovies.getValue() == null){
                        historyMovies.postValue(response.body());
                        Log.i("GET HISTORY MOVIES LIST", "Success : "+response.body().getData().size());
//                    }else{
//                        historyMovies.getValue().getData().addAll(response.body().getData());
//                        historyMovies.postValue(historyMovies.getValue());
//                    }

                }else{
                    Log.e("GET HISTORY MOVIES LIST", Objects.requireNonNull(response.errorBody().toString()));
                }
            }

            @Override
            public void onFailure(Call<ListResponse<InteractionDAOExtended>> call, Throwable t) {
                Log.e("GET HISTORY MOVIES LIST", Objects.requireNonNull(t.getMessage()));
            }
        });
    }
    public void requestGetHistoryMoviesNext(){
        if(historyMovies.getValue() == null) requestGetHistoryMovies(1);
        else{
            int page = historyMovies.getValue().getPage() + 1;
            requestGetHistoryMovies(page);
        }
    }

    public void requestGetFavoriteMovies(int page){
        Call<ListResponse<InteractionDAOExtended>> favoriteMoviesCall = backendAPI.getFavorMovies(page);
        favoriteMoviesCall.enqueue(new Callback<ListResponse<InteractionDAOExtended>>() {
            @Override
            public void onResponse(Call<ListResponse<InteractionDAOExtended>> call, Response<ListResponse<InteractionDAOExtended>> response) {
                if(response.isSuccessful()){
                    favoriteMovies.postValue(response.body());
                }else{
                    Log.e("GET FAVORITE MOVIES LIST", Objects.requireNonNull(response.errorBody().toString()));
                }
            }

            @Override
            public void onFailure(Call<ListResponse<InteractionDAOExtended>> call, Throwable t) {
                Log.e("GET FAVORITE MOVIES LIST", Objects.requireNonNull(t.getMessage()));
            }
        });
    }

    public void requestGetFavoriteMoviesNext() {
        if (favoriteMovies.getValue() == null) requestGetFavoriteMovies(1);
        else {
            int page = favoriteMovies.getValue().getPage() + 1;
            requestGetFavoriteMovies(page);
        }
    }
}
