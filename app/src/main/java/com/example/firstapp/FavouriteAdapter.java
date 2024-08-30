package com.example.firstapp;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.firstapp.databinding.FavoriteItemBinding;

import java.util.ArrayList;

public class FavouriteAdapter extends RecyclerView.Adapter<FavouriteAdapter.Holder> {


    private ArrayList<ProductModel> favouriteList;
    private OnItemClick onItemClick;

    private OnRemoveClick onRemoveClick;

    public void setOnItemClick(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }


    public void setOnRemoveClick(OnRemoveClick onRemoveClick) {
        this.onRemoveClick = onRemoveClick;
    }

    public void setFavouriteList(ArrayList<ProductModel> favouriteList) {
        this.favouriteList = favouriteList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        FavoriteItemBinding binding = FavoriteItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new Holder(binding);
    }


    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.bind(favouriteList.get(position));
    }

    @Override
    public int getItemCount() {
        return favouriteList.size();
    }

    class Holder extends RecyclerView.ViewHolder {

        private FavoriteItemBinding binding;

        public Holder(@NonNull FavoriteItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            // Set click listener for the root view
            binding.getRoot().setOnClickListener(view -> {
                if (onItemClick != null) {
                    onItemClick.onClick(favouriteList.get(getAdapterPosition()));
                }
            });



        }

        public void bind(ProductModel productModel) {
            binding.favName.setText(productModel.getName());
            binding.favPrice.setText(productModel.getPrice());
            // Assuming product.getImageRes() returns a valid image resource or URL
            Glide.with(binding.getRoot().getContext())
                    .load(productModel.getImageRes())
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(16)))
                    .into(binding.favImage);

            // Set click listener for the delete button
            binding.deleteButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && position < favouriteList.size()) {

                    // Remove the item from the list
                    ProductModel removedProduct = favouriteList.remove(position);

                    // Notify the adapter of the removed item
                    notifyItemRemoved(position);

                    // Notify the adapter of the changed range
                    notifyItemRangeChanged(position, favouriteList.size() - position);


                    // Notify any listeners about the change
                    if (onRemoveClick != null) {
                        onRemoveClick.onRemoveClick(removedProduct);
                    }
                    // Update FavoriteManager
                    FavoriteManager.getInstance(itemView.getContext()).removeFavorite(removedProduct);
                }

            });

        }
    }

    public interface OnItemClick {
        void onClick(ProductModel productModel);
    }
    public interface OnRemoveClick {
        void onRemoveClick(ProductModel productModel);
    }


}
