package com.example.movieapp.Model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class AccountModel implements Parcelable, Serializable{
    protected AccountModel(Parcel in) {
        user_id = in.readString();
        username = in.readString();
        email = in.readString();
        password = in.readString();
        sms = in.readString();
        google_id = in.readString();
        facebook_id = in.readString();
        avatar = in.readString();
    }

    public static final Creator<AccountModel> CREATOR = new Creator<AccountModel>() {
        @Override
        public AccountModel createFromParcel(Parcel in) {
            return new AccountModel(in);
        }

        @Override
        public AccountModel[] newArray(int size) {
            return new AccountModel[size];
        }
    };

    public String getUser_id() {
        return user_id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getSms() {
        return sms;
    }

    public String getGoogle_id() {
        return google_id;
    }

    public String getFacebook_id() {
        return facebook_id;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar){
        this.avatar = avatar;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setSms(String sms) {
        this.sms = sms;
    }

    public void setGoogle_id(String google_id) {
        this.google_id = google_id;
    }

    public void setFacebook_id(String facebook_id) {
        this.facebook_id = facebook_id;
    }

    String user_id;
    String username;
    String email;
    String password;
    String sms;
    String google_id;
    String facebook_id;
    String avatar;

    public AccountModel(){

    }
    public AccountModel(String user_id, String username, String email, String password, String sms, String google_id, String facebook_id, String avatar) {
        this.user_id = user_id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.sms = sms;
        this.google_id = google_id;
        this.facebook_id = facebook_id;
        this.avatar = avatar;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(user_id);
        dest.writeString(username);
        dest.writeString(email);
        dest.writeString(password);
        dest.writeString(sms);
        dest.writeString(google_id);
        dest.writeString(facebook_id);
        dest.writeString(avatar);
    }
}
