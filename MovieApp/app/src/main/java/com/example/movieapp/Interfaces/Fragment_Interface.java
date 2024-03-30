package com.example.movieapp.Interfaces;

import android.widget.PopupWindow;

public interface Fragment_Interface {


    public void initFilterBar();
    public void initFilterPopup();
    public void initRatingPopup();

    public void initSort();

    public void sortBtnChange(int n_sort);
}
