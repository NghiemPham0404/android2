package com.example.movieapp.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.compose.ui.text.input.ImeAction;
import androidx.compose.ui.text.input.ImeOptions;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.movieapp.Adapters.CastAdapter;
import com.example.movieapp.Adapters.FilmAdapter;
import com.example.movieapp.Adapters.MovieSearchAdapter;
import com.example.movieapp.Interfaces.Edittext_interface;
import com.example.movieapp.Model.AccountModel;
import com.example.movieapp.Model.CastModel;
import com.example.movieapp.Model.MovieModel;
import com.example.movieapp.R;
import com.example.movieapp.ViewModel.MovieListViewModel;
import com.example.movieapp.ViewModel.PersonViewModel;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class SearchPage extends AppCompatActivity implements Edittext_interface {
    EditText search_box;
    Button search_btn;
    ImageButton back_btn;
    TextView search_movie_tilte;
    RecyclerView search_movie_recycler, people_recycler;
    String search_text;
    AccountModel loginAccount;

    // ViewModel
    MovieListViewModel movieListSearchViewModel;
    PersonViewModel personViewModel;

    //Adapters
    MovieSearchAdapter movieSearchAdapter;
    CastAdapter peopleAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_page);
        loginAccount = getIntent().getParcelableExtra("loginAccount");
        initViewModel();
        initComponents();
        initFeature();
        ObserveAnyChange();
    }

    public void ObserveAnyChange(){
        if (movieListSearchViewModel != null) {
            movieListSearchViewModel.getMovies().observe(this, new Observer<List<MovieModel>>() {
                @Override
                public void onChanged(List<MovieModel> movieModels) {
                    if (movieModels != null) {
                       movieSearchAdapter.setMovies(movieModels);
                       movieSearchAdapter.notifyDataSetChanged();

                        String search_title = (movieModels.size() > 1) ? movieModels.size() + "-" + movieListSearchViewModel.getTotalResults() + " results has been found" :
                                0 + " result has been found";
                        search_movie_tilte.setText(search_title);
                    }
                }
            });
        }

        if(personViewModel != null){
            personViewModel.getPeople().observe(this, new Observer<List<CastModel>>() {
                @Override
                public void onChanged(List<CastModel> castModels) {
                    if(castModels!=null){
                        peopleAdapter.setCasts(castModels);
                        peopleAdapter.notifyDataSetChanged();
                    }else{
                        Toast.makeText(getApplicationContext(), "people is null ", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public void initViewModel(){
        movieListSearchViewModel = new ViewModelProvider(this).get(MovieListViewModel.class);
        personViewModel = new ViewModelProvider(this).get(PersonViewModel.class);
    }

    public void initComponents(){
        back_btn = findViewById(R.id.back_btn);
        search_box = findViewById(R.id.searchBox);
        search_btn = findViewById(R.id.searchBtn);
        search_movie_tilte = findViewById(R.id.movie_result_title);

        search_movie_recycler = findViewById(R.id.movie_search_recycler);
        people_recycler = findViewById(R.id.person_search_recycler);
        configurationRecycler();
    }

    public void configurationRecycler(){
        movieSearchAdapter = new MovieSearchAdapter(this, null, loginAccount);
        peopleAdapter = new CastAdapter(this,  loginAccount);

        search_movie_recycler.setLayoutManager(new LinearLayoutManager(this));
        search_movie_recycler.setAdapter(movieSearchAdapter);

        people_recycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        people_recycler.setAdapter(peopleAdapter);
    }

    public void initFeature(){
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                overridePendingTransition(0, 0);
            }
        });
        search_box.requestFocus();
        search_box.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId ==  EditorInfo.IME_ACTION_SEARCH){
                    search();
                    return true;
                }
                return false;
            }
        });
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search();
            }
        });
    }

    public void search(){
        search_text = search_box.getText().toString().trim();
        movieListSearchViewModel.searchMovieApi(search_text, 1);
        personViewModel.searchPeople(search_text, 1);
    }
    public void search_movie_next(){
        movieListSearchViewModel.searchMovieApiNextPage();
    }
    public void search_people_next(){
        personViewModel.searchPeopleNext();
    }
    @Override
    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(search_box.getWindowToken(), 0);
        }
    }
}