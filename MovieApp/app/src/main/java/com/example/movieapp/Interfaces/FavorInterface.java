package com.example.movieapp.Interfaces;

import android.widget.Button;

import com.example.movieapp.Model.MovieModel;

public interface FavorInterface {

    public void openMovie(int movieId);

    public void playMovie(MovieModel movie, Button button);

    public void changeFavorite(int movieId);

}
