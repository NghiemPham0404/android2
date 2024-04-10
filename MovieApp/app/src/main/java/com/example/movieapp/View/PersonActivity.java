package com.example.movieapp.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.movieapp.Adapters.CareerAdapter;
import com.example.movieapp.Adapters.CastAdapter;
import com.example.movieapp.Model.CastModel;
import com.example.movieapp.Model.CreditModel;
import com.example.movieapp.Model.ExternalLinkModel;
import com.example.movieapp.R;
import com.example.movieapp.Request.ImageLoader;
import com.example.movieapp.Request.MyService;
import com.example.movieapp.Response.CreditResponse;
import com.example.movieapp.utils.Credentials;
import com.example.movieapp.utils.MovieApi;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PersonActivity extends AppCompatActivity {

    private int cast_id;
    private CastModel cast;
    private MovieApi movieApi = MyService.getMovieApi();
    ImageView avatar;
    ImageButton twitter_x, facebook, instagram, tiktok, youtube;

    TextView know_for, gender, date_of_birth, place_of_birth, biography, name;
    RecyclerView career_recyclerView;
    FloatingActionButton pg_up_button;
    ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);

        cast_id = getIntent().getIntExtra("cast_id", -1);

        initComponents();
    }
    public void initComponents(){
        avatar = findViewById(R.id.avatar_person);
        name = findViewById(R.id.person_name_lbl);

        scrollView = findViewById(R.id.scrollView_person);
        pg_up_button = findViewById(R.id.pg_up_btn);

        pg_up_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollView.scrollTo(0,0);
            }
        });

        twitter_x = findViewById(R.id.twitter_x_person);
        facebook = findViewById(R.id.facebook_person);
        instagram = findViewById(R.id.instagram_person);
        tiktok = findViewById(R.id.tiktok_person);
        youtube = findViewById(R.id.youtube_person);

        know_for = findViewById(R.id.know_for_person);
        gender = findViewById(R.id.gender_person);
        date_of_birth = findViewById(R.id.date_of_birth_person);
        place_of_birth = findViewById(R.id.place_of_birth_person);
        biography = findViewById(R.id.biography_person);

        career_recyclerView = findViewById(R.id.career_recyclerview_person);
        initInformation();
        initCareer();
        initExternalLink();
    }


    public void initInformation(){
        Call<CastModel> personResponseCall = movieApi.searchPersonByID(cast_id, Credentials.API_KEY);
        personResponseCall.enqueue(new Callback<CastModel>() {
            @Override
            public void onResponse(Call<CastModel> call, Response<CastModel> response) {
                cast = response.body();
                    name.setText(cast.getName());
                    know_for.setText(cast.getKnown_for_department());
                    String gender_str = (cast.getGender()==0)?"Male":"Female";
                    gender.setText(gender_str);
                    date_of_birth.setText(cast.getBirthday());
                    place_of_birth.setText(cast.getPlace_of_birth());
                    biography.setText(cast.getBiography());
                    new ImageLoader().loadImageIntoImageView(PersonActivity.this ,Credentials.BASE_IMAGE_URL + cast.getProfile_path(), avatar);
            }

            @Override
            public void onFailure(Call<CastModel> call, Throwable t) {
                Log.e("ERROR CAST", "error : false to load Person");
            }
        });
    }
    public void initExternalLink(){
        Call<ExternalLinkModel> externalLinkModelCall = movieApi.searchPersonExternalIdByID(cast_id, Credentials.API_KEY);
        externalLinkModelCall.enqueue(new Callback<ExternalLinkModel>() {
            @Override
            public void onResponse(Call<ExternalLinkModel> call, Response<ExternalLinkModel> response) {
                if(response.code() == 200){
                    ExternalLinkModel externalLinkModel = response.body();
                    if(externalLinkModel.getFacebook_id()!=null)
                        initExternalLinkButtons(externalLinkModel.getFacebook_id(), 0);
                    else
                       facebook.setVisibility(View.GONE);

                    if(externalLinkModel.getInstagram_id()!=null)
                        initExternalLinkButtons(externalLinkModel.getInstagram_id(), 1);
                    else
                        instagram.setVisibility(View.GONE);

                    if(externalLinkModel.getTwitter_id()!=null)
                        initExternalLinkButtons(externalLinkModel.getTwitter_id(), 2);
                    else
                        twitter_x.setVisibility(View.GONE);

                    if(externalLinkModel.getYoutube_id()!=null)
                        initExternalLinkButtons(externalLinkModel.getYoutube_id(), 3);
                    else
                        youtube.setVisibility(View.GONE);

                    if(externalLinkModel.getTiktok_id()!=null)
                        initExternalLinkButtons(externalLinkModel.getTiktok_id(), 4);
                    else
                       tiktok.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ExternalLinkModel> call, Throwable t) {

            }
        });
    }

    public void initCareer(){
        Call<CreditResponse> creditListResponseCall = movieApi.searchPersonCreditByID(cast_id, Credentials.API_KEY);
        creditListResponseCall.enqueue(new Callback<CreditResponse>() {
            @Override
            public void onResponse(Call<CreditResponse> call, Response<CreditResponse> response) {
                if(response.code()==200){
                    List<CreditModel> creditModelList = response.body().getCreditModelList();
                    CareerAdapter careerAdapter = new CareerAdapter(PersonActivity.this, creditModelList);

                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(PersonActivity.this);
                     career_recyclerView.setAdapter(careerAdapter);
                     career_recyclerView.setLayoutManager(linearLayoutManager);
                }
            }

            @Override
            public void onFailure(Call<CreditResponse> call, Throwable t) {
                Log.e("ERROR CAST", "error : false to load Person Credit");
            }
        });
    }

    public void initExternalLinkButtons(String url, int type){
        switch (type){
            case 0:
                facebook.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent fb_intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/" + url));
                        startActivity(fb_intent);
                    }
                });
                break;
            case 1 :
                instagram.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent ins_intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/" + url));
                        startActivity(ins_intent);
                    }
                });
                break;
            case 2:
                twitter_x.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent twitter_intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.twitter.com/" + url));
                        startActivity(twitter_intent);
                    }
                });
                break;
            case 3 :
                youtube.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent youtube_intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/" + url));
                        startActivity(youtube_intent);
                    }
                });
                break;
            case 4 :
               tiktok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent tiktok_intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.tiktok.com/@" + url));
                        startActivity(tiktok_intent);
                    }
                });
                break;
        }
    }


}