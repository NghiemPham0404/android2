package com.example.movieapp.Model;

public class CastModel {

    private int id;
    private String name;
    private String profile_path;
    private String character;

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
