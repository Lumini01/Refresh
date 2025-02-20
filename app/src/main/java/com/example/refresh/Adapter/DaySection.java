package com.example.refresh.Adapter;

import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.refresh.Model.Meal;
import com.example.refresh.R;

import java.util.ArrayList;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;

public class DaySection extends Section {

    private String day;         // e.g., "Sunday"
    private ArrayList<Meal> meals;   // List of meals for that day

    public DaySection(String day, ArrayList<Meal> meals) {
        super(SectionParameters.builder()
                .itemResourceId(R.layout.item_meal)
                .headerResourceId(R.layout.item_header)
                .build());
        this.day = day;
        this.meals = meals;
    }

    // Return the number of meal items in this section
    @Override
    public int getContentItemsTotal() {
        return meals.size();
    }

    // Provide a ViewHolder for a meal item
    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new MealViewHolder(view);
    }

    // Bind data to the meal item ViewHolder
    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        Meal meal = meals.get(position);
        MealViewHolder itemHolder = (MealViewHolder) holder;
        itemHolder.mealTitle.setText(meal.getMealTitle());
        itemHolder.mealDescription.setText(meal.getMealDescription());
    }

    // Provide a ViewHolder for the header (the day title)
    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        return new DayHeaderViewHolder(view);
    }

    // Bind data to the header ViewHolder
    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        DayHeaderViewHolder headerHolder = (DayHeaderViewHolder) holder;
        headerHolder.headerTitle.setText(day);
    }

    // Define the ViewHolder for meal items
    private class MealViewHolder extends RecyclerView.ViewHolder {
        private final TextView mealTitle;
        private final TextView mealDescription;

        public MealViewHolder(View itemView) {
            super(itemView);
            mealTitle = itemView.findViewById(R.id.meal_title);
            mealDescription = itemView.findViewById(R.id.meal_description);
        }
    }

    // Define the ViewHolder for the header (day)
    private class DayHeaderViewHolder extends RecyclerView.ViewHolder {
        private final TextView headerTitle;

        public DayHeaderViewHolder(View itemView) {
            super(itemView);
            headerTitle = itemView.findViewById(R.id.headerTitle);
        }
    }
}
