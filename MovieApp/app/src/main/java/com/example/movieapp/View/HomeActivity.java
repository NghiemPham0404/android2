package com.example.movieapp.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;

import com.example.movieapp.Model.AccountModel;
import com.example.movieapp.R;
import com.example.movieapp.View.UserPackage.UserPage;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    private AccountModel loginAccount;
    private ViewPager viewPager;

    public HomePage homePage;
    public FavorPage favorPage;
    public DiscoverPage discoverPage;
    public UserPage userPage;

    private int alert_out = 1;

    int selectedItem = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_window);

        viewPager = findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(2);
        BottomNavigationView navigationView = findViewById(R.id.bottomNavigationView);
        navigationView.setItemIconTintList(null);

        loginAccount = (AccountModel) getIntent().getParcelableExtra("loginAccount");

        homePage = HomePage.newInstance(loginAccount);
        favorPage = FavorPage.newInstance(loginAccount);
        discoverPage = DiscoverPage.newInstance(loginAccount);
        userPage = userPage.newInstance(loginAccount);

        HomePagerAdapter adapter = new HomePagerAdapter(getSupportFragmentManager(), loginAccount, homePage, favorPage, discoverPage, userPage);
        viewPager.setAdapter(adapter);

        // chọn 1 nav item lần đầu
        navigationView.setOnNavigationItemSelectedListener(item -> {
            if(item.getItemId() == R.id.home){
                viewPager.setCurrentItem(0);
            }else if(item.getItemId() == R.id.discover){
                viewPager.setCurrentItem(1);
            }else if(item.getItemId() ==R.id.favor){
                viewPager.setCurrentItem(2);
            }else if(item.getItemId() == R.id.user){
                viewPager.setCurrentItem(3);
            }
           return  true;
        });

        // chọn 1 nav lần hai
        navigationView.setOnNavigationItemReselectedListener(item -> {
            alert_out = 1;
            if(item.getItemId() == R.id.home){
                adapter.homePage.stopTimer();
                adapter.homePage.initFeatures();
            }else if(item.getItemId() == R.id.discover){
                adapter.discoverPage.initFeatures();
            }else if(item.getItemId() ==R.id.favor){
                adapter.favorPage.initFeatures();
            }else if(item.getItemId() == R.id.user){
                adapter.userPage.initFeature();
            }
            adapter.notifyDataSetChanged();
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        navigationView.setSelectedItemId(R.id.home);
                        break;
                    case 1:
                        navigationView.setSelectedItemId(R.id.discover);
                        break;
                    case 2:
                        navigationView.setSelectedItemId(R.id.favor);
                        break;
                    case 3:
                        navigationView.setSelectedItemId(R.id.user);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.setCurrentItem(0);
    }

    private static class HomePagerAdapter extends FragmentPagerAdapter {

        public AccountModel loginAccount;

        public HomePage homePage;
        public FavorPage favorPage;
        public DiscoverPage discoverPage;
        public UserPage userPage;
        public HomePagerAdapter(FragmentManager fm, AccountModel loginAccount, HomePage homePage, FavorPage favorPage, DiscoverPage discoverPage, UserPage userPage) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            this.loginAccount = loginAccount;
            this.homePage = homePage;
            this.favorPage = favorPage;
            this.discoverPage = discoverPage;
            this.userPage = userPage;
        }
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return   homePage;
                case 1:
                    return   discoverPage;
                case 2:
                    return  favorPage;
                case 3:
                    return userPage;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 4; // Number of fragments
        }
    }

    @Override
    public void onBackPressed() {
        if(alert_out == 1){
            Toast.makeText(this, "press again to exit app", Toast.LENGTH_SHORT).show();
            alert_out--;
        }else{
            super.onBackPressed();
            moveTaskToBack(true);
            Process.killProcess(Process.myPid());
            System.exit(1);
        }
    }
}