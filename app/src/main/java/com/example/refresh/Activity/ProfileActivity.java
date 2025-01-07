package com.example.refresh.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentContainerView;

import com.example.refresh.R;
import com.example.refresh.Fragments.SettingsFragment;

public class ProfileActivity extends AppCompatActivity {

    // Toolbar components
    private ImageView backArrow;
    private TextView toolbarTitle;
    private ImageButton settingsButton;

    // Profile Section components
    private ImageView profilePicture;
    private TextView profileName, profileAge, currentWeight, weightGoal, dietType;
    private FragmentContainerView fragmentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize Views
        initViews();

        // Set up Toolbar
        setupToolbar();
    }

    private void initViews() {
        // Toolbar components
        backArrow = findViewById(R.id.backArrow);
        toolbarTitle = findViewById(R.id.toolbarTitle);
        settingsButton = findViewById(R.id.settings_button);

        // Profile Section components
        profilePicture = findViewById(R.id.profilePicture);
        profileName = findViewById(R.id.profileName);
        profileAge = findViewById(R.id.profileAge);
        currentWeight = findViewById(R.id.currentWeight);
        weightGoal = findViewById(R.id.weightGoal);
        dietType = findViewById(R.id.dietType);

        fragmentContainer = findViewById(R.id.fragment_container);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Handle back arrow click
        backArrow.setOnClickListener(v -> finish());

        // Handle settings button click
        settingsButton.setOnClickListener(v -> {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new SettingsFragment())
                    .commit();

            fragmentContainer.setVisibility(View.VISIBLE); // Ensure visibility
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
