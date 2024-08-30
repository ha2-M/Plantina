package com.example.firstapp;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.firstapp.databinding.ProductItemBinding;

import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    private ArrayList<ProductModel> productList;
    private OnItemClick onItemClick;
    private OnFavoriteClick onFavoriteClick;

    private OnCartClick onCartClick;



    public void addItem(ProductModel product, Context context) {
        if (!productList.contains(product)) {
            productList.add(product);
            notifyItemInserted(productList.size() - 1);
        } else {
            Toast.makeText(context, "Product already added", Toast.LENGTH_SHORT).show();
        }
    }

    public void setOnItemClick(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }
    public void setOnFavoriteClick(OnFavoriteClick onFavoriteClick) {
        this.onFavoriteClick = onFavoriteClick;
    }

    public void setOnCartClick(OnCartClick onCartClick) {
        this.onCartClick = onCartClick;
    }

    public  void setProductList(ArrayList<ProductModel> productList){
        this.productList = productList;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout using View Binding
        ProductItemBinding binding = ProductItemBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        holder.bind(productList.get(position));


    }



    @Override
    public int getItemCount() {

        return productList==null?0: productList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ProductItemBinding binding;

        public ViewHolder(@NonNull ProductItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;



            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClick.onClick(productList.get(getLayoutPosition()));

                }
            });
            binding.favoriteButton.setOnClickListener(v -> {
                // Notify the adapter to refresh the view
                FavoriteManager favoriteManager = FavoriteManager.getInstance(v.getContext());
                ProductModel product = productList.get(getAdapterPosition());
                if (!favoriteManager.isFavorite(product)) {
                    favoriteManager.addFavorite(product);
                    binding.favoriteButton.setColorFilter(Color.RED);
                    Toast.makeText(v.getContext(), product.getName() + " added to favorites", Toast.LENGTH_SHORT).show();
                } else {
                    favoriteManager.removeFavorite(product);
                    binding.favoriteButton.setColorFilter(Color.BLACK);
                    Toast.makeText(v.getContext(), product.getName() + " removed from favorites", Toast.LENGTH_SHORT).show();
                }

                // Notify the adapter to refresh the item
                notifyItemChanged(getAdapterPosition());

            });


        }

        public void bind(ProductModel product) {

            binding.productName.setText(product.getName());
            binding.productPrice.setText(product.getPrice());
            Glide.with(binding.getRoot().getContext())
                    .load(product.getImageRes())
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(16))) // 16 is the corner radius, you can adjust it
                    .into(binding.productImage);

            // Set click listener for add to cart
            binding.addToCartButton.setOnClickListener(v -> {
                CartManager cartManager = CartManager.getInstance(v.getContext());
                if (!cartManager.isInCart(product))  {
                    cartManager.addToCart(product);
                    Toast.makeText(v.getContext(), product.getName() +" added to cart", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(v.getContext(), product.getName() +" already in cart", Toast.LENGTH_SHORT).show();
                }
            });


            // Update favorite button color based on whether the product is in favorites
            FavoriteManager favoriteManager = FavoriteManager.
                    getInstance(binding.getRoot().getContext());

            if (favoriteManager.isFavorite(product)) {
                binding.favoriteButton.setColorFilter(Color.RED);
            } else {
                binding.favoriteButton.setColorFilter(Color.BLACK);
            }

        }

    }


    //filter

    public void filterList(ArrayList<ProductModel> filteredList) {
        productList = filteredList;
        notifyDataSetChanged();
    }

    interface OnItemClick{
        void onClick(ProductModel productModel);
    }
    public interface OnFavoriteClick {
        void onFavoriteClick(ProductModel product);
    }

    public interface OnCartClick {
        void OnCartClick(ProductModel product);
    }
}