package com.example.movieapp.Repositories;

import androidx.lifecycle.LiveData;

import com.example.movieapp.Model.CastModel;
import com.example.movieapp.Request.PersonApiClient;

import java.util.List;

public class PersonRepository {
    private static PersonRepository instance;
    private PersonApiClient personApiClient;
    private String query;
    private int page;
    public static PersonRepository getInstance(){
        if(instance == null){
            instance = new PersonRepository();
        }
        return instance;
    }
    public PersonRepository(){
        personApiClient = PersonApiClient.getInstance();
    }
    public void searchPeople(String query, int page){
        personApiClient.searchPeople(query, page);
    }
    public void searchPeopleNextPage(){
      searchPeople(query, page+1);
    }

    public LiveData<List<CastModel>> getPeople(){
        return personApiClient.getPeople();
    }
}
