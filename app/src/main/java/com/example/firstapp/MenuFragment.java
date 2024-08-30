package com.example.firstapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.firstapp.databinding.FragmentMenuBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MenuFragment extends DialogFragment {
    private FragmentMenuBinding binding;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance()
            .getReference("Users")
            .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMenuBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        FirebaseUser firebaseUser = auth.getCurrentUser();

        if (firebaseUser == null) {
            Toast.makeText(getContext(), "Please Login", Toast.LENGTH_SHORT).show();
        } else {
            showData(firebaseUser);
        }

        // Setup menu item click listeners
        setupMenuListeners();


    }

    private void setupMenuListeners() {
        binding.homeMenu.setOnClickListener(v -> navigateToFragment("home_fragment"));
        binding.cartMenu.setOnClickListener(v -> navigateToFragment("cart_fragment"));
        binding.favouriteMenu.setOnClickListener(v -> navigateToFragment("favourite_fragment"));
        binding.profileMenu.setOnClickListener(v -> navigateToFragment("profile_fragment"));
        binding.chatbotMenu.setOnClickListener(v -> Toast.makeText(getContext(), "Coming Soon", Toast.LENGTH_SHORT).show());
        binding.logoutMenu.setOnClickListener(v -> {
            auth.signOut();
            signOutUser();
        });
    }

    private void navigateToFragment(String fragmentName) {
        Intent intent = new Intent(getActivity(), MainHome.class);
        intent.putExtra("fragment_to_open", fragmentName);
        startActivity(intent);

    }

    private void signOutUser() {
        Intent intent = new Intent(getActivity(), Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        dismiss();
    }

    private void showData(FirebaseUser firebaseUser) {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserModel user = dataSnapshot.getValue(UserModel.class);
                if (user != null) {
                    binding.name.setText(user.getName());
                    Glide.with(binding.getRoot().getContext())
                            .load(user.getImageRes())
                            .circleCrop()
                            .into(binding.profileImage);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            // Get the screen width and height
            int screenWidth = getResources().getDisplayMetrics().widthPixels;
            int screenHeight = getResources().getDisplayMetrics().heightPixels;

            // Set the dialog window's size to be smaller than the full screen
            int dialogWidth = (int) (screenWidth * 0.8);
            int dialogHeight = (int) (screenHeight * 0.75);

            // Apply the size and center the dialog
            getDialog().getWindow().setLayout(dialogWidth, dialogHeight);
            getDialog().getWindow().setGravity(Gravity.CENTER);
        }
    }




}