package com.example.movieapp.Request;

import com.example.movieapp.utils.ApiAvatarService;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyAvatarService {
    private static final String BASE_URL = "https://api.imgbb.com/";
    public static final String key = "4b43b1654bc435a599ba30d5c6d5b4f2";

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
