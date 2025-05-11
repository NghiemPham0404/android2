package com.example.movieapp.ViewModel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.movieapp.data.Model.DTO.InteractionDTO;
import com.example.movieapp.data.Repositories.InteractionRepo;
import com.example.movieapp.data.Response.ListResponse;

public class InteractionsListViewModel extends AndroidViewModel {
    private final InteractionRepo interactionRepo;

    public InteractionsListViewModel(Application application) {
        super(application);
        interactionRepo = new InteractionRepo(application.getApplicationContext());
    }

    public void requestGetHistoryMovies(int page) {
        interactionRepo.requestGetHistoryMovies(page);
    }

    public void requestGetFavoriteMovies(int page) {
        interactionRepo.requestGetFavoriteMovies(page);
    }

    public void requestGetHistoryMoviesNext() {
        interactionRepo.requestGetHistoryMoviesNext();
    }

    public void requestGetFavoriteMoviesNext() {
        interactionRepo.requestGetFavoriteMoviesNext();
    }

    public LiveData<ListResponse<InteractionDTO.InteractionDAOExtended>> getFavoritesMovie(){
        return interactionRepo.getFavoriteMovies();
    }
    public LiveData<ListResponse<InteractionDTO.InteractionDAOExtended>> getHistoryMovie(){
        return interactionRepo.getHistoryMovies();
    }
}
