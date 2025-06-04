package com.example.movieapp.Request;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.movieapp.AppExecutors;
import com.example.movieapp.data.Model.PersonModel;
import com.example.movieapp.data.Response.PeopleResponse;
import com.example.movieapp.utils.Credentials;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Response;

public class PersonApiClient {
    private static PersonApiClient instance;
    private MutableLiveData<List<PersonModel>> people;

    private MutableLiveData<PersonModel> person;
    private RetrievePeopleRunnable retrievePeopleRunnable;

    public static PersonApiClient getInstance(){
        if(instance == null){
            instance = new PersonApiClient();
        }
        return instance;
    }

    public PersonApiClient(){
        people = new MutableLiveData<>();
        person = new MutableLiveData<>();
    }

    public void searchPeople(String query, int page){
        if(retrievePeopleRunnable!=null){
            retrievePeopleRunnable = null;
        }
        retrievePeopleRunnable = new RetrievePeopleRunnable(query, page);
        final Future myHandler = AppExecutors.getInstance().networkIO().submit(retrievePeopleRunnable);
        AppExecutors.getInstance().networkIO().schedule(new Runnable() {
            @Override
            public void run() {
                // Hủy lời gọi Retrofit
                myHandler.cancel(true);
            }
        }, 10, TimeUnit.SECONDS);
    }

    public void searchPerson(int person_id){
        if(retrievePeopleRunnable!=null){
            retrievePeopleRunnable = null;
        }
        retrievePeopleRunnable = new RetrievePeopleRunnable(person_id);
        final Future myHandler = AppExecutors.getInstance().networkIO().submit(retrievePeopleRunnable);
        AppExecutors.getInstance().networkIO().schedule(new Runnable() {
            @Override
            public void run() {
                // Hủy lời gọi Retrofit
                myHandler.cancel(true);
            }
        }, 10, TimeUnit.SECONDS);
    }


    public MutableLiveData<List<PersonModel>> getPeople(){
        return people;
    }
    public MutableLiveData<PersonModel> getPerson(){return person;}
    private class RetrievePeopleRunnable implements Runnable{
        String query;
        int person_id = -1;
        int page;
        boolean cancelRequest;
        public RetrievePeopleRunnable(String query, int page){
            this.page = page;
            this.query = query;
            this.cancelRequest = false;
        }

        public RetrievePeopleRunnable(int person_id) {
            this.person_id  = person_id;
        }

        @Override
        public void run() {
            Response response;
            try {
                if(query!=null){
                    response = getPeople().execute();
                    if(response.isSuccessful()){
                        List<PersonModel> people_list = new ArrayList<>(((PeopleResponse)response.body()).getPeople());
                        if(page==1){
                            people.postValue(people_list);
                        }else{
                            List<PersonModel> current_people = people.getValue();
                            current_people.addAll(people_list);
                            people.postValue(current_people);
                        }
                    }
                }else if(person_id != -1){
                    response = getPerson().execute();
                    if(response.isSuccessful()){
                        PersonModel person_response = (PersonModel)response.body();
                        person.postValue(person_response);
                    }
                }
                if (cancelRequest) {
                    cancelRequest();
                    return;
                }
            }catch (Exception e){
                Log.e("people list","Fail" + e);
            }
        }
        public Call<PeopleResponse>  getPeople() {
            return MyService.getMovieApi().searchPerson(query, page, Locale.getDefault().getLanguage());
        }

        public Call<PersonModel> getPerson(){
            return MyService.getMovieApi().searchPersonByID(person_id, Credentials.append_person, Locale.getDefault().getLanguage());
        }
        private void cancelRequest() {
            Log.v("QUERY TASK", "Canceled request");
        }
    }
}
