package com.example.movieapp.utils;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiAvatarService {
    @Multipart
    @POST
    Call<ResponseBody> uploadFile(@Part MultipartBody.Part file);
}
