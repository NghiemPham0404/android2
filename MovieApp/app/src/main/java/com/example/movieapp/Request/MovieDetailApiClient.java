package com.example.movieapp.Request;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.movieapp.AppExecutors;
import com.example.movieapp.Model.DetailModel;
import com.example.movieapp.Model.MovieModel;
import com.example.movieapp.Response.DetailResponse;
import com.example.movieapp.utils.Credentials;

import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Response;

public class MovieDetailApiClient {
    static MovieDetailApiClient  instance;
    private MutableLiveData<MovieModel> movie;
    private MutableLiveData<DetailModel> movie_detail;
    private MutableLiveData<List<DetailModel>> movie_reviews;
    RetrieveMovieRunnable retrieveMovieRunnable;
    RetrieveMovieRunnable retrieveMovieDetailRunnable;
    RetrieveMovieRunnable retrieveMovieReviewsRunnable;
    public static MovieDetailApiClient getInstance() {
        if (instance == null) {
            instance = new MovieDetailApiClient();
        }
        return instance;
    }

    public MovieDetailApiClient(){
        this.movie = new MutableLiveData<>();
        this.movie_detail = new MutableLiveData<>();
        this.movie_reviews = new MutableLiveData<>();
    }

    public MutableLiveData<MovieModel> getMovie(){
        return movie;
    }
    public MutableLiveData<DetailModel> getMovieDetail() {return  movie_detail;}
    public MutableLiveData<List<DetailModel>> getMovieReviews() {return movie_reviews;}

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

    public void searchMovieDetailApi(int id, String user_id, String function_name){
        if(retrieveMovieDetailRunnable!=null){
            retrieveMovieDetailRunnable=null;
        }
        retrieveMovieDetailRunnable = new RetrieveMovieRunnable(id, user_id, function_name);

        final Future myHandler = AppExecutors.getInstance().networkIO().submit(retrieveMovieDetailRunnable);
        AppExecutors.getInstance().networkIO().schedule(new Runnable() {
            @Override
            public void run() {
                // Hủy lời gọi Retrofit
                myHandler.cancel(true);
            }
        }, 10, TimeUnit.SECONDS);
    }

    public void changeFavor(int id, String user_id, String function_name){
        if(retrieveMovieDetailRunnable!=null){
            retrieveMovieDetailRunnable=null;
        }
        retrieveMovieDetailRunnable = new RetrieveMovieRunnable(id, user_id, function_name);
        final Future myHandler = AppExecutors.getInstance().networkIO().submit(retrieveMovieDetailRunnable);
        AppExecutors.getInstance().networkIO().schedule(new Runnable() {
            @Override
            public void run() {
                // Hủy lời gọi Retrofit
                myHandler.cancel(true);
            }
        }, 10, TimeUnit.SECONDS);
    }

    public void getReviews(int id){
        if(retrieveMovieReviewsRunnable!=null){
            retrieveMovieReviewsRunnable = null;
        }
        retrieveMovieReviewsRunnable = new RetrieveMovieRunnable(id, null, Credentials.functionname_detail);
        final Future myHandler = AppExecutors.getInstance().networkIO().submit(retrieveMovieReviewsRunnable);
        AppExecutors.getInstance().networkIO().schedule(new Runnable() {
            @Override
            public void run() {
                // Hủy lời gọi Retrofit
                myHandler.cancel(true);
            }
        }, 10, TimeUnit.SECONDS);
    }

    public void deleteReview(int id, String user_id, String function_name){
        if(retrieveMovieDetailRunnable!=null){
            retrieveMovieDetailRunnable=null;
        }
        retrieveMovieDetailRunnable = new RetrieveMovieRunnable(id, user_id, function_name);
        final Future myHandler = AppExecutors.getInstance().networkIO().submit(retrieveMovieDetailRunnable);
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
        public String user_id, function_name;
        public RetrieveMovieRunnable(int id){
            this.id = id;
            this.cancelRequest = false;
        }

        public RetrieveMovieRunnable(int id, String user_id, String function_name){
            this.id = id;
            this.user_id = user_id;
            this.function_name = function_name;
            this.cancelRequest = false;
        }

        @Override
        public void run() {
            try {
                if(function_name ==null){
                    Response response = getMovie().execute();
                    if(response.isSuccessful()){
                        MovieModel movieModel = ((Response<MovieModel>)response).body();
                        movie.postValue(movieModel);
                    }
                }else{
                    if(function_name.equalsIgnoreCase(Credentials.functionname_video)){
                        Response response = getMovieDetail().execute();
                        if(response.isSuccessful()){
                            DetailModel  detailModel = ((Response<DetailModel>) response).body();
                            movie_detail.postValue(detailModel);
                        }
                    }else if(function_name.equalsIgnoreCase(Credentials.functionname_detail)){
                        if(this.user_id!=null){
                            // change favor
                            Response response = changeFavor().execute();
                            if(response.isSuccessful()){
                                DetailModel  detailModel = ((Response<DetailModel>) response).body();
                                DetailModel currentDetailModel = movie_detail.getValue();
                                if(currentDetailModel == null){
                                    movie_detail.postValue(detailModel);
                                }else{
                                    currentDetailModel.setFavor(detailModel.isFavor());
                                    movie_detail.postValue(currentDetailModel);
                                }
                            }
                        }else{
                            // get review
                            Response response = getReviews().execute();
                            if(response.isSuccessful()){
                                List<DetailModel> detailModels = ((DetailResponse)response.body()).getAllReviews();
                                movie_reviews.postValue(detailModels);
                            }
                        }
                    }else if(function_name.equalsIgnoreCase(Credentials.functionname_delete_detail)){
                            Response response = deleteReview().execute();
                            if(response.isSuccessful()){
                                Log.i("delete review","success");
                            }else{
                                Log.e("delete review","fail");
                            }
                    }
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
                    "credits,videos,external_ids");
        }
        public Call<DetailModel> getMovieDetail(){
            return MyService2.getApi().getMovieVideo(function_name, id, user_id);
        }

        public Call<DetailModel> changeFavor(){
            return MyService2.getApi().addToFavor(function_name, user_id, id);
        }
        public Call<DetailResponse> getReviews(){
            return MyService2.getApi().getReviewByFilmId(function_name, id);
        }
        public Call<DetailModel>  deleteReview(){
            return MyService2.getApi().deleteReview(Credentials.functionname_delete_detail, user_id, id);
        }
    }
    private void cancelRequest() {
        Log.v("QUERY TASK", "Canceled request");
    }
}
