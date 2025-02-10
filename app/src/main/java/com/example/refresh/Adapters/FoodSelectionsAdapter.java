package com.example.refresh.Adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.refresh.Model.SearchResult;
import com.example.refresh.R;

import java.util.ArrayList;
import java.util.List;

public class FoodSelectionsAdapter extends RecyclerView.Adapter<FoodSelectionsAdapter.FoodSelectionViewHolder> {

    private List<SearchResult> foodSelectionsList;

    // Constructor
    public FoodSelectionsAdapter(List<SearchResult> foodSelectionsList) {
        this.foodSelectionsList = (foodSelectionsList != null) ? foodSelectionsList : new ArrayList<>();
    }

    @NonNull
    @Override
    public FoodSelectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_food, parent, false);
        return new FoodSelectionViewHolder(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull FoodSelectionViewHolder holder, int position) {
        SearchResult foodSelection = foodSelectionsList.get(position);
        holder.bind(foodSelection);

        // Add click listener to navigate to the correct screen
        holder.removeButton.setOnClickListener(v -> {
            //TODO: Remove the food from the selected foods list.

            Toast.makeText(v.getContext(), foodSelection.getTitle() + "Removed from the List", Toast.LENGTH_SHORT).show();
        });

        holder.itemContainer.setOnClickListener(v -> {
            //TODO: Navigate to the food fragment.
        });

        // Prevent ImageButton click from triggering LinearLayout click
        holder.removeButton.setOnTouchListener((v, event) -> {
            v.getParent().requestDisallowInterceptTouchEvent(true);
            if (event.getAction() == MotionEvent.ACTION_UP) {
                v.performClick(); // Call performClick() to satisfy accessibility requirements
            }
            return false; // Allow normal click behavior
        });
    }

    @Override
    public int getItemCount() {
        return foodSelectionsList != null ? foodSelectionsList.size() : 0;
    }

    // Method to update data efficiently
    public void updateFoodSelections(List<SearchResult> newFoodSelections) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return foodSelectionsList.size();
            }

            @Override
            public int getNewListSize() {
                return newFoodSelections.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                SearchResult oldItem = foodSelectionsList.get(oldItemPosition);
                SearchResult newItem = newFoodSelections.get(newItemPosition);

                if (oldItem == null || newItem == null) {
                    return false;
                }

                return oldItem.getTitle().equals(newItem.getTitle()) && oldItem.getDescription().equals(newItem.getDescription());
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                return foodSelectionsList.get(oldItemPosition).equals(newFoodSelections.get(newItemPosition));
            }
        });

        foodSelectionsList = new ArrayList<>(newFoodSelections);
        diffResult.dispatchUpdatesTo(this);
    }

    // ViewHolder (placed at the bottom, following best practices)
    public static class FoodSelectionViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewTitle;
        private final TextView textViewDescription;
        private final ImageButton removeButton;
        private final LinearLayout itemContainer;

        public FoodSelectionViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewSelectionTitle);
            textViewDescription = itemView.findViewById(R.id.textViewSelectionDescription);
            removeButton = itemView.findViewById(R.id.buttonAdd);
            itemContainer = itemView.findViewById(R.id.item_container);

        }

        public void bind(SearchResult foodSelection) {
            textViewTitle.setText(foodSelection.getTitle() != null ? foodSelection.getTitle() : "No Title");

            if (foodSelection.getDescription() != null && !foodSelection.getDescription().isEmpty()) {
                textViewDescription.setVisibility(View.VISIBLE);
                textViewDescription.setText(foodSelection.getDescription());
            } else {
                textViewDescription.setVisibility(View.GONE);
            }
        }
    }
}
