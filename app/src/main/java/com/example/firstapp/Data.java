package com.example.firstapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.firstapp.databinding.ActivityDataBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

public class Data extends AppCompatActivity {

    private ActivityDataBinding binding;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    private String uriImage;

    private ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri dataUri = result.getData().getData();
                        if (dataUri != null) {
                            uriImage = dataUri.toString();
                            binding.image.setImageURI(dataUri);
                        }
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDataBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToGallery();
            }
        });

        binding.addUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = binding.name.getText().toString().trim();
                String price = binding.price.getText().toString().trim();
                String description = binding.description.getText().toString().trim();
                int quntity=1;
                if (name.isEmpty() || price.isEmpty() ||
                        description.isEmpty() || uriImage == null) {
                    Toast.makeText(Data.this,
                            "Please fill all fields and select an image.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                uploadImage(auth.getUid(), name, price, description, Uri.parse(uriImage),quntity);
            }
        });
    }

    private void goToGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        activityResultLauncher.launch(intent);
    }

    private void uploadImage(String userId, String name, String price, String description, Uri uriImage,int quntity) {
        StorageReference storage = storageReference.child("UserImages/")
                .child(System.currentTimeMillis() + userId);

        storage.putFile(uriImage)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                saveProduct( name, price, description, uri.toString(),quntity);
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Data.this, "Image upload failed: "
                                + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveProduct( String name, String price,
                             String description, String imageUrl,int quntity) {

// Get a reference to the "Product" node
        DatabaseReference productRef = reference.child("Product");

        // Generate a new unique key for the product
        DatabaseReference newProductRef = productRef.push();

        // Create a unique product ID using the generated key
        String uniqueId = newProductRef.getKey();

        // Create a ProductModel object with the unique ID
        ProductModel product = new ProductModel(uniqueId, name, price, description, imageUrl,quntity);


        // Set the value at the new reference location
        newProductRef.setValue(product)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        clearInputs();
                        startActivity(new Intent(Data.this, MainHome.class));
                        finishAffinity();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Data.this, "Product save failed: "
                                + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void clearInputs() {
        binding.name.setText("");
        binding.price.setText("");
        binding.description.setText("");
        binding.image.setImageURI(null);
        uriImage = null;
    }
}