package com.example.refresh.Adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.refresh.Fragment.SelectedFoodsFragment;
import com.example.refresh.Model.Food;
import com.example.refresh.Model.ListItem;
import com.example.refresh.R;

import java.util.ArrayList;

public class FoodSelectionsAdapter extends RecyclerView.Adapter<FoodSelectionsAdapter.FoodSelectionViewHolder> {

    private ArrayList<ListItem<Food>> foodSelections;
    private final SelectedFoodsFragment fragment;

    // Constructor
    public FoodSelectionsAdapter(ArrayList<ListItem<Food>> foodSelectionsList, SelectedFoodsFragment fragment) {
        this.foodSelections = (foodSelectionsList != null) ? foodSelectionsList : new ArrayList<>();
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public FoodSelectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_selected_food, parent, false);
        return new FoodSelectionViewHolder(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull FoodSelectionViewHolder holder, int position) {
        ListItem<Food> foodSelection = foodSelections.get(position);
        holder.bind(foodSelection);

        // Add click listener to navigate to the correct screen
        holder.removeBtn.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition != RecyclerView.NO_POSITION) {
                fragment.removeFoodFromSelectedFoods(currentPosition);
                Toast.makeText(v.getContext(), foodSelection.getTitle() + " Removed from the List", Toast.LENGTH_SHORT).show();
            }
        });

        holder.itemContainer.setOnClickListener(v -> {
            fragment.navigateToFoodInfo(foodSelection.getModel());
        });
    }

    @Override
    public int getItemCount() {
        return foodSelections != null ? foodSelections.size() : 0;
    }

    // **Add an item at a specific position**
    public void addItem(ListItem<Food> newItem, int position) {
        notifyItemInserted(position); // Notify RecyclerView about the new item
    }

    // **Remove an item from a specific position**
    public void removeItem(int position) {
        if (position >= 0 && position < foodSelections.size() + 1) {
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, foodSelections.size()); // Ensure indices update
        }

        if (foodSelections.isEmpty()) {
            notifyDataSetChanged(); // If list becomes empty, full refresh
        }
    }

    public void clearAll() {
        notifyDataSetChanged();
    }

    // ViewHolder (placed at the bottom, following best practices)
    public static class FoodSelectionViewHolder extends RecyclerView.ViewHolder {

        private final TextView titleTV;
        private final TextView descriptionTV;
        private final ImageButton removeBtn;
        private final LinearLayout itemContainer;

        public FoodSelectionViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTV = itemView.findViewById(R.id.meal_title);
            descriptionTV = itemView.findViewById(R.id.meal_description);
            removeBtn = itemView.findViewById(R.id.buttonRemove);
            itemContainer = itemView.findViewById(R.id.item_container);

        }

        public void bind(ListItem<Food> foodSelection) {
            titleTV.setText(foodSelection.getTitle() != null ? foodSelection.getTitle() : "No Title");

            if (foodSelection.getDescription() != null && !foodSelection.getDescription().isEmpty()) {
                descriptionTV.setVisibility(View.VISIBLE);
                descriptionTV.setText(foodSelection.getDescription());
            } else {
                descriptionTV.setVisibility(View.GONE);
            }
        }
    }
}
