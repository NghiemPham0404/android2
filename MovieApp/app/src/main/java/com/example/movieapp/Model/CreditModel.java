package com.example.movieapp.Model;

import java.util.Date;

public class CreditModel implements  Comparable<CreditModel>{
    public CreditModel(int id, String title, String credit_id, String character,String known_for_department, String release_date, float vote_average) {
        this.credit_id = credit_id;
        this.character = character;
        this.known_for_department = known_for_department;
        this.release_date = release_date;
        this.vote_average = vote_average;
        this.id = id;
        this.title = title;
    }

    private int id;
    private String credit_id;
    private String character;

    public String getKnown_for_department() {
        return known_for_department;
    }

    private String known_for_department;
    private String release_date;
    private float vote_average;

    private String title;

    public boolean isDisplayYear() {
        return displayYear;
    }

    public void setDisplayYear(boolean displayYear) {
        this.displayYear = displayYear;
    }

    private boolean displayYear;

    public String getCredit_id() {
        return credit_id;
    }

    public String getCharacter() {
        return character;
    }

    public String getRelease_date() {
        return release_date;
    }

    public float getVote_average() {
        return vote_average;
    }
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getYear(){
        try {
            int year = Integer.parseInt(getRelease_date().split("-")[0]);
            return year;
        }catch (Exception e){
            return new Date(System.currentTimeMillis()).getYear()+1900;
        }
    }


    @Override
    public int compareTo(CreditModel o) {
        return this.getYear() - o.getYear();
    }
}
