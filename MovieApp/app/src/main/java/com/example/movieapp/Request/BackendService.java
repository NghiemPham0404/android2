package com.example.movieapp.Request;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.movieapp.BuildConfig;
import com.example.movieapp.R;
import com.example.movieapp.utils.BackendAPI;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BackendService {
    private static Retrofit backendRetrofit;

    private static BackendAPI authAPI;

    private Retrofit backendAuthorizedRetrofit;

    private BackendAPI authorizedBackendAPI;

    public static Retrofit getBackendRetrofit(Context context) {
        if (backendRetrofit == null) {
            backendRetrofit = new Retrofit.Builder()
                    .baseUrl(BuildConfig.BACKEND_URL)
                    .addConverterFactory(
                            GsonConverterFactory.create()
                    )
                    .build();
        }
        return backendRetrofit;
    }

    public Retrofit getAuthBackendRetrofit(Context context) {
        if (backendAuthorizedRetrofit == null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences("bearer",
                    Context.MODE_PRIVATE);
            String token = sharedPreferences.getString("jwt_token", null);
            Log.i("JWT TOKEN RETROFIT", token+" ");
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(chain -> {
                        Request original = chain.request();
                        Request request = original.newBuilder()
                                .header("Authorization", "Bearer " + token)
                                .build();
                        return chain.proceed(request);
                    })
                    .build();
            backendAuthorizedRetrofit = new Retrofit.Builder()
                    .baseUrl(context.getResources().getString(R.string.backendUrl))
                    .client(okHttpClient)
                    .addConverterFactory(
                            GsonConverterFactory.create()
                    )
                    .build();
        }
        return backendAuthorizedRetrofit;
    }

    /**
     * TODO : get non authorization api for login, signup, forget-password api endpoints
     * */
    public static BackendAPI getAuthAPI(Context context){
        if(authAPI == null){
            authAPI = getBackendRetrofit(context).create(BackendAPI.class);
        }
        return authAPI;
    }

    /**
     * TODO : get authorized api for calling all other api endpoints
     * */
    public BackendAPI getAuthorizedBackendAPI(Context context){
        if(authorizedBackendAPI == null){
            return getAuthBackendRetrofit(context).create(BackendAPI.class);
        }
        return authorizedBackendAPI;
    }
}
