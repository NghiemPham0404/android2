package com.example.movieapp.View;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.movieapp.Adapters.FilmAdapter;
import com.example.movieapp.Model.AccountModel;
import com.example.movieapp.Model.MovieModel;
import com.example.movieapp.Model.CountryModel;
import com.example.movieapp.R;
import com.example.movieapp.Request.ImageLoader;
import com.example.movieapp.Request.MyService;
import com.example.movieapp.Response.GenreResponse;
import com.example.movieapp.Response.MovieSearchResponse;
import com.example.movieapp.utils.Credentials;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DiscoverPage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DiscoverPage extends Fragment {

    LinearLayout layout, position_bar;
    AccountModel loginAccount;
    List<MovieModel.Genre> genres;
    List<CountryModel> regions;
    RecyclerView recyclerview_discover;
    Button init_discover_filter_btn;
    FloatingActionButton pre_discover_btn, next_discover_btn;
    PopupWindow discoverFilterPopup;
    View popupView;
    View discover_item;

    ChipGroup selected_chip, genre_group, country_group;
    Button genre_group_btn, reset_filter_discover, filter_accept_discover;
    AutoCompleteTextView country_auto, year_auto;
    ArrayAdapter country_apdater, year_adapter;
    String selected_country = "", selected_country_display = "";
    int selected_year = -1, position = 0;
    private String gerne_id_string;
    List<MovieModel> movies;

    ImageView image_view_discover;
    TextView film_title_discover, overview_discover, movie_rating, publish_date_discover,  time_discover, current_pos, max_pos;

    public DiscoverPage() {
        // Required empty public constructor
    }
    public static DiscoverPage newInstance(AccountModel loginAccount) {
        DiscoverPage fragment = new DiscoverPage();
        Bundle args = new Bundle();
        args.putParcelable("loginAccount", loginAccount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            loginAccount = getArguments().getParcelable("loginAccount");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_discover_page, container, false);
        initComponents(view);
        return view;
    }
    public void initComponents(View view){
        layout = view.findViewById(R.id.linearLayout3);
        position_bar = view.findViewById(R.id.position_bar);
        current_pos = view.findViewById(R.id.current_pos);
        max_pos = view.findViewById(R.id.max_pos);

        init_discover_filter_btn = view.findViewById(R.id.init_discover_filter_btn);
        selected_chip = view.findViewById(R.id.chip_group_discover);
        recyclerview_discover = view.findViewById(R.id.recycler_discover);
        discover_item = view.findViewById(R.id.discover_item);
        pre_discover_btn = view.findViewById(R.id.pre_discover_btn);
        next_discover_btn = view.findViewById(R.id.next_discover_btn);

        image_view_discover = view.findViewById(R.id.image_view_discover);
        film_title_discover = view.findViewById(R.id.film_title_discover);
        overview_discover = view.findViewById(R.id.overview_discover);
        movie_rating = view.findViewById(R.id.movie_rating);
        publish_date_discover = view.findViewById(R.id.publish_date_discover);
        time_discover = view.findViewById(R.id.time_discover);
        initFeatures();
    }

    public void initFeatures(){
        displayNonFilter();
        initGenres();
        initRegion();
        init_discover_filter_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(discoverFilterPopup==null){
                    initPopup();
                    displayDiscoverPopup();
                }else{
                    displayDiscoverPopup();
                }
            }
        });
    }

    public void initGenres(){
        Call<GenreResponse> genreResponseCall = MyService.getMovieApi().getAllGenre(Credentials.API_KEY);
        genreResponseCall.enqueue(new Callback<GenreResponse>() {
            @Override
            public void onResponse(Call<GenreResponse> call, Response<GenreResponse> response) {
                if(response.code() == 200){
                    genres = response.body().getGenres();
                }
            }

            @Override
            public void onFailure(Call<GenreResponse> call, Throwable t) {
                Log.i("GENRES INIT FAIL", t.toString());
            }
        });
    }
    public void initRegion(){
        Call<List<CountryModel>> regionResponseCall = MyService.getMovieApi().getAllRegion(Credentials.API_KEY);
        regionResponseCall.enqueue(new Callback<List<CountryModel>>() {
            @Override
            public void onResponse(Call<List<CountryModel>> call, Response<List<CountryModel>> response) {
                if(response.code() == 200) {
                    regions = response.body();
                }
            }

            @Override
            public void onFailure(Call<List<CountryModel>> call, Throwable t) {
                Log.i("REGIONS INIT FAIL", t.toString());
            }
        });
    }

    public void initPopup(){
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        popupView = inflater.inflate(R.layout.discover_filter, null);

        boolean focusable = true;
        discoverFilterPopup = new PopupWindow(popupView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT, focusable);
        discoverFilterPopup.setAnimationStyle(R.anim.popup_animation);

        // Define your colors
        int checkedColor = getResources().getColor(R.color.neon_pink);
        int uncheckedColor = getResources().getColor(R.color.dark_gray);

    // Create a ColorStateList for chip background color
            ColorStateList colorStateList = new ColorStateList(
                    new int[][] {
                            new int[] { android.R.attr.state_checked },
                            new int[] {}
                    },
                    new int[] {
                            checkedColor,
                            uncheckedColor
                    }
            );


        genre_group = popupView.findViewById(R.id.genre_group);
        genre_group_btn = popupView.findViewById(R.id.genre_group_btn);
        genre_group_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(genre_group.getVisibility() == View.GONE){
                    genre_group.setVisibility(View.VISIBLE);
                }else{
                    genre_group.setVisibility(View.GONE);
                }
            }
        });
        for(int i = 0; i<genres.size(); i++){
            Chip chip = new Chip(getContext());
            chip.setCheckable(true);
            chip.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            chip.setChipBackgroundColor(colorStateList);
            chip.setChipStrokeColor(ColorStateList.valueOf(getResources().getColor(R.color.neon_pink)));
            chip.setChipStrokeWidth(1.0f);
            chip.setText(genres.get(i).getName());
            chip.setId(genres.get(i).getId());
            genre_group.addView(chip);
        }

        country_group = popupView.findViewById(R.id.region_group);
        country_auto = popupView.findViewById(R.id.country_auto);
        List<String> display_country = new ArrayList<>();
        for (CountryModel obj : regions) {
            display_country.add(obj.getEnglish_name());
        }

        country_apdater = new ArrayAdapter<String> (getContext(), R.layout.spinner_item, display_country);
        country_auto.setAdapter(country_apdater);
        country_auto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedCountry = (String) parent.getItemAtPosition(position);
                for(int i = 0; i<regions.size(); i++){
                    if(regions.get(i).getEnglish_name() == selectedCountry){
                        selected_country = regions.get(i).getIso_3166_1();
                        selected_country_display = regions.get(i).getEnglish_name();
                    }
                }
                // Do whatever you want with the ISO code, e.g., show it in a Toast or log it
                Toast.makeText(getContext(), selected_country, Toast.LENGTH_SHORT).show();
                Log.i("Choosen Country", selected_country);
            }
        });


        year_auto = popupView.findViewById(R.id.year_auto);
        List<String> year = new ArrayList<>();
        for(int i = new Date(System.currentTimeMillis()).getYear() + 1900; i>= 1900; i--){
            year.add(i+"");
        }

        year_adapter = new ArrayAdapter<String> (getContext(), R.layout.spinner_item, year);
        year_auto.setAdapter(year_adapter);

        year_auto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // When an item is selected, fetch the corresponding attribute (e.g., ISO code)
                selected_year = Integer.parseInt(year.get(position));
                // Do whatever you want with the ISO code, e.g., show it in a Toast or log it
                Toast.makeText(getContext(),selected_year+"" , Toast.LENGTH_SHORT).show();
                Log.i("Choosen Country", selected_year+"");
            }
        });

        reset_filter_discover = popupView.findViewById(R.id.reset_filter_discover);
        reset_filter_discover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected_country = "";
                country_auto.setText("");
                selected_year = 0;
                year_auto.setText("");
                genre_group.clearCheck();
            }
        });
        filter_accept_discover = popupView.findViewById(R.id.filter_accept_discover);
        filter_accept_discover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptFilter();
            }
        });
    }
    public void displayDiscoverPopup(){
        discoverFilterPopup.showAtLocation(layout, Gravity.TOP, 0, 0);
    }

    public void acceptFilter(){
        selected_chip.removeAllViews();
        List<Integer> genre_ids = genre_group.getCheckedChipIds();
        StringBuilder stringBuilder = new StringBuilder();
        gerne_id_string= "";
        for (int i = 0; i < genre_ids.size(); i++) {
            Chip chip = popupView.findViewById(genre_ids.get(i));
            if (i == 0) {
                stringBuilder.append(chip.getId());
            } else {
                stringBuilder.append("|").append(chip.getId());
            }

            Chip display_filter_chip = new Chip(getContext());
            display_filter_chip.setText(chip.getText());
            display_filter_chip.setChipBackgroundColor(ColorStateList.valueOf(getResources().getColor(R.color.neon_blue)));
            display_filter_chip.setTextColor(getResources().getColor(R.color.white));
            selected_chip.addView(display_filter_chip);
        }
        if(!selected_country_display.equalsIgnoreCase("")){
            Chip country_chip = new Chip(getContext());
            country_chip.setText(selected_country_display);
            country_chip.setChipBackgroundColor(ColorStateList.valueOf(getResources().getColor(R.color.neon_blue)));
            country_chip.setTextColor(getResources().getColor(R.color.white));
            selected_chip.addView(country_chip);
        }

        if(selected_year!=-1){
            Chip year_chip = new Chip(getContext());
            year_chip.setText(selected_year+"");
            year_chip.setChipBackgroundColor(ColorStateList.valueOf(getResources().getColor(R.color.neon_blue)));
            year_chip.setTextColor(getResources().getColor(R.color.white));
            selected_chip.addView(year_chip);
        }

        gerne_id_string = stringBuilder.toString();
        displayFilterSelection();
    }


    public void displayFilterSelection(){
        hideDisplayNonFilter();
        Call<MovieSearchResponse> call = MyService.getMovieApi().discoverMovie(Credentials.API_KEY, gerne_id_string, selected_country, selected_year, 1);
        call.enqueue(new Callback<MovieSearchResponse>() {
            @Override
            public void onResponse(Call<MovieSearchResponse> call, Response<MovieSearchResponse> response) {
                if(response.code() == 200){
                    movies = response.body().getMovies();
                    if(movies.size() > 0){
                        position = 0;
                        current_pos.setText(""+(position +1));
                        max_pos.setText(""+movies.size());
                        initDisplayItem(0);
                    }
                    Log.i("INIT DISCOVER", "SUCCESSFUL");
                }
            }

            @Override
            public void onFailure(Call<MovieSearchResponse> call, Throwable t) {
                Log.e("INIT DISCOVER", "FAIL");
            }
        });
    }
    public void initDisplayItem(int pos){
        pre_discover_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(pos == 0){
                    position = movies.size()-1;
               }else{
                   position = pos-1;
               }
                initDisplayItem(position);
                current_pos.setText(""+(position +1));
            }
        });

        next_discover_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pos == movies.size()-1){
                    position = 0;
                    initDisplayItem(position);
                }else{
                    position =pos+1;
                    initDisplayItem(position);
                }
                current_pos.setText(""+(position +1));
            }
        });

        MovieModel movie = movies.get(pos);
        new ImageLoader().loadImageIntoImageView(getContext(), Credentials.BASE_IMAGE_URL + movie.getPoster_path() ,image_view_discover);
        film_title_discover.setText(movie.getTitle());
        overview_discover.setText(movie.getOverview());
        movie_rating.setText((Math.round(movie.getVote_average()*10)*1.0/10) + "");
        try {
            int year = Integer.parseInt(movie.getRelease_date().split("-")[0]);
            publish_date_discover.setText("("+year+")");
        }catch(Exception e){
            publish_date_discover.setText(movie.getRelease_date());
        }
        time_discover.setText(movie.getMaxDurationTime());

        discover_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent movie_info_intent= new Intent(getContext(), Movie_infomation.class);
                movie_info_intent.putExtra("loginAccount", loginAccount);
                movie_info_intent.putExtra("film_id", movie.getId());
                getContext().startActivity(movie_info_intent);
            }
        });
    }

    public void displayNonFilter(){
        hideDisplayFilter();
        Call<MovieSearchResponse> call = MyService.getMovieApi().searchMoviesList(Credentials.BASE_URL
                +Credentials.NOW_PLAYING, Credentials.API_KEY, 1);
        call.enqueue(new Callback<MovieSearchResponse>() {
            @Override
            public void onResponse(Call<MovieSearchResponse> call, Response<MovieSearchResponse> response) {
                movies = response.body().getMovies();
                recyclerview_discover.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
                FilmAdapter filmAdapter = new FilmAdapter(movies,getContext(), loginAccount);
                recyclerview_discover.setAdapter(filmAdapter);
            }

            @Override
            public void onFailure(Call<MovieSearchResponse> call, Throwable t) {
                Log.e("INIT DEFAULT DISCOVER", "FAIL");
            }
        });
    }
    public void hideDisplayFilter(){
        discover_item.setVisibility(View.GONE);
        next_discover_btn.setVisibility(View.GONE);
        pre_discover_btn.setVisibility(View.GONE);
        position_bar.setVisibility(View.GONE);

        recyclerview_discover.setVisibility(View.VISIBLE);
    }

    public void hideDisplayNonFilter(){
        recyclerview_discover.setVisibility(View.GONE);

        discover_item.setVisibility(View.VISIBLE);
        next_discover_btn.setVisibility(View.VISIBLE);
        pre_discover_btn.setVisibility(View.VISIBLE);
        position_bar.setVisibility(View.VISIBLE);
    }

}