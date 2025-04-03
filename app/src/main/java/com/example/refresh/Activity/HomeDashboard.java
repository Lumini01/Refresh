package com.example.refresh.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.refresh.Helper.WaterLogHelper;
import com.example.refresh.R;
import com.example.refresh.Start;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

// Home Dashboard activity which is the main activity of the app
public class HomeDashboard extends AppCompatActivity {

    private TextView title;
    private Toolbar mainToolbar;
    private ImageButton nextSummary;
    private ImageButton lastSummary;
    private LinearLayout logWaterButton;
    private BottomNavigationView bottomNavigationView;
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

        waterLogHelper = new WaterLogHelper(this);

        setUpUI();

        // Setup Toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Setup Bottom Navigation
        setupBottomNavigationMenu();
    }

    private void setUpUI() {
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
        setTitle(LocalDate.now());
    }

    private void setUpListeners() {
        nextSummary.setOnClickListener(v -> {

        });

        lastSummary.setOnClickListener(v -> {

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
//            else if (itemId == R.id.nav_recipes) {
//                // Handle recipes click
//                return true;
//            }

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

    }

    protected void onResume() {
        super.onResume();
        bottomNavigationView.setSelectedItemId(R.id.nav_today);
    }
}
