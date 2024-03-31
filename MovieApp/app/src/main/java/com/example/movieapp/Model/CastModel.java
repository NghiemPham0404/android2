package com.example.movieapp.Model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class CastModel {

    private int id;
    private String name;
    private String profile_path;
    private String character;

    protected CastModel(Parcel in) {
        id = in.readInt();
        name = in.readString();
        profile_path = in.readString();
        character = in.readString();
    }



    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getProfile_path() {
        return profile_path;
    }

    public String getCharacter() {
        return character;
    }

    public CastModel(int id, String name, String profile_path, String character) {
        this.id = id;
        this.name = name;
        this.profile_path = profile_path;
        this.character = character;
    }

}
