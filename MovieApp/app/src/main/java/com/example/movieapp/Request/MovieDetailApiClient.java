package com.example.movieapp.Request;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.movieapp.AppExecutors;
import com.example.movieapp.Model.MovieModel;
import com.example.movieapp.utils.Credentials;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Response;

public class MovieDetailApiClient {
    static MovieDetailApiClient  instance;

    private MutableLiveData<MovieModel> movie;
    RetrieveMovieRunnable retrieveMovieRunnable;

    public static MovieDetailApiClient getInstance() {
        if (instance == null) {
            instance = new MovieDetailApiClient();
        }
        return instance;
    }

    public MovieDetailApiClient(){
        this.movie = new MutableLiveData<>();
    }

    public MutableLiveData<MovieModel> getMovie(){
        return movie;
    }

    public void searchMovieApi(int id){
        if(retrieveMovieRunnable!=null){
            retrieveMovieRunnable=null;
        }
        retrieveMovieRunnable = new RetrieveMovieRunnable(id);

        final Future myHandler = AppExecutors.getInstance().networkIO().submit(retrieveMovieRunnable);
        AppExecutors.getInstance().networkIO().schedule(new Runnable() {
            @Override
            public void run() {
                // Hủy lời gọi Retrofit
                myHandler.cancel(true);
            }
        }, 10, TimeUnit.SECONDS);
    }

    private class RetrieveMovieRunnable implements Runnable {
        int id;
        public boolean cancelRequest;
        public RetrieveMovieRunnable(int id){
            this.id = id;
            this.cancelRequest = false;
        }
        @Override
        public void run() {
            try {
                Response response = getMovie().execute();
                if(response.isSuccessful()){
                    MovieModel movieModel = ((Response<MovieModel>)response).body();
                    movie.postValue(movieModel);
                }

                if (cancelRequest) {
                    cancelRequest();
                    return;
                }
            }catch (Exception e){
                Log.i("Get movie detail", "fail : " + e);
                movie.postValue(null);
            }
        }

        public Call<MovieModel> getMovie(){
            return MyService.getMovieApi().searchMovieDetail(id,Credentials.API_KEY,
                    "credits,videos");
        }
    }
    private void cancelRequest() {
        Log.v("QUERY TASK", "Canceled request");
    }
}
