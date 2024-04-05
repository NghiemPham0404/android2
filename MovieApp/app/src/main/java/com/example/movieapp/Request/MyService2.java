package com.example.movieapp.Request;

import com.example.movieapp.utils.Credentials;
import com.example.movieapp.utils.ManagerApi;
import com.example.movieapp.utils.MovieApi;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyService2 {
    private static Retrofit.Builder retrofitBuilder =  new Retrofit.Builder()
            .baseUrl(Credentials.BASE_MANAGE_URL)
            .addConverterFactory(GsonConverterFactory.create());
    private static Retrofit retrofit = retrofitBuilder.build();

    private static ManagerApi managerApi = retrofit.create(ManagerApi.class);

    public static ManagerApi getApi(){
        return managerApi;
    }
}
