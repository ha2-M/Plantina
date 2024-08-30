package com.example.firstapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firstapp.databinding.FragmentFavoriteBinding;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Map;

public class FavoriteFragment extends Fragment {

    private FragmentFavoriteBinding binding;
    private FavouriteAdapter favoriteAdapter=  new FavouriteAdapter();
    private final FavoriteManager.OnFavoritesChangedListener
            onFavoritesChangedListener = this::updateFavoriteList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFavoriteBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set up RecyclerView
        binding.recyclerViewFavorite.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewFavorite.setAdapter(favoriteAdapter);

        // Initialize with current favorite products
        updateFavoriteList();

        // Register listener for changes
        FavoriteManager.getInstance(getContext()).addOnFavoritesChangedListener(
                onFavoritesChangedListener);

        // Set up item click listener
        favoriteAdapter.setOnItemClick(product -> {
            Intent intent = new Intent(getContext(), ProductDetails.class);
            intent.putExtra("selectedProductId", product.getId());
            startActivity(intent);
        });
    }

    private void updateFavoriteList() {
        ArrayList<ProductModel> updatedFavorites = FavoriteManager.
                getInstance(getContext()).getFavoriteProducts();
        favoriteAdapter.setFavouriteList(updatedFavorites);
        favoriteAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        FavoriteManager.getInstance(getContext()).
                removeOnFavoritesChangedListener(onFavoritesChangedListener);
        binding = null;
    }
}