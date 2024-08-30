package com.example.firstapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class FavoriteManager {

    private static final String PREFS_NAME = "FavoritePrefs";
    private static final String FAVORITES_KEY = "Favorites";
    private static FavoriteManager instance;
    private final SharedPreferences sharedPreferences;
    private final Gson gson;
    private Set<String> favoriteProductIds; // Use a Set for efficient lookup
    private final ArrayList<ProductModel> favoriteProducts;
    private final ArrayList<OnFavoritesChangedListener> listeners = new ArrayList<>();

    private FavoriteManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
        favoriteProductIds = loadFavoriteIds();
        favoriteProducts = loadFavorites();
    }

    public static synchronized FavoriteManager getInstance(Context context) {
        if (instance == null) {
            instance = new FavoriteManager(context);
        }
        return instance;
    }

    public ArrayList<ProductModel> getFavoriteProducts() {
        return new ArrayList<>(favoriteProducts);
    }

    public boolean isFavorite(ProductModel product) {
        return favoriteProductIds.contains(product.getId());
    }
    public void addFavorite(ProductModel product) {
        if (!isFavorite(product)) {
            favoriteProductIds.add(product.getId());
            favoriteProducts.add(product);
            saveFavorites(); // Save updated favorites list
            notifyFavoritesChanged(); // Notify listeners about the change
        }
    }

    public void removeFavorite(ProductModel product) {
        if (favoriteProductIds.remove(product.getId())) {
            favoriteProducts.remove(product);
            saveFavorites(); // Save updated favorites list
            notifyFavoritesChanged(); // Notify listeners about the change
        }
    }


    public void addOnFavoritesChangedListener(OnFavoritesChangedListener listener) {
        listeners.add(listener);
    }

    public void removeOnFavoritesChangedListener(OnFavoritesChangedListener listener) {
        listeners.remove(listener);
    }

    private void notifyFavoritesChanged() {
        for (OnFavoritesChangedListener listener : listeners) {
            listener.onFavoritesChanged();
        }
    }

    private ArrayList<ProductModel> loadFavorites() {
        String json = sharedPreferences.getString(FAVORITES_KEY, "[]");
        Type type = new TypeToken<ArrayList<ProductModel>>() {}.getType();
        return gson.fromJson(json, type);
    }

    private Set<String> loadFavoriteIds() {
        Set<String> ids = new HashSet<>();
        for (ProductModel product : loadFavorites()) {
            ids.add(product.getId());
        }
        return ids;
    }

    private void saveFavorites() {
        String json = gson.toJson(favoriteProducts);
        sharedPreferences.edit().putString(FAVORITES_KEY, json).apply();
    }

    public interface OnFavoritesChangedListener {
        void onFavoritesChanged();
    }
}