package com.example.movieapp.View.UserPackage.Notification;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.movieapp.Model.NotificationModel;
import com.example.movieapp.R;
import com.example.movieapp.Request.ImageLoader;

public class NotificationContentView extends AppCompatActivity {
    TextView title, body, content;
    ImageView image;
    NotificationModel notification;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_content_view);
        notification = getIntent().getParcelableExtra("notification");
        initComponents();
        initFeatures();
    }
    public void initComponents(){
        title = findViewById(R.id.nofitication_title);
        body = findViewById(R.id.notification_body);
        content = findViewById(R.id.notification_content);
        image = findViewById(R.id.notification_image);
    }
    public void initFeatures(){
        title.setText(notification.getTitle());
        body.setText(notification.getBody());
        content.setText(notification.getContent());
        new ImageLoader().loadImageIntoImageView(this, notification.getLargeImage(), image);
    }

}