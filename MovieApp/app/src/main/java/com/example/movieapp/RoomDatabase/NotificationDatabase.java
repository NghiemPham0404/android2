package com.example.movieapp.RoomDatabase;
import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.movieapp.Model.NotificationModel;

@Database(entities = {NotificationModel.class}, version = 1)
public abstract class NotificationDatabase extends RoomDatabase {
    public abstract NotificationDAO notificationDao();

    private static volatile NotificationDatabase instance;
    public static NotificationDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (NotificationDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                                    NotificationDatabase.class, "notification_database")
                            .build();
                }
            }
        }
        return instance;
    }
}
