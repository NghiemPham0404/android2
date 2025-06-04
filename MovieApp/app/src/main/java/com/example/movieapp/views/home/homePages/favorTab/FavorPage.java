package com.example.movieapp.views.home.homePages.favorTab;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.movieapp.views.home.homePages.favorTab.history.HistoryFragment;
import com.example.movieapp.views.home.homePages.favorTab.favor.FavorFragment;
import com.example.movieapp.data.Model.AccountModel;
import com.example.movieapp.R;
import com.google.android.material.tabs.TabLayout;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FavorPage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FavorPage extends Fragment{

    FrameLayout layout;
    TabLayout favor_navigation;
    final FavorFragment favorFragment;
    final HistoryFragment historyFragment;

    public FavorPage() {
        favorFragment = FavorFragment.newInstance();
        historyFragment = HistoryFragment.newInstance();
    }

    // TODO: Rename and change types and number of parameters
    public static FavorPage newInstance() {
        return new FavorPage();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favor_page, container, false);
        initComponents(view);
        initFeatures();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(activeFragment == null) switchFragment(favorFragment);
    }

    public void initComponents(View view){
        layout = view.findViewById(R.id.fav_layout);
        favor_navigation = view.findViewById(R.id.favor_navigation);
        for (int i = 0; i < favor_navigation.getTabCount(); i++) {
            TabLayout.Tab tab = favor_navigation.getTabAt(i);
            if (tab != null) {
                tab.setText(tab.getText().toString().toLowerCase()); // Convert tab text to lowercase
            }
        }
    }

    public void initFeatures(){
        favor_navigation.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                switch (position) {
                    case 0:
                        Toast.makeText(getContext(), "Favorite", Toast.LENGTH_SHORT).show();
                        switchFragment(favorFragment);
                        break;
                    case 1:
                        Toast.makeText(getContext(), "History", Toast.LENGTH_SHORT).show();
                        switchFragment(historyFragment);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if ( tab.getId() == R.id.favor_f) {
                    Toast.makeText(getContext(), "favorite", Toast.LENGTH_SHORT).show();
                } else if ( tab.getId() == R.id.history_f) {
                    Toast.makeText(getContext(), "history", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private Fragment activeFragment = null;

    private void switchFragment(Fragment target) {
        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        if (activeFragment == target) return;

        if (activeFragment != null) {
            ft.hide(activeFragment);
        }

        if (!target.isAdded()) {
            ft.add(R.id.fragment_favor, target);
        } else {
            ft.show(target);
        }

        activeFragment = target;
        ft.commitNowAllowingStateLoss(); // or commitAllowingStateLoss() for safety
    }

}