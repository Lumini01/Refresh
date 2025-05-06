package com.example.refresh;

import static com.example.refresh.Database.FoodsTable.populateFoodsTable;
import static com.example.refresh.Database.NotificationTemplatesTable.populateNotificationTemplatesTable;

import android.Manifest;
import android.app.AlarmManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.refresh.Activity.HomeDashboardActivity;
import com.example.refresh.Activity.LoginActivity;
import com.example.refresh.Helper.DatabaseHelper;
import com.example.refresh.Helper.UserInfoHelper;
import com.example.refresh.Notification.NotificationScheduler;

import java.util.ArrayList;

/**
 * Start Activity - This is the initial screen of the app before login,
 * featuring a countdown timer and a button to proceed to the login screen.
 */
public class StartActivity extends AppCompatActivity {

    // UI Components
    private TextView cdtTV;
    private Button continueBTN;
    private CountDownTimer timer;

    private static final String PREFS_NAME = MyApplication.PREFS_NAME;
    private static final String FIRST_LAUNCH_KEY = MyApplication.FIRST_LAUNCH_KEY;
    private static final String LOGGED_USER_ID_KEY = MyApplication.LOGGED_USER_ID_KEY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        // Initialize UI components
        initUI();

        // Check if it's the first launch and perform necessary actions
        checkFirstLaunch();

        // Check user status and decide navigation flow
        checkUserStatus();

        // Hide action bar temporarily
        hideActionBar();

        final WindowInsetsController controller = getWindow().getInsetsController();
        if (controller != null) {
            // hide both status bar & navigation bar
            controller.hide(WindowInsets.Type.navigationBars());
            // allow swipe to temporarily reveal
            controller.setSystemBarsBehavior(
                    WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            );
        }
    }

    /**
     * Initializes the UI components and sets up event listeners.
     */
    private void initUI() {
        continueBTN = findViewById(R.id.continue_btn);
        cdtTV = findViewById(R.id.cdt);

        // Edge-to-edge padding for system UI adjustments
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Setup 'Continue' button click listener
        continueBTN.setOnClickListener(view -> onContinueButtonClicked());
    }

    /**
     * Handles the actions when the continue button is clicked.
     */
    private void onContinueButtonClicked() {
        cdtTV.setText(""); // Clear countdown timer text
        if (timer != null) {
            timer.cancel(); // Stop the countdown timer when the user clicks continue
        }

        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int loggedUserID = sharedPreferences.getInt(LOGGED_USER_ID_KEY, -1);

        if (loggedUserID != -1) {
            navigateToHome();
        }
        else {
            navigateToLogin();
        }
    }

    /**
     * Checks if this is the first launch of the app.
     */
    private void checkFirstLaunch() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isFirstLaunch = sharedPreferences.getBoolean(FIRST_LAUNCH_KEY, true);

        if (isFirstLaunch) {
            firstLaunchActions();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(FIRST_LAUNCH_KEY, false);
            editor.apply();
        }
    }

    /**
     * Performs actions specific to the first launch of the app.
     */
    private void firstLaunchActions() {
        requestNotificationPermission();
        populateNotificationTemplatesTable(getApplicationContext());
        populateFoodsTable(getApplicationContext());
        setDefaultNotifications();
        TestingGrounds.test(getApplicationContext());
    }

    /**
     * Checks if the user is already logged in and navigates accordingly.
     */
    private void checkUserStatus() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int loggedUserID = sharedPreferences.getInt(LOGGED_USER_ID_KEY, -1);

        if (loggedUserID != -1) {
            navigateToHome();
        }
        else {
            startCountdownTimer();
        }
    }

    /**
     * Starts a 10-second countdown timer that transitions to the login screen once finished.
     */
    private void startCountdownTimer() {
        timer = new CountDownTimer(10000, 1000) {  // 10 seconds countdown, update every second
            @Override
            public void onTick(long millisUntilFinished) {
                cdtTV.setText("Continuing in " + (millisUntilFinished / 1000) + " Seconds");
            }

            @Override
            public void onFinish() {
                cdtTV.setText("Loading...");
                SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                int loggedUserID = sharedPreferences.getInt(LOGGED_USER_ID_KEY, -1);

                if (loggedUserID != -1) {
                    navigateToHome();
                } else {
                    navigateToLogin();
                }
            }
        }.start();
    }

    /**
     * Navigates to the Home Dashboard activity.
     */
    private void navigateToHome() {
        Intent intent = new Intent(StartActivity.this, HomeDashboardActivity.class);
        UserInfoHelper helper = new UserInfoHelper(this);

        if (helper.getStartDate() == null || helper.getStartWeight() == 0 || helper.getWeight() == 0 || helper.getGoal().equals("")) {
            intent.putExtra("firstLog", true);
        }

        startActivity(intent);
        finish();
    }

    /**
     * Navigates to the Login activity.
     */
    private void navigateToLogin() {
        Intent intent = new Intent(StartActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Requests notification permissions from the user (Android 13 and higher).
     */
    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 0);
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            if (!alarmManager.canScheduleExactAlarms()) {
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent);
            }
        }
    }

    /**
     * Sets default notification instances in the app.
     */
    private void setDefaultNotifications() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        ArrayList<Integer> templateIDs = new ArrayList<>();
        ArrayList<String> times = new ArrayList<>();

        templateIDs.add(1);
        templateIDs.add(2);
        templateIDs.add(3);

        times.add("9:00");
        times.add("14:00");
        times.add("19:00");

        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean defaultNotificationsSet = sharedPreferences.getBoolean("defaultNotificationsSet", false);

        if (defaultNotificationsSet) {
            ArrayList<Integer> instanceIDs = new ArrayList<>();
            instanceIDs.add(1);
            instanceIDs.add(2);
            instanceIDs.add(3);
            NotificationScheduler.addDefaultNotifications(this, instanceIDs, templateIDs, times);
        } else {
            NotificationScheduler.addNotificationInstances(this, templateIDs, times);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("defaultNotificationsSet", true);
            editor.apply();
        }
    }

    /**
     * Hides the action bar.
     */
    private void hideActionBar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }
}
