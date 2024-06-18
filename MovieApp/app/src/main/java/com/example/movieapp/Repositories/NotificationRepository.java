package com.example.movieapp.Repositories;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.movieapp.Model.NotificationModel;
import com.example.movieapp.RoomDatabase.NotificationDAO;
import com.example.movieapp.RoomDatabase.NotificationDatabase;

import java.util.List;

public class NotificationRepository {
    private NotificationDAO notificationDao;
    private LiveData<List<NotificationModel>> allNotifications;
    public NotificationRepository(Application application) {
        NotificationDatabase database = NotificationDatabase.getInstance(application);
        notificationDao = database.notificationDao();
        allNotifications = notificationDao.getAllNotifications();
    }
    public void insert(NotificationModel notification) {
        new InsertNotificationAsyncTask(notificationDao).execute(notification);
    }
    public LiveData<List<NotificationModel>> getAllNotifications() {
        return allNotifications;
    }

    private static class InsertNotificationAsyncTask extends AsyncTask<NotificationModel, Void, Void> {
        private NotificationDAO notificationDao;

        private InsertNotificationAsyncTask(NotificationDAO notificationDao) {
            this.notificationDao = notificationDao;
        }

        @Override
        protected Void doInBackground(NotificationModel... notifications) {
            notificationDao.insert(notifications[0]);
            return null;
        }
    }
}
