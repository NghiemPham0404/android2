package com.example.movieapp.utils;

public class Credentials {

    public static final String BASE_URL = "https://api.themoviedb.org";
    public static final String API_KEY = "cf32372af846ed46863011b283bdcba1";

    //TODO : movies list
    public static final String NOW_PLAYING = "/3/movie/now_playing";
    public static final String POPULAR = "/3/movie/popular";
    public static final String TOP_RATED = "/3/movie/top_rated";
    public static final String UPCOMING = "/3/movie/upcoming";

    //TODO :  search movie
    public static final String SEARCH_MOVIE_URL = "https://api.themoviedb.org/3/search/movie";
    public static final String BASE_IMAGE_URL = "https://image.tmdb.org/t/p/w500";


    public static final String BASE_MOVIE_URL = "https://api.themoviedb.org/3/movie/";
    // youtube link for embeded youtube trailer (if exist)
    public static final String embed_pattern = "<iframe width=\"560\" height=\"315\" src=\"https://www.youtube.com/embed/%s\" title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\" allowfullscreen></iframe>";

    public  static final String us_language = "en-US";
    public static final String vn_language = "vi";
    public static final String es_language = "es";

    //todo: TÌM KIẾM NGƯỜI HOẠT ĐỘNG NGHỆ THUẬT
    //https://api.themoviedb.org/3/person/{person_id}
    public static final String search_person = "3/search/person";
    public static final String append_person = "external_ids,movie_credits";
    // TODO : SIGNIN, SIGNUP
    public static final String signup_link = "https://script.google.com/macros/s/AKfycbztDFqDQ27AEJtc7HR5PQa_Jtj1sTYum8yyP94FOqhRzT_nPiu5ySlHsQ3c3oll77cA/exec";

    public static final String manage_url = "AKfycbwb6OpcqdD1yJifVIZMeR5x2Ae1R5Ak-V04ASpXUNnkF1IjzbClCW8ZAdC0hoCE6QRp/exec";
    public static final String functionname_detail = "detail";
    public static final String functionname_video = "movies";

    public static final String deleteDetail = "deleteDetail";
    public static final String functionname_login = "login";

    public static final String functionname_regis = "regis";
    public static final String BASE_MANAGE_URL = "https://script.google.com/macros/s/";

    public static final String USER_FILE_PATH = "user.txt";
    public static final String RECOMMEND_SEARCH_FILE_PATH = "search.txt";
}
