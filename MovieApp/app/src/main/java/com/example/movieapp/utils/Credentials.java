package com.example.movieapp.utils;

import com.example.movieapp.BuildConfig;

public class Credentials {
    public static final String BASE_URL = BuildConfig.BASE_URL;
    public static final String API_KEY = BuildConfig.API_KEY;
    public static final String TMDB_ACCESS_TOKEN = BuildConfig.TMDB_ACCESS_TOKEN;
    
    public static final String BASE_IMAGE_URL = "https://image.tmdb.org/t/p/w500";

    //todo: TÌM KIẾM NGƯỜI HOẠT ĐỘNG NGHỆ THUẬT
    public static final String append_person = "external_ids,movie_credits";
    // TODO : SIGNIN, SIGNUP

    public static final String RECOMMEND_SEARCH_FILE_PATH = "search.txt";
}
