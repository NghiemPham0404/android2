package com.example.movieapp.Response;

import com.example.movieapp.Model.DetailModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DetailResponse {

    public float getTotalRating() {
        return totalRating;
    }

    public List<DetailModel> getAllReviews() {
        return allReviews;
    }

    @SerializedName("totalRating")
    @Expose
    public float totalRating;

    @SerializedName("allReviews")
    @Expose
    private List<DetailModel> allReviews;

    @Override
    public String toString(){
        return "All reviews ={ "+allReviews.toString()+"}";
    }
}
