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

import com.example.refresh.Fragments.SelectedFoodsFragment;
import com.example.refresh.Model.Food;
import com.example.refresh.Model.ListItem;
import com.example.refresh.R;

import java.util.ArrayList;

public class FoodSelectionsAdapter extends RecyclerView.Adapter<FoodSelectionsAdapter.FoodSelectionViewHolder> {

    private ArrayList<ListItem<Food>> foodSelections;
    private SelectedFoodsFragment fragment;

    // Constructor
    public FoodSelectionsAdapter(ArrayList<ListItem<Food>> foodSelectionsList, SelectedFoodsFragment fragment) {
        this.foodSelections = (foodSelectionsList != null) ? foodSelectionsList : new ArrayList<>();
        this.fragment = fragment;
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
        ListItem<Food> foodSelection = foodSelections.get(position);
        holder.bind(foodSelection);

        // Add click listener to navigate to the correct screen
        holder.removeButton.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition != RecyclerView.NO_POSITION) {
                fragment.removeFoodFromSelectedFoods(currentPosition);
                Toast.makeText(v.getContext(), foodSelection.getTitle() + "Removed from the List", Toast.LENGTH_SHORT).show();
            }
        });

        holder.itemContainer.setOnClickListener(v -> {
            //TODO: Navigate to the food fragment.
            fragment.navigateToFoodInfo(foodSelection.getModel());
        });
    }

    @Override
    public int getItemCount() {
        return foodSelections != null ? foodSelections.size() : 0;
    }

    // Method to update data efficiently
    public void updateFoodSelections(ArrayList<ListItem<Food>> newFoodSelections) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return foodSelections.size();
            }

            @Override
            public int getNewListSize() {
                return newFoodSelections.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                ListItem<Food> oldItem = foodSelections.get(oldItemPosition);
                ListItem<Food> newItem = newFoodSelections.get(newItemPosition);

                if (oldItem == null || newItem == null) {
                    return false;
                }

                return oldItem.getTitle().equals(newItem.getTitle()) && oldItem.getDescription().equals(newItem.getDescription());
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                return foodSelections.get(oldItemPosition).equals(newFoodSelections.get(newItemPosition));
            }
        });

        foodSelections = new ArrayList<>(newFoodSelections);
        diffResult.dispatchUpdatesTo(this);
    }

    // **Add an item at a specific position**
    public void addItem(ListItem<Food> newItem, int position) {
        notifyItemInserted(position); // Notify RecyclerView about the new item
    }

    // **Remove an item from a specific position**
    public void removeItem(int position) {
        if (foodSelections.isEmpty()) {
            // When the list is empty, do a full refresh.
            notifyDataSetChanged();
        } else if (position >= 0 && position < foodSelections.size()) {
            // Otherwise, notify that an item was removed.
            notifyItemRemoved(position);
        }
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
            removeButton = itemView.findViewById(R.id.buttonRemove);
            itemContainer = itemView.findViewById(R.id.item_container);

        }

        public void bind(ListItem<Food> foodSelection) {
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
