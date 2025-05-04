package com.example.refresh.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentContainerView;

import com.example.refresh.Fragment.UserInfoFragment;
import com.example.refresh.Helper.DailySummaryHelper;
import com.example.refresh.Helper.UserInfoHelper;
import com.example.refresh.Helper.WaterLogHelper;
import com.example.refresh.Model.DaySummary;
import com.example.refresh.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Main dashboard activity showing daily summaries, water/meal/weight logs,
 * and navigation shortcuts. Implements user details fragment listener.
 */
public class HomeDashboardActivity extends AppCompatActivity
        implements UserInfoFragment.OnUserInfoFragmentListener {

    // UI containers
    private FragmentContainerView userInfoContainer;
    private BottomNavigationView bottomNavigation;

    // Date and summary
    private LocalDate currentDate;
    private DaySummary daySummary;

    // Helpers
    private DailySummaryHelper dailySummaryHelper;
    private WaterLogHelper waterLogHelper;
    private UserInfoHelper userInfoHelper;

    // Views
    private TextView title;
    private ImageButton nextBtn;
    private ImageButton prevBtn;
    private LinearLayout logWaterBtn;
    private LinearLayout logMealBtn;
    private LinearLayout logWeightBtn;
    private ImageButton profileBtn;
    private ImageButton notificationBtn;
    private TextView weightProgressLabel;
    private TextView weightProgressValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_dashboard);

        // Handle system bar insets
        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main), (v, insets) -> {
                    Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
                    return insets;
                });

        // Initialize date and helpers
        currentDate = LocalDate.now();
        waterLogHelper = new WaterLogHelper(this);
        userInfoHelper = new UserInfoHelper(this);

        initViews();
        initToolbar();
        initBottomNavigation();
        setupUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        bottomNavigation.setSelectedItemId(R.id.nav_today);
    }

    /**
     * Show user details fragment.
     */
    private void showUserInfo() {
        int userId = getLoggedUserId();
        if (userId >= 0) {
            UserInfoFragment fragment = UserInfoFragment.newInstance(
                    UserInfoFragment.States.FIRST_LOG.getStateName(), userId);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.user_info_container, fragment)
                    .commit();
            userInfoContainer.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Hides the user details fragment (interface callback).
     */
    @Override
    public void hideUserInfo() {
        getSupportFragmentManager().beginTransaction()
                .remove(getSupportFragmentManager()
                        .findFragmentById(R.id.user_info_container))
                .commit();
        userInfoContainer.setVisibility(View.GONE);
    }

    // ====== Initialization helpers ======

    /**
     * Sets up toolbar with title and navigation.
     */
    private void initToolbar() {
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    /**
     * Initializes bottom navigation view and its listeners.
     */
    private void initBottomNavigation() {
        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_today) {
                return true;
            } else if (id == R.id.nav_log) {
                launchActivity(MealLogActivity.class);
                return true;
            } else if (id == R.id.nav_progress) {
                launchActivity(ProgressActivity.class);
                return true;
            }
            return false;
        });
        bottomNavigation.setSelectedItemId(R.id.nav_today);
    }

    /**
     * Initializes view references.
     */
    private void initViews() {
        userInfoContainer = findViewById(R.id.user_info_container);
        title = findViewById(R.id.date_title_tv);
        nextBtn = findViewById(R.id.next_summary_btn);
        prevBtn = findViewById(R.id.last_summary_btn);
        logWaterBtn = findViewById(R.id.log_water_btn);
        logMealBtn = findViewById(R.id.meal_log_shortcut_btn);
        logWeightBtn = findViewById(R.id.log_weight_btn);
        profileBtn = findViewById(R.id.profile_btn);
        notificationBtn = findViewById(R.id.notification_btn);
        weightProgressLabel = findViewById(R.id.gain_lose_weight_tv);
        weightProgressValue = findViewById(R.id.gain_lose_weight_value_tv);
    }

    /**
     * Orchestrates UI data population and listeners.
     */
    private void setupUI() {
        updateDaySummary();
        setupDateNavigation();
        updateWeightProgressUI();

        if (getIntent().getBooleanExtra("firstLog", false)) {
            showUserInfo();
        }

        logWaterBtn.setOnClickListener(v -> waterLogHelper.logWaterCup());
        logMealBtn.setOnClickListener(v -> launchActivity(MealLogActivity.class));
        logWeightBtn.setOnClickListener(v -> showWeightDialog());
        profileBtn.setOnClickListener(v -> launchActivity(ProfileActivity.class));
    }

    /**
     * Sets up previous and next day buttons.
     */
    private void setupDateNavigation() {
        setTitleForDate();
        nextBtn.setOnClickListener(v -> navigateDate(1));
        prevBtn.setOnClickListener(v -> navigateDate(-1));
    }

    // ====== Business logic ======

    /**
     * Navigates date by given days offset and refreshes UI.
     * @param days positive for next, negative for previous
     */
    private void navigateDate(int days) {
        if (days > 0 && currentDate.isBefore(LocalDate.now())) {
            currentDate = currentDate.plusDays(days);
            refreshUI();
        } else if (days > 0) {
            showToast("Cannot view future dates.");
        } else {
            currentDate = currentDate.plusDays(days);
            refreshUI();
        }
    }

    /**
     * Formats and sets title text for currentDate.
     */
    private void setTitleForDate() {
        String formatted = currentDate.format(DateTimeFormatter.ofPattern("EEE"))
                + ".│ " + currentDate.getDayOfMonth()
                + "." + currentDate.getMonthValue()
                + "." + currentDate.getYear();
        title.setText(formatted);
    }

    /**
     * Fetches or initializes daily summary.
     */
    private void updateDaySummary() {
        if (dailySummaryHelper == null) {
            dailySummaryHelper = new DailySummaryHelper(this);
        }
        daySummary = dailySummaryHelper.getDailySummary(currentDate);
    }

    /**
     * Populates weight progress UI based on user goal.
     */
    private void updateWeightProgressUI() {
        String goal = userInfoHelper.getGoal();
        int startWeight = userInfoHelper.getStartWeight();
        int currentWeight = userInfoHelper.getWeight();
        int targetWeight = userInfoHelper.getTargetWeight();

        String label;
        String value;
        boolean showValue = true;

        switch (goal) {
            case "lose":
                label = "Using Refresh You've Lost:";
                value = (startWeight - targetWeight) + " kg";
                break;
            case "gain":
                label = "Using Refresh You've Gained:";
                value = (currentWeight - startWeight) + " kg";
                break;
            case "maintain":
            case "gain_muscle":
                int delta = currentWeight - startWeight;
                label = "Your weight is " + Math.abs(delta)
                        + " kg " + (delta >= 0 ? "More" : "Less") + " than when you started";
                value = "";
                showValue = false;
                break;
            default:
                label = "";
                value = "";
                showValue = false;
        }

        weightProgressLabel.setText(label);
        weightProgressValue.setText(value);
        weightProgressValue.setVisibility(showValue ? View.VISIBLE : View.GONE);
    }

    /**
     * Refreshes UI components after date or data change.
     */
    private void refreshUI() {
        setTitleForDate();
        updateDaySummary();
        // TODO: refresh nutrition fragment when implemented
    }

    /**
     * Shows a dialog to input custom weight.
     */
    private void showWeightDialog() {
        View view = LayoutInflater.from(this)
                .inflate(R.layout.layout_weight_dialog, null);
        EditText input = view.findViewById(R.id.customEditText);

        new AlertDialog.Builder(this)
                .setView(view)
                .setTitle("Enter Your Weight")
                .setPositiveButton("Save", (dialog, which) -> {
                    String text = input.getText().toString().trim();
                    if (!text.isEmpty()) {
                        userInfoHelper.setWeight(Integer.parseInt(text));
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel())
                .show();
    }

    // ====== Utility methods ======

    /**
     * Retrieves the logged-in user ID from preferences.
     */
    private int getLoggedUserId() {
        return getSharedPreferences("AppPreferences", MODE_PRIVATE)
                .getInt("loggedUserID", -1);
    }

    /**
     * Launches an activity by class.
     */
    private void launchActivity(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
    }

    /**
     * Displays a short toast message.
     */
    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        new Handler(Looper.getMainLooper()).postDelayed(() -> {}, 0);
    }
}
