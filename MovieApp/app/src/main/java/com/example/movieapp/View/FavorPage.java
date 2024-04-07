package com.example.movieapp.View;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.example.movieapp.Adapters.FavorAdapter;
import com.example.movieapp.Interfaces.Fragment_Interface;
import com.example.movieapp.Model.AccountModel;
import com.example.movieapp.Model.DetailModel;
import com.example.movieapp.Model.MovieModel;
import com.example.movieapp.Model.VideoModel;
import com.example.movieapp.R;
import com.example.movieapp.Request.MyService;
import com.example.movieapp.Request.MyService2;
import com.example.movieapp.utils.Credentials;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FavorPage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FavorPage extends Fragment{

    private Button filter_btn, star_btn, sort_btn;
    int c_sort = 0;

    AccountModel loginAccount;

    PopupWindow filter_popup, rating_popup;

    FrameLayout layout;

    RecyclerView favorRecyclerView;
    ConstraintLayout loadingScreen;
    FavorAdapter favorAdapter;

    List<MovieModel> fav_movies;


    FavorAdapter.favor_interface favor_click = new FavorAdapter.favor_interface() {
        @Override
        public void openMovie(int movieId) {
            Intent openMovieIntent = new Intent(getContext(), Movie_infomation.class);
            openMovieIntent.putExtra("film_id", movieId);
            openMovieIntent.putExtra("userId", loginAccount.getUser_id());
            startActivity(openMovieIntent);
        }

        @Override
        public void playMovie(MovieModel movie, Button button) {
            Call<VideoModel> getVideoCall = MyService2.getApi().getMovieVideo(Credentials.functionname_video, movie.getId());
            getVideoCall.enqueue(new Callback<VideoModel>() {
                @Override
                public void onResponse(Call<VideoModel> call, Response<VideoModel> response) {
                        String video_link = response.body().getUrl();
                        if(!video_link.equalsIgnoreCase("")){
                            button.setBackgroundResource(R.drawable.gradient_corner_bg);
                            button.setText("watch now");
                            button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(getContext(), PlayingFilm.class);
                                    intent.putExtra("videoUrl",video_link);
                                    intent.putExtra("film_id", movie.getId());
                                    intent.putExtra("userId", loginAccount.getUser_id());
                                    intent.putExtra("movie_name", movie.getTitle());
                                    intent.putExtra("duration", movie.getDuration());
                                    startActivity(intent);
                                }
                            });
                        }else{
                            String trailer = movie.getTrailer();
                            if(trailer!=null){
                                button.setBackgroundResource(R.drawable.play_trailer_btn);
                                button.setText("play trailer");
                                button.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(getContext(), PlayingTrailer.class);
                                        intent.putExtra("video_string", trailer);
                                        startActivity(intent);
                                    }
                                });
                            }else{
                                button.setText("Unavailable");
                            }
                        }
                }

                @Override
                public void onFailure(Call<VideoModel> call, Throwable t) {
                    Log.e("GET VIDEO IN FAVOR ", " : failure");
                }
            });
        }
        @Override
        public void changeFavorite(int movieId) {
            Call<DetailModel> detailModelCall = MyService2.getApi().addToFavor("detail",  loginAccount.getUser_id(), movieId);
            detailModelCall.enqueue(new Callback<DetailModel>() {
                @Override
                public void onResponse(Call<DetailModel> call, Response<DetailModel> response) {
                    if (response.code() == 200) {

                    }
                }

                @Override
                public void onFailure(Call<DetailModel> call, Throwable t) {
                    Log.e("FAVOR TASK", "change favor fail");
                }
            });
        }
    };

    public FavorPage() {
    }

    // TODO: Rename and change types and number of parameters
    public static FavorPage newInstance(AccountModel loginAccount) {
        FavorPage fragment = new FavorPage();
        Bundle args = new Bundle();
        args.putParcelable("loginAccount", loginAccount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            loginAccount = (AccountModel) getArguments().get("loginAccount");
            Toast.makeText(getContext(), "userID : "+loginAccount.getUser_id(), Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getContext(), "agrs is null",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favor_page, container, false);
        initComponents(view);
        return view;
    }

    public void initComponents(View view){
        star_btn = view.findViewById(R.id.rating_filter_btn);
        sort_btn = view.findViewById(R.id.sort_btn);
        layout = view.findViewById(R.id.fav_layout);

        favorRecyclerView = view.findViewById(R.id.favorRecycleView);
        loadingScreen = view.findViewById(R.id.loadingLayout);
        initFeatures();
    }

    public void initFeatures(){
        loadingScreen.setVisibility(View.VISIBLE);
        fav_movies = new ArrayList<MovieModel>();
        favorAdapter = new FavorAdapter(getContext(), fav_movies, favor_click);
        Call<List<DetailModel>> favorListCall = MyService2.getApi().getFavorListByUserId("detail", loginAccount.getUser_id());
       favorListCall.enqueue(new Callback<List<DetailModel>>() {
           @Override
           public void onResponse(Call<List<DetailModel>> call, Response<List<DetailModel>> response) {
               if(response.code() == 200){
                   for(DetailModel movie : response.body()){
                        initListItem(movie.getMovieId(), movie.getDuration());
                   }
                   initList();
                   loadingScreen.setVisibility(View.GONE);
               }
           }

           @Override
           public void onFailure(Call<List<DetailModel>> call, Throwable t) {
                Log.e("FAVOR TASK", t.toString());
           }
       });
    }
    public void initList(){
        Log.e("ERROR adapter is null : " , " " + (favorAdapter == null) );
        favorRecyclerView.setAdapter(favorAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        favorRecyclerView.setLayoutManager(linearLayoutManager);
    }

    public void initListItem(int movieId, String duration){
        Call<MovieModel> movieCall = MyService.getMovieApi().searchMovieDetail(movieId, Credentials.API_KEY, "videos");
        movieCall.enqueue(new Callback<MovieModel>() {
            @Override
            public void onResponse(Call<MovieModel> call, Response<MovieModel> response) {
                MovieModel movie = response.body();
                movie.setDuration(duration);
                fav_movies.add(movie);
                favorAdapter.notifyDataSetChanged();
                Log.i("Favor movie : " , movie.getTitle());
            }

            @Override
            public void onFailure(Call<MovieModel> call, Throwable t) {
                Log.i("FAVOR TASK", "Fail to add movie : " + t.toString());
            }
        });
    }

}