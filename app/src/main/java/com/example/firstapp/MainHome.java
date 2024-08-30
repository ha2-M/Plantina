package com.example.firstapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.firstapp.databinding.ActivityMainHomeBinding;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class MainHome extends AppCompatActivity {


   private ActivityMainHomeBinding binding;
   private TabAdapter tabAdapter = new TabAdapter(getSupportFragmentManager(), getLifecycle());
    private ArrayList<TabModel> list = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        //add fragment in array
        list.add(new TabModel(new HomeFragment(), R.drawable.home));
        list.add(new TabModel(new CartFragment(), R.drawable.cart));
        list.add(new TabModel(new FavoriteFragment(), R.drawable.favorite));
        list.add(new TabModel(new ProfileFragment(), R.drawable.profile));

        // send to adapter list of fragment
        tabAdapter.setFragments(list);
        // adapter show fragment in view pager
        binding.pager.setAdapter(tabAdapter);

        new TabLayoutMediator(binding.MaintabLayout, binding.pager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {

                tab.setIcon(list.get(position).getImageResId());
            }
        }).attach();

        // Handle the intent that started this activity
        handleIntent(getIntent());
        }

        @Override
        protected void onNewIntent (Intent intent){
            super.onNewIntent(intent);
            // Handle intents if this activity was already running
            handleIntent(intent);
        }

        private void handleIntent (Intent intent){
            if (intent != null && intent.hasExtra("fragment_to_open")) {
                String fragmentToOpen = intent.getStringExtra("fragment_to_open");
                switch (fragmentToOpen) {
                    case "home_fragment":
                        switchToFragment(new HomeFragment());
                        break;
                    case "cart_fragment":
                        switchToFragment(new CartFragment());
                        break;
                    case "favourite_fragment":
                        switchToFragment(new FavoriteFragment());
                        break;
                    case "profile_fragment":
                        switchToFragment(new ProfileFragment());
                        break;

                }
            }
        }
        private void switchToFragment (Fragment fragment){
            int position = tabAdapter.getFragmentPosition(fragment);
            if (position != -1) {
                tabAdapter.replaceFragment(fragment, position);
                tabAdapter.notifyDataSetChanged();
                binding.pager.setCurrentItem(position);
            }
        }

}





