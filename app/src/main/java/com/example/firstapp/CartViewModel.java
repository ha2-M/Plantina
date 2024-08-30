package com.example.firstapp;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CartViewModel extends ViewModel {
    private final MutableLiveData<Integer> itemCountLiveData = new MutableLiveData<>();

    public LiveData<Integer> getItemCount() {
        return itemCountLiveData;
    }

    public void setItemCount(int count) {
        itemCountLiveData.setValue(count);
    }
}