package com.example.refresh.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.refresh.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends AppCompatActivity {

    // Toolbar components
    private ImageView backArrow;
    private TextView toolbarTitle;
    private ImageButton settingsButton;

    // Profile Section components
    private ImageView profilePicture;
    private TextView profileName, profileAge, currentWeight, weightGoal, dietType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize Views
        initViews();

        // Set up Toolbar
        setupToolbar();

        // Set up Bottom Navigation
        setupBottomNavigation();
    }

    private void initViews() {
        // Toolbar components
        backArrow = findViewById(R.id.backArrow);
        toolbarTitle = findViewById(R.id.toolbarTitle);
        settingsButton = findViewById(R.id.settingsButton);

        // Profile Section components
        profilePicture = findViewById(R.id.profilePicture);
        profileName = findViewById(R.id.profileName);
        profileAge = findViewById(R.id.profileAge);
        currentWeight = findViewById(R.id.currentWeight);
        weightGoal = findViewById(R.id.weightGoal);
        dietType = findViewById(R.id.dietType);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Handle back arrow click
        backArrow.setOnClickListener(v -> onBackPressed());

        // Handle settings button click
        settingsButton.setOnClickListener(v -> {
            // TODO: Navigate to Settings screen or perform settings-related actions
        });
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_today) {
                // TODO: Navigate to Today screen
                return true;
            } else if (itemId == R.id.nav_progress) {
                // TODO: Navigate to Progress screen
                return true;
            } else if (itemId == R.id.nav_log) {
                // TODO: Navigate to Log screen
                return true;
            } else if (itemId == R.id.nav_suggestions) {
                // TODO: Navigate to Suggestions screen
                return true;
            } else if (itemId == R.id.nav_recipes) {
                // TODO: Navigate to Recipes screen
                return true;
            }
            return false;
        });
    }

    private void populateProfileData() {
        // Simulated data fetching and updating views
        profileName.setText("John Doe");
        profileAge.setText("Age: 30");
        currentWeight.setText("Current Weight: 75kg");
        weightGoal.setText("Goal: Maintain Weight");
        dietType.setText("Diet: Vegetarian");

        // Optionally load profile picture with a library like Glide or Picasso
        // Glide.with(this).load(profilePictureUrl).into(profilePicture);
    }
}
