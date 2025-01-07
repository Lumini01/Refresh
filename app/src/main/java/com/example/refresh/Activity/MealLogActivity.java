package com.example.refresh.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.refresh.Fragments.SearchResultsFragment;
import com.example.refresh.Fragments.SelectedFoodsFragment;
import com.example.refresh.Fragments.SettingsFragment;
import com.example.refresh.Model.SearchResult;
import com.example.refresh.R;

import java.util.ArrayList;
import java.util.List;

public class MealLogActivity extends AppCompatActivity {

    private EditText searchBarET;
    private ImageButton clearButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_log);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        searchBarET = findViewById(R.id.searchEditText);
        clearButton = findViewById(R.id.clearButton);

        searchBarET.setOnClickListener(v -> clearButton.setVisibility(ImageButton.VISIBLE));

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new SelectedFoodsFragment())
                .commit();
    }

    // Method to display search results
    private void showSearchResultsFragment(List<SearchResult> results) {
        SearchResultsFragment fragment = SearchResultsFragment.newInstance(new ArrayList<>(results));
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.search_results_fragment, fragment)
                .commit();

        // Make the fragment visible
        findViewById(R.id.search_results_fragment).setVisibility(View.VISIBLE);

        // Optionally hide other fragments
        findViewById(R.id.macronutrient_fragment).setVisibility(View.GONE);
    }

    // Method to hide search results
    private void hideSearchResults() {
        findViewById(R.id.search_results_fragment).setVisibility(View.GONE);
        findViewById(R.id.macronutrient_fragment).setVisibility(View.VISIBLE);
    }

}