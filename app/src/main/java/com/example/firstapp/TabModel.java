package com.example.firstapp;

import androidx.fragment.app.Fragment;

public class TabModel {

    //class for data of tabs
    private Fragment fragment;

    private int imageResId;

    public TabModel(Fragment fragment, int imageResId) {
        this.fragment = fragment;
        this.imageResId = imageResId;
    }

    public Fragment getFragment() {
        return fragment;
    }

    public int getImageResId() {
        return imageResId;
    }
}
