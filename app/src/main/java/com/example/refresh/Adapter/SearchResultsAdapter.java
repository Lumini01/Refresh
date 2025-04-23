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

import com.example.refresh.Fragment.SearchResultsFragment;
import com.example.refresh.Model.Food;
import com.example.refresh.Model.ListItem;
import com.example.refresh.R;

import java.util.ArrayList;
import java.util.List;

public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.ResultViewHolder> {

    private ArrayList<ListItem<Food>> resultsList;
    private final SearchResultsFragment fragment;

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
        holder.addBtn.setOnClickListener(v -> {
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

    // ViewHolder (placed at the bottom, following best practices)
    public static class ResultViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleTV;
        private final TextView descriptionTV;
        private final ImageButton addBtn;
        private final LinearLayout itemContainer;

        public ResultViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTV = itemView.findViewById(R.id.textViewResultTitle);
            descriptionTV = itemView.findViewById(R.id.textViewResultDescription);
            addBtn = itemView.findViewById(R.id.buttonAdd);
            itemContainer = itemView.findViewById(R.id.item_container);
        }

        public void bind(ListItem<Food> result) {
            titleTV.setText(result.getTitle() != null ? result.getTitle() : "No Title");

            if (result.getDescription() != null && !result.getDescription().isEmpty()) {
                descriptionTV.setVisibility(View.VISIBLE);
                descriptionTV.setText(result.getDescription());
            } else {
                descriptionTV.setVisibility(View.GONE);
            }
        }
    }
}
