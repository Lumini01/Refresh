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

import com.example.refresh.Fragments.SearchResultsFragment;
import com.example.refresh.Model.Food;
import com.example.refresh.Model.ListItem;
import com.example.refresh.R;

import java.util.ArrayList;
import java.util.List;

public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.ResultViewHolder> {

    private ArrayList<ListItem<Food>> resultsList;
    private SearchResultsFragment fragment;

    // Constructor
    public SearchResultsAdapter(ArrayList<ListItem<Food>> resultsList, SearchResultsFragment fragment) {
        this.resultsList = (resultsList != null) ? resultsList : new ArrayList<>();
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public ResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search_result, parent, false);
        return new ResultViewHolder(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull ResultViewHolder holder, int position) {
        ListItem<Food> result = resultsList.get(position);
        holder.bind(result);

        // Add click listener to navigate to the correct screen
        holder.addButton.setOnClickListener(v -> {
            fragment.addFoodToSelectedFoods(result);
            Toast.makeText(v.getContext(), result.getTitle() + "Added to the List", Toast.LENGTH_SHORT).show();
        });

        holder.itemContainer.setOnClickListener(v -> {
            fragment.navigateToFoodInfo(result.getModel());
        });

    }

    @Override
    public int getItemCount() {
        return resultsList != null ? resultsList.size() : 0;
    }

    // Method to update data efficiently
    public void updateResults(List<ListItem<Food>> newResults) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return resultsList.size();
            }

            @Override
            public int getNewListSize() {
                return newResults.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                ListItem<Food> oldItem = resultsList.get(oldItemPosition);
                ListItem<Food> newItem = newResults.get(newItemPosition);

                if (oldItem == null || newItem == null) {
                    return false;
                }

                return oldItem.getTitle().equals(newItem.getTitle()) && oldItem.getDescription().equals(newItem.getDescription());
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                return resultsList.get(oldItemPosition).equals(newResults.get(newItemPosition));
            }
        });

        resultsList = new ArrayList<>(newResults);
        diffResult.dispatchUpdatesTo(this);
    }

    // ViewHolder (placed at the bottom, following best practices)
    public static class ResultViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewTitle;
        private final TextView textViewDescription;
        private final ImageButton addButton;
        private final LinearLayout itemContainer;

        public ResultViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewResultTitle);
            textViewDescription = itemView.findViewById(R.id.textViewResultDescription);
            addButton = itemView.findViewById(R.id.buttonAdd);
            itemContainer = itemView.findViewById(R.id.item_container);

        }

        public void bind(ListItem<Food> result) {
            textViewTitle.setText(result.getTitle() != null ? result.getTitle() : "No Title");

            if (result.getDescription() != null && !result.getDescription().isEmpty()) {
                textViewDescription.setVisibility(View.VISIBLE);
                textViewDescription.setText(result.getDescription());
            } else {
                textViewDescription.setVisibility(View.GONE);
            }
        }
    }
}
