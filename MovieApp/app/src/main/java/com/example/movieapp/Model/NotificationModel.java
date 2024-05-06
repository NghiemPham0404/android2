package com.example.movieapp.Model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.text.SimpleDateFormat;
import java.util.Date;

@Entity(tableName = "notifications")
public class NotificationModel implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String body;
    private String content;
    private String largeImage;
    private int movieId;
    private String userId;
    private String time;
    public  NotificationModel(){

    }
    protected NotificationModel(Parcel in) {
        id = in.readInt();
        title = in.readString();
        body = in.readString();
        content = in.readString();
        largeImage = in.readString();
        movieId = in.readInt();
        userId = in.readString();
        time = in.readString();
    }

    public static final Creator<NotificationModel> CREATOR = new Creator<NotificationModel>() {
        @Override
        public NotificationModel createFromParcel(Parcel in) {
            return new NotificationModel(in);
        }

        @Override
        public NotificationModel[] newArray(int size) {
            return new NotificationModel[size];
        }
    };

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getLargeImage() {
        return largeImage;
    }

    public void setLargeImage(String largeImage) {
        this.largeImage = largeImage;
    }
    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }
    public int getMovieId() {
        return movieId;
    }
    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public long getTimeMilisecond(){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-YYYY HH:mm:ss");
            return sdf.parse(time).getTime();
        }catch (Exception e){
            return new Date(System.currentTimeMillis()).getTime();
        }
    }
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(body);
        dest.writeString(content);
        dest.writeString(largeImage);
        dest.writeInt(movieId);
        dest.writeString(userId);
        dest.writeString(time);
    }
}
