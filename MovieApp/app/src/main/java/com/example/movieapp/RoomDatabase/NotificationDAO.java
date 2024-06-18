package com.example.movieapp.RoomDatabase;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.movieapp.Model.NotificationModel;

import java.util.List;

@Dao
public interface NotificationDAO {
    @Insert
    void insert(NotificationModel notification);

    @Update
    void update(NotificationModel... notifications);

    @Delete
    void delete(NotificationModel notification);

    @Query("SELECT * FROM notifications ORDER BY time DESC")
    LiveData<List<NotificationModel>> getAllNotifications();

    @Query("SELECT * FROM notifications WHERE id = :id")
    NotificationModel getNotificationById(int id);
}
