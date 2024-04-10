package com.example.movieapp.View;


import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.movieapp.Adapters.MoviesGroupAdapter;
import com.example.movieapp.Interfaces.Fragment_Interface;
import com.example.movieapp.Model.AccountModel;
import com.example.movieapp.Model.MovieModel;
import com.example.movieapp.Model.MoviesGroup;
import com.example.movieapp.R;
import com.example.movieapp.Request.MyService;
import com.example.movieapp.Response.MovieSearchResponse;
import com.example.movieapp.ViewModel.MovieListViewModel;
import com.example.movieapp.utils.Credentials;
import com.example.movieapp.utils.MovieApi;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomePage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomePage extends Fragment{

    private static final int REQUEST_CODE_SPEECH_INPUT = 100;
    private static final int PERMISSION_REQUEST_RECORD_AUDIO = 99;
    RecyclerView moviesGroupsRecyclerView;
    Button searchBtn,textToSpeechBtn;;
    EditText searchBox;

    List<MoviesGroup> moviesGroups;
    MoviesGroupAdapter moviesGroupAdapter;

    LinearLayout divSearch;

    // LIVE data
    MovieListViewModel movieList_search_ViewModel, movieList_popular_ViewModel, movieList_nowPlaying_ViewModel, movieList_topRated_ViewModel,
    movieList_upcoming_ViewModel;

    AccountModel loginAccount;
    int page = 1;

    private LinearLayout layout;

    public HomePage() {
        // Required empty public constructor
    }

    public static HomePage newInstance(AccountModel loginAccount) {
        HomePage fragment = new HomePage();
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
            Toast.makeText(getContext(), loginAccount.getUser_id(), Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getContext(), "Agrs is null", Toast.LENGTH_SHORT).show();
        }
        movieList_search_ViewModel =new ViewModelProvider(this).get(MovieListViewModel.class);
        ObserveAnyChanges();
    }

    public void ObserveAnyChanges(){
        if(movieList_search_ViewModel.getMovies() == null){
            Log.e("TAG", "ERROR");
        }
        if(movieList_search_ViewModel !=null){
            movieList_search_ViewModel.getMovies().observe(this, new Observer<List<MovieModel>>() {
                @Override
                public void onChanged(List<MovieModel> movieModels) {
                    if(movieModels!=null){
                        moviesGroups = new ArrayList<>();
                        List<MovieModel> movies = movieList_search_ViewModel.getMovies().getValue();

                        String search_title = (movies.size()> 1)? movies.size()+" results has been found" :
                                                            0 + " result has been found";

                        MoviesGroup  movieGroup = new MoviesGroup(movies, search_title);
                        moviesGroups.add(movieGroup);

                        moviesGroupAdapter = new MoviesGroupAdapter(getContext(), moviesGroups, loginAccount);
                        moviesGroupsRecyclerView.setAdapter(moviesGroupAdapter);

                        for(MovieModel movieModel : movieModels){
                            Log.v("Tagv", "On change" + movieModel.getTitle());
                        }
                    }
                }
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_home_page, container, false);
        initComponents(view);
        configureSearchRecycleView();
        searchBox.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    divSearch.setBackgroundResource(R.drawable.text_input_onfocus);
                }else{
                    divSearch.setBackgroundResource(R.drawable.text_input);
                }
            }
        });

        initFeatures();
        return view;
    }

    public void initComponents(View view){
        moviesGroupsRecyclerView = view.findViewById(R.id.movie_groups_recycleview);
        searchBox = view.findViewById(R.id.searchBox);
        searchBtn = view.findViewById(R.id.searchBtn);
        textToSpeechBtn = view.findViewById(R.id.mirco_btn);
        divSearch = view.findViewById(R.id.searchDiv);
        layout = view.findViewById(R.id.home_layout);;
    }

    public void initFeatures(){
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchBox.clearFocus();
                searchMovie(searchBox.getText().toString().trim(), page);
                divSearch.setBackgroundResource(R.drawable.text_input);
            }
        });

        searchBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchBox.clearFocus();
                    searchMovie(searchBox.getText().toString().trim(), page);
                    divSearch.setBackgroundResource(R.drawable.text_input);
                    // giấu bàn phím
                    hideKeyboard();
                    return true;
                }
                return false;
            }
        });

        textToSpeechBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textToSpeechStart();
            }
        });

        MovieApi movieApi = MyService.getMovieApi();

        // Gọi các danh sách film
        Call<MovieSearchResponse> NowPlayingResponseCall =
                movieApi.searchMoviesList(Credentials.BASE_URL
                        +Credentials.NOW_PLAYING, Credentials.API_KEY, 1);

        Call<MovieSearchResponse>PopularResponseCall =
                movieApi.searchMoviesList(Credentials.BASE_URL
                        +Credentials.POPULAR, Credentials.API_KEY, 1);

        Call<MovieSearchResponse> TopRatedResponseCall =
                movieApi.searchMoviesList(Credentials.BASE_URL
                        +Credentials.TOP_RATED, Credentials.API_KEY, 1);

        Call<MovieSearchResponse>UpcomingResponseCall =
                movieApi.searchMoviesList(Credentials.BASE_URL
                        +Credentials.UPCOMING, Credentials.API_KEY, 1);

        moviesGroups = new ArrayList<>();

        onResponseMovieList(NowPlayingResponseCall, "Now playing");
        onResponseMovieList(PopularResponseCall, "Popular");
        onResponseMovieList(TopRatedResponseCall, "Top rated");
        onResponseMovieList(UpcomingResponseCall, "Upcoming");


        moviesGroupAdapter = new MoviesGroupAdapter(getContext(), moviesGroups, loginAccount);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        moviesGroupsRecyclerView.setLayoutManager(linearLayoutManager);
        moviesGroupsRecyclerView.setAdapter(moviesGroupAdapter);
    }

    public void onResponseMovieList(Call<MovieSearchResponse> responseCall, String group_title){
        responseCall.enqueue(new Callback<MovieSearchResponse>() {
            @Override
            public void onResponse(Call<MovieSearchResponse> call, Response<MovieSearchResponse> response) {
                if(response.code() == 200){
                    List<MovieModel> movies = new ArrayList<>(response.body().getMovies());
                    for(MovieModel movieModel : movies){
                        Log.v("Tag", Credentials.BASE_IMAGE_URL +  movieModel.getBackgrop_path());
                    }
                    String title = group_title;

                    if(title.equalsIgnoreCase("search")){
                        title = (movies.size()==1||movies.size()==0)
                                ?movies.size()+ " result has been found with \" "+ searchBox.getText().toString().trim() +"\""
                                :movies.size()+" results has been found with \" "+ searchBox.getText().toString().trim() +"\"";
                    }

                    MoviesGroup  movieGroup = new MoviesGroup(movies, title);
                    moviesGroups.add(movieGroup);

                    Collections.sort(moviesGroups, (o1, o2) -> o1.compareTo(o2));
                    moviesGroupAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<MovieSearchResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Failure", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void searchMovie(String query, int page){
        movieList_search_ViewModel.searchMovieApi(query, page);
    }

    public void configureSearchRecycleView(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        moviesGroupsRecyclerView.setLayoutManager(linearLayoutManager);
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm!=null){
            imm.hideSoftInputFromWindow(searchBox.getWindowToken(), 0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_RECORD_AUDIO) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startRecord();
            } else {

            }
        }
    }

    private void textToSpeechStart(){
        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) getContext(), new String[]{android.Manifest.permission.RECORD_AUDIO},
                    PERMISSION_REQUEST_RECORD_AUDIO);
        } else {
            startRecord();
        }
    }

    public void startRecord(){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak something...");

        startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                if (result != null && result.size() > 0) {
                    String recognizedText = result.get(0);
                    searchBox.setText(recognizedText);
                    searchBtn.performClick();
                }
            }
        }
    }
}