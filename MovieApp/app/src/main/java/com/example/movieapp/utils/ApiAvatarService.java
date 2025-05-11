package com.example.movieapp.utils;

import com.example.movieapp.data.Response.AvatarResponse;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ApiAvatarService {
    @Multipart
    @POST("1/upload")
    Call<AvatarResponse> uploadImage(
            @Query("key") String apiKey,
            @Part MultipartBody.Part image
    );
}
