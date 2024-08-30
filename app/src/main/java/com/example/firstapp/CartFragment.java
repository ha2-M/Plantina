package com.example.firstapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.firstapp.databinding.FragmentCartBinding;

import java.util.ArrayList;

public class CartFragment extends Fragment  {

        private FragmentCartBinding binding;
         private CartManager cartManager;
        private CartAdapter cartAdapter = new CartAdapter();

    private CartViewModel cartViewModel;
    private final CartManager.OnCartChangedListener
            onCartChangedListener = this::updateCartList;

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            binding = FragmentCartBinding.inflate(inflater, container, false);
            return binding.getRoot();
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            // Initialize ViewModel
            cartViewModel = new ViewModelProvider(requireActivity()).
                    get(CartViewModel.class);

            // Set up RecyclerView

            binding.recyclerViewChart.setLayoutManager(new LinearLayoutManager(getContext()));
            binding.recyclerViewChart.setAdapter(cartAdapter);

            // Initialize cart items
            updateCartList();

            // Set up listeners
            CartManager.getInstance(getContext()).
                    addOnCartChangedListener(onCartChangedListener);


            // Set up item
            cartAdapter.setOnItemClick(product -> {
                Intent intent = new Intent(getContext(), ProductDetails.class);
                intent.putExtra("selectedProductId", product.getId());
                startActivity(intent);
            });
           binding.check.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   Toast.makeText(getContext(), "Order Successfully",
                           Toast.LENGTH_SHORT).show();
               }
           });
        }

    private void updateCartList() {
        ArrayList<ProductModel> cartItems = CartManager.getInstance(getContext())
                .getCartProducts();
        cartAdapter.setCartList(cartItems);
        cartAdapter.notifyDataSetChanged();  // Update UI

        // Update ViewModel with item count
        cartViewModel.setItemCount(cartItems.size());

        // Calculate and display order summary
        calculateOrderSummary(cartItems);
    }
    private void calculateOrderSummary(ArrayList<ProductModel> cartItems) {
        double totalAmount = 0.0;
        boolean hasItems = !cartItems.isEmpty();

        // Calculate total amount based on price and quantity
        for (ProductModel item : cartItems) {
            double price = 0.0;
            // Parse price from string to double, handle format errors
            if (item.getPrice() != null && !item.getPrice().isEmpty()) {
                try {
                    price = Double.parseDouble(item.getPrice().replace(" EGP", "").trim());

                } catch (NumberFormatException e) {
                    // Handle invalid price format, default to 0.0
                    price = 0.0;
                }
            }
            // Ensure quantity is valid
            int quantity = item.getQuantity() > 0 ? item.getQuantity() : 0;

            // Add to total amount
            totalAmount += price * quantity;
        }

        // Define delivery charge based on cart content
        double deliveryCharge = hasItems ? 5.00 : 0.00; // Adjust delivery charge as needed

        // Calculate the order total
        double orderTotal = totalAmount + deliveryCharge;

        // Update UI with formatted prices
        binding.textViewOrderTotalValue.setText(String.format("%.2f EGP", totalAmount));
        binding.textViewDeliveryChargeValue.setText(String.format("%.2f EGP",
                deliveryCharge));

        if (hasItems) {
            binding.textViewDeliveryChargeValue.setVisibility(View.VISIBLE);
        } else {

            binding.textViewDeliveryChargeValue.setText("0.00 EGP");

        }

        binding.textViewTotalValue.setText(String.format("%.2f EGP", orderTotal));
    }


    @Override
    public void onResume() {
        super.onResume();
        updateCartList();

    }
    @Override
        public void onDestroyView() {
            super.onDestroyView();
            binding = null;
        CartManager.getInstance(getContext()).removeOnCartChangedListener(
                onCartChangedListener);

    }
    }