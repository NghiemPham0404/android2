package com.example.movieapp.data.Repositories;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.movieapp.Request.BackendService;
import com.example.movieapp.data.Model.user.UserDTO.*;
import com.example.movieapp.utils.BackendAPI;

import java.util.Objects;

import lombok.Getter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class UserRepo {
    private static UserRepo instance;

    @Getter
    private final MutableLiveData<UserInfo> currentUser;

    private static BackendAPI authorizedBackendAPI;

    public static UserRepo getInstance(Context context){
        if(instance == null){
            instance = new UserRepo(context);
        }
        return instance;
    }
    public UserRepo(Context context){
        currentUser = new MutableLiveData<>();
        authorizedBackendAPI = new BackendService().getAuthorizedBackendAPI(context);
    }

    public void requestCurrentUser(){
        Call<UserInfo> accountModelCall = authorizedBackendAPI.getCurrentUser();
        accountModelCall.enqueue(new Callback<UserInfo>() {
            @Override
            public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {
                if(response.isSuccessful()){
                    currentUser.postValue(response.body());
                }else{
                    Log.e("GET CURRENT USER ERROR :", Objects.requireNonNull(response.errorBody()).toString());
                }
            }

            @Override
            public void onFailure(Call<UserInfo> call, Throwable t) {
                Log.e("GET CURRENT USER ERROR :", Objects.requireNonNull(t.getMessage()));
            }
        });
    }

    public void requestUpdateUser(UserUpdate userUpdate){
        Call<UserInfo> accountModelCall = authorizedBackendAPI.updateUser(getCurrentUser().getValue().getUser_id(),userUpdate);
        accountModelCall.enqueue(new Callback<UserInfo>() {
            @Override
            public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {
                if(response.isSuccessful()){
                    currentUser.postValue(response.body());
                }else{
                    Log.e("UPDATE CURRENT USER ERROR :", Objects.requireNonNull(response.errorBody()).toString());
                }
            }

            @Override
            public void onFailure(Call<UserInfo> call, Throwable t) {
                Log.e("UPDATE CURRENT USER ERROR :", Objects.requireNonNull(t.getMessage()));
            }
        });
    }

    public void logOut() {
        currentUser.postValue(null);
    }
}
