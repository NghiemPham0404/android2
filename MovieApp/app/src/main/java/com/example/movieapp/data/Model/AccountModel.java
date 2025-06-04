package com.example.movieapp.data.Model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountModel implements Parcelable, Serializable{
    protected AccountModel(Parcel in) {
        user_id = in.readString();
        name = in.readString();
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

    String user_id;
    String name;
    String email;
    String password;
    String sms;
    String google_id;
    String facebook_id;
    String avatar;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(user_id);
        dest.writeString(name);
        dest.writeString(email);
        dest.writeString(password);
        dest.writeString(sms);
        dest.writeString(google_id);
        dest.writeString(facebook_id);
        dest.writeString(avatar);
    }
}
