package com.example.movieapp.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.widget.Toast;

import com.example.movieapp.Model.AccountModel;
import com.example.movieapp.R;
import com.example.movieapp.View.UserPackage.UserPage;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    private AccountModel loginAccount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_window);

        BottomNavigationView navigationView = findViewById(R.id.bottomNavigationView);
        navigationView.setItemIconTintList(null);

        loginAccount = (AccountModel) getIntent().getParcelableExtra("loginAccount");

        replaceFragment(HomePage.newInstance(loginAccount));

        navigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.home) {
                replaceFragment(HomePage.newInstance(loginAccount));
            } else if (item.getItemId() == R.id.discover) {
                replaceFragment(new DiscoverPage());
            }  else if (item.getItemId() == R.id.favor) {
                replaceFragment(FavorPage.newInstance(loginAccount));
            } else if (item.getItemId() == R.id.history) {
                replaceFragment(new HistoryPage());
            } else if (item.getItemId() == R.id.user) {
                replaceFragment(UserPage.newInstance(loginAccount));
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