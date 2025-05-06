package com.example.refresh.Activity;

import static com.example.refresh.Fragment.UserInfoFragment.States.*;
import static com.example.refresh.MyApplication.*;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentContainerView;

import com.example.refresh.Database.UsersTable;
import com.example.refresh.Fragment.UserInfoFragment;
import com.example.refresh.Helper.DatabaseHelper;
import com.example.refresh.Helper.UserInfoHelper;
import com.example.refresh.MyApplication;
import com.example.refresh.R;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ProfileActivity extends AppCompatActivity
        implements UserInfoFragment.OnUserInfoFragmentListener {

    // Toolbar components
    private ImageView backBtn;
    private TextView title;

    // Profile Section components
    private ImageView profilePictureIV;
    private TextView nameTV;
    private TextView ageTV;
    private TextView goalTV;
    private LinearLayout targetWeightLayout;
    private TextView targetWeightTV;
    private TextView startDateTV;
    private LinearLayout personalInfoLayout;
    private LinearLayout accountDetailsLayout;
    private LinearLayout lifestyleGoalLayout;
    private LinearLayout goalAdjustmentLayout;
    private FragmentContainerView userInfoContainer;
    private Button logOutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize Views
        initViews();

        // Set up Toolbar
        setupToolbar();

        setUpListeners();

        loadProfileData();

        final WindowInsetsController controller = getWindow().getInsetsController();
        if (controller != null) {
            // hide both status bar & navigation bar
            controller.hide(WindowInsets.Type.navigationBars());
            // allow swipe to temporarily reveal
            controller.setSystemBarsBehavior(
                    WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            );
        }

        // hide title text (app name)
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    // Initialize Views
    private void initViews() {
        // Toolbar components
        title = findViewById(R.id.toolbar_title_tv);
        backBtn = findViewById(R.id.back_btn);

        // Profile Section components
        profilePictureIV = findViewById(R.id.profile_picture_iv);
        nameTV = findViewById(R.id.name_tv);
        ageTV = findViewById(R.id.age_tv);

        // User Stats
        goalTV = findViewById(R.id.goal_tv);
        targetWeightTV = findViewById(R.id.target_weight_tv);
        startDateTV = findViewById(R.id.start_date_tv);

        // Card Layouts
        personalInfoLayout = findViewById(R.id.personal_info_layout);
        accountDetailsLayout = findViewById(R.id.account_details_layout);
        lifestyleGoalLayout = findViewById(R.id.lifestyle_goal_layout);
        goalAdjustmentLayout = findViewById(R.id.goal_adjustment_layout);

        // Other Views
        userInfoContainer = findViewById(R.id.user_info_container);
        logOutBtn = findViewById(R.id.log_out_btn);
        targetWeightLayout = findViewById(R.id.target_weight_layout);
    }

    // Set up Toolbar
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        title.setText("Profile");
        backBtn.setOnClickListener(v -> finish());

        ImageButton extraBtn = findViewById(R.id.extra_btn);
        extraBtn.setVisibility(View.GONE);
    }

    private void setUpListeners() {
        // Section Layouts Listeners
        personalInfoLayout.setOnClickListener(v ->
                onProfileSectionClick(PERSONAL_INFO.getStateName()));

        accountDetailsLayout.setOnClickListener(v ->
                onProfileSectionClick(ACCOUNT_DETAILS.getStateName()));

        lifestyleGoalLayout.setOnClickListener(v ->
                onProfileSectionClick(LIFESTYLE_GOAL.getStateName()));

        goalAdjustmentLayout.setOnClickListener(v ->
                onProfileSectionClick(ADJUST_GOAL.getStateName()));

        // Log Out Listener
        logOutBtn.setOnClickListener(v -> {
            SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt(LOGGED_USER_ID_KEY, -1);
            editor.putString(LOGGED_USER_SP_NAME_KEY, "");
            editor.apply();
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void onProfileSectionClick(String section) {
        UserInfoFragment fragment = UserInfoFragment.newInstance(
                section, MyApplication.getInstance()
                        .getLoggedUserID());
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.user_info_container, fragment)
                .commitNow();

        userInfoContainer.setVisibility(View.VISIBLE);
    }

    public void hideUserInfo() {
        getSupportFragmentManager().beginTransaction()
                .remove(getSupportFragmentManager()
                        .findFragmentById(R.id.user_info_container))
                .commitNow();
        userInfoContainer.setVisibility(View.GONE);

        loadProfileData();
    }

    // Populate Profile Data
    private void loadProfileData() {
        UserInfoHelper userInfoHelper = new UserInfoHelper(this);
        DatabaseHelper dbHelper = new DatabaseHelper(this);

        String name = dbHelper.getFromRecordByValue(
                DatabaseHelper.Tables.USERS,
                UsersTable.Columns.NAME,
                UsersTable.Columns.ID,
                userInfoHelper.getUserId() + ""
        );

        nameTV.setText(name);
        ageTV.setText((String) (userInfoHelper.getAge() + " years old"));

        String goal = userInfoHelper.getGoal();

        if (goal.equalsIgnoreCase("lose") || goal.equalsIgnoreCase("gain")) {
            targetWeightTV.setText((String) (userInfoHelper.getTargetWeight() + " kg"));
            targetWeightLayout.setVisibility(View.VISIBLE);
        }
        else {
            targetWeightLayout.setVisibility(View.GONE);
        }

        if (!goal.equalsIgnoreCase("gain muscle")) {
            goal += " weight";
        }
        goalTV.setText(goal);

        LocalDate startDate = userInfoHelper.getStartDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formattedStartDate = startDate.format(formatter);
        startDateTV.setText((String) (formattedStartDate));
    }
}
