package com.example.movieapp.Response;

import androidx.annotation.NonNull;

import com.example.movieapp.Model.AvatarModel;
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
