package com.example.movieapp.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import com.example.movieapp.Adapters.MovieSearchAdapter;
import com.example.movieapp.Adapters.SearchRecommendAdapter;
import com.example.movieapp.Interfaces.Edittext_interface;
import com.example.movieapp.Model.AccountModel;
import com.example.movieapp.Model.PersonModel;
import com.example.movieapp.Model.MovieModel;
import com.example.movieapp.R;
import com.example.movieapp.Request.SearchRecommendRequest;
import com.example.movieapp.ViewModel.MovieListViewModel;
import com.example.movieapp.ViewModel.PersonViewModel;

import java.util.ArrayList;
import java.util.List;

public class SearchPage extends AppCompatActivity implements Edittext_interface {
    EditText search_box;
    Button search_btn;
    private TextView delete_search_history_btn;
    ImageButton back_btn;
    TextView search_movie_tilte;
    RecyclerView search_movie_recycler, people_recycler, recommend_search_recycler;
    String search_text;
    AccountModel loginAccount;

    // ViewModel
    MovieListViewModel movieListSearchViewModel;
    PersonViewModel personViewModel;

    //Adapters
    MovieSearchAdapter movieSearchAdapter;
    CastAdapter peopleAdapter;
    SearchRecommendAdapter searchRecommendAdapter;
    List<String> search_history_texts;

    // Khi chọn 1 item trong danh sách recommend
    SearchRecommendAdapter.OnItemClick onItemClick = new SearchRecommendAdapter.OnItemClick() {
        @Override
        public void itemClick(String search_txt) {
            search(search_txt);
            showSearchHistory(false);
            search_box.setText(search_txt);
        }
    };


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
        // hiển thị kết quả tìm kiếm phim
        if (movieListSearchViewModel != null) {
            movieListSearchViewModel.getMovies().observe(this, new Observer<List<MovieModel>>() {
                @Override
                public void onChanged(List<MovieModel> movieModels) {
                    if (movieModels != null) {
                        // hiển thị tìm kiếm
                       movieSearchAdapter.setMovies(movieModels);
                       movieSearchAdapter.notifyDataSetChanged();

                        String search_title = (movieModels.size() > 0) ? (movieModels.size()) + "-" + movieListSearchViewModel.getTotalResults() + " results has been found" :
                                0 + " result has been found";
                        search_movie_tilte.setText(search_title);

                        // thêm recommend movie vào danh sách recommend (tối đa 10)
                        List<String> recommend_text = new ArrayList<String>();
                        for(int i =0; i< movieModels.size(); i++){
                            if(i<10){
                                if(!recommend_text.contains(movieModels.get(i).getTitle()))
                                    recommend_text.add(movieModels.get(i).getTitle());
                            }
                        }
                        if(search_text!=null){
                            searchRecommendAdapter.setRecommends(recommend_text);
                            if(recommend_text.size() == 0){
                                findViewById(R.id.no_history_err2).setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }
            });
        }

        // Hiển thị kết quả tìm kiếm người
        if(personViewModel != null){
            personViewModel.getPeople().observe(this, new Observer<List<PersonModel>>() {
                @Override
                public void onChanged(List<PersonModel> castModels) {
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
        delete_search_history_btn = findViewById(R.id.delete_search_history_btn);

        search_movie_recycler = findViewById(R.id.movie_search_recycler);
        people_recycler = findViewById(R.id.person_search_recycler);
        recommend_search_recycler = findViewById(R.id.recommend_search_recycler);
        configurationRecycler();
    }

    private void initSearchHistory() {
        search_history_texts = SearchRecommendRequest.getRecommendSearchTextFromFile(this);
//        Toast.makeText(this, "history texts init", Toast.LENGTH_SHORT).show();
        if(searchRecommendAdapter == null){
            searchRecommendAdapter = new SearchRecommendAdapter(this,search_history_texts ,  onItemClick);
        }else{
            searchRecommendAdapter.setRecommends(search_history_texts);
        }
    }
    public void configurationRecycler(){
        movieSearchAdapter = new MovieSearchAdapter(this, null, loginAccount);
        peopleAdapter = new CastAdapter(this,  loginAccount);

        search_movie_recycler.setLayoutManager(new LinearLayoutManager(this));
        search_movie_recycler.setAdapter(movieSearchAdapter);
        search_movie_recycler.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(!recyclerView.canScrollVertically(1)){
                    search_movie_next();
                }
            }
        });

        people_recycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        people_recycler.setAdapter(peopleAdapter);
        people_recycler.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(!recyclerView.canScrollHorizontally(1)){
                    search_people_next();
                }
            }
        });
        initSearchHistory();
        recommend_search_recycler.setLayoutManager(new LinearLayoutManager(this));
        recommend_search_recycler.setAdapter(searchRecommendAdapter);
    }

    public void initFeature(){
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                overridePendingTransition(0, 0);
            }
        });
        search_box.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                   showSearchHistory(true);
                }else{
                   showSearchHistory(false);
                }
            }
        });
        search_box.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId ==  EditorInfo.IME_ACTION_SEARCH){
                    search();
                    showSearchHistory(false);
                    SearchRecommendRequest.addSearchTextIntoFile(search_text, SearchPage.this);
                    Log.i("keyboard", "press 1");
                    return true;
                }
                return false;
            }
        });
        search_box.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(search_box.getText().toString().equalsIgnoreCase("")){ // rỗng để trống thì hiển thị lịch sử
                    initSearchHistory();
                }else{
//                    Toast.makeText(getApplicationContext(), "change", Toast.LENGTH_SHORT).show();;
                    search(); // không rỗng thì tìm recommend
                }
            }
        });
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                SearchRecommendRequest.addSearchTextIntoFile(search_text, SearchPage.this);
                search_box.clearFocus();
            }
        });
        delete_search_history_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchRecommendRequest.deleteSearchTextsFromFile(getBaseContext());
                searchRecommendAdapter.setRecommends(null);
                searchRecommendAdapter.notifyDataSetChanged();
            }
        });
        search_box.requestFocus();
    }

    public void search(){
        search_text = search_box.getText().toString().trim();
        movieListSearchViewModel.searchMovieApi(search_text, 1);
        personViewModel.searchPeople(search_text, 1);
    }

    public void search(String search_text){
        this.search_text = search_text;
        movieListSearchViewModel.searchMovieApi(search_text, 1);
        personViewModel.searchPeople(search_text, 1);
        SearchRecommendRequest.addSearchTextIntoFile(search_text, this);
        hideKeyboard();
        search_box.clearFocus();
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
    public void showSearchHistory(boolean show){
        if(show){
            findViewById(R.id.search_recommend).setVisibility(View.VISIBLE);
            findViewById(R.id.search_result).setVisibility(View.GONE);
        }else{
            findViewById(R.id.search_recommend).setVisibility(View.GONE);
            findViewById(R.id.search_result).setVisibility(View.VISIBLE);
        }
    }
}