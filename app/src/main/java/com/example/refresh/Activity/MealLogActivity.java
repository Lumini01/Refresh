package com.example.refresh.Activity;

import static com.example.refresh.Fragment.SelectedFoodsFragment.newInstance;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentContainerView;

import com.example.refresh.Database.MealsTable;
import com.example.refresh.Fragment.DailyProgressFragment;
import com.example.refresh.Helper.DailySummaryHelper;
import com.example.refresh.Helper.DatabaseHelper;
import com.example.refresh.Database.FoodsTable;
import com.example.refresh.Fragment.FoodInfoFragment;
import com.example.refresh.Fragment.SearchResultsFragment;
import com.example.refresh.Fragment.SelectedFoodsFragment;
import com.example.refresh.Helper.UserInfoHelper;
import com.example.refresh.Model.DaySummary;
import com.example.refresh.Model.Food;
import com.example.refresh.Model.ListItem;
import com.example.refresh.Model.Meal;
import com.example.refresh.Model.User;
import com.example.refresh.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class MealLogActivity extends AppCompatActivity implements SearchResultsFragment.OnSearchResultsFragmentListener, SelectedFoodsFragment.OnSelectedFoodsFragmentListener, FoodInfoFragment.OnFoodInfoFragmentListener {

    // Domain model
    private Meal meal;
    private ArrayList<Food> mealFoods = new ArrayList<>();

    // Date & time
    private LocalDate mealDate;
    private LocalTime mealTime;
    private LocalDate oldDate;

    // UI elements
    private TextView title;
    private TextView mealDateTimeTV;
    private ImageButton backBtn;
    private ImageButton datePickerBtn;
    private EditText searchBarET;
    private ImageButton clearBtn;
    private FragmentContainerView searchResultsContainer;
    private FragmentContainerView dailyProgressContainer;
    private FragmentContainerView selectedFoodsContainer;
    private FragmentContainerView foodInfoFragmentContainer;
    private SelectedFoodsFragment selectedFoodsFragment;
    private FoodInfoFragment foodInfoFragment;
    private EditText notesET;
    private Button logMealBtn;
    private BottomNavigationView bottomNavigationView;

    // State flag
    private boolean inEditMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_log);

        inEditMode = getIntent().getBooleanExtra("edit_mode", false);


        applyWindowInsets();
        setupBottomNavigationMenu();
        initializeUI();

        if (!inEditMode)
            initializeLogModeUI();
        else {
            initializeEditModeUI();
        }

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

    public void onAddingToSelectedFoods(ListItem<Food> addedFood) {
        addFoodToSelectedFoods(addedFood);
        cancelSearchInteraction();
    }

    public void onNavigateToFoodInfo(Food food) {
        hideKeyboard(searchBarET);

        foodInfoFragment = FoodInfoFragment.newInstance(food);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.food_info_container, foodInfoFragment)
                .commitNow();
        foodInfoFragmentContainer.setVisibility(View.VISIBLE);
    }

    public void onExitFoodInfoFragment() {
        getSupportFragmentManager().beginTransaction()
                .remove(foodInfoFragment)
                .commitNow();
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
        initializeViews();
        initializeToolbar();
        initializeButtons();
        initializeFragments();
        initializeSearchInteraction();
        setupDatePickerMenu();
        initializeLogButton();
    }

    private void initializeLogModeUI() {
        initializeDateTimeViews();
    }

    private void initializeEditModeUI() {
        logMealBtn.setText(R.string.save_changes);

        meal = (Meal) getIntent().getSerializableExtra("meal");
        oldDate = meal.getDate();

        if (meal != null) {
            mealDate = meal.getDate();
            mealTime = meal.getTime();
            title.setText(Meal.getMealLogTitle(mealDate, mealTime));
            updateMealDate(mealDate);
            notesET.setText(meal.getNotes());

            ArrayList<Integer> mealFoodIDs = meal.getFoodIDs();
            parseMealFoods(mealFoodIDs, meal.getServingSizes());
            ArrayList<ListItem<Food>> foodListItems = populateSelectedFoods();

            selectedFoodsFragment = newInstance(foodListItems);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.selected_foods_container, selectedFoodsFragment)
                    .commitNow();
        }
    }

    private void initializeViews() {
        // Toolbar
        title = findViewById(R.id.toolbar_title_tv);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f);

        // Date & Time views
        mealDateTimeTV = findViewById(R.id.meal_dateTime_tv);

        // Buttons
        datePickerBtn = findViewById(R.id.extra_btn);
        backBtn = findViewById(R.id.back_btn);
        clearBtn = findViewById(R.id.clear_btn);
        logMealBtn = findViewById(R.id.log_meal_btn);


        // Fragments containers and search bar
        searchResultsContainer = findViewById(R.id.search_results_fragment_container);
        dailyProgressContainer = findViewById(R.id.daily_progress_container);
        selectedFoodsContainer = findViewById(R.id.selected_foods_container);
        foodInfoFragmentContainer = findViewById(R.id.food_info_container);
        searchBarET = findViewById(R.id.search_et);

        // Notes
        notesET = findViewById(R.id.notes_et);
    }

    private void initializeToolbar() {
        // Now the title view is already found in initializeViews()
        title.setText(Meal.getMealLogTitle(LocalDate.now(), LocalTime.now()));
    }

    private void initializeDateTimeViews() {
        // mealDateAndTimeTV is already initialized
        updateMealDate(LocalDate.now());
    }

    private void initializeButtons() {
        // Set button resources and images
        datePickerBtn.setImageResource(R.drawable.ic_calendar);
    }

    private void initializeLogButton() {
        if (!inEditMode) {
            logMealBtn.setOnClickListener(v -> {
                hideKeyboard(searchBarET);
                logMeal();
            });
            backBtn.setImageResource(R.drawable.ic_clear_all);
            backBtn.setOnClickListener(v -> clearAll());
        }
        else {
            logMealBtn.setOnClickListener(v -> {
                hideKeyboard(searchBarET);
                updateMeal();
            });
            backBtn.setOnClickListener(v -> {
                hideKeyboard(searchBarET);
                finish();
            });
        }
    }

    private void initializeFragments() {
        // Initialize fragments using the pre-found container views
        selectedFoodsFragment = new SelectedFoodsFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.selected_foods_container, selectedFoodsFragment)
                .commitNow();
    }

    private void initializeSearchInteraction() {
        // Set up search bar click and text change listeners
        searchBarET.setOnClickListener(v -> clearBtn.setVisibility(View.VISIBLE));
        searchBarET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Show the clear button only if the text is not empty
                if (!(searchBarET.getText() + "").isEmpty()) {
                    performSearch();
                    clearBtn.setVisibility(View.VISIBLE);
                }
                else {
                    clearBtn.setVisibility(View.GONE);
                }
            }
        });

        searchBarET.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus)
                cancelSearchInteraction();
        });

        clearBtn.setOnClickListener(v -> cancelSearchInteraction());
    }

    private void setupDatePickerMenu() {
        MaterialDatePicker.Builder<Long> dateBuilder = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select a date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds());

        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder()
                .setValidator(DateValidatorPointBackward.now());
        dateBuilder.setCalendarConstraints(constraintsBuilder.build());

        MaterialDatePicker<Long> datePicker = dateBuilder.build();

        datePickerBtn.setOnClickListener(v -> {
            datePicker.show(getSupportFragmentManager(), "DATE_PICKER");
            cancelSearchInteraction();
        });

        datePicker.addOnPositiveButtonClickListener(selection -> {
            LocalDate localDate = Instant.ofEpochMilli(selection)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            updateMealDate(localDate);
            setupTimePickerMenu(localDate);
        });
    }

    private void setupTimePickerMenu(LocalDate selectedDate) {
        // Build the time picker (24-hour format as an example)
        MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(LocalTime.now().getHour())      // Default hour
                .setMinute(LocalTime.now().getMinute())  // Default minute
                .setTitleText("Select a time")
                .build();

        // Listen for the user to confirm a time
        timePicker.addOnPositiveButtonClickListener(dialog -> {
            int hour = timePicker.getHour();
            int minute = timePicker.getMinute();

            // Convert the picked hour/minute to a LocalTime
            LocalTime selectedTime = LocalTime.of(hour, minute);

            // Validate the picked time
            validateTimeSelection(selectedDate, selectedTime);
        });

        // Show the time picker
        timePicker.show(getSupportFragmentManager(), "TIME_PICKER");
    }

    private void validateTimeSelection(LocalDate selectedDate, LocalTime selectedTime) {
        // If the date is before today, we don't care about the time (it's all in the past).
        LocalDate today = LocalDate.now();
        if (selectedDate.isBefore(today)) {
            updateMealTime(selectedTime);
            return;
        }

        // If the date is after today, let's say we don't allow that at all, or handle differently.
        // (If you already disallowed future dates in the date picker, this might never happen.)
        if (selectedDate.isAfter(today)) {
            // For example, show an error or skip.
            handleInvalidDateTime();
            return;
        }

        // If the date is exactly today, ensure the time is not in the future.
        if (selectedDate.isEqual(today)) {
            LocalTime nowTime = LocalTime.now();
            if (selectedTime.isAfter(nowTime)) {
                // Show an error or ignore
                handleInvalidDateTime();
            } else {
                // It's valid
                updateMealTime(selectedTime);
            }
        }
    }

    private void handleInvalidDateTime() {
        Toast.makeText(this, "Cannot pick a future time for today.", Toast.LENGTH_SHORT).show();
        datePickerBtn.setEnabled(false);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            datePickerBtn.setEnabled(true);
        }, 2000);
    }

    private void updateMealDate(LocalDate date) {
        mealDate = date;
        if (mealTime == null)
            mealTime = LocalTime.now();
        updateMealDateTimeTV();
    }

    private void updateMealTime(LocalTime time) {
        mealTime = time;
        if (mealDate == null)
            mealDate = LocalDate.now();
        updateMealDateTimeTV();
        title.setText(Meal.getMealLogTitle(mealDate, mealTime));

    }

    private void updateMealDateTimeTV() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String timeText = mealTime.withSecond(0).withNano(0).toString();
        String text = mealDate.format(formatter) + "  |  " + timeText;
        mealDateTimeTV.setText(text);
        refreshDailyProgressFragment();
    }

    private void refreshDailyProgressFragment() {
        DailySummaryHelper dailySummaryHelper = new DailySummaryHelper(this);
        DaySummary daySummary = dailySummaryHelper.getDailySummary(mealDate);

        DailyProgressFragment fragment = DailyProgressFragment.newInstance(daySummary, new UserInfoHelper(this));
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.daily_progress_container, fragment)
                .commitNow();
        dailyProgressContainer.setVisibility(View.VISIBLE);
    }

    private void cancelSearchInteraction() {
        clearBtn.setVisibility(View.GONE);
        searchBarET.setText("");
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
                .commitNow();

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
            Toast.makeText(this, "You Didn't Search For Anything", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList<Food> foodResults = dbHelper.getRecordsLike(DatabaseHelper.Tables.FOODS, FoodsTable.Columns.NAME, query);

        if ( foodResults == null || foodResults.isEmpty()) {
            Toast.makeText(this, "No Results Found For: " + rawQuery, Toast.LENGTH_SHORT).show();
        } else {
            showSearchResultsFragment(foodResults);
        }

        dbHelper.close();
    }

    private ArrayList<ListItem<Food>> parseSearchResults(ArrayList<Food> results) {
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

    private ListItem<Food> parseSelectedFood(Food food) {
        return new ListItem<>(food.getName(), food.getServingSize() + "g  ✦  " + food.getActualCalories() + " kcal" , food);
    }

    private void addFoodToSelectedFoods(@NonNull ListItem<Food> addedFood) {
        ListItem<Food> parsedFood = parseSelectedFood(addedFood.getModel());
        int servingSize = existsInSelectedFoods(parsedFood.getModel().getId());
        if (servingSize != -1) {
            selectedFoodsFragment.removeFoodFromSelectedFoodsByFoodID(parsedFood.getModel().getId());
            parsedFood.getModel().setServingSize(parsedFood.getModel().getServingSize());
        }

        selectedFoodsFragment.addFoodToSelectedFoods(parsedFood);
    }

    private void removeFoodFromSelectedFoods(int position) {
        selectedFoodsFragment.removeFoodFromSelectedFoods(position);
    }

    private int existsInSelectedFoods(int id) {
        for (ListItem<Food> food : selectedFoodsFragment.getSelectedFoods()) {
            if (food.getModel().getId() == id)
                return food.getModel().getServingSize();
        }

        return -1;
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        view.clearFocus(); // Remove focus from EditText
    }

    private ArrayList<ListItem<Food>> populateSelectedFoods() {
        ArrayList<ListItem<Food>> parsedFoodItems = new ArrayList<>();
        for (Food food : mealFoods) {
            ListItem<Food> foodItem = parseSelectedFood(food);
            parsedFoodItems.add(foodItem);
        }

        return parsedFoodItems;
    }

    private void parseMealFoods(ArrayList<Integer> mealFoodIDs, ArrayList<Integer> mealServingSizes) {
        mealFoods = new ArrayList<>();
        for (int i = 0; i < mealFoodIDs.size(); i++) {
            int foodID = mealFoodIDs.get(i);
            Food food = FoodsTable.getFoodByID(this, foodID);
            if (food != null) {
                food.setServingSize(mealServingSizes.get(i));
                mealFoods.add(food);
            }
        }
    }

    private void logMeal() {
        meal = getFinalMealModel();

        if (!meal.getMealFoods(this).isEmpty()) {
            DatabaseHelper dbHelper = new DatabaseHelper(this);
            dbHelper.insert(DatabaseHelper.Tables.MEALS, meal);
            dbHelper.close();

            Toast.makeText(this, "Meal logged!", Toast.LENGTH_SHORT).show();
            finish();
        }
        else {
            Toast.makeText(this, "Can't Log an Empty Meal", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateMeal() {
        meal = getFinalMealModel();

        if (!meal.getMealFoods(this).isEmpty()) {
            DatabaseHelper dbHelper = new DatabaseHelper(this);
            dbHelper.editRecord(DatabaseHelper.Tables.MEALS, meal, MealsTable.Columns.MEAL_ID, new String[]{String.valueOf(meal.getId())});
            dbHelper.close();

            Toast.makeText(this, "Meal Updated!", Toast.LENGTH_SHORT).show();

            Intent resultIntent = new Intent();
            resultIntent.putExtra("meal", meal);
            resultIntent.putExtra("adapter_position", getIntent().getIntExtra("adapter_position", -1));
            resultIntent.putExtra("old_date", oldDate);
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        }
        else {
            Toast.makeText(this, "Can't Save an Empty Meal", Toast.LENGTH_SHORT).show();
        }
    }

    private Meal getFinalMealModel() {
        setMealFoods();
        ArrayList<Integer> mealFoodIDs = new ArrayList<>();
        ArrayList<Integer> mealServingSizes = new ArrayList<>();
        for (Food food : mealFoods) {
            mealFoodIDs.add(food.getId());
            mealServingSizes.add(food.getServingSize());
        }

        String mealType = Meal.determineMealType(mealTime);
        String notes = String.valueOf(notesET.getText());

        int id = 0;
        if (meal != null)
            return new Meal(meal.getId(), mealDate, mealTime, mealType, notes, mealFoodIDs, mealServingSizes, meal.getUserID());

        return new Meal(mealDate, mealTime, mealType, notes, mealFoodIDs, mealServingSizes);
    }

    private void setMealFoods() {
        ArrayList<ListItem<Food>> foodItemsList = selectedFoodsFragment.getSelectedFoods();
        mealFoods = new ArrayList<>();
        for (ListItem<Food> foodItem : foodItemsList) {
            Food food = foodItem.getModel();
            mealFoods.add(food);
        }
    }

    private void setupBottomNavigationMenu() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_today) {
                hideKeyboard(searchBarET);
                Intent intent = new Intent(MealLogActivity.this, HomeDashboardActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_log) {
                hideKeyboard(searchBarET);
                return true;
            } else if (itemId == R.id.nav_progress) {
                hideKeyboard(searchBarET);
                Intent intent = new Intent(MealLogActivity.this, ProgressActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                return true;
            }

            return false;
        });
    }

    private void clearAll() {
        searchBarET.setText("");
        hideSearchResults();
        hideKeyboard(searchBarET);
        selectedFoodsFragment.clearSelectedFoods();
        updateMealDate(LocalDate.now());
        updateMealTime(LocalTime.now());
        notesET.setText("");
    }

    protected void onResume() {
        super.onResume();
        bottomNavigationView.setSelectedItemId(R.id.nav_log);
    }
}