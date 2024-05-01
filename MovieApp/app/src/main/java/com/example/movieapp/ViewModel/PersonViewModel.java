package com.example.movieapp.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.movieapp.Model.CastModel;
import com.example.movieapp.Repositories.PersonRepository;

import java.util.List;

public class PersonViewModel extends ViewModel {
    private static PersonRepository personRepository;
    public PersonViewModel(){
        personRepository = PersonRepository.getInstance();
    }

    public void searchPeople(String query, int page){
        personRepository.searchPeople(query, page);
    }

    public void searchPeopleNext(){
        personRepository.searchPeopleNextPage();
    }

    public LiveData<List<CastModel>> getPeople(){
        return personRepository.getPeople();
    }
}
