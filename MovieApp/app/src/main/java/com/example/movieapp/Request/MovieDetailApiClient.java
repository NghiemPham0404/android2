package com.example.movieapp.Request;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.movieapp.AppExecutors;
import com.example.movieapp.Model.DetailModel;
import com.example.movieapp.Model.MovieModel;
import com.example.movieapp.Response.DetailResponse;
import com.example.movieapp.utils.Credentials;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Response;

public class MovieDetailApiClient {
    static MovieDetailApiClient instance;
    private MutableLiveData<MovieModel> movie;
    private MutableLiveData<DetailModel> movie_detail;
    private MutableLiveData<List<DetailModel>> movie_reviews;
    private MutableLiveData<List<DetailModel>> favor_history_movies;
    RetrieveMovieRunnable retrieveMovieRunnable;
    RetrieveMovieRunnable retrieveMovieDetailRunnable;
    RetrieveMovieRunnable retrieveMovieReviewsRunnable;
    RetrieveMovieRunnable retrieveFavHisMovieRunnable;

    public static MovieDetailApiClient getInstance() {
        if (instance == null) {
            instance = new MovieDetailApiClient();
        }
        return instance;
    }

    public MovieDetailApiClient() {
        this.movie = new MutableLiveData<>();
        this.movie_detail = new MutableLiveData<>();
        this.movie_reviews = new MutableLiveData<>();
        this.favor_history_movies = new MutableLiveData<>();
    }

    public MutableLiveData<MovieModel> getMovie() {
        return movie;
    }

    public MutableLiveData<DetailModel> getMovieDetail() {
        return movie_detail;
    }

    public MutableLiveData<List<DetailModel>> getMovieReviews() {
        return movie_reviews;
    }

    public MutableLiveData<List<DetailModel>> getFavHisMovies() {
        return favor_history_movies;
    }

    public void searchMovieApi(int id) {
        if (retrieveMovieRunnable != null) {
            retrieveMovieRunnable = null;
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

    public void searchMovieDetailApi(int id, String user_id) {
        if (retrieveMovieDetailRunnable != null) {
            retrieveMovieDetailRunnable = null;
        }
        retrieveMovieDetailRunnable = new RetrieveMovieRunnable(id, user_id, Credentials.functionname_video);

        final Future myHandler = AppExecutors.getInstance().networkIO().submit(retrieveMovieDetailRunnable);
        AppExecutors.getInstance().networkIO().schedule(new Runnable() {
            @Override
            public void run() {
                // Hủy lời gọi Retrofit
                myHandler.cancel(true);
            }
        }, 10, TimeUnit.SECONDS);
    }

    public void changeFavor(int id, String user_id) {
        if (retrieveMovieDetailRunnable != null) {
            retrieveMovieDetailRunnable = null;
        }
        retrieveMovieDetailRunnable = new RetrieveMovieRunnable(id, user_id, Credentials.functionname_detail);
        final Future myHandler = AppExecutors.getInstance().networkIO().submit(retrieveMovieDetailRunnable);
        AppExecutors.getInstance().networkIO().schedule(new Runnable() {
            @Override
            public void run() {
                // Hủy lời gọi Retrofit
                myHandler.cancel(true);
            }
        }, 10, TimeUnit.SECONDS);
    }

    public void getReviews(int id) {
        if (retrieveMovieReviewsRunnable != null) {
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

    public void deleteReview(int id, String user_id) {
        if (retrieveMovieReviewsRunnable != null) {
            retrieveMovieReviewsRunnable = null;
        }
        retrieveMovieReviewsRunnable = new RetrieveMovieRunnable(id, user_id, Credentials.functionname_delete_detail);
        final Future myHandler = AppExecutors.getInstance().networkIO().submit(retrieveMovieReviewsRunnable);
        AppExecutors.getInstance().networkIO().schedule(new Runnable() {
            @Override
            public void run() {
                // Hủy lời gọi Retrofit
                myHandler.cancel(true);
            }
        }, 10, TimeUnit.SECONDS);
    }

    public void postReview(int id, String user_id, String rating, String review) {
        if (retrieveMovieReviewsRunnable != null) {
            retrieveMovieReviewsRunnable = null;
        }
        retrieveMovieReviewsRunnable = new RetrieveMovieRunnable(id, user_id, rating, review, Credentials.functionname_detail);
        final Future myHandler = AppExecutors.getInstance().networkIO().submit(retrieveMovieReviewsRunnable);
        AppExecutors.getInstance().networkIO().schedule(new Runnable() {
            @Override
            public void run() {
                // Hủy lời gọi Retrofit
                myHandler.cancel(true);
            }
        }, 10, TimeUnit.SECONDS);
    }

    public void searchFavHisMovies(String user_id) {
        if (retrieveFavHisMovieRunnable != null) {
            retrieveFavHisMovieRunnable = null;
        }
        retrieveFavHisMovieRunnable = new RetrieveMovieRunnable(user_id, Credentials.functionname_detail);
        final Future myHandler = AppExecutors.getInstance().networkIO().submit(retrieveFavHisMovieRunnable);
        AppExecutors.getInstance().networkIO().schedule(new Runnable() {
            @Override
            public void run() {
                // Hủy lời gọi Retrofit
                myHandler.cancel(true);
            }
        }, 10, TimeUnit.SECONDS);
    }

    public void searchHisMovies(String user_id) {
        if (retrieveFavHisMovieRunnable != null) {
            retrieveFavHisMovieRunnable = null;
        }
        retrieveFavHisMovieRunnable = new RetrieveMovieRunnable(user_id, Credentials.functionname_history);
        final Future myHandler = AppExecutors.getInstance().networkIO().submit(retrieveFavHisMovieRunnable);
        AppExecutors.getInstance().networkIO().schedule(new Runnable() {
            @Override
            public void run() {
                // Hủy lời gọi Retrofit
                myHandler.cancel(true);
            }
        }, 10, TimeUnit.SECONDS);
    }


    private class RetrieveMovieRunnable implements Runnable {
        int id = -1;
        public boolean cancelRequest;
        public String user_id, function_name, rating, review;

        public RetrieveMovieRunnable(int id) {
            this.id = id;
            this.cancelRequest = false;
        }

        public RetrieveMovieRunnable(int id, String user_id, String function_name) {
            this.id = id;
            this.user_id = user_id;
            this.function_name = function_name;
            this.cancelRequest = false;
        }

        public RetrieveMovieRunnable(int id, String user_id, String rating, String review, String function_name) {
            this.id = id;
            this.user_id = user_id;
            this.rating = rating;
            this.review = review;
            this.function_name = function_name;
        }

        public RetrieveMovieRunnable(String user_id, String function_name) {
            this.user_id = user_id;
            this.function_name = function_name;
        }

        @Override
        public void run() {
            try {
                if (id>0) {
                    Log.i("Get movie detail", "id > 0");
                    if (user_id != null) {
                        Log.i("Get movie detail", "user id > 0");
                        // get movie video
                        if (function_name.equalsIgnoreCase(Credentials.functionname_video)) {
                            Log.i("Get movie detail", "function = movies");
                            Response response = getMovieDetail().execute();
                            if (response.isSuccessful()) {
                                DetailModel detailModel = ((Response<DetailModel>) response).body();
                                movie_detail.postValue(detailModel);
                            }
                        } else if (function_name.equalsIgnoreCase(Credentials.functionname_detail)) {
                            Log.i("Get movie detail", "function  = detail");
                            if (rating != null) {
                                Log.i("Get movie detail", "rating !=null");
                                // add review
                                Response response = postReview().execute();
                                if (response.isSuccessful()) {
                                    DetailModel my_review = ((Response<DetailModel>) response).body();
                                    List<DetailModel> currentReviews = movie_reviews.getValue();
                                    currentReviews.add(my_review);
                                    movie_reviews.postValue(currentReviews);
                                    Log.i("post review", "success");
                                }
                            } else {
                                // change favor
                                DetailModel currentDetailModel = movie_detail.getValue();
                                if (currentDetailModel != null) {
                                    currentDetailModel.setFavor(!currentDetailModel.isFavor());
                                    movie_detail.postValue(currentDetailModel);
                                    List<DetailModel> currentFavHisList = favor_history_movies.getValue();
                                    boolean flag = false;
                                    for(int i = 0; i<currentFavHisList.size(); i++){
                                        if(currentFavHisList.get(i).getMovieId() == currentDetailModel.getMovieId()){
                                            currentFavHisList.remove(i);
                                            i--;
                                            flag = true;
                                        }
                                    }
                                    if(!flag){
                                        currentFavHisList.add(currentDetailModel);
                                    }
                                    favor_history_movies.postValue(currentFavHisList);
                                }
                                Response response = changeFavor().execute();
                                if (response.isSuccessful()) {
                                    Log.i("change favor", "success");
                                }
                            }
                        }
                    } else {
                        if(function_name == null) {
                            Log.i("Get movie detail", "functionname = null");
                            // get movie info
                            Response response = getMovie().execute();
                            if (response.isSuccessful()) {
                                MovieModel movieModel = ((Response<MovieModel>) response).body();
                                movie.postValue(movieModel);
                                Log.i("Get movie detail", "movie info notnull");
                            }
                        }else if (function_name.equalsIgnoreCase(Credentials.functionname_detail)) {
                            // get reviews
                            Log.i("Get movie detail", "get reviews");
                            Response response = getReviews().execute();
                            if (response.isSuccessful()) {
                                List<DetailModel> detailModels = ((DetailResponse) response.body()).getAllReviews();
                                movie_reviews.postValue(detailModels);
                            }
                        }
                    }
                } else {
                    if (user_id != null) {
                        if (function_name.equalsIgnoreCase(Credentials.functionname_detail)) {
                            //get history, favor list
                            Response reponse = getFavorHistoryList().execute();
                            if (reponse.isSuccessful()) {
                                List<DetailModel> favHisList = ((Response<List<DetailModel>>) reponse).body();
                                favor_history_movies.postValue(favHisList);
                                Log.i("Get history favor list", "sụcesses");
                            }
                        } else if (function_name.equalsIgnoreCase(Credentials.functionname_delete_detail)) {
                            // delete review
                            Response response = deleteReview().execute();
                            if (response.isSuccessful()) {
                                Log.i("delete review", "success");
                            }
                        }else if(function_name.equalsIgnoreCase(Credentials.functionname_history)){
                            //get history, favor list
                            Response reponse = getHistoryList().execute();
                            if (reponse.isSuccessful()) {
                                List<DetailModel> favHisList = ((Response<List<DetailModel>>) reponse).body();
                                favor_history_movies.postValue(favHisList);
                                Log.i("Get history favor list", "sucesses");
                            }
                        }
                    }
                }
//                //---------------------------------------------------------------------
//                // get movie detail info
//                if (function_name == null) {
//                    Response response = getMovie().execute();
//                    if (response.isSuccessful()) {
//                        MovieModel movieModel = ((Response<MovieModel>) response).body();
//                        movie.postValue(movieModel);
//                    }
//                } else {
//                    // get movie videos
//                    if (function_name.equalsIgnoreCase(Credentials.functionname_video)) {
//                        Response response = getMovieDetail().execute();
//                        if (response.isSuccessful()) {
//                            DetailModel detailModel = ((Response<DetailModel>) response).body();
//                            movie_detail.postValue(detailModel);
//                        }
//                    } else if (function_name.equalsIgnoreCase(Credentials.functionname_detail)) {
//                        if (this.user_id != null) {
//                            if (rating == null) {
//                                // change favor
//                                Response response = changeFavor().execute();
//                                if (response.isSuccessful()) {
//                                    DetailModel detailModel = ((Response<DetailModel>) response).body();
//                                    DetailModel currentDetailModel = movie_detail.getValue();
//                                    if (currentDetailModel == null) {
//                                        movie_detail.postValue(detailModel);
//                                    } else {
//                                        currentDetailModel.setFavor(detailModel.isFavor());
//                                        movie_detail.postValue(currentDetailModel);
//                                    }
//                                }
//                            } else {
//                                if (!(id + "").equalsIgnoreCase("")) {
//                                    // add review
//                                    Response response = postReview().execute();
//                                    if (response.isSuccessful()) {
//                                        DetailModel my_review = ((Response<DetailModel>) response).body();
//                                        List<DetailModel> currentReviews = movie_reviews.getValue();
//                                        currentReviews.add(my_review);
//                                        movie_reviews.postValue(currentReviews);
//                                        Log.i("post review", "success");
//                                    } else {
//                                        Log.i("post review", "fail");
//                                    }
//                                } else {
//                                    //get history, favor list
//                                    Response reponse = getFavorHistoryList().execute();
//                                    if (reponse.isSuccessful()) {
//                                        List<DetailModel> favHisList = ((Response<List<DetailModel>>) reponse).body();
//                                        favor_history_movies.postValue(favHisList);
//                                        Log.i("Get history favor list", "sụcesses");
//                                    } else {
//                                        Log.i("Get history favor list", "fail");
//                                    }
//                                }
//                            }
//                        }else {
//
//                        }
//                    } else if (function_name.equalsIgnoreCase(Credentials.functionname_delete_detail)) {
//                        // delete review
//                        Response response = deleteReview().execute();
//                        if (response.isSuccessful()) {
//                            Log.i("delete review", "success");
//                        } else {
//                            Log.e("delete review", "fail");
//                        }
//                    }
//                }

                if (cancelRequest) {
                    cancelRequest();
                    return;
                }
            } catch (Exception e) {
                Log.i("Get movie detail", "fail : " + e);
            }
        }

        public Call<MovieModel> getMovie() {
            return MyService.getMovieApi().searchMovieDetail(id, Credentials.API_KEY,
                    "credits,videos,external_ids", Locale.getDefault().getLanguage());
        }

        public Call<DetailModel> getMovieDetail() {
            return MyService2.getApi().getMovieVideo(function_name, id, user_id);
        }

        public Call<DetailModel> changeFavor() {
            return MyService2.getApi().addToFavor(function_name, user_id, id);
        }

        public Call<DetailResponse> getReviews() {
            return MyService2.getApi().getReviewByFilmId(function_name, id);
        }

        public Call<DetailModel> deleteReview() {
            this.review = null;
            this.rating = null;
            return MyService2.getApi().deleteReview(Credentials.functionname_delete_detail, user_id, id);
        }

        public Call<DetailModel> postReview() {
            return MyService2.getApi().addReview(Credentials.functionname_detail, user_id, id, rating, review);
        }

        public Call<List<DetailModel>> getFavorHistoryList() {
            return MyService2.getApi().getFavorListByUserId(Credentials.functionname_detail, user_id);
        }
        public Call<List<DetailModel>> getHistoryList(){
            return MyService2.getApi().getFavorListByUserId(Credentials.functionname_history, user_id);
        }
    }

    private void cancelRequest() {
        Log.v("QUERY TASK", "Canceled request");
    }
}
