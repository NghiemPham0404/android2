package com.example.movieapp.data.Model;

import java.util.List;

public class MoviesGroup implements Comparable<MoviesGroup> {
        private List<MovieModel> movies;
        private String title;

    public List<MovieModel> getMovies() {
        return movies;
    }

    public String getTitle() {
        return title;
    }

    public MoviesGroup(List<MovieModel> movies, String title) {
        this.movies = movies;
        this.title = title;
    }

    public int toCompareOrder(){
        switch (this.title){
            case "Now playing":
                return 0;
            case "Popular":
                return 1;
            case "Top rated":
                return 2;
            case "Upcoming":
                return 3;
            default:
            return 4;
        }
    }

    @Override
    public int compareTo(MoviesGroup o) {
        return this.toCompareOrder() - o.toCompareOrder();
    }
}
