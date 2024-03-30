package com.example.movieapp.View;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.example.movieapp.Adapters.MoviesGroupAdapter;
import com.example.movieapp.Interfaces.Fragment_Interface;
import com.example.movieapp.Model.MovieModel;
import com.example.movieapp.Model.MoviesGroup;
import com.example.movieapp.R;
import com.example.movieapp.Request.MyService;
import com.example.movieapp.Response.MovieSearchResponse;
import com.example.movieapp.ViewModel.MovieListViewModel;
import com.example.movieapp.utils.Credentials;
import com.example.movieapp.utils.MovieApi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomePage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomePage extends Fragment implements Fragment_Interface {

    RecyclerView moviesGroupsRecyclerView;
    Button searchBtn;
    EditText searchBox;

    List<MoviesGroup> moviesGroups;
    MoviesGroupAdapter moviesGroupAdapter;

    LinearLayout divSearch, filter_bar;

    // LIVE data
    MovieListViewModel movieListViewModel;

    // Filter
    private Button filter_btn, star_btn, sort_btn;
    int c_sort = 0;
    PopupWindow filter_popup, rating_popup;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";



    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private LinearLayout layout;

    public HomePage() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomePage.
     */
    // TODO: Rename and change types and number of parameters
    public static HomePage newInstance(String param1, String param2) {
        HomePage fragment = new HomePage();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        movieListViewModel =new ViewModelProvider(this).get(MovieListViewModel.class);
    }

    public void ObserveAnyChanges(){
        movieListViewModel.getMovies().observe(this, new Observer<List<MovieModel>>() {
            @Override
            public void onChanged(List<MovieModel> movieModels) {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_home_page, container, false);
        initComponents(view);
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
        divSearch = view.findViewById(R.id.searchDiv);
        filter_bar = view.findViewById(R.id.filter_bar);
        filter_btn = view.findViewById(R.id.filter_btn);
        star_btn = view.findViewById(R.id.rating_filter_btn);
        sort_btn = view.findViewById(R.id.sort_btn);
        layout = view.findViewById(R.id.home_layout);;
        initFilterBar();
    }

    public void initFeatures(){
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchMovie();
                divSearch.setBackgroundResource(R.drawable.text_input);
            }
        });

        MovieApi movieApi = MyService.getMovieApi();

        // Gọi các danh sách film
        Call<MovieSearchResponse> NowPlayingResponseCall =
                movieApi.searchMoviesList(Credentials.BASE_URL
                        +Credentials.NOW_PLAYING, Credentials.API_KEY);

        Call<MovieSearchResponse>PopularResponseCall =
                movieApi.searchMoviesList(Credentials.BASE_URL
                        +Credentials.POPULAR, Credentials.API_KEY);

        Call<MovieSearchResponse> TopRatedResponseCall =
                movieApi.searchMoviesList(Credentials.BASE_URL
                        +Credentials.TOP_RATED, Credentials.API_KEY);

        Call<MovieSearchResponse>UpcomingResponseCall =
                movieApi.searchMoviesList(Credentials.BASE_URL
                        +Credentials.UPCOMING, Credentials.API_KEY);

        moviesGroups = new ArrayList<>();

        onResponseMovieList(NowPlayingResponseCall, "Now playing");
        onResponseMovieList(PopularResponseCall, "Popular");
        onResponseMovieList(TopRatedResponseCall, "Top rated");
        onResponseMovieList(UpcomingResponseCall, "Upcoming");


        moviesGroupAdapter = new MoviesGroupAdapter(getContext(), moviesGroups);
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

    public void searchMovie(){
        if(!this.searchBox.getText().toString().trim().equalsIgnoreCase("")){
            MovieApi movieApi = MyService.getMovieApi();
            Call<MovieSearchResponse> searchCall = movieApi.searchMovie(
                    Credentials.API_KEY,
                    this.searchBox.getText().toString(),
                    1);
            moviesGroups = new ArrayList<>();
            onResponseMovieList(searchCall, "search");

            moviesGroupAdapter = new MoviesGroupAdapter(getContext(), moviesGroups);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            moviesGroupsRecyclerView.setLayoutManager(linearLayoutManager);
            moviesGroupsRecyclerView.setAdapter(moviesGroupAdapter);
            filter_bar.setVisibility(View.VISIBLE);
        }else{
            Toast.makeText(getContext(), "Please input searching text", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void initFilterBar(){

        filter_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initFilterPopup();
            }
        });

        star_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initRatingPopup();
            }
        });
    }

    @Override
    public void initFilterPopup(){
        if(filter_popup == null){
            LayoutInflater inflater = (LayoutInflater)   this.getContext().getSystemService(getContext().LAYOUT_INFLATER_SERVICE);
            View popUpFilter = inflater.inflate(R.layout.filter_search_popup, null);

            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            boolean focusable = true;
            filter_popup= new PopupWindow(popUpFilter, width, height, focusable);
            filter_popup.setAnimationStyle(R.style.PopupAnimation);
        }
        layout.post(new Runnable() {
            @Override
            public void run() {
                filter_popup.showAtLocation(layout, Gravity.TOP, 0,450);
            }
        });
    }

    @Override
    public void initRatingPopup(){
        if(rating_popup == null){
            LayoutInflater inflater = (LayoutInflater)   this.getContext().getSystemService(getContext().LAYOUT_INFLATER_SERVICE);
            View popUpFilter = inflater.inflate(R.layout.filter_starbar, null);
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            boolean focusable = true;
            rating_popup= new PopupWindow(popUpFilter, width, height, focusable);
            rating_popup.setAnimationStyle(R.style.PopupAnimation);
        }
        layout.post(new Runnable() {
            @Override
            public void run() {
                rating_popup.showAtLocation(layout, Gravity.TOP, 0,450);
            }
        });
    }

    @Override
    public void initSort(){
        sort_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    @Override
    public void sortBtnChange(int n_sort) {
        if(c_sort == 0){
            c_sort = 1;
            sort_btn.setBackgroundResource(R.drawable.toggle_stroke);
        }else if(c_sort == 1){
            c_sort = 2;
            sort_btn.setBackgroundResource(R.drawable.neon_blue_corner);
        }else{
            c_sort =0;
            sort_btn.setBackgroundResource(R.drawable.nomal_stroke);
        }
    }
}