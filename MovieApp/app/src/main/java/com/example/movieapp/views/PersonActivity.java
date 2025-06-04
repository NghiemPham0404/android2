package com.example.movieapp.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.movieapp.Adapters.CareerAdapter;
import com.example.movieapp.Interfaces.LoadingActivity;
import com.example.movieapp.data.Model.AccountModel;
import com.example.movieapp.data.Model.CreditModel;
import com.example.movieapp.data.Model.PersonModel;
import com.example.movieapp.data.Model.ExternalLinkModel;
import com.example.movieapp.R;
import com.example.movieapp.Request.ImageLoader;
import com.example.movieapp.ViewModel.PersonViewModel;
import com.example.movieapp.utils.Credentials;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class PersonActivity extends AppCompatActivity implements LoadingActivity {

    private int person_id;
    private PersonModel personModel;
    ImageView avatar;
    ImageButton twitter_x, facebook, instagram, tiktok, youtube;
ConstraintLayout loading_screen, layout;
    TextView know_for, gender, date_of_birth, place_of_birth, biography, name;
    RecyclerView career_recyclerView;
    FloatingActionButton pg_up_button;
    ScrollView scrollView;

    ImageButton back_btn;
    private PersonViewModel personViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);

        person_id = getIntent().getIntExtra("cast_id", -1);
        personViewModel = new ViewModelProvider(this).get(PersonViewModel.class);
        ObserveAnyChange();
        initComponents();
        initFeatures();
    }

    public void ObserveAnyChange() {
        if (personViewModel != null) {
            personViewModel.getPerson().observe(this, new Observer<PersonModel>() {
                @Override
                public void onChanged(PersonModel person) {
                    if (person != null) {
                        personModel = person;
                        initInformation();
                    }
                }
            });
        }
    }

    public void initComponents() {
        loading_screen = findViewById(R.id.loadingLayout);
        layout = findViewById(R.id.person_layout);
        back_btn = findViewById(R.id.backBtn_person);
        avatar = findViewById(R.id.avatar_person);
        name = findViewById(R.id.person_name_lbl);
        scrollView = findViewById(R.id.scrollView_person);
        pg_up_button = findViewById(R.id.pg_up_btn);
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

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        pg_up_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollView.scrollTo(0, 0);
            }
        });
    }
    public void initFeatures(){
        turnOnLoadingScreen();
       personViewModel.searchPerson(person_id);
    }
    public void initInformation() {
        initCareer();
        initExternalLink();
        name.setText(personModel.getName());
        know_for.setText(personModel.getKnown_for_department());
        String gender_str = (personModel.getGender() == 0) ? "Male" : "Female";
        gender.setText(gender_str);
        date_of_birth.setText(personModel.getBirthday());
        place_of_birth.setText(personModel.getPlace_of_birth());
        biography.setText(personModel.getBiography());
        new ImageLoader().loadImageIntoImageView(PersonActivity.this, Credentials.BASE_IMAGE_URL + personModel.getProfile_path(), avatar);
        new ImageLoader().loadImageIntoImageView(PersonActivity.this, Credentials.BASE_IMAGE_URL + personModel.getProfile_path(), findViewById(R.id.imageView6));
        turnOffLoadingScreen();
    }

    public void initExternalLink() {
        ExternalLinkModel externalLinkModel = personModel.getExternal_ids();
        if (externalLinkModel.getFacebook_id() != null)
            initExternalLinkButtons(externalLinkModel.getFacebook_id(), 0);
        else
            facebook.setVisibility(View.GONE);

        if (externalLinkModel.getInstagram_id() != null)
            initExternalLinkButtons(externalLinkModel.getInstagram_id(), 1);
        else
            instagram.setVisibility(View.GONE);

        if (externalLinkModel.getTwitter_id() != null)
            initExternalLinkButtons(externalLinkModel.getTwitter_id(), 2);
        else
            twitter_x.setVisibility(View.GONE);

        if (externalLinkModel.getYoutube_id() != null)
            initExternalLinkButtons(externalLinkModel.getYoutube_id(), 3);
        else
            youtube.setVisibility(View.GONE);

        if (externalLinkModel.getTiktok_id() != null)
            initExternalLinkButtons(externalLinkModel.getTiktok_id(), 4);
        else
            tiktok.setVisibility(View.GONE);
    }

    public void initCareer() {
        List<CreditModel> creditModelList = personModel.getMovie_credits().getCast();
        creditModelList.addAll(personModel.getMovie_credits().getCrew());
        CareerAdapter careerAdapter = new CareerAdapter(PersonActivity.this, creditModelList);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(PersonActivity.this);
        career_recyclerView.setAdapter(careerAdapter);
        career_recyclerView.setLayoutManager(linearLayoutManager);
    }

    public void initExternalLinkButtons(String url, int type) {
        switch (type) {
            case 0:
                facebook.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent fb_intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/" + url));
                        startActivity(fb_intent);
                    }
                });
                break;
            case 1:
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
            case 3:
                youtube.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent youtube_intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/" + url));
                        startActivity(youtube_intent);
                    }
                });
                break;
            case 4:
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

    public void turnOnLoadingScreen(){
        loading_screen.setVisibility(View.VISIBLE);
        setViewAndChildrenEnabled(layout, false);
    }
    public void turnOffLoadingScreen(){
        loading_screen.setVisibility(View.GONE);
        setViewAndChildrenEnabled(layout, true);
    }
}