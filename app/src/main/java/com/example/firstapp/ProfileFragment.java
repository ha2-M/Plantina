package com.example.firstapp;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.firstapp.databinding.FragmentHomeBinding;
import com.example.firstapp.databinding.FragmentProfileBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ProfileFragment extends Fragment {
   private FragmentProfileBinding binding;
   private FirebaseAuth auth = FirebaseAuth.getInstance();
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users")
            .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

    private ActivityResultLauncher<Intent> activityResultLauncher;
    private String profileImageUrl; // Store the URL of the uploaded image

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentProfileBinding.bind(view);

        binding.btnLogOut.setOnClickListener(v -> {
            auth.signOut();
            signOutUser();
        });

        binding.profileImage.setOnClickListener(v -> goToGallery());

        binding.btnEdit.setOnClickListener(v -> {
            binding.yourName.setEnabled(true);
            binding.yourPhone.setEnabled(true);
            binding.yourEmail.setEnabled(true);
            binding.btnSave.setVisibility(View.VISIBLE);
        });

        binding.btnSave.setOnClickListener(v -> {
            saveUserData();
            binding.yourName.setEnabled(false);
            binding.yourPhone.setEnabled(false);
            binding.yourEmail.setEnabled(false);
            binding.btnSave.setVisibility(View.GONE);
        });

//to add data to firebase
//        binding.btnData.setOnClickListener(v -> {
//            startActivity(new Intent(getContext(), Data.class));
//                });

        FirebaseUser firebaseUser = auth.getCurrentUser();
        if (firebaseUser != null) {
            showData(firebaseUser);
        } else {
            Toast.makeText(getContext(), "Please Login", Toast.LENGTH_SHORT).show();
        }

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri dataUri = result.getData().getData();
                        if (dataUri != null) {
                            uploadImage(dataUri);
                        }
                    }
                });
    }

    private void signOutUser() {
        Intent intent = new Intent(getContext(), Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void showData(FirebaseUser firebaseUser) {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserModel user = dataSnapshot.getValue(UserModel.class);
                if (user != null) {
                    binding.yourName.setText(user.getName());
                    binding.yourEmail.setText(user.getEmail());
                    binding.yourPhone.setText(user.getPhone());

                    profileImageUrl = user.getImageRes(); // Store the profile image URL
                    if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                        Glide.with(getContext()).load(profileImageUrl).into(binding.profileImage);
                    }

                    binding.yourName.setEnabled(false);
                    binding.yourPhone.setEnabled(false);
                    binding.yourEmail.setEnabled(false);
                    binding.btnSave.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void goToGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        activityResultLauncher.launch(intent);
    }

    private void uploadImage(Uri uriImage) {
        StorageReference storage = storageReference.child("UserImages/")
                .child(System.currentTimeMillis() + "_" + auth.getUid());

        storage.putFile(uriImage)
                .addOnSuccessListener(taskSnapshot -> storage.getDownloadUrl().addOnSuccessListener(uri -> {
                    profileImageUrl = uri.toString(); // Update the profile image URL
                    // Save the download URL in the user's profile
                    databaseReference.child("imageRes").setValue(profileImageUrl);
                    binding.profileImage.setImageURI(uriImage); // Update profile image immediately
                }))
                .addOnFailureListener(e -> Toast.makeText(getContext(),
                        "Image upload failed: " + e.getLocalizedMessage(),
                        Toast.LENGTH_SHORT).show());
    }

    private void saveUserData() {
        UserModel user = new UserModel();
        user.setName(binding.yourName.getText().toString());
        user.setPhone(binding.yourPhone.getText().toString());
        user.setEmail(binding.yourEmail.getText().toString());
        user.setImageRes(profileImageUrl); // Include the profile image URL

        databaseReference.setValue(user);
    }
}
