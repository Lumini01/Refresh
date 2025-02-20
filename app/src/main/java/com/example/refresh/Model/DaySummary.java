package com.example.refresh.Model;

import android.content.Context;

import com.example.refresh.Database.MealsTable;
import com.example.refresh.Helper.DatabaseHelper;

import java.time.LocalDate;
import java.util.ArrayList;

public class DaySummary {
    private LocalDate day;
    private ArrayList<Meal> meals;
    private int mealCount;
    private int totalCalories;
    private int totalCarbs;
    private int totalProtein;
    private int totalFat;
    private int totalWater;

    public DaySummary() {
        // Default constructor
        this.day = LocalDate.now();
    }

    public DaySummary(LocalDate day) {
        this.day = day;
    }

    public DaySummary(Context context, ArrayList<Meal> meals) {
        this.day = LocalDate.now();
        this.meals = meals;
        mealCount = meals.size();
        calcSummaryMetrics(context);
    }

    public DaySummary(Context context, LocalDate day, ArrayList<Meal> meals) {
        this.day = day;
        this.meals = meals;
        mealCount = meals.size();
        calcSummaryMetrics(context);
    }

    public DaySummary(LocalDate day, ArrayList<Meal> meals, int totalCalories, int totalCarbs, int totalProtein, int totalFat, int totalWater) {
        this.day = day;
        this.meals = meals;
        this.mealCount = meals.size();
        this.totalCalories = totalCalories;
        this.totalCarbs = totalCarbs;
        this.totalProtein = totalProtein;
        this.totalFat = totalFat;
        this.totalWater = totalWater;
    }

    public LocalDate getDay() {
        return day;
    }

    public void setDay(LocalDate day) {
        this.day = day;
    }

    public ArrayList<Meal> getMeals() {
        return meals;
    }

    public void setMeals(ArrayList<Meal> meals) {
        this.meals = meals;
    }

    public int getMealCount() {
        return mealCount;
    }

    public void setMealCount(int mealCount) {
        this.mealCount = mealCount;
    }

    public int getTotalCalories() {
        return totalCalories;
    }

    public void setTotalCalories(int totalCalories) {
        this.totalCalories = totalCalories;
    }

    public int getTotalCarbs() {
        return totalCarbs;
    }

    public void setTotalCarbs(int totalCarbs) {
        this.totalCarbs = totalCarbs;
    }

    public int getTotalProtein() {
        return totalProtein;
    }

    public void setTotalProtein(int totalProtein) {
        this.totalProtein = totalProtein;
    }

    public int getTotalFat() {
        return totalFat;
    }

    public void setTotalFat(int totalFat) {
        this.totalFat = totalFat;
    }

    public int getTotalWater() {
        return totalWater;
    }

    public void setTotalWater(int totalWater) {
        this.totalWater = totalWater;
    }

    private void calcSummaryMetrics(Context context) {
        setTotalCalories(calcTotalCalories(context));
        setTotalCarbs(calcTotalCarbs(context));
        setTotalProtein(calcTotalProtein(context));
        setTotalFat(calcTotalFat(context));
        setTotalWater(calcTotalWater(context));
    }

    private int calcTotalCalories(Context context) {
        int totalCalories = 0;
        for (Meal meal : meals) {
            totalCalories += meal.getCalories(context);
        }
        return totalCalories;
    }

    private int calcTotalCarbs(Context context) {
        int totalCarbs = 0;
        for (Meal meal : meals) {
            totalCarbs += meal.getCarbs(context);
        }
        return totalCarbs;
    }

    private int calcTotalProtein(Context context) {
        int totalProtein = 0;
        for (Meal meal : meals) {
            totalProtein += meal.getProtein(context);
        }
        return totalProtein;
    }

    private int calcTotalFat(Context context) {
        int totalFat = 0;
        for (Meal meal : meals) {
            totalFat += meal.getFat(context);
        }
        return totalFat;
    }

    private int calcTotalWater(Context context) {
        return MealsTable.getWaterIntakeForDay(context, day);
    }
}
