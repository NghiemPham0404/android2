package com.example.movieapp.ViewModel;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.movieapp.data.Model.NotificationModel;
import com.example.movieapp.data.Repositories.NotificationRepository;

import java.util.List;

public class NotificationViewModel extends ViewModel {
    private NotificationRepository repository;
    private LiveData<List<NotificationModel>> allNotifications;

    public NotificationViewModel(Application application) {
        repository = new NotificationRepository(application);
        allNotifications = repository.getAllNotifications();
    }
    public void insert(NotificationModel notification) {
        repository.insert(notification);
    }
    public LiveData<List<NotificationModel>> getAllNotifications() {
        return allNotifications;
    }
}
