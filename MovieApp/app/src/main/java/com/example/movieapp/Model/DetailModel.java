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

    public String getComment() {
        return comment;
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
    private String comment;
    private String duration;

    private boolean favor;

    public DetailModel(String userId, int movieId, String rating, String comment, String duration, boolean favor) {
        this.userId = userId;
        this.movieId = movieId;
        this.rating = rating;
        this.comment = comment;
        this.duration = duration;
        this.favor = favor;
    }
}
