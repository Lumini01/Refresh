package com.example.refresh.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentContainerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.refresh.Adapter.DaySection;
import com.example.refresh.Database.MealsTable;
import com.example.refresh.Fragment.TrendGraphFragment;
import com.example.refresh.Helper.DailySummaryHelper;
import com.example.refresh.Helper.DatabaseHelper;
import com.example.refresh.Model.DaySummary;
import com.example.refresh.Model.ListItem;
import com.example.refresh.Model.Meal;
import com.example.refresh.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

public class Progress extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageButton refreshButton;
    private ImageButton calenderButton;
    private TextView title;
    private RecyclerView recyclerViewMeals;
    private TextView WeekDatesTV;
    private ImageButton lastWeekButton;
    private ImageButton nextWeekButton;
    private ArrayList<Meal> weekMeals;
    private SectionedRecyclerViewAdapter sectionAdapter;
    private LocalDate currentWeekStart;
    private BottomNavigationView bottomNavigationView;
    private FragmentContainerView weekGraphContainer;
    private TrendGraphFragment trendGraphFragment;
    private SharedPreferences userSP;
    private DailySummaryHelper dailySummaryHelper;
    private ArrayList<DaySummary> daySummaries;
    private ActivityResultLauncher<Intent> editMealLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        dailySummaryHelper = new DailySummaryHelper(this);
        userSP = getSharedPreferences(getSharedPreferences("AppPreferences", MODE_PRIVATE)
                        .getString("loggedUserSPName", null),
                MODE_PRIVATE);

        initializeUI();
        setupUI();

        // Set up the toolbar
        setSupportActionBar(toolbar);

        currentWeekStart = LocalDate.now().with(java.time.DayOfWeek.MONDAY);
        daySummaries = dailySummaryHelper.getSummariesBetween(currentWeekStart, getCurrentWeekEnd());

        setupBottomNavigationMenu();
        setupRecyclerView();


        trendGraphFragment = TrendGraphFragment.newInstance(daySummaries, userSP.getInt("calorieGoal", 0));
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.week_graph_container, trendGraphFragment) // put it in our container
                .commit();

        editMealLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            Meal updatedMeal = (Meal) data.getSerializableExtra("meal");
                            int adapterPosition = data.getIntExtra("adapter_position", -1);
                            LocalDate oldDate = (LocalDate) data.getSerializableExtra("old_date");

                            if (updatedMeal != null && adapterPosition != -1)
                                updateMealInAdapter(updatedMeal, adapterPosition, oldDate);
                        }
                    }
                }
        );
    }

    public void initializeUI() {
        weekGraphContainer = findViewById(R.id.week_graph_container);
        toolbar = findViewById(R.id.toolbar);
        recyclerViewMeals = findViewById(R.id.recycler_view_meals);
        refreshButton = findViewById(R.id.backArrow);
        calenderButton = findViewById(R.id.extra_button);
        WeekDatesTV = findViewById(R.id.WeekDatesTV);
        lastWeekButton = findViewById(R.id.lastWeekButton);
        nextWeekButton = findViewById(R.id.nextWeekButton);
        title = findViewById(R.id.toolbarTitle);
    }

    public void setupUI() {
        refreshButton.setImageResource(R.drawable.ic_refresh);
        calenderButton.setImageResource(R.drawable.ic_calendar);
        title.setText("Progress");

        refreshButton.setOnClickListener(v -> {
            refreshActivity();
        });

        lastWeekButton.setOnClickListener(v -> {
            previousWeek();
            updateWeekDates();
            refreshActivity();
        });

        nextWeekButton.setOnClickListener(v -> {
            nextWeek();
            updateWeekDates();
            refreshActivity();
        });
    }

    private void refreshActivity() {
        updateRecyclerView();
        updateDaySummaries();
        updateTrendGraph();
    }

    private void setupBottomNavigationMenu() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_today) {
                Intent intent = new Intent(Progress.this, HomeDashboard.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_log) {
                Intent intent = new Intent(Progress.this, MealLogActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_progress) {

                return true;
            }
//            else if (itemId == R.id.nav_recipes) {
//                // Handle recipes click
//                return true;
//            }

            return false;
        });

        bottomNavigationView.setSelectedItemId(R.id.nav_progress);
    }

    public void setupRecyclerView() {
        recyclerViewMeals.setLayoutManager(new LinearLayoutManager(this));

        // Create the SectionedRecyclerViewAdapter instance
        sectionAdapter = new SectionedRecyclerViewAdapter();

        recyclerViewMeals.setAdapter(sectionAdapter);

        updateRecyclerView();
    }

    private void updateMealInAdapter(Meal updatedMeal, int adapterPosition, LocalDate oldDate) {
        LocalDate weekStart = currentWeekStart;
        LocalDate weekEnd = getCurrentWeekEnd();
        LocalDate updatedDate = updatedMeal.getDate();
        boolean inWeek = !updatedDate.isBefore(weekStart) && !updatedDate.isAfter(weekEnd);
        String updatedDay = updatedDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());

        // Find the source section and local position from the global adapterPosition.
        int currentPosition = 0;
        DaySection sourceSection = null;
        int localPosition = -1;
        for (Section section : sectionAdapter.getCopyOfSectionsMap().values()) {
            int headerCount = section.hasHeader() ? 1 : 0;
            int sectionItemCount = section.getContentItemsTotal();
            int sectionTotal = headerCount + sectionItemCount;
            if (adapterPosition >= currentPosition && adapterPosition < currentPosition + sectionTotal) {
                localPosition = adapterPosition - currentPosition - headerCount;
                if (section instanceof DaySection) {
                    sourceSection = (DaySection) section;
                }
                break;
            }
            currentPosition += sectionTotal;
        }
        if (sourceSection == null) return; // nothing to update if not found

        // Get the day string of the source section
        String sourceDay = sourceSection.getDay();

        if (!inWeek) {
            // The updated meal is no longer in this week: remove it.
            sourceSection.removeMealItem(localPosition);
            sectionAdapter.notifyItemRemoved(adapterPosition);
        } else {
            if (sourceDay.equals(updatedDay)) {
                // Same day/section: update the item in place.
                ListItem<Meal> updatedMealItem = new ListItem<>(
                        updatedMeal.getMealTitle(),
                        updatedMeal.getMealDescription(this),
                        updatedMeal
                );
                sourceSection.updateMealItem(localPosition, updatedMealItem);
                sectionAdapter.notifyItemChanged(adapterPosition);
            } else {
                // The meal's date has changed to another day in the current week.
                // Remove it from the source section first.
                sourceSection.removeMealItem(localPosition);
                sectionAdapter.notifyItemRemoved(adapterPosition);

                // Now find the target section and insert the updated meal.
                int newGlobalPosition = 0;
                boolean inserted = false;
                for (Section section : sectionAdapter.getCopyOfSectionsMap().values()) {
                    int headerCount = section.hasHeader() ? 1 : 0;
                    int sectionItemCount = section.getContentItemsTotal();
                    if (section instanceof DaySection) {
                        DaySection daySection = (DaySection) section;
                        if (daySection.getDay().equals(updatedDay)) {
                            // Insert at the end of the target section.
                            ListItem<Meal> updatedMealItem = new ListItem<>(
                                    updatedMeal.getMealTitle(),
                                    updatedMeal.getMealDescription(this),
                                    updatedMeal
                            );
                            daySection.addMealItem(updatedMealItem);
                            // The insertion position is computed as:
                            // current accumulated global position + header (if any) + new item index.
                            int insertionPosition = newGlobalPosition + headerCount + sectionItemCount;
                            sectionAdapter.notifyItemInserted(insertionPosition);
                            inserted = true;
                            break;
                        }
                    }
                    newGlobalPosition += headerCount + sectionItemCount;
                }
                if (!inserted) {
                    // If the target section wasn't found (shouldn't happen in a complete week),
                    // fall back to updating the whole RecyclerView.
                    updateRecyclerView();
                }
            }
        }
        int dayNum = (int) ChronoUnit.DAYS.between(weekStart, updatedDate);
        int oldDayNum = (int) ChronoUnit.DAYS.between(weekStart, oldDate);

        daySummaries.set(oldDayNum, dailySummaryHelper.getSummariesBetween(oldDate, oldDate).get(0));
        if (ChronoUnit.DAYS.between(weekStart, updatedDate) >= 0 && ChronoUnit.DAYS.between(weekStart, updatedDate) <= 6) {
            daySummaries.set(dayNum, dailySummaryHelper.getSummariesBetween(updatedDate, updatedDate).get(0));
        }

        updateTrendGraph();
    }

    private void updateRecyclerView() {
        LocalDate weekStart = currentWeekStart;
        LocalDate weekEnd = getCurrentWeekEnd();

        // Query your database to get meals for the week
        setWeekMeals(weekStart, weekEnd);

        // Group meals by DayOfWeek
        Map<DayOfWeek, ArrayList<Meal>> mealsByDay = new EnumMap<>(DayOfWeek.class);
        for (DayOfWeek day : DayOfWeek.values()) {
            mealsByDay.put(day, new ArrayList<>());
        }
        for (Meal meal : weekMeals) {
            LocalDate mealDate = meal.getDate();
            if (!mealDate.isBefore(weekStart) && !mealDate.isAfter(weekEnd)) {
                Objects.requireNonNull(mealsByDay.get(mealDate.getDayOfWeek())).add(meal);
            }
        }

        // Clear current sections from the adapter
        sectionAdapter.removeAllSections();

        // Add a section for each day that has meals
        for (DayOfWeek day : DayOfWeek.values()) {
            ArrayList<Meal> dayMeals = mealsByDay.get(day);
            if (dayMeals != null && !dayMeals.isEmpty()) {
                String dayName = day.getDisplayName(TextStyle.FULL, Locale.getDefault());
                ArrayList<ListItem<Meal>> dayMealsItems = new ArrayList<>();
                for (Meal meal : dayMeals) {
                    dayMealsItems.add(new ListItem<>(meal.getMealTitle(), meal.getMealDescription(this), meal));
                }

                // Create a DaySection passing the edit listener
                DaySection section = new DaySection(dayName, dayMealsItems,
                        (mealItem, adapterPosition) -> {
                    // Create the Intent to launch the edit activity
                    Intent editIntent = new Intent(Progress.this, MealLogActivity.class);
                    editIntent.putExtra("edit_mode", true);
                    // Pass the Meal object for editing
                    editIntent.putExtra("meal", mealItem.getModel());
                    // Optionally, pass the adapterPosition to know which item to update when the result returns
                    editIntent.putExtra("adapter_position", adapterPosition);
                    // Launch the edit activity using the registered ActivityResultLauncher
                    editMealLauncher.launch(editIntent);
                },
                        (mealItem, adapterPosition) -> {
                    deleteMeal(mealItem.getModel(), adapterPosition);
                });

                sectionAdapter.addSection(section);
            }
        }
        sectionAdapter.notifyDataSetChanged();
    }


    private void updateDaySummaries() {
        LocalDate weekStart = currentWeekStart;
        LocalDate weekEnd = getCurrentWeekEnd();
        daySummaries = dailySummaryHelper.getSummariesBetween(weekStart, weekEnd);
    }

    private void setWeekMeals(LocalDate weekStart, LocalDate weekEnd) {

        weekMeals = MealsTable.getMealsInRange(this, weekStart, weekEnd);

        if (weekMeals == null) {
            weekMeals = new ArrayList<>();
        }
    }

    private LocalDate getCurrentWeekEnd() {
        return currentWeekStart.plusDays(6);
    }

    private void nextWeek() {
        currentWeekStart = currentWeekStart.plusWeeks(1);
    }

    private void previousWeek() {
        currentWeekStart = currentWeekStart.minusWeeks(1);
    }

    protected void onResume() {
        super.onResume();
        bottomNavigationView.setSelectedItemId(R.id.nav_progress);
    }

    private void updateTrendGraph() {
        trendGraphFragment.updateLineChart(daySummaries, userSP.getInt("calorieGoal", 0));
    }

    public void updateWeekDates() {

        int startDayInt = currentWeekStart.getDayOfMonth();
        int endDayInt = getCurrentWeekEnd().getDayOfMonth();

        String startDay = getDayString(startDayInt);
        String endDay = getDayString(endDayInt);

        String weekDates = currentWeekStart.getMonth().getDisplayName(TextStyle.SHORT, Locale.getDefault()) + " " + startDay + " - " + getCurrentWeekEnd().getMonth().getDisplayName(TextStyle.SHORT, Locale.getDefault()) + " " + endDay;
        WeekDatesTV.setText(weekDates);
    }

    @NonNull
    private static String getDayString(int dayInt) {
        String startDay = dayInt + "";
        switch (dayInt) {
            case 1:
                startDay += "st";
                break;
            case 2:
                startDay += "nd";
                break;
            case 3:
                startDay += "rd";
                break;
            default:
                startDay += "th";
                break;
        }
        return startDay;
    }

    private void deleteMeal(Meal meal, int adapterPosition) {

        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        databaseHelper.deleteRecords(DatabaseHelper.Tables.MEALS, MealsTable.Columns.MEAL_ID, new String[]{String.valueOf(meal.getId())});

        int currentPosition = 0;
        DaySection targetSection = null;
        int localPosition = -1;

        // Iterate through sections to find the section containing the deleted item
        for (Section section : sectionAdapter.getCopyOfSectionsMap().values()) {
            int headerCount = section.hasHeader() ? 1 : 0;
            int sectionItemCount = section.getContentItemsTotal();
            int sectionTotal = headerCount + sectionItemCount;

            if (adapterPosition >= currentPosition && adapterPosition < currentPosition + sectionTotal) {
                localPosition = adapterPosition - currentPosition - headerCount;
                if (section instanceof DaySection) {
                    targetSection = (DaySection) section;
                    targetSection.removeMealItem(localPosition);
                }
                break;
            }
            currentPosition += sectionTotal;
        }

        // Notify the adapter about the removal
        sectionAdapter.notifyItemRemoved(adapterPosition);

        // Check if the target section is now empty and remove it if so
        if (targetSection != null && targetSection.getContentItemsTotal() == 0) {
            sectionAdapter.removeSection(targetSection);
            sectionAdapter.notifyDataSetChanged();
        }

        updateDaySummaries();
        updateTrendGraph();
    }
}

