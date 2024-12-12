package com.example.refresh;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.view.View;
import android.Manifest;

import com.example.refresh.Activity.HomeDashboard;
import com.example.refresh.Activity.Login;
import com.example.refresh.Activity.SignUp;

import java.util.Arrays;
import java.util.HashSet;

/**
 * Start Activity - This is the initial screen of the app before login,
 * featuring a countdown timer and a button to proceed to the login screen.
 */
public class Start extends AppCompatActivity {

    // UI Components
    private LinearLayout mainLayout;
    private TextView brandName;
    private ImageView logo;
    private Button continueBTN;
    private TextView cdtView;  // Countdown timer text
    private CountDownTimer timer;  // Countdown timer object

    /**
     * Initializes the activity, sets up the UI components, and starts the countdown timer.
     * @param savedInstanceState saved instance state bundle for restoring previous activity state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        // Setting edge-to-edge padding to account for system UI (status bar, navigation bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI components
        mainLayout = findViewById(R.id.main);
        brandName = findViewById(R.id.brandName);
        logo = findViewById(R.id.logo);
        continueBTN = findViewById(R.id.continueBTN);
        cdtView = findViewById(R.id.cdt);

        // Set up the 'Continue' button click listener to transition to the login screen
        continueBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToLogin();
                timer.cancel();  // Stop the countdown timer when the user clicks continue
            }
        });

        // Start the countdown timer
        startCountdownTimer();

        // Request notification permission
        requestPostNotificationPermission();

        // Set default notification times
        setDefaultNotificationInstances();

        Context context = getApplicationContext();
        TestingGrounds.test(context);

        // Hide the action bar temporarily
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    /**
     * Starts a 10-second countdown timer that transitions to the login screen once finished.
     */
    private void startCountdownTimer() {
        timer = new CountDownTimer(10000, 1000) {  // 10 seconds countdown, update every second
            @Override
            public void onTick(long millisUntilFinished) {
                // Update the countdown text every second
                String stTimerText = millisUntilFinished / 1000 + "";
                cdtView.setText("Continuing in " + stTimerText + " Seconds");
            }

            @Override
            public void onFinish() {
                // When the timer finishes, update text and navigate to the login screen
                cdtView.setText("Continuing...");
                navigateToLogin();
            }
        }.start();  // Start the timer
    }

    /**
     * Helper method to start the Login activity.
     */
    private void navigateToLogin() {
        Intent intent = new Intent(Start.this, Login.class);
        startActivity(intent);
    }

    /**
     * Create the options menu for the activity.
     * @param menu Menu object that is inflated with items.
     * @return true to display the menu, false otherwise.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    /**
     * Handle menu item selection.
     * @param item The selected menu item.
     * @return true if an item was selected, false otherwise.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // Handle actions based on selected menu item
        if (id == R.id.item1) {
            navigateToLogin();
            return true;
        }
        else if (id == R.id.item2) {
            Intent intentSignUp = new Intent(Start.this, SignUp.class);
            startActivity(intentSignUp);
            return true;
        }
        else if (id == R.id.item3) {
            Intent intentDashboard = new Intent(Start.this, HomeDashboard.class);
            startActivity(intentDashboard);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void requestPostNotificationPermission() {
        // Check for POST_NOTIFICATIONS permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {  // Android 13 (API 33) and higher
            // Check if the permission is already granted
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                // Request notification permission if not granted
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 0);
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
            startActivity(intent);
        }
    }

    private void setDefaultNotificationInstances() {
        SharedPreferences prefs = getSharedPreferences("NotificationPrefs", MODE_PRIVATE);
        if (!prefs.contains("notification_instances")) {  // Check if defaults are already set
            SharedPreferences.Editor editor = prefs.edit();
            // Default times in HH:mm format (24-hour)
            editor.putStringSet("notification_instances", new HashSet<>(Arrays.asList("09:00", "12:00", "18:00")));
            editor.apply();
        }
    }
}
