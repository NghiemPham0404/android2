package com.example.movieapp.View.UserPackage.Notification;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.example.movieapp.Adapters.NotificationAdapter;
import com.example.movieapp.Model.AccountModel;
import com.example.movieapp.Model.NotificationModel;
import com.example.movieapp.R;
import com.example.movieapp.ViewModel.NotificationViewModel;

import java.util.List;

public class NotificationsView extends AppCompatActivity {

    RecyclerView notifications_recycler;
    private NotificationViewModel notificationViewModel;
    List<NotificationModel> notifications;
    NotificationAdapter notificationAdapter;
    AccountModel loginAccount;

    ImageButton back_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications_view);
        loginAccount = getIntent().getParcelableExtra("loginAccount");
        notificationViewModel = new NotificationViewModel(getApplication());
        ObserverAnyChange();
        initComponents();
        onConfigurationRecycler();
        initFeature();
    }

    private void onConfigurationRecycler() {
        notifications_recycler.setLayoutManager(new LinearLayoutManager(this));
        notificationAdapter = new NotificationAdapter(this, loginAccount);
        notifications_recycler.setAdapter(notificationAdapter);
    }

    public void ObserverAnyChange(){
        notificationViewModel.getAllNotifications().observe(this, new Observer<List<NotificationModel>>() {
            @Override
            public void onChanged(List<NotificationModel> notificationModels) {
                if(notificationModels!=null){
                    notifications = notificationModels;
                    notificationAdapter.setNotifications(notifications);
                    Log.i("notifications : ", notifications.size()+"");
                    findViewById(R.id.loadingLayout).setVisibility(View.GONE);
                }
            }
        });
    }
    private void initFeature() {
        findViewById(R.id.loadingLayout).setVisibility(View.VISIBLE);
        notificationViewModel.getAllNotifications();

    }

    private void initComponents() {
        back_btn = findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        notifications_recycler = findViewById(R.id.notification_recycler);
        onConfigurationRecycler();
    }
}