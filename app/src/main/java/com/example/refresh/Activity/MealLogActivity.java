package com.example.refresh.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.refresh.Fragments.SearchResultsFragment;
import com.example.refresh.Fragments.SelectedFoodsFragment;
import com.example.refresh.Model.SearchResult;
import com.example.refresh.R;

import java.util.ArrayList;
import java.util.List;

public class MealLogActivity extends AppCompatActivity {

    // UI Elements
    private EditText searchBarET;
    private ImageButton clearButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_log);

        applyWindowInsets();
        initializeUI();
        loadDefaultFragment();
    }

    /**
     * Applies system window insets for immersive UI.
     */
    private void applyWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    /**
     * Initializes UI components and sets event listeners.
     */
    private void initializeUI() {
        searchBarET = findViewById(R.id.searchEditText);
        clearButton = findViewById(R.id.clearButton);

        searchBarET.setOnClickListener(v -> clearButton.setVisibility(View.VISIBLE));
    }

    /**
     * Loads the default fragment when the activity starts.
     */
    private void loadDefaultFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new SelectedFoodsFragment())
                .commit();
    }

    /**
     * Displays the SearchResultsFragment with the provided list of results.
     * @param results List of search results.
     */
    private void showSearchResultsFragment(ArrayList<SearchResult> results) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("search_results", results);

        SearchResultsFragment searchResultsFragment = new SearchResultsFragment();
        searchResultsFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.search_results_fragment, searchResultsFragment)
                .commit();

        findViewById(R.id.search_results_fragment).setVisibility(View.VISIBLE);
        findViewById(R.id.macronutrient_fragment).setVisibility(View.GONE);
    }

    /**
     * Hides the search results and restores the macronutrient view.
     */
    private void hideSearchResults() {
        findViewById(R.id.search_results_fragment).setVisibility(View.GONE);
        findViewById(R.id.macronutrient_fragment).setVisibility(View.VISIBLE);
    }

    /**
     * Placeholder method for performing a search.
     * TODO: Implement search logic.
     */
    private void performSearch() {
        String query = searchBarET.getText().toString().trim();

        if (query.isEmpty()) {
            Toast.makeText(this, "You didn't search for anything", Toast.LENGTH_SHORT).show();
            return;
        }

        // placeholder arraylist
        ArrayList<SearchResult> searchResults = new ArrayList<>();

        if (searchResults.isEmpty()) {
            Toast.makeText(this, "No results found for: " + query, Toast.LENGTH_SHORT).show();
        } else {
            showSearchResultsFragment(searchResults);
        }
    }
}
