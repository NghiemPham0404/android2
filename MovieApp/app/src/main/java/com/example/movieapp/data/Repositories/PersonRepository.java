package com.example.movieapp.data.Repositories;

import androidx.lifecycle.LiveData;

import com.example.movieapp.data.Model.PersonModel;
import com.example.movieapp.Request.PersonApiClient;

import java.util.List;

public class PersonRepository {
    private static PersonRepository instance;
    private PersonApiClient personApiClient;
    private String query;
    private int page;
    private int person_id;
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

    public LiveData<List<PersonModel>> getPeople(){
        return personApiClient.getPeople();
    }

    public void searchPerson(int person_id) {
        this.person_id = person_id;
        personApiClient.searchPerson(person_id);
    }

    public LiveData<PersonModel> getPerson(){
        return personApiClient.getPerson();
    }
}
