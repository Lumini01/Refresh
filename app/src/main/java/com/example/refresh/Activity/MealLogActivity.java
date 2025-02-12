package com.example.refresh.Activity;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentContainerView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.refresh.Database.DatabaseHelper;
import com.example.refresh.Database.Tables.FoodsTable;
import com.example.refresh.Fragments.FoodInfoFragment;
import com.example.refresh.Fragments.SearchResultsFragment;
import com.example.refresh.Fragments.SelectedFoodsFragment;
import com.example.refresh.Model.Food;
import com.example.refresh.Model.ListItem;
import com.example.refresh.Model.Meal;
import com.example.refresh.R;

import java.util.ArrayList;
import java.util.Date;

public class MealLogActivity extends AppCompatActivity implements SearchResultsFragment.OnSearchResultsFragmentListener, SelectedFoodsFragment.OnSelectedFoodsFragmentListener, FoodInfoFragment.OnFoodInfoFragmentListener {

    private Meal meal;
    private ArrayList<Food> mealFoods = new ArrayList<>();

    // UI Elements
    private TextView title;
    private ImageButton backArrow;
    private ImageButton extraButton;
    private EditText searchBarET;
    private ImageButton clearButton;
    private FragmentContainerView searchResultsContainer;
    private FragmentContainerView selectedFoodsContainer;
    private RecyclerView searchResultsRecycler;
    private SelectedFoodsFragment selectedFoodsFragment;
    private FragmentContainerView foodInfoFragmentContainer;
    private FoodInfoFragment foodInfoFragment;
    private Button logMealBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_log);

        applyWindowInsets();
        initializeUI();
    }

    public void onAddingToSelectedFoods(ListItem<Food> addedFood) {
        cancelSearchInteraction();
        addFoodToSelectedFoods(addedFood);
        
    }

    public void onNavigateToFoodInfo(Food food) {
        foodInfoFragment = FoodInfoFragment.newInstance(food);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.food_info_fragment_container, foodInfoFragment)
                .commit();
        foodInfoFragmentContainer.setVisibility(View.VISIBLE);
    }

    public void onExitFoodInfoFragment() {
        getSupportFragmentManager().beginTransaction()
                .remove(foodInfoFragment)
                .commit();
        foodInfoFragmentContainer.setVisibility(View.GONE);
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
        title = findViewById(R.id.toolbarTitle);
        title.setText(R.string.meal_log);

        extraButton = findViewById(R.id.extra_button);
        extraButton.setVisibility(View.GONE);

        backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(v -> finish());

        searchBarET = findViewById(R.id.searchEditText);
        clearButton = findViewById(R.id.clearButton);
        logMealBtn = findViewById(R.id.btn_log_meal);
        searchResultsContainer = findViewById(R.id.search_results_fragment_container);

        // Initialize the SelectedFoodsFragment of the Meal Log Activity
        selectedFoodsContainer = findViewById(R.id.selected_foods_fragment_container);
        selectedFoodsFragment = new SelectedFoodsFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.selected_foods_fragment_container, selectedFoodsFragment)
                .commit();

        foodInfoFragmentContainer = findViewById(R.id.food_info_fragment_container);

        searchBarET.setOnClickListener(v -> clearButton.setVisibility(View.VISIBLE));

        searchBarET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                performSearch();
            }
        });

        clearButton.setOnClickListener(v -> {
            cancelSearchInteraction();
        });

        logMealBtn.setOnClickListener(v -> {
            logMeal();
        });
    }

    private void cancelSearchInteraction() {
        searchBarET.setText("");
        clearButton.setVisibility(View.GONE);
        hideSearchResults();
        hideKeyboard(searchBarET);
    }

    /**
     * Displays the SearchResultsFragment with the provided list of results.
     * @param results List of search results.
     */
    private void showSearchResultsFragment(ArrayList<Food> results) {
        ArrayList<ListItem<Food>> parsedResults = parseSearchResults(results);
        SearchResultsFragment searchResultsFragment = SearchResultsFragment.newInstance(parsedResults);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.search_results_fragment_container, searchResultsFragment)
                .commit();

        searchResultsContainer.setVisibility(View.VISIBLE);
    }

    /**
     * Hides the search results and restores the macronutrient view.
     */
    private void hideSearchResults() {
        searchResultsContainer.setVisibility(View.GONE);
    }

    /**
     * Placeholder method for performing a search.
     */
    private void performSearch() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        String rawQuery = searchBarET.getText().toString();
        String[] query = rawQuery.split("\\s*,\\s*");

        searchResultsContainer.setVisibility(View.GONE);

        if (query.length == 0 || rawQuery.isEmpty()) {
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

    public ArrayList<ListItem<Food>> parseSearchResults(ArrayList<Food> results) {
        ArrayList<ListItem<Food>> parsedResults = new ArrayList<>();

        for (Food food : results) {
            String separator = "  ✦  "; // Separator
            ArrayList<String> tailList = new ArrayList<>();

            if (food.getCalories() >= 400 && !food.getCategory().equals("Herbs/Spices"))
                tailList.add("⚠️\uD83D\uDD25");
            if (food.getProtein() >= 10 && !food.getCategory().equals("Herbs/Spices"))
                tailList.add("✅\ud83e\udd69");
            if (food.getFat() >= 25 && !food.getCategory().equals("Herbs/Spices"))
                tailList.add("⚠️\ud83e\udd51");

            String tail = String.join(separator, tailList);

            ListItem<Food> searchResult = new ListItem<>(food.getName(), food.getCategory() + "  ✦  " + tail, food);
            parsedResults.add(searchResult);
        }

        return parsedResults;
    }

    public ListItem<Food> parseSelectedFood(Food food) {
        return new ListItem<>(food.getName(), food.getServingSize() + "g  ✦  " + food.getActualCalories() + " kcal" , food);
    }

    public void addFoodToSelectedFoods(ListItem<Food> addedFood) {
        ListItem<Food> parsedFood = parseSelectedFood(addedFood.getModel());
        selectedFoodsFragment.addFoodToSelectedFoods(parsedFood);
    }

    public void removeFoodFromSelectedFoods(int position) {
        selectedFoodsFragment.removeFoodFromSelectedFoods(position);
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        view.clearFocus(); // Remove focus from EditText
    }

    private void logMeal() {

        ArrayList<ListItem<Food>> foodItemsList = selectedFoodsFragment.getSelectedFoods();
        for (ListItem<Food> foodItem : foodItemsList) {
            Food food = foodItem.getModel();
            mealFoods.add(food);
        }

        Date date =


        meal = new Meal(date, time, mealType, notes, mealFoods);

        Toast.makeText(this, "Meal logged!", Toast.LENGTH_SHORT).show();
        finish();
    }
}
