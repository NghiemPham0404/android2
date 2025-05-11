package com.example.movieapp.Request;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.movieapp.R;
import com.example.movieapp.utils.Credentials;
import com.example.movieapp.utils.MovieApi;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BackendService {
    private static Retrofit backendRetrofit;

    private static Retrofit backendAuthRetrofit;

    public static Retrofit getBackendRetrofit(Context context) {
        if (backendRetrofit == null) {
            backendRetrofit = new Retrofit.Builder()
                    .baseUrl(context.getResources().getString(R.string.backendUrl))
                    .addConverterFactory(
                            GsonConverterFactory.create()
                    )
                    .build();
        }
        return backendRetrofit;
    }

    public static Retrofit getAuthBackendRetrofit(Context context) {
        if (backendAuthRetrofit == null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences("bearer", Context.MODE_PRIVATE);
            String token = sharedPreferences.getString("jwt_token", null);
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(chain -> {
                        Request original = chain.request();
                        Request request = original.newBuilder()
                                .header("Authorization", "Bearer " + token)
                                .build();
                        return chain.proceed(request);
                    })
                    .build();
            backendAuthRetrofit = new Retrofit.Builder()
                    .baseUrl(context.getResources().getString(R.string.backendUrl))
                    .client(okHttpClient)
                    .addConverterFactory(
                            GsonConverterFactory.create()
                    )
                    .build();
        }
        return backendAuthRetrofit;
    }

}
