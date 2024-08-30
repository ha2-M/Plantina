package com.example.firstapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firstapp.databinding.ItemChatBinding;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.holher> {
    private ArrayList<ChatModel> list;

    public void setList(ArrayList<ChatModel> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public holher onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemChatBinding binding= ItemChatBinding.inflate(LayoutInflater.
                from(parent.getContext()),parent,false);


        return new holher(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.holher holder, int position) {
        holder.bind(list.get(position));

    }

    @Override
    public int getItemCount() {
        return  list.size();
    }

    public class holher extends RecyclerView.ViewHolder {
        private ItemChatBinding binding;

        public holher(@NonNull ItemChatBinding binding) {
            super(binding.getRoot());
            this.binding = binding;


        }

        public void bind(ChatModel chat) {
            if (chat.getType().equals(MessageType.TEXT.name())) {
                binding.textChat.setVisibility(View.VISIBLE);
                binding.imageChat.setVisibility(View.GONE);
                binding.textChat.setText(chat.getMessage());
            }
        }
    }

}
