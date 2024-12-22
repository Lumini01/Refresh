package com.example.refresh;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends AppCompatActivity {

    // Declare UI components
    private Toolbar toolbar;
    private ImageView profileImageView;
    private TextView userNameTextView;
    private TextView emailTextView;
    private Switch notificationsSwitch;
    private Spinner themeSpinner;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile); // Link to XML layout file

        // Initialize the UI components
        toolbar = findViewById(R.id.toolbar);
        profileImageView = findViewById(R.id.profileImage);
        userNameTextView = findViewById(R.id.userNameTextView);
        emailTextView = findViewById(R.id.emailTextView);
        notificationsSwitch = findViewById(R.id.notifications_switch);
        themeSpinner = findViewById(R.id.theme_spinner);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set up the toolbar (you can add a back button or other menu items here if needed)
        setSupportActionBar(toolbar);
        // toolbar.setNavigationIcon(R.drawable.ic_back); // Uncomment if you want a back button

        // Set the profile details
        loadProfileData();

        // Set up the event listeners for settings
        setUpEventListeners();
    }

    private void loadProfileData() {
        // Placeholder: Load user data here (you could fetch from SharedPreferences or a database)
        // Example:
        userNameTextView.setText("User Name");
        emailTextView.setText("user@example.com");
        profileImageView.setImageResource(R.drawable.ic_profile); // Replace with actual image if needed
    }

    private void setUpEventListeners() {
        // 1. Notifications Switch: Toggle notifications settings
        notificationsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Handle notifications toggle (store in SharedPreferences or adjust app behavior)
            if (isChecked) {
                // Enable notifications
            } else {
                // Disable notifications
            }
        });

        // 2. Theme Spinner: Handle theme selection (Light/Dark mode)
        themeSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Handle theme selection (apply Light/Dark theme based on position)
                switch (position) {
                    case 0:
                        // Set Light Theme
                        // You can use AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        break;
                    case 1:
                        // Set Dark Theme
                        // You can use AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        break;
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parentView) {
                // Do nothing if no item is selected
            }
        });

        // 3. Bottom Navigation: Handle item clicks (if needed)
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home_menu:
                    // Navigate to Home activity
                    // startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
                    return true;
                case R.id.profile_menu:
                    // Stay in profile activity
                    return true;
                // Add other menu items here if necessary
            }
            return false;
        });
    }

    // Optional: Handle toolbar actions (e.g., back button, settings options)
    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Handle back button press if added to toolbar
                finish(); // Finish activity
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
