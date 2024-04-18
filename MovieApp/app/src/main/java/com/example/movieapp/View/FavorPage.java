package com.example.movieapp.View;

import android.content.Intent;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.example.movieapp.Adapters.FavorAdapter;
import com.example.movieapp.Model.AccountModel;
import com.example.movieapp.Model.DetailModel;
import com.example.movieapp.Model.MovieModel;
import com.example.movieapp.Model.VideoModel;
import com.example.movieapp.R;
import com.example.movieapp.Request.MyService;
import com.example.movieapp.Request.MyService2;
import com.example.movieapp.View.UserPackage.UserPage;
import com.example.movieapp.utils.Credentials;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FavorPage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FavorPage extends Fragment{


    AccountModel loginAccount;
    FrameLayout layout;
    TabLayout favor_navigation;


    public FavorPage() {
    }

    // TODO: Rename and change types and number of parameters
    public static FavorPage newInstance(AccountModel loginAccount) {
        FavorPage fragment = new FavorPage();
        Bundle args = new Bundle();
        args.putParcelable("loginAccount", loginAccount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            loginAccount = (AccountModel) getArguments().get("loginAccount");
            Toast.makeText(getContext(), "userID : "+loginAccount.getUser_id(), Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getContext(), "agrs is null",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favor_page, container, false);
        initComponents(view);
        return view;
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
        initFeatures();
    }

    public void initFeatures(){
        favor_navigation.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                switch (position) {
                    case 0:
                        Toast.makeText(getContext(), "Favorite", Toast.LENGTH_SHORT).show();
                        replaceFragment(FavorFragment.newInstance(loginAccount));
                        break;
                    case 1:
                        Toast.makeText(getContext(), "History", Toast.LENGTH_SHORT).show();
                        replaceFragment(HistoryFragment.newInstance(loginAccount));
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
        replaceFragment(FavorFragment.newInstance(loginAccount));
    }

    private void replaceFragment(Fragment f){
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_favor, f);
        ft.commit();
    }

}