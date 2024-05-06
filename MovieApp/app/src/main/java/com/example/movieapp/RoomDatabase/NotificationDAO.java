package com.example.movieapp.RoomDatabase;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.movieapp.Model.NotificationModel;

import java.util.List;

@Dao
public interface NotificationDAO {
    @Insert
    void insert(NotificationModel notification);

    @Query("SELECT * FROM notifications ORDER BY time DESC")
    LiveData<List<NotificationModel>> getAllNotifications();
}
