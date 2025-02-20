package com.example.refresh.Helper;

import android.content.Context;

import com.example.refresh.Database.MealsTable;
import com.example.refresh.Model.DaySummary;
import com.example.refresh.Model.Meal;

import java.time.LocalDate;
import java.util.ArrayList;

public class DailySummaryHelper {
    private final DatabaseHelper dbHelper; // Your database access object

    public DailySummaryHelper(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    // Retrieves the summary for a given day.
    public DaySummary getDailySummary(Context context, LocalDate day) {
        ArrayList<Meal> meals = MealsTable.getMealsInRange(dbHelper.getContext(), day, day);

        // Create and return a summary model.
        return new DaySummary(context, day, meals);
    }

    public ArrayList<DaySummary> getSummariesBetween(Context context, LocalDate startDate, LocalDate endDate) {
        ArrayList<DaySummary> summaries = new ArrayList<>();

        for (LocalDate day = startDate; day.isBefore(endDate.plusDays(1)); day = day.plusDays(1)) {
            summaries.add(getDailySummary(context, day));
        }

        return summaries;
    }

    // Additional methods for more detailed statistics.
    public double calculateAverageCalories(LocalDate startDate, LocalDate endDate) {
        // Implement logic for average calorie calculation over a range of days.
        ArrayList<DaySummary> summaries = getSummariesBetween(dbHelper.getContext(), startDate, endDate);
        int total = 0;
        for (DaySummary summary : summaries) {
            total += summary.getTotalCalories();
        }
        return summaries.isEmpty() ? 0 : (double) total / summaries.size();
    }

    // Other helper methods can be added here as needed (e.g., getting macronutrient breakdowns).
}
