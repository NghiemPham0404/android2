package com.example.movieapp.Response;

import androidx.annotation.NonNull;

import com.example.movieapp.Model.CastModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CastResponse {
    @SerializedName("cast")
    @Expose
    List<CastModel> castModels;

    public List<CastModel> getCastModels(){
        return castModels;
    }

    @NonNull
    @Override
    public String toString() {
        return "CastModels = "+castModels.toString();
    }
}
