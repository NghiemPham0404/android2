package com.example.movieapp.data.Response;

import androidx.annotation.NonNull;

import com.example.movieapp.data.Model.VideoModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VideoResponse {

    @SerializedName("results")
    @Expose
    List<VideoModel> videoModels;

    public  List<VideoModel> getVideoModels(){
        return  videoModels;
    }

    @NonNull
    @Override
    public String toString() {
        return "CastModels = "+videoModels.toString();
    }
}
