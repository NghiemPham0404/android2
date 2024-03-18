package com.example.movieapp.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.example.movieapp.R;
import com.example.movieapp.View.FavorPage;
import com.example.movieapp.View.HistoryPage;
import com.example.movieapp.View.HomePage;
import com.example.movieapp.View.UserPage;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_window);

        BottomNavigationView navigationView = findViewById(R.id.bottomNavigationView);
        navigationView.setItemIconTintList(null);

        replaceFragment(new HomePage());

        navigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.home) {
                replaceFragment(new HomePage());
            } else if (item.getItemId() == R.id.favor) {
                replaceFragment(new FavorPage());
            } else if (item.getItemId() == R.id.history) {
                replaceFragment(new HistoryPage());
            } else if (item.getItemId() == R.id.user) {
                replaceFragment(new UserPage());
            }
            return true;
        });

    }
    private void replaceFragment(Fragment f){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frameLayout, f);
        ft.commit();
    }


}