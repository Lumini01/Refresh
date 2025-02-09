package com.example.refresh.Activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentContainerView;

import com.example.refresh.Database.DatabaseHelper;
import com.example.refresh.Database.Tables.FoodsTable;
import com.example.refresh.Fragments.SearchResultsFragment;
import com.example.refresh.Fragments.SelectedFoodsFragment;
import com.example.refresh.Model.Food;
import com.example.refresh.Model.SearchResult;
import com.example.refresh.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MealLogActivity extends AppCompatActivity {

    // UI Elements
    private EditText searchBarET;
    private ImageButton clearButton;
    private FragmentContainerView searchResultsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_log);

        applyWindowInsets();
        initializeUI();
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
        searchResultsContainer = findViewById(R.id.search_results_fragment);

        searchBarET.setOnClickListener(v -> clearButton.setVisibility(View.VISIBLE));

        searchBarET.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {

                    performSearch();
                    return true;
                }
                return false;
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchBarET.setText("");
                clearButton.setVisibility(View.GONE);
                hideSearchResults();
                hideKeyboard(searchBarET);
            }
        });
    }

    /**
     * Displays the SearchResultsFragment with the provided list of results.
     * @param results List of search results.
     */
    private void showSearchResultsFragment(ArrayList<Food> results) {
        ArrayList<SearchResult> parsedResults = parseResults(results);
        SearchResultsFragment searchResultsFragment = SearchResultsFragment.newInstance(parsedResults);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.search_results_fragment, searchResultsFragment)
                .commit();

        searchResultsContainer.setVisibility(View.VISIBLE);
    }

    /**
     * Hides the search results and restores the macronutrient view.
     */
    private void hideSearchResults() {
        findViewById(R.id.search_results_fragment).setVisibility(View.GONE);
    }

    /**
     * Placeholder method for performing a search.
     */
    private void performSearch() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        String rawQuery = searchBarET.getText().toString();
        String[] query = rawQuery.split("\\s*,\\s*");
        searchResultsContainer.setVisibility(View.GONE);

        if (query.length == 0 || rawQuery == null || rawQuery.equals("")) {
            Toast.makeText(this, "You didn't search for anything", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList<Food> foodResults = dbHelper.getRecords(DatabaseHelper.Tables.FOODS, FoodsTable.Columns.NAME, query);

        if ( foodResults == null || foodResults.isEmpty()) {
            Toast.makeText(this, "No results found for: " + rawQuery, Toast.LENGTH_SHORT).show();
        } else {
            showSearchResultsFragment(foodResults);
        }
    }

    public ArrayList<SearchResult> parseResults(ArrayList<Food> results) {
        ArrayList<SearchResult> parsedResults = new ArrayList<>();
        Set<String> foodSearchResultIDs = new HashSet<>();

        for (Food food : results) {
            SearchResult searchResult = new SearchResult(food.getName(), food.getCategory(), "food", food.getId());
            parsedResults.add(searchResult);
            foodSearchResultIDs.add(String.valueOf(food.getId()));
        }

        SharedPreferences sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        sharedPreferences.edit().putStringSet("foodSearchResultIDs", foodSearchResultIDs).apply();

        return parsedResults;
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        view.clearFocus(); // Remove focus from EditText
    }
}
