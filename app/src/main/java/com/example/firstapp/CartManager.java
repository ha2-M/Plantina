package com.example.firstapp;


import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private static final String PREFS_NAME = "CartPrefs";
    private static final String CART_KEY = "CartItems";
    private static CartManager instance;
    private final SharedPreferences sharedPreferences;
    private final Gson gson;
    private ArrayList<ProductModel> cartProducts;
    private final List<OnCartChangedListener> listeners = new ArrayList<>();


    private CartManager(Context context) {

        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
        cartProducts = loadCartFromPreferences();
    }

    public static synchronized CartManager getInstance(Context context) {
        if (instance == null) {
            instance = new CartManager(context);
        }
        return instance;
    }

    public ArrayList<ProductModel> getCartProducts() {
        return new ArrayList<>(cartProducts); // Return a copy of the cart
    }

    public void addToCart(ProductModel product) {
        int existingProductIndex = findProductIndex(product);

        if (existingProductIndex != -1) {
            // If the product is already in the cart, update its quantity
            ProductModel existingProduct = cartProducts.get(existingProductIndex);
            existingProduct.setQuantity(existingProduct.getQuantity() + product.getQuantity());
        } else {
            // If the product is not in the cart, add it to the cart
            cartProducts.add(product);
        }

        saveCartToPreferences(); // Save the updated cart to preferences
        notifyCartChanged(); // Notify listeners about the cart change
    }


    public boolean isInCart(ProductModel product) {
        return cartProducts.contains(product);
    }


    public void removeFromCart(ProductModel product) {
        if (cartProducts.remove(product)) {
            saveCartToPreferences();
            notifyCartChanged();
        }
        int existingProductIndex = findProductIndex(product);
        if (existingProductIndex != -1) {
            cartProducts.remove(existingProductIndex);
            saveCartToPreferences();
            notifyCartChanged();
        }
    }
    public ProductModel getProductById(String productId) {
        for (ProductModel cartProduct : cartProducts) {
            if (cartProduct.getId().equals(productId)) {
                return cartProduct; // Return the matching product
            }
        }
        return null; // Return null if not found
    }
    public void addOnCartChangedListener(OnCartChangedListener listener) {
        listeners.add(listener);
    }

    public void removeOnCartChangedListener(OnCartChangedListener listener) {
        listeners.remove(listener);
    }

    public void notifyCartChanged() {
        for (OnCartChangedListener listener : listeners) {
            listener.onCartChanged();
        }

    }

    private ArrayList<ProductModel> loadCartFromPreferences() {
        String jsonCart = sharedPreferences.getString(CART_KEY, "[]");
        Type type = new TypeToken<ArrayList<ProductModel>>() {}.getType();
        return gson.fromJson(jsonCart, type);
    }

    public void saveCartToPreferences() {
        String jsonCart = gson.toJson(cartProducts);
        sharedPreferences.edit().putString(CART_KEY, jsonCart).apply();
    }
    private int findProductIndex(ProductModel product) {
        for (int i = 0; i < cartProducts.size(); i++) {
            if (cartProducts.get(i).getId().equals(product.getId())) {
                return i;
            }
        }
        return -1;
    }
    public void updateProductQuantity(ProductModel product, int newQuantity) {
        for (ProductModel cartProduct : cartProducts) {
            if (cartProduct.getId().equals(product.getId())) {
                cartProduct.setQuantity(newQuantity);
                saveCartToPreferences();
                notifyCartChanged();
                break;
            }
        }
    }


    public interface OnCartChangedListener {
        void onCartChanged();
    }

}
