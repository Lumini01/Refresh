package com.example.refresh.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.refresh.R;
import com.example.refresh.Start;
import com.google.android.material.bottomnavigation.BottomNavigationView;
// Home Dashboard activity which is the main activity of the app
public class HomeDashboard extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

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

        // Setup Toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Setup Bottom Navigation
        setupBottomNavigationMenu();
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

    protected void onResume() {
        super.onResume();
        bottomNavigationView.setSelectedItemId(R.id.nav_today);
    }
}
