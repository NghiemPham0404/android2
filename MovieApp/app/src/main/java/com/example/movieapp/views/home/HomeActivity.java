package com.example.movieapp.views.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.os.Process;
import android.widget.Toast;

import com.example.movieapp.views.home.homePages.discoveryTab.DiscoverPage;
import com.example.movieapp.views.home.homePages.favorTab.FavorPage;
import com.example.movieapp.views.home.homePages.homeTab.HomePage;
import com.example.movieapp.data.Model.AccountModel;
import com.example.movieapp.R;
import com.example.movieapp.views.home.homePages.userTab.UserPage;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    private ViewPager viewPager;

    public HomePage homePage;
    public FavorPage favorPage;
    public DiscoverPage discoverPage;
    public UserPage userPage;

    private int alert_out = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_window);

        viewPager = findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(2);
        BottomNavigationView navigationView = findViewById(R.id.bottomNavigationView);
        navigationView.setItemIconTintList(null);

        homePage = HomePage.newInstance();
        favorPage = FavorPage.newInstance();
        discoverPage = DiscoverPage.newInstance();
        userPage = UserPage.newInstance();

        HomePagerAdapter adapter = new HomePagerAdapter(getSupportFragmentManager(), homePage, discoverPage, favorPage, userPage);
        viewPager.setAdapter(adapter);

        // chọn 1 nav item lần đầu
        navigationView.setOnItemSelectedListener(item -> {
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

//        // chọn 1 nav lần hai
//        navigationView.setOnNavigationItemReselectedListener(item -> {
//            alert_out = 1;
//            if(item.getItemId() == R.id.home){
//                adapter.homePage.stopTimer();
//                adapter.homePage.initFeatures();
//            }else if(item.getItemId() == R.id.discover){
//                adapter.discoverPage.initFeatures();
//            }else if(item.getItemId() ==R.id.favor){
//                adapter.favorPage.initFeatures();
//            }else if(item.getItemId() == R.id.user){
//                adapter.userPage.initFeature();
//            }
//            adapter.notifyDataSetChanged();
//        });

//        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//                switch (position) {
//                    case 0:
//                        navigationView.setSelectedItemId(R.id.home);
//                        break;
//                    case 1:
//                        navigationView.setSelectedItemId(R.id.discover);
//                        break;
//                    case 2:
//                        navigationView.setSelectedItemId(R.id.favor);
//                        break;
//                    case 3:
//                        navigationView.setSelectedItemId(R.id.user);
//                        break;
//                }
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });
    }

    private static class HomePagerAdapter extends FragmentPagerAdapter {

        private final Fragment[] fragments;

        public HomePagerAdapter(FragmentManager fm,  Fragment ...fragments) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            this.fragments = fragments;
        }
        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments[position];
        }

        @Override
        public int getCount() {
            return fragments.length; // Number of fragments
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