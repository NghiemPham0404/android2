package com.example.movieapp.data.Response;

import androidx.annotation.NonNull;

import com.example.movieapp.data.Model.AvatarModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AvatarResponse {
    @SerializedName("data")
    @Expose
    AvatarModel avatarModel;


    public AvatarModel getAvatarModel(){
        return  avatarModel;
    }
    @NonNull
    @Override
    public String toString(){
        return avatarModel.toString();
    }
}
