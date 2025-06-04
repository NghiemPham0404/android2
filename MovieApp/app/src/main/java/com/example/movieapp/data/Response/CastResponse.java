package com.example.movieapp.data.Response;

import androidx.annotation.NonNull;

import com.example.movieapp.data.Model.PersonModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CastResponse {
    @SerializedName("cast")
    @Expose
    List<PersonModel> castModels;

    public List<PersonModel> getCastModels(){
        return castModels;
    }

    @NonNull
    @Override
    public String toString() {
        return "CastModels = "+castModels.toString();
    }
}
