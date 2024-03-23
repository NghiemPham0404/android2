package com.example.movieapp.View;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.example.movieapp.Interfaces.Fragment_Interface;
import com.example.movieapp.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FavorPage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FavorPage extends Fragment implements Fragment_Interface {

    private Button filter_btn, star_btn, sort_btn;
    int c_sort = 0;

    private PopupWindow filter_popup, rating_popup;
    FrameLayout layout;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FavorPage() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FavorPage.
     */
    // TODO: Rename and change types and number of parameters
    public static FavorPage newInstance(String param1, String param2) {
        FavorPage fragment = new FavorPage();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
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
        filter_btn = view.findViewById(R.id.filter_btn);
        star_btn = view.findViewById(R.id.rating_filter_btn);
        sort_btn = view.findViewById(R.id.sort_btn);
        layout = view.findViewById(R.id.fav_layout);;
        initFilterBar();
    }

    @Override
    public void initFilterBar(){

            filter_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    initFilterPopup();
                }
            });

            star_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    initRatingPopup();
                }
            });
    }

    @Override
    public void initFilterPopup(){
        if(filter_popup == null){
            LayoutInflater inflater = (LayoutInflater)   this.getContext().getSystemService(getContext().LAYOUT_INFLATER_SERVICE);
            View popUpFilter = inflater.inflate(R.layout.filter_search_popup, null);

            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            boolean focusable = true;
            filter_popup= new PopupWindow(popUpFilter, width, height, focusable);
            filter_popup.setAnimationStyle(R.style.PopupAnimation);
        }
        layout.post(new Runnable() {
            @Override
            public void run() {
                filter_popup.showAtLocation(layout, Gravity.TOP, 0,400);
            }
        });
    }

    @Override
    public void initRatingPopup(){
        if(rating_popup == null){
            LayoutInflater inflater = (LayoutInflater)   this.getContext().getSystemService(getContext().LAYOUT_INFLATER_SERVICE);
            View popUpFilter = inflater.inflate(R.layout.filter_starbar, null);
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            boolean focusable = true;
            rating_popup= new PopupWindow(popUpFilter, width, height, focusable);
            rating_popup.setAnimationStyle(R.style.PopupAnimation);
        }
        layout.post(new Runnable() {
            @Override
            public void run() {
                rating_popup.showAtLocation(layout, Gravity.TOP, 0,400);
            }
        });
    }

    @Override
    public void initSort(){
        sort_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    @Override
    public void sortBtnChange(int n_sort) {
        if(c_sort == 0){
            c_sort = 1;
            sort_btn.setBackgroundResource(R.drawable.toggle_stroke);
        }else if(c_sort == 1){
            c_sort = 2;
            sort_btn.setBackgroundResource(R.drawable.neon_blue_corner);
        }else{
            c_sort =0;
            sort_btn.setBackgroundResource(R.drawable.nomal_stroke);
        }
    }
}