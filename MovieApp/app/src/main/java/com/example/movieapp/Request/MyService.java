package com.example.movieapp.Request;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.movieapp.R;
import com.example.movieapp.utils.Credentials;
import com.example.movieapp.utils.MovieApi;

import lombok.Getter;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyService {
    private static Retrofit retrofit;

    public static Retrofit getAuthBackendRetrofit() {
        if (retrofit == null) {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(chain -> {
                        Request original = chain.request();
                        Request request = original.newBuilder()
                                .header("Authorization", "Bearer " + Credentials.TMDB_ACCESS_TOKEN)
                                .build();
                        return chain.proceed(request);
                    })
                    .build();
            retrofit = new Retrofit.Builder()
                    .baseUrl(Credentials.BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(
                            GsonConverterFactory.create()
                    )
                    .build();
        }
        return retrofit;
    }

    @Getter
    private final static MovieApi movieApi = getAuthBackendRetrofit().create(MovieApi.class);
}
