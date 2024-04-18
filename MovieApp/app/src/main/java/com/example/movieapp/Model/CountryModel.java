package com.example.movieapp.Model;

public class CountryModel {
    String iso_3166_1;
    String english_name;
    public CountryModel(String iso_3166_1, String english_name) {
        this.iso_3166_1 = iso_3166_1;
        this.english_name = english_name;
    }
    public String getIso_3166_1() {
        return iso_3166_1;
    }

    public String getEnglish_name() {
        return english_name;
    }
}