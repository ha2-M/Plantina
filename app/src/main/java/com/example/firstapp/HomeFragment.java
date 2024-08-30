package com.example.firstapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.firstapp.databinding.FragmentHomeBinding;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.ArrayList;



public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private DatabaseReference reference = FirebaseDatabase.getInstance()
            .getReference("Product");

    private ArrayList<ProductModel> productList = new ArrayList<>();

    private ArrayList<String> btnProductList = new ArrayList<>();

    private ProductAdapter productAdapter = new ProductAdapter();

    private CartViewModel cartViewModel;
    private ButtonProductAdapter buttonProductAdapter = new ButtonProductAdapter();
    private ChildEventListener childEventListener;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentHomeBinding.bind(view);

        // Initialize ViewModel
        cartViewModel = new ViewModelProvider(requireActivity()).get(CartViewModel.class);
        cartViewModel.getItemCount().observe(getViewLifecycleOwner(),
                this::updateCartItemCount);

        setupUI();
        setupRecyclerView();
        setupButtonRecyclerView();
        setupSearchView();
        fetchProducts();  // fetching products from Firebase


    }
    private void setupUI() {
        binding.menuIcon.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
               MenuFragment menuFragment = new MenuFragment();
                menuFragment.show(getChildFragmentManager(), menuFragment.getTag());
            }
        });


        binding.cartIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MainHome.class);
                intent.putExtra("fragment_to_open", "cart_fragment");
                startActivity(intent);
            }
        });

    }

    private void setupRecyclerView() {
        productAdapter.setProductList(productList);
        binding.recyclerProducts.setAdapter(productAdapter);

        // Set item click listener
        productAdapter.setOnItemClick(product -> {
            Intent intent = new Intent(getContext(), ProductDetails.class);
            intent.putExtra("selectedProductId", product.getId());
            startActivity(intent);
        });

        // Set up favorite button click listener
        productAdapter.setOnFavoriteClick(product -> {
            FavoriteManager.getInstance(getContext()).addFavorite(product);
        });
        productAdapter.setOnCartClick(product -> {
            CartManager.getInstance(getContext()).addToCart(product);
        });
    }

    private void setupButtonRecyclerView() {
        btnProductList.add("ALL");
        btnProductList.add("Indoor");
        btnProductList.add("Flower");
        btnProductList.add("Outdoor");
        btnProductList.add("Garden");
        btnProductList.add("Office");

        buttonProductAdapter.setProductList(btnProductList);
        binding.btnItemRecyclerView.setAdapter(buttonProductAdapter);

        buttonProductAdapter.setOnItemClick(castId -> filterProductsByName(castId));
    }

    private void setupSearchView() {
        SearchView searchView = binding.searchPlants;
        searchView.clearFocus();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return true;
            }
        });
    }

    private void filterList(String text) {
        ArrayList<ProductModel> filteredList = new ArrayList<>();
        for (ProductModel item : productList) {
            if (item.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }

        if (filteredList.isEmpty()) {
            Toast.makeText(getContext(), "Not Found", Toast.LENGTH_SHORT).show();        }
        else {
            productAdapter.setProductList(filteredList);
            productAdapter.notifyDataSetChanged();
        }
    }

    private void filterProductsByName(String name) {
        ArrayList<ProductModel> filteredList = new ArrayList<>();

        if (name.equalsIgnoreCase("all") || name.trim().isEmpty()) {
            // Show all products if "all" is selected or the search term is empty
            productAdapter.setProductList(productList);
        } else {
            // Filter products based on partial name match
            for (ProductModel product : productList) {
                if (product.getName().toLowerCase().contains(name.toLowerCase())) {
                    filteredList.add(product);
                }
            }
            productAdapter.setProductList(filteredList);
        }

        // Notify the adapter about the data set change
        productAdapter.notifyDataSetChanged();
    }


    private void fetchProducts() {

        productList.clear();
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                ProductModel productModel = dataSnapshot.getValue(ProductModel.class);
                if (productModel != null && !productList.contains(productModel)) {
                    productList.add(productModel);
                    productAdapter.notifyItemInserted(productList.size() - 1);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load products.", Toast.LENGTH_SHORT).show();
            }
        };
        reference.addChildEventListener(childEventListener);
    }

    private void updateCartItemCount(Integer count) {
        if (count != null) {
            binding.cartItemCount.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
            binding.cartItemCount.setText(String.valueOf(count));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        productAdapter.notifyDataSetChanged();

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        reference.removeEventListener(childEventListener); // Remove Firebase listener
        binding = null;
    }
}
