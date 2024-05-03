package com.example.movieapp.Model;

import java.util.List;

public class PersonModel {
    public PersonModel(int id, String name, String profile_path, String character, String known_for_department, int gender, String birthday, String place_of_birth, String biography, ExternalLinkModel external_ids, MovieCredits movie_credits) {
        this.id = id;
        this.name = name;
        this.profile_path = profile_path;
        this.character = character;
        this.known_for_department = known_for_department;
        this.gender = gender;
        this.birthday = birthday;
        this.place_of_birth = place_of_birth;
        this.biography = biography;
        this.external_ids = external_ids;
        this.movie_credits = movie_credits;
    }
    private int id;
    private String name;
    private String profile_path;
    private String character;
    private String known_for_department;
    private int gender;
    private String birthday;
    private String place_of_birth;
    private String biography;
    private ExternalLinkModel external_ids;
    private MovieCredits movie_credits;

    public ExternalLinkModel getExternal_ids() {
        return external_ids;
    }
    public MovieCredits getMovie_credits() {
        return movie_credits;
    }

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
    public class MovieCredits{
        public List<CreditModel> cast;
        public List<CreditModel> crew;
        public MovieCredits(List<CreditModel> cast, List<CreditModel> crew) {
            this.cast = cast;
            this.crew = crew;
        }

        public List<CreditModel> getCast() {
            return cast;
        }

        public List<CreditModel> getCrew() {
            return crew;
        }
    }
}
