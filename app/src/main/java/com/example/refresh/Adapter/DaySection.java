package com.example.refresh.Adapter;

import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.refresh.Model.ListItem;
import com.example.refresh.Model.Meal;
import com.example.refresh.R;

import java.util.ArrayList;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;

public class DaySection extends Section {

    private final String day; // e.g., "Sunday"
    private final ArrayList<ListItem<Meal>> mealItems;   // List of mealLogs for that day
    private final OnEditMealListener editMealListener; // add a listener member
    private final OnDeleteMealListener deleteMealListener;


    public interface OnEditMealListener {
        void onEditMeal(ListItem<Meal> mealItem, int adapterPosition);
    }

    public interface OnDeleteMealListener {
        void onDeleteMeal(ListItem<Meal> mealItem, int adapterPosition);
    }

    public DaySection(String day, ArrayList<ListItem<Meal>> mealLogs,
                      OnEditMealListener editMealListener,
                      OnDeleteMealListener deleteMealListener) {
        super(SectionParameters.builder()
                .itemResourceId(R.layout.item_logged_meal)
                .headerResourceId(R.layout.item_day_header)
                .build());
        this.day = day;
        this.mealItems = mealLogs;
        this.editMealListener = editMealListener;
        this.deleteMealListener = deleteMealListener;
    }

    public String getDay() {
        return day;
    }

    // Return the number of meal items in this section
    @Override
    public int getContentItemsTotal() {
        return mealItems.size();
    }

    // Provide a ViewHolder for a meal item
    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new MealViewHolder(view);
    }

    // Bind data to the meal item ViewHolder
    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        ListItem<Meal> mealItem = mealItems.get(position);
        MealViewHolder itemHolder = (MealViewHolder) holder;

        itemHolder.bind(mealItem);

        itemHolder.editBtn.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION && editMealListener != null) {
                // Pass the meal item and its adapter position to the listener
                editMealListener.onEditMeal(mealItem, adapterPosition); }
        });

        itemHolder.removeBtn.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION && deleteMealListener != null) {
                deleteMealListener.onDeleteMeal(mealItem, adapterPosition);
            }
        });
    }

    public void addMealItem(ListItem<Meal> mealItem) {
        if (mealItem == null) {
            throw new IllegalArgumentException("Meal item cannot be null");
        }
        mealItems.add(mealItem);
    }

    public void removeMealItem(int position) {
        if (position < 0 || position >= mealItems.size()) {
            throw new IndexOutOfBoundsException("Invalid position: " + position);
        }
        mealItems.remove(position);
    }

    // Method to update an individual meal in the list
    public void updateMealItem(int position, ListItem<Meal> mealItem) {
        mealItems.set(position, mealItem);
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
        headerHolder.headerTitleTV.setText(day);
    }

    // Define the ViewHolder for meal items
    private class MealViewHolder extends RecyclerView.ViewHolder {
        private final TextView mealTitleTV;
        private final TextView mealDescriptionTV;
        private final ImageButton removeBtn;
        private final ImageButton editBtn;
        private final LinearLayout itemContainer;

        public MealViewHolder(View itemView) {
            super(itemView);
            mealTitleTV = itemView.findViewById(R.id.meal_title);
            mealDescriptionTV = itemView.findViewById(R.id.meal_description);
            removeBtn = itemView.findViewById(R.id.buttonRemove);
            editBtn = itemView.findViewById(R.id.buttonEdit);
            itemContainer = itemView.findViewById(R.id.item_container);
        }

        public void bind(ListItem<Meal> mealLog) {
            mealTitleTV.setText(mealLog.getTitle());
            mealDescriptionTV.setText(mealLog.getDescription());
        }
    }

    // Define the ViewHolder for the header (day)
    private class DayHeaderViewHolder extends RecyclerView.ViewHolder {
        private final TextView headerTitleTV;

        public DayHeaderViewHolder(View itemView) {
            super(itemView);
            headerTitleTV = itemView.findViewById(R.id.headerTitle);
        }
    }
}
