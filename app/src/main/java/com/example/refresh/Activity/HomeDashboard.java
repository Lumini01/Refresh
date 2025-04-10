package com.example.refresh.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.refresh.Helper.DailySummaryHelper;
import com.example.refresh.Helper.WaterLogHelper;
import com.example.refresh.Model.DaySummary;
import com.example.refresh.R;
import com.example.refresh.Start;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

// Home Dashboard activity which is the main activity of the app
public class HomeDashboard extends AppCompatActivity {

    private TextView title;
    private Toolbar mainToolbar;
    private LocalDate date;
    private DaySummary daySummary;
    private ImageButton nextSummary;
    private ImageButton lastSummary;
    private LinearLayout logWaterButton;
    private BottomNavigationView bottomNavigationView;
    private DailySummaryHelper dailySummaryHelper;
    private WaterLogHelper waterLogHelper;

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

        setUpUI();

        // Setup Toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Setup Bottom Navigation
        setupBottomNavigationMenu();
    }

    private void setUpUI() {
        updateDaySummary();

        initializeViews();
        initializeUIData();
        setUpListeners();
        initializeFragments();
    }

    private void initializeViews() {
        title = findViewById(R.id.dateTitleTV);
        mainToolbar = findViewById(R.id.toolbar);
        nextSummary = findViewById(R.id.nextSummaryButton);
        lastSummary = findViewById(R.id.lastSummaryButton);
        logWaterButton = findViewById(R.id.log_water_layout);
    }

    private void initializeUIData() {
        setTitle(date);
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
                Intent intent = new Intent(HomeDashboard.this, MealLogActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_progress) {
                Intent intent = new Intent(HomeDashboard.this, Progress.class);
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
