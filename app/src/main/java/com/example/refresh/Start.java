package com.example.refresh;

import static com.example.refresh.Database.Tables.FoodsTable.populateFoodsTable;
import static com.example.refresh.Database.Tables.NotificationTemplatesTable.populateNotificationTemplatesTable;

import android.app.AlarmManager;
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
import android.widget.Toast;

import com.example.refresh.Activity.HomeDashboard;
import com.example.refresh.Activity.Login;
import com.example.refresh.Activity.SignUp;
import com.example.refresh.Database.DatabaseHelper;
import com.example.refresh.Notification.NotificationScheduler;

import java.util.ArrayList;

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
                SharedPreferences sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
                String loggedUserEmail = sharedPreferences.getString("loggedUser", null );
                if (loggedUserEmail != null)
                    navigateToHome();
                else {
                    navigateToLogin();
                }

                cdtView.setText("");

                if (timer != null)
                    timer.cancel();  // Stop the countdown timer when the user clicks continue
            }
        });

        SharedPreferences sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        boolean isFirstLaunch = sharedPreferences.getBoolean("isFirstLaunch", true);

        if (isFirstLaunch) {
            // Perform first-time launch functionalities
            firstLaunchActions();

            // Update the flag so this block doesn't run again
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isFirstLaunch", false);
            editor.apply();
        }

        // Navigate to the home screen if the user is already logged in
        checkUserStatus();

        // Hide the action bar temporarily
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    private void checkUserStatus() {
        SharedPreferences sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        String loggedUserEmail = sharedPreferences.getString("loggedUser", null );
        if (loggedUserEmail != null)
            navigateToHome();
        else {
            // Start the countdown timer
            startCountdownTimer();
        }
    }

    private void firstLaunchActions() {
        // Request notification permission
        requestPostNotificationPermission();

        // Populate the notification templates table and the foods table if needed
        Context context = getApplicationContext();
        populateNotificationTemplatesTable(context);
        populateFoodsTable(context);

        // Set default notification times
        setDefaultNotificationInstances();


        // Run testing methods
        TestingGrounds.test(context);
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
                cdtView.setText("Loading...");
                navigateToLogin();
            }
        }.start();  // Start the timer
    }

    private void navigateToHome() {
        Intent intent = new Intent(Start.this, HomeDashboard.class);
        startActivity(intent);
    }
    /**
     * Helper method to start the Login activity.
     */
    private void navigateToLogin() {
        Intent intent = new Intent(Start.this, Login.class);
        startActivity(intent);
        cdtView.setText("");
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
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                // Check if the user has denied the permission permanently
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.POST_NOTIFICATIONS)) {
                    // Show rationale (optional): You can explain why the permission is needed here
                    Toast.makeText(this, "Notifications are important for this app. Please allow them.", Toast.LENGTH_SHORT).show();

                } else {
                    // Request permission
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.POST_NOTIFICATIONS}, 0);
                }
            }
        }

        // Check for SCHEDULE_EXACT_ALARM permission (API 31+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            if (!alarmManager.canScheduleExactAlarms()) {
                // Only start the intent if the permission is not already granted
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent);
            }
        }
    }

    private void setDefaultNotificationInstances() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        ArrayList<Integer> templateIDs = new ArrayList<>();
        ArrayList<String> times = new ArrayList<>();

        templateIDs.add(1);
        templateIDs.add(2);
        templateIDs.add(3);

        times.add("9:00");
        times.add("14:00");
        times.add("19:00");

        SharedPreferences sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        boolean defaultNotificationsSet = sharedPreferences.getBoolean("defaultNotificationsSet", false);

        if (defaultNotificationsSet) {
            ArrayList<Integer> instanceIDs = new ArrayList<>();

            instanceIDs.add(1);
            instanceIDs.add(2);
            instanceIDs.add(3);

            NotificationScheduler.addDefaultNotifications(this, instanceIDs, templateIDs, times);
        }
        else {
            NotificationScheduler.addNotificationInstances(this, templateIDs, times);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("defaultNotificationsSet", true);
            editor.apply();
        }
    }
    protected void onResume() {
        super.onResume();

        /*Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);*/
    } 
}
