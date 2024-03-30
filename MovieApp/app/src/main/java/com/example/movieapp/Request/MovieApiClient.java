package com.example.movieapp.Request;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.movieapp.AppExecutors;
import com.example.movieapp.Model.MovieModel;
import com.example.movieapp.Repositories.MovieRepository;
import com.example.movieapp.Response.MovieResponse;
import com.example.movieapp.Response.MovieSearchResponse;
import com.example.movieapp.utils.Credentials;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Response;

public class MovieApiClient {
    private static MovieApiClient instance;
    private MutableLiveData<List<MovieModel>> mMovies;

    RetrieveMoviesRunnable retrieveMoviesRunnable;

    public static MovieApiClient getInstance(){
            if(instance == null){
                instance = new MovieApiClient();
            }
            return instance;
    }

    public MovieApiClient(){
        mMovies = new MutableLiveData<>();
    }

    public MutableLiveData<List<MovieModel>> getMovies(){
        return mMovies;
    }

    public void searchMovieApi(String query, int page){
        if(retrieveMoviesRunnable!=null){
            retrieveMoviesRunnable = null;
        }
        retrieveMoviesRunnable = new RetrieveMoviesRunnable(query,page);
        final Future myHandler = AppExecutors.getInstance().networkIO().submit(new Runnable() {
            @Override
            public void run() {

            }
        });

        AppExecutors.getInstance().networkIO().schedule(new Runnable() {
            @Override
            public void run() {
                // Hủy lời gọi Retrofit
                myHandler.cancel(true);
            }
        }, 10, TimeUnit.SECONDS );



    }
    // Lấy dữ liệu từ Retrofit bằng class Runnable
    // cần 2 loại truy vấn, theo ID và tiềm kiếm
    private class RetrieveMoviesRunnable implements Runnable{

        private String query;
        private int pageNumber;
        boolean cancelRequest;

        public RetrieveMoviesRunnable(String query, int pageNumber){
            this.query = query;
            this.pageNumber = pageNumber;
            this.cancelRequest = false;
        }

        @Override
        public void run() {
            try {
                Response response = getMovies(query, pageNumber).execute();
                if(cancelRequest){
                    return;
                }

                if(response.code() == 200){
                    List<MovieModel> list = new ArrayList<>(((MovieSearchResponse) response.body()).getMovies());
                    if(pageNumber == 1){
                        mMovies.postValue(list);
                    }else{
                        List currentList = mMovies.getValue();
                        currentList.addAll(list);
                        mMovies.postValue(currentList);
                    }
                }else{
                    String error = response.errorBody().string();
                    Log.v("Tag", error);
                    mMovies.postValue(null);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        private Call<MovieSearchResponse> getMovies(String query, int pageNumber){
            return MyService.getMovieApi().searchMovie(Credentials.API_KEY, query, pageNumber );
        }

        private void cancelRequest(){
            Log.v("QUERY TASK", "Canceled request");

        }
    }
}
