package com.example.firstapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.firstapp.databinding.ActivityProductDetailsBinding;
import com.example.firstapp.databinding.ActivityRegisterBinding;
import com.example.firstapp.databinding.ProductItemBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProductDetails extends AppCompatActivity {

   private ActivityProductDetailsBinding binding;
   private ProductModel product = new ProductModel();

    private DatabaseReference reference = FirebaseDatabase.getInstance()
            .getReference("Product");
    private int quantity = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityProductDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get the product ID from the Intent
        String productId = getIntent().getStringExtra("selectedProductId");

        // Initialize the product's quantity from CartManager
        CartManager cartManager = CartManager.getInstance(this);
        ProductModel cartProduct = cartManager.getProductById(productId);
        if (cartProduct != null) {
            quantity = cartProduct.getQuantity(); // Set the initial quantity to the one in the cart
        }


        reference.child(productId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ProductModel productModel = dataSnapshot.getValue(ProductModel.class);
                        if (productModel != null) {
                            product = productModel; // Store the product details
                            product.setId(productId); // Ensure the ID is set

                            // Display product details
                            Glide.with(binding.getRoot().getContext())
                                    .load(productModel.getImageRes())
                                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(16)))
                                    .into(binding.imageProduct);
                            binding.nameProduct.setText(productModel.getName());
                            binding.priceProduct.setText(productModel.getPrice());
                            binding.details.setText(productModel.getDescription());
                            binding.quantityText.setText(String.valueOf(quantity));                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle the error if needed
                    }
                });
        binding.increaseButton.setOnClickListener(v -> {
            quantity++;
            binding.quantityText.setText(String.valueOf(quantity));
            product.setQuantity(quantity); // Update the product quantity
            CartManager.getInstance(this).updateProductQuantity(product, quantity);
        });

        binding.decreaseButton.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                binding.quantityText.setText(String.valueOf(quantity));
                product.setQuantity(quantity); // Update the product quantity
                CartManager.getInstance(this).updateProductQuantity(product, quantity);
            }
        });


// Handle "Add to Cart" button click
        binding.addToCart.setOnClickListener(v -> {
            addToCart(product);
        });
    }

    private void addToCart(ProductModel product) {
        CartManager cartManager = CartManager.getInstance(this);

        // Check if the product is already in the cart
        ProductModel existingProduct = cartManager.getProductById(product.getId());

        if (existingProduct != null) {
            // Check if the quantity is different from the existing quantity
            if (existingProduct.getQuantity() != quantity) {
                // Update the quantity if it is different
                cartManager.updateProductQuantity(product, quantity);

            }

        } else {
            // Add the product with the current quantity if not already in the cart
            product.setQuantity(quantity); // Set the quantity for the new product
            cartManager.addToCart(product);

            // Create the toast message with the added quantity (initially as specified)
            Toast.makeText(this, product.getName() + " added to cart ", Toast.LENGTH_SHORT).show();
        }
    }





}
