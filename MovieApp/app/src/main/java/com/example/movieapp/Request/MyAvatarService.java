package com.example.movieapp.Request;

import com.example.movieapp.utils.ApiAvatarService;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyAvatarService {
    private static final String BASE_URL = "";

    private static ApiAvatarService Api = null;

    public static ApiAvatarService getApi() {
        if (Api == null) {
            Api = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(ApiAvatarService.class);
        }
        return Api;
    }
}
