package com.example.firstapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.firstapp.databinding.CartItemBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.Holder> {

    private ArrayList<ProductModel> cartList = new ArrayList<>();
    private OnItemClick onItemClick;
    private OnRemoveClick onRemoveClick;


    public void setOnItemClick(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }
    public void setOnRemoveClick(OnRemoveClick onRemoveClick) {
        this.onRemoveClick = onRemoveClick;
    }
    public void setCartList(ArrayList<ProductModel> cartList) {
        this.cartList = cartList;
        notifyDataSetChanged();  // Notify the adapter to refresh the entire list
    }


    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CartItemBinding binding = CartItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new Holder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.bind(cartList.get(position));
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    class Holder extends RecyclerView.ViewHolder {

        private  CartItemBinding binding;

        public Holder(@NonNull CartItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            // Set click listener for the root view
            binding.getRoot().setOnClickListener(view -> {
                if (onItemClick != null) {
                    onItemClick.onClick(cartList.get(getAdapterPosition()));
                }
            });

            // Set click listener for the delete button
            binding.deleteButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && position < cartList.size()) {
                    ProductModel product = cartList.get(position);

                    // Remove the item from the list
                    cartList.remove(position);

                    // Notify the adapter of the removed item
                    notifyItemRemoved(position);

                    // Notify the adapter of the changed range
                    notifyItemRangeChanged(position, cartList.size() - position);

                    // Notify CartManager to update the data
                    CartManager.getInstance(v.getContext()).removeFromCart(product);

                    // Notify any listeners about the change
                    if (onRemoveClick != null) {
                        onRemoveClick.onRemoveClick(product);
                    }
                }
            });

            // Set click listener for the decrease button
            binding.decreaseButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    ProductModel product = cartList.get(position);
                    int currentQuantity = product.getQuantity(); // Assuming ProductModel has a `quantity` field
                    if (currentQuantity > 1) {
                        product.setQuantity(currentQuantity - 1);
                        CartManager.getInstance(v.getContext()).updateProductQuantity(product, currentQuantity - 1);
                        notifyItemChanged(position);
                    }
                }
            });

            // Set click listener for the increase button
            binding.increaseButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    ProductModel product = cartList.get(position);
                    int currentQuantity = product.getQuantity(); // Assuming ProductModel has a `quantity` field
                    product.setQuantity(currentQuantity + 1);
                    CartManager.getInstance(v.getContext()).updateProductQuantity(product, currentQuantity + 1);
                    notifyItemChanged(position);
                }
            });


        }

        public void bind(ProductModel productModel) {
            binding.cartName.setText(productModel.getName());
            binding.cartPrice.setText(productModel.getPrice());
            binding.quantityText.setText(String.valueOf(productModel.getQuantity())); // Display the current quantity
            // Assuming productModel.getImageRes() returns a valid image URL or resource
            Glide.with(binding.getRoot().getContext())
                    .load(productModel.getImageRes())
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(16))) // 16 is the corner radius
                    .into(binding.cartImage);
        }
    }

    public interface OnItemClick {
        void onClick(ProductModel productModel);
    }

    public interface OnRemoveClick {
        void onRemoveClick(ProductModel productModel);
    }
}
