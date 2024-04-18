package com.example.movieapp.Response;

import com.example.movieapp.Model.CountryModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RegionResponse {

    @SerializedName("results")
    @Expose
    List<CountryModel> regions;

    public List<CountryModel> getRegions(){
        return regions;
    }
}
