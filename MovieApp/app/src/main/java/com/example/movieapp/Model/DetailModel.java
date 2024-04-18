package com.example.movieapp.Model;

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

    public String getMovieUrl() {
        return movieUrl;
    }

    public void setMovieUrl(String movieUrl) {
        this.movieUrl = movieUrl;
    }

    private String movieUrl;
    private boolean favor;

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

    private String time;

    public DetailModel(String userId, int movieId, String rating, String review, String duration, boolean favor, String time, String username, String avatar, String movieUrl, String timeFavor) {
        this.userId = userId;
        this.movieId = movieId;
        this.rating = rating;
        this.review = review;
        this.duration = duration;
        this.favor = favor;
        this.time = time;
        this.username = username;
        this.avatar = avatar;
        this.movieUrl = movieUrl;
        this.timeFavor = timeFavor;
    }
}
