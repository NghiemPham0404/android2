package com.example.movieapp.Response;

import androidx.annotation.NonNull;

import com.example.movieapp.Model.CreditModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CreditResponse {
    @SerializedName("cast")
    @Expose
    List<CreditModel> creditModelList;

    public List<CreditModel> getCreditModelList(){
        return creditModelList;
    }

    @NonNull
    @Override
    public String toString() {
        return "Cast = {"+this.creditModelList+"}";
    }
}
