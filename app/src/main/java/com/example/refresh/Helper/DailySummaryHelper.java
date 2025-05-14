package com.example.refresh.Helper;

import android.content.Context;

import com.example.refresh.Database.MealsTable;
import com.example.refresh.Model.DaySummary;
import com.example.refresh.Model.Meal;

import java.time.LocalDate;
import java.util.ArrayList;

public class DailySummaryHelper {
    private final Context context;

    public DailySummaryHelper(Context context) {
        this.context = context;
    }

    // Retrieves the summary for a given day.
    public DaySummary getDailySummary(LocalDate day) {
        ArrayList<Meal> meals = MealsTable.getMealsInRange(context, day, day);

        // Create and return a summary model.
        return new DaySummary(context, day, meals);
    }

    public ArrayList<DaySummary> getSummariesBetween(LocalDate startDate, LocalDate endDate) {
        ArrayList<DaySummary> summaries = new ArrayList<>();

        for (LocalDate day = startDate; day.isBefore(endDate.plusDays(1)); day = day.plusDays(1)) {
            summaries.add(getDailySummary(day));
        }

        return summaries;
    }
}
