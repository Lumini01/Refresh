package com.example.refresh.Activity;

import android.content.DialogInterface;
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
import androidx.appcompat.widget.Toolbar;
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

// Home Dashboard activity which is the main activity of the app
public class HomeDashboardActivity extends AppCompatActivity implements UserInfoFragment.OnUserDetailsFragmentListener {

    private FragmentContainerView userDetailsFragmentContainer;
    private UserInfoFragment userDetailsFragment;
    private TextView title;
    private Toolbar mainToolbar;
    private LocalDate date;
    private DaySummary daySummary;
    private ImageButton nextSummary;
    private ImageButton lastSummary;
    private LinearLayout logWaterButton;
    private LinearLayout logMealButton;
    private LinearLayout logWeightButton;
    private LinearLayout progressShortcutBtn;
    private BottomNavigationView bottomNavigationView;
    private TextView weightProgressTV;
    private TextView weightProgressValueTV;
    private DailySummaryHelper dailySummaryHelper;
    private WaterLogHelper waterLogHelper;
    private ImageButton profileButton;
    private UserInfoHelper userInfoHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        // Handle system bars insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        date = LocalDate.now();
        waterLogHelper = new WaterLogHelper(this);
        userInfoHelper = new UserInfoHelper(this);

        setUpUI();

        // Setup Toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Setup Bottom Navigation
        setupBottomNavigationMenu();

    }

    public void onNavigateToUserDetails() {
        int userId = getSharedPreferences("AppPreferences", MODE_PRIVATE).getInt("loggedUserID", -1);
        if (userId != -1) {
            userDetailsFragment = UserInfoFragment.newInstance(UserInfoFragment.States.FIRST_LOG.getStateName(), userId);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.user_details_container, userDetailsFragment)
                    .commit();
            userDetailsFragmentContainer.setVisibility(View.VISIBLE);
        }
    }

    public void onExitUserDetailsFragment() {
        getSupportFragmentManager().beginTransaction()
                .remove(userDetailsFragment)
                .commit();
        userDetailsFragmentContainer.setVisibility(View.GONE);
    }

    private void setUpUI() {
        updateDaySummary();

        initializeViews();

        if (getIntent().getBooleanExtra("firstLog", false))
            onNavigateToUserDetails();

        initializeUIData();
        setUpListeners();
        initializeFragments();
    }

    private void initializeViews() {
        userDetailsFragmentContainer = findViewById(R.id.user_details_container);
        title = findViewById(R.id.date_title_tv);
        mainToolbar = findViewById(R.id.toolbar);
        nextSummary = findViewById(R.id.next_summary_btn);
        lastSummary = findViewById(R.id.last_summary_btn);
        logWaterButton = findViewById(R.id.log_water_btn);
        logMealButton = findViewById(R.id.meal_log_shortcut_btn);
        logWeightButton = findViewById(R.id.log_weight_btn);
        progressShortcutBtn = findViewById(R.id.progress_shortcut_btn);
        profileButton = findViewById(R.id.profile_btn);
        weightProgressTV = findViewById(R.id.gain_lose_weight_tv);
        weightProgressValueTV = findViewById(R.id.gain_lose_weight_value_tv);
    }

    private void initializeUIData() {
        setTitle(date);

        if (userInfoHelper.getGoal().equals("lose")) {
            String weightProgressTVValue = "Using Refresh You've Lost:";
            weightProgressTV.setText(weightProgressTVValue);
            String weightProgressValue = (userInfoHelper.getStartWeight() - userInfoHelper.getTargetWeight()) + " kg";
            weightProgressValueTV.setText(weightProgressValue);
        }
        else if (userInfoHelper.getGoal().equals("gain")) {
            String weightProgressTVValue = "Using Refresh You've Gained:";
            weightProgressTV.setText(weightProgressTVValue);
            String weightProgressValue = (userInfoHelper.getWeight() - userInfoHelper.getStartWeight()) + " kg";
            weightProgressValueTV.setText(weightProgressValue);
        }
        else if (userInfoHelper.getGoal().equals("maintain")) {
            int weightDelta = userInfoHelper.getWeight() - userInfoHelper.getStartWeight();
            String weightProgressTVValue = "Your Weight is" + Math.abs(weightDelta) + " kg" + (weightDelta >= 0 ? " More" : " Less") + "Then When You Started";
            weightProgressTV.setText(weightProgressTVValue);
            weightProgressValueTV.setVisibility(View.GONE);
        }
        else if (userInfoHelper.getGoal().equals("gain_muscle")) {
            int weightDelta = userInfoHelper.getWeight() - userInfoHelper.getStartWeight();
            String weightProgressTVValue = "Your Weight is" + Math.abs(weightDelta) + " kg" + (weightDelta >= 0 ? " More" : " Less") + "Then When You Started";
            weightProgressTV.setText(weightProgressTVValue);
            weightProgressValueTV.setVisibility(View.GONE);
        }
    }

    private void setUpListeners() {
        nextSummary.setOnClickListener(v -> {
            if (date.isBefore(LocalDate.now())) {
                date = date.plusDays(1);
                refreshUI();
            }
            else {
                Toast.makeText(this, "Cant view future dates.", Toast.LENGTH_SHORT).show();
                nextSummary.setEnabled(false);

                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    nextSummary.setEnabled(true);
                }, 2000);
            }
        });

        lastSummary.setOnClickListener(v -> {
            date = date.minusDays(1);
            refreshUI();

            nextSummary.setEnabled(true);
        });

        logWaterButton.setOnClickListener(v -> {
            waterLogHelper.logWaterCup();
        });

        logMealButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeDashboardActivity.this, MealLogActivity.class);
            startActivity(intent);
        });

        userInfoHelper = new UserInfoHelper(this);
        logWeightButton.setOnClickListener(v -> {
            LayoutInflater inflater = LayoutInflater.from(this);
            View customView = inflater.inflate(R.layout.dialog_custom, null);

            final EditText customEditText = customView.findViewById(R.id.customEditText);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(customView)
                    .setTitle("Enter Your Value")
                    .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Retrieve the input when the positive button is pressed
                            String userInput = customEditText.getText().toString().trim();
                            if (!userInput.isEmpty()) {
                                userInfoHelper.setWeight(Integer.parseInt(userInput));
                            }
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

            AlertDialog dialog = builder.create();
            dialog.show();
        });

        progressShortcutBtn.setOnClickListener(v -> {
            Intent intent = new Intent(HomeDashboardActivity.this, ProgressActivity.class);
            startActivity(intent);
        });

        profileButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeDashboardActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
    }

    private void initializeFragments() {

    }

    private void setupBottomNavigationMenu() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_today) {
                // Handle home click
                return true;
            } else if (itemId == R.id.nav_log) {
                Intent intent = new Intent(HomeDashboardActivity.this, MealLogActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_progress) {
                Intent intent = new Intent(HomeDashboardActivity.this, ProgressActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                return true;
            }

            return false;
        });

        bottomNavigationView.setSelectedItemId(R.id.nav_today);
    }

    private void setTitle(LocalDate date) {
        String strTitle = date.format(DateTimeFormatter.ofPattern("EEE")) + ".│ "
                + date.getDayOfMonth() + "."
                + date.getMonthValue() + "."
                + date.getYear();

        title.setText(strTitle);
    }

    private void refreshUI() {
        setTitle(date);
        updateDaySummary();
        refreshNutritionFragment();
    }

    private void refreshNutritionFragment() {

    }

    private void updateDaySummary() {
        if (dailySummaryHelper == null)
            dailySummaryHelper = new DailySummaryHelper(this);
        daySummary = dailySummaryHelper.getDailySummary(date);
    }

    protected void onResume() {
        super.onResume();
        bottomNavigationView.setSelectedItemId(R.id.nav_today);
    }
}
