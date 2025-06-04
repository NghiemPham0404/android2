package com.example.movieapp.data.Repositories;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.example.movieapp.Request.BackendService;
import com.example.movieapp.data.Model.AccountModel;
import com.example.movieapp.data.Model.auth.AuthDTO.*;
import com.example.movieapp.data.Model.auth.AuthResponse;
import com.example.movieapp.utils.BackendAPI;

import lombok.Getter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepo {
    private static AuthRepo instance;

    private static BackendAPI backendAPI;

    Context context;
    @Getter
    private MutableLiveData<AuthResponse> authResponseMutable;

    public AuthRepo(Context context){
        backendAPI = BackendService.getAuthAPI(context);
        authResponseMutable = new MutableLiveData<>();
        this.context = context;
    }

    public static AuthRepo getInstance(Context context){
        if(instance == null) {
            instance = new AuthRepo(context);
        }
        return instance;
    }

    public void loginWithEmail(LoginModel loginModel){
        Call<AuthResponse> authResponseCall = backendAPI.loginWithAccount(loginModel);
        authResponseCall.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if(response.isSuccessful()){
                    authResponseMutable.postValue(response.body());
                }else{
                    Log.e("LOGIN EMAIL TASK", "Error:" + response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                Log.e("LOGIN EMAIL TASK", "Error:" + t.getMessage());
            }
        });
    }

    public void signUpWithEmail(AccountCreate accountCreate){
        Call<AuthResponse> authResponseCall = backendAPI.signUpWithInfo(accountCreate);
        authResponseCall.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if(response.isSuccessful()){
                    authResponseMutable.postValue(response.body());
                }else{
                    Log.e("LOGIN EMAIL TASK", "Error:" + response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                Log.e("LOGIN EMAIL TASK", "Error:" + t.getMessage());
            }
        });
    }

    public void loginWithGoogle(AccountCreateGoogle accountCreateGoogle){
        String googleId = accountCreateGoogle.getGoogle_id();
        LoginModelGoogle loginModelGoogle = new LoginModelGoogle(googleId);
        Call<AuthResponse> authResponseCall = backendAPI.loginWithGoogle(loginModelGoogle);
        authResponseCall.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if(response.isSuccessful()){
                    authResponseMutable.postValue(response.body());
                }else if(response.code() == 404){
                    signUpWithGoogle(accountCreateGoogle);
                    Toast.makeText(context, "Sign up", Toast.LENGTH_SHORT).show();
                }
                else{
                    Log.e("LOGIN GOOGLE TASK", "Error:" + response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                Log.e("LOGIN GOOGLE TASK", "Error:" + t.getMessage());
            }
        });
    }

    public void signUpWithGoogle(AccountCreateGoogle accountCreateGoogle){
        Call<AuthResponse> authResponseCall = backendAPI.signupWithGoogle(accountCreateGoogle);
        authResponseCall.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if(response.isSuccessful()){
                    authResponseMutable.postValue(response.body());
                }else{
                    Log.e("LOGIN GOOGLE TASK", "Error:" + response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                Log.e("LOGIN GOOGLE TASK", "Error:" + t.getMessage());
            }
        });
    }

    public void loginWithFacebook(AccountCreateFacebook accountCreateFacebook){
        LoginModelFacebook loginModelFacebook = new LoginModelFacebook(accountCreateFacebook.getFacebook_id());
        Call<AuthResponse> authResponseCall = backendAPI.loginWithFacebook(loginModelFacebook);
        authResponseCall.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if(response.isSuccessful()){
                    authResponseMutable.postValue(response.body());
                }else if(response.code() == 404){
                    signUpWithFacebook(accountCreateFacebook);
                }
                else{
                    Log.e("LOGIN FACEBOOK TASK", "Error:" + response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                Log.e("LOGIN FACEBOOK TASK", "Error:" + t.getMessage());
            }
        });
    }

    public void signUpWithFacebook(AccountCreateFacebook accountCreateFacebook){
        Call<AuthResponse> authResponseCall = backendAPI.signUpWithFacebook(accountCreateFacebook);
        authResponseCall.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if(response.isSuccessful()){
                    authResponseMutable.postValue(response.body());
                }else{
                    Log.e("LOGIN FACEBOOK TASK", "Error:" + response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                Log.e("LOGIN FACEBOOK TASK", "Error:" + t.getMessage());
            }
        });
    }
    public void logOut(){
        authResponseMutable.postValue(null);
    }
}
