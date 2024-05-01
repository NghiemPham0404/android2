package com.example.movieapp.Response;

import androidx.annotation.NonNull;

import com.example.movieapp.Model.CastModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PeopleResponse {
    @SerializedName("results")
    @Expose
    List<CastModel> people;

    @SerializedName("total_results")
    @Expose
    int totalResults;
    public int getTotalResults(){
        return totalResults;
    }

    public List<CastModel> getPeople(){
     return people;
    }

    @NonNull
    @Override
    public String toString() {
        return "CastModels = "+people.toString();
    }
}