package com.example.movieapp.Model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class CastModel {

    private int id;
    private String name;
    private String profile_path;
    private String character;

    private String known_for_department;

    private int gender;

    private String birthday;

    private String place_of_birth;

    private String biography;

    public String getKnown_for_department() {
        return known_for_department;
    }

    public int getGender() {
        return gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getPlace_of_birth() {
        return place_of_birth;
    }

    public String getBiography() {
        return biography;
    }


    public CastModel(int id, String name, String profile_path, String known_for_department, int gender, String birthday, String place_of_birth, String biography) {
        this.id = id;
        this.name = name;
        this.profile_path = profile_path;
        this.known_for_department = known_for_department;
        this.gender = gender;
        this.birthday = birthday;
        this.place_of_birth = place_of_birth;
        this.biography = biography;
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

    public void setCharacter(String character) {
        this.character = character;
    }

    public void setKnown_for_department(String known_for_department) {
        this.known_for_department = known_for_department;
    }

    public CastModel(int id, String name, String profile_path, String character) {
        this.id = id;
        this.name = name;
        this.profile_path = profile_path;
        this.character = character;
    }

}
