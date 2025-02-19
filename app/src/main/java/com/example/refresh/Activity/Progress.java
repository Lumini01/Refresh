package com.example.refresh.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.refresh.Adapters.DaySection;
import com.example.refresh.Database.DatabaseHelper;
import com.example.refresh.Database.Tables.MealsTable;
import com.example.refresh.Model.Meal;
import com.example.refresh.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

public class Progress extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerViewMeals;
    private LineChart chart;
    private ArrayList<Meal> weekMeals;
    private SectionedRecyclerViewAdapter sectionAdapter;
    private LocalDate currentWeekStart;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        // Set up the toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        currentWeekStart = LocalDate.now().with(java.time.DayOfWeek.MONDAY);

        recyclerViewMeals = findViewById(R.id.recycler_view_meals);
        setupBottomNavigationMenu();
        setupRecyclerView();
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

        updateRecyclerViewForWeek();
        // Set the adapter to the RecyclerView
        recyclerViewMeals.setAdapter(sectionAdapter);
    }

    // TODO: bug test - this doesn't work...
    private void updateRecyclerViewForWeek() {
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
            LocalDate mealDate = meal.getDate(); // Make sure Meal has getDate() returning LocalDate
            if (!mealDate.isBefore(weekStart) && !mealDate.isAfter(weekEnd)) {
                mealsByDay.get(mealDate.getDayOfWeek()).add(meal);
            }
        }

        // Clear current sections from the adapter
        sectionAdapter.removeAllSections();

        // Add a section for each day that has meals
        for (DayOfWeek day : DayOfWeek.values()) {
            ArrayList<Meal> dayMeals = mealsByDay.get(day);
            if (dayMeals != null && !dayMeals.isEmpty()) {
                String dayName = day.getDisplayName(TextStyle.FULL, Locale.getDefault());
                sectionAdapter.addSection(new DaySection(dayName, dayMeals));
            }
        }

        sectionAdapter.notifyDataSetChanged();
    }

    private void setWeekMeals(LocalDate weekStart, LocalDate weekEnd) {
        // Build the selection query.
        // This converts the stored date ("DD/MM/YYYY") into ISO format ("YYYY-MM-DD") on the fly.
        String selection = "(substr(date, 7, 4) || '-' || substr(date, 4, 2) || '-' || substr(date, 1, 2)) >= ? " +
                "AND (substr(date, 7, 4) || '-' || substr(date, 4, 2) || '-' || substr(date, 1, 2)) <= ?";
        // Use ISO-formatted week boundaries as selection arguments.
        String[] selectionArgs = { weekStart.toString(), weekEnd.toString() };

        // Query the "meals" table. Change null to specify columns if needed.
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        weekMeals = dbHelper.getRecords(DatabaseHelper.Tables.MEALS, MealsTable.Columns.DATE, selection, selectionArgs);

        if (weekMeals == null) {
            weekMeals = new ArrayList<>();
        }
    }


    public void setupLineChart() {
        // Find the chart from the layout
        chart = findViewById(R.id.lineChart);

        // Prepare sample data entries
        ArrayList<Entry> entries = new ArrayList<>();
        entries.add(new Entry(0f, 1f));
        entries.add(new Entry(1f, 3f));
        entries.add(new Entry(2f, 2f));
        entries.add(new Entry(3f, 5f));
        entries.add(new Entry(4f, 4f));

        // Create a dataset and configure styling for a modern look
        LineDataSet dataSet = new LineDataSet(entries, "Health Data");
        dataSet.setColor(Color.parseColor("#4CAF50")); // Modern flat color
        dataSet.setLineWidth(2f);
        dataSet.setDrawCircles(false);
        dataSet.setDrawValues(false);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER); // Smooth, curved lines
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(Color.parseColor("#4CAF50"));
        dataSet.setFillAlpha(80); // Adjust transparency

        // Create a LineData object with the dataset
        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);

        // Customize the X-Axis
        XAxis xAxis = chart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setTextColor(Color.DKGRAY);

        // Customize the Left Y-Axis
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setDrawAxisLine(false);
        leftAxis.setTextColor(Color.DKGRAY);

        // Disable the Right Y-Axis
        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);

        // Remove the description label
        Description description = new Description();
        description.setEnabled(false);
        chart.setDescription(description);

        // Disable the legend for a minimalist look
        Legend legend = chart.getLegend();
        legend.setEnabled(false);

        // Enable touch gestures and pinch zoom
        chart.setTouchEnabled(true);
        chart.setPinchZoom(true);

        // Set a transparent background for a modern feel
        chart.setBackgroundColor(Color.TRANSPARENT);

        // Animate the chart horizontally
        chart.animateX(1500);

        // Refresh the chart
        chart.invalidate();
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
}

