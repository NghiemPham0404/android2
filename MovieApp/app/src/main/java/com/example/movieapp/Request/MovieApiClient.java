package com.example.movieapp.Request;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.movieapp.AppExecutors;
import com.example.movieapp.Model.MovieModel;
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
    private MutableLiveData<List<MovieModel>> mMovies, popularMovies, upcommingMovies, discoverMovies;
    private int totalResults;
    private RetrieveMoviesRunnable retrieveMoviesRunnable;
    private RetrieveMoviesRunnable retrievePopularMoviesRunnable;
    private RetrieveMoviesRunnable retrieveUpcommingMoviesRunnable;
    private RetrieveMoviesRunnable retrieveDiscoverMoviesRunnable;
    private RetrieveMoviesRunnable retrieveRecommendSearchMoviesRunnable;

    public static MovieApiClient getInstance() {
        if (instance == null) {
            instance = new MovieApiClient();
        }
        return instance;
    }

    public MovieApiClient() {
        mMovies = new MutableLiveData<>();
        popularMovies = new MutableLiveData<>();
        upcommingMovies = new MutableLiveData<>();
        discoverMovies = new MutableLiveData<>();
    }

    public MutableLiveData<List<MovieModel>> getMovies() {
        return mMovies;
    }
    public MutableLiveData<List<MovieModel>> getPopularMovies() {
        return popularMovies;
    }
    public MutableLiveData<List<MovieModel>> getUpcommingMovies() {
        return upcommingMovies;
    }
    public MutableLiveData<List<MovieModel>> getDiscoverMovies() {
        return discoverMovies;
    }
    public void searchMovieApi(String query, int page) {
        if (retrieveMoviesRunnable != null) {
            retrieveMoviesRunnable = null;
        }
        retrieveMoviesRunnable = new RetrieveMoviesRunnable(query, page);
        final Future myHandler = AppExecutors.getInstance().networkIO().submit(retrieveMoviesRunnable);

        AppExecutors.getInstance().networkIO().schedule(new Runnable() {
            @Override
            public void run() {
                // Hủy lời gọi Retrofit
                myHandler.cancel(true);
            }
        }, 10, TimeUnit.SECONDS);
    }

    public void searchFavorMovieApi(int page){
        if (retrievePopularMoviesRunnable != null) {
            retrievePopularMoviesRunnable = null;
        }
        retrievePopularMoviesRunnable = new RetrieveMoviesRunnable(2, page);
        final Future myHandler = AppExecutors.getInstance().networkIO().submit(retrievePopularMoviesRunnable);

        AppExecutors.getInstance().networkIO().schedule(new Runnable() {
            @Override
            public void run() {
                // Hủy lời gọi Retrofit
                myHandler.cancel(true);
            }
        }, 10, TimeUnit.SECONDS);
    }

    public void searchUpcommingMovieApi(int page){
        if(retrieveUpcommingMoviesRunnable !=null){
            retrieveUpcommingMoviesRunnable = null;
        }
        retrieveUpcommingMoviesRunnable = new RetrieveMoviesRunnable(4, page);
        final Future myHandler = AppExecutors.getInstance().networkIO().submit(retrieveUpcommingMoviesRunnable);

        AppExecutors.getInstance().networkIO().schedule(new Runnable() {
            @Override
            public void run() {
                // Hủy lời gọi Retrofit
                myHandler.cancel(true);
            }
        }, 10, TimeUnit.SECONDS);
    }

    public void discoverMovieApi(String genre_str, String country ,int year, int pageNumber){
        if(retrieveDiscoverMoviesRunnable != null) {
            retrieveDiscoverMoviesRunnable = null;
        }
        retrieveDiscoverMoviesRunnable = new RetrieveMoviesRunnable(genre_str, country, year, pageNumber);
        final Future myHandler = AppExecutors.getInstance().networkIO().submit(retrieveDiscoverMoviesRunnable);

        AppExecutors.getInstance().networkIO().schedule(new Runnable() {
            @Override
            public void run() {
                // Hủy lời gọi Retrofit
                myHandler.cancel(true);
            }
        }, 10, TimeUnit.SECONDS);
    }

    public void searchRecommendMovieApi(String search_text){
        if(retrieveRecommendSearchMoviesRunnable!=null){
            retrieveRecommendSearchMoviesRunnable = null;
        }
        retrieveRecommendSearchMoviesRunnable = new RetrieveMoviesRunnable("", 1);
        final Future myHandler = AppExecutors.getInstance().networkIO().submit( retrieveRecommendSearchMoviesRunnable);

        AppExecutors.getInstance().networkIO().schedule(new Runnable() {
            @Override
            public void run() {
                // Hủy lời gọi Retrofit
                myHandler.cancel(true);
            }
        }, 10, TimeUnit.SECONDS);
    }

    public int getTotalResults(){
        if(mMovies!=null){
            return totalResults;
        }else{
            return 0;
        }

    }

    // Lấy dữ liệu từ Retrofit bằng class Runnable
    // cần 2 loại truy vấn, theo ID và tiềm kiếm
    private class RetrieveMoviesRunnable implements Runnable {

        private int year;
        private String genre_str;
        private  String country;
        private String query;
        private int pageNumber;
        boolean cancelRequest;
        private int list_type = 0;


        public RetrieveMoviesRunnable(String query, int pageNumber) {
            this.query = query;
            this.pageNumber = pageNumber;
            this.cancelRequest = false;
        }

        public RetrieveMoviesRunnable(int list_type, int pageNumber) {
            this.list_type = list_type;
            this.pageNumber = pageNumber;
            this.cancelRequest = false;
        }

        public RetrieveMoviesRunnable(String genre_str, String country ,int year, int pageNumber){
            this.genre_str = genre_str;
            this.country = country;
            this.year =year;
            this.pageNumber = pageNumber;
        }

        @Override
        public void run() {
            Response response;
            try {
                // discover
                if(genre_str!=null){
                    response = getMovies(genre_str, country, year, pageNumber).execute();
                    parseDataIntoList(response, discoverMovies);
                }else{
                    //search
                    if (list_type == 0) {
                        response = getMovies(query, pageNumber).execute();
                        parseDataIntoList(response, mMovies);
                    } else {
                        // favor
                        response = getMovies(list_type, pageNumber).execute();
                        switch (list_type){
                            case  2 :
                                parseDataIntoList(response, popularMovies);
                                break;
                            case 4 :
                                parseDataIntoList(response, upcommingMovies);
                                break;
                        }
                    }
                }
                if (cancelRequest) {
                    cancelRequest();
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("movielist", "error");
            }
        }

        private void parseDataIntoList(Response response, MutableLiveData<List<MovieModel>> Movies){
            if (response.code() == 200) {
                List<MovieModel> list = new ArrayList<>(((MovieSearchResponse) response.body()).getMovies());
                if (pageNumber == 1) {
                    Movies.postValue(list);
                } else {
                    List currentList = Movies.getValue();
                    currentList.addAll(list);
                    Movies.postValue(currentList);
                }
                Log.i("movielist","finding 7");
                totalResults =((MovieSearchResponse) response.body()).total_results;
            } else {
                Log.v("parse data", "Fail to call response" + response.errorBody());
                Movies.postValue(null);
            }
        }

        private Call<MovieSearchResponse> getMovies(String query, int pageNumber) {
            return MyService.getMovieApi().searchMovie(Credentials.API_KEY, query, pageNumber);
        }

        private Call<MovieSearchResponse> getMovies(int list_type, int pageNumber) {
            Log.i("movielist","finding5");
            switch (list_type) {
                case 1:
                    Log.i("movielist","finding 6");
                    return MyService.getMovieApi().searchMoviesList(Credentials.BASE_URL + Credentials.NOW_PLAYING, Credentials.API_KEY, pageNumber);
                case 2:
                    return MyService.getMovieApi().searchMoviesList(Credentials.BASE_URL + Credentials.POPULAR, Credentials.API_KEY, pageNumber);
                case 3:
                    return MyService.getMovieApi().searchMoviesList(Credentials.BASE_URL + Credentials.TOP_RATED, Credentials.API_KEY, pageNumber);
                case 4:
                    return MyService.getMovieApi().searchMoviesList(Credentials.BASE_URL + Credentials.UPCOMING, Credentials.API_KEY, pageNumber);
            }
            return null;
        }
        private Call<MovieSearchResponse> getMovies(String genre_str, String country ,int year, int pageNumber) {
            return MyService.getMovieApi().discoverMovie(Credentials.API_KEY, genre_str, country, year, pageNumber);
        }


        private void cancelRequest() {
            Log.v("QUERY TASK", "Canceled request");
        }
    }
}
