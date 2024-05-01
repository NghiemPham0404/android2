package com.example.movieapp.Request;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.movieapp.AppExecutors;
import com.example.movieapp.Model.CastModel;
import com.example.movieapp.Response.PeopleResponse;
import com.example.movieapp.utils.Credentials;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Response;

public class PersonApiClient {
    private static PersonApiClient instance;
    private MutableLiveData<List<CastModel>> people;

    private RetrievePeopleRunnable retrievePeopleRunnable;

    public static PersonApiClient getInstance(){
        if(instance == null){
            instance = new PersonApiClient();
        }
        return instance;
    }

    public PersonApiClient(){
        people = new MutableLiveData<>();
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

    public MutableLiveData<List<CastModel>> getPeople(){
        return people;
    }
    private class RetrievePeopleRunnable implements Runnable{
        String query;
        int page;
        boolean cancelRequest;
        public RetrievePeopleRunnable(String query, int page){
            this.page = page;
            this.query = query;
            this.cancelRequest = false;
        }

        @Override
        public void run() {
            Response response;
            try {
                if(query!=null){
                    response = getPeople().execute();
                    if(response.isSuccessful()){
                        List<CastModel> people_list = new ArrayList<>(((PeopleResponse)response.body()).getPeople());
                        if(page==1){
                            people.postValue(people_list);
                        }else{
                            List<CastModel> current_people = people.getValue();
                            current_people.addAll(people_list);
                            people.postValue(current_people);
                        }
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
            return MyService.getMovieApi().searchPerson(Credentials.API_KEY,query, page);
        }
        private void cancelRequest() {
            Log.v("QUERY TASK", "Canceled request");
        }
    }
}
