package com.example.firstapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ButtonProductAdapter extends RecyclerView.Adapter<ButtonProductAdapter.ViewHolder> {
    private ArrayList<String> btnProductList;

    private OnItemClick onItemClick;


    public void setOnItemClick(OnItemClick onItemClick) {

        this.onItemClick = onItemClick;
    }

    public void setProductList(ArrayList<String> productList) {
        this.btnProductList = productList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.btn_product_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.btnProductName.setText(String.valueOf(btnProductList.get(position)));


    }

    @Override
    public int getItemCount() {

        return btnProductList == null ? 0 : btnProductList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView btnProductName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            btnProductName = itemView.findViewById(R.id.btn_product);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onItemClick != null) {
                        onItemClick.OnClick(btnProductList.get(getLayoutPosition()));
                    }
                }
            });
        }

    }

    public interface OnItemClick {

        void OnClick(String productName);

    }


}
