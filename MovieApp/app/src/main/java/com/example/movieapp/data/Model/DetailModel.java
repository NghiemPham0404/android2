package com.example.movieapp.data.Model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DetailModel {
    public String getUserId() {
        return userId;
    }

    public int getMovieId() {
        return movieId;
    }

    public String getRating() {
        return rating;
    }

    public String getReview() {
        return review;
    }

    public String getDuration() {
        return duration;
    }

    public boolean isFavor() {
        return favor;
    }

    private String userId;
    private int movieId;
    private String rating;
    private String review;
    private String duration;

    private String url;
    private boolean favor;
    public void setFavor(boolean favor){
        this.favor  = favor;
    }

    public String getUrl() {
        return url;
    }

    public void setMovieUrl(String movieUrl) {
        this.url = movieUrl;
    }

    public String getUrl720() {
        return url720;
    }

    private String url720;


    public String getTimeFavor() {
        return timeFavor;
    }

    public void setTimeFavor(String timeFavor) {
        this.timeFavor = timeFavor;
    }

    private String timeFavor;

    public String getUsername() {
        return username;
    }

    public String getAvatar() {
        return avatar;
    }

    private String username;
    private String avatar;

    public String getTime() {
        return time;
    }

    public Date getTimeAsDate()  {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");
            Date date = formatter.parse(getTime());
            return date;
        }catch (Exception e){
            return new Date(System.currentTimeMillis());
        }
    }

    private String time;

    public DetailModel(String userId, int movieId, String rating, String review, String duration, boolean favor, String time, String username, String avatar, String url, String url720, String timeFavor) {
        this.userId = userId;
        this.movieId = movieId;
        this.rating = rating;
        this.review = review;
        this.duration = duration;
        this.favor = favor;
        this.time = time;
        this.username = username;
        this.avatar = avatar;
        this.url = url;
        this.url720 = url720;
        this.timeFavor = timeFavor;
    }
}
