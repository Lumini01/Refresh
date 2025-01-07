package com.example.refresh.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.refresh.Model.SearchResult;
import com.example.refresh.R;

import java.util.List;

public class ResultsAdapter extends RecyclerView.Adapter<ResultsAdapter.ResultViewHolder> {

    private List<SearchResult> resultsList;

    // Constructor
    public ResultsAdapter(List<SearchResult> resultsList) {
        this.resultsList = resultsList;
    }

    // ViewHolder Class
    public static class ResultViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle;
        TextView textViewDescription; // Optional

        public ResultViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewResultTitle);
            textViewDescription = itemView.findViewById(R.id.textViewResultDescription);
        }
    }

    @NonNull
    @Override
    public ResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search_result, parent, false);
        return new ResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultViewHolder holder, int position) {
        // Bind data to the views
        SearchResult result = resultsList.get(position);
        holder.textViewTitle.setText(result.getTitle());

        // Optional: Set description if available
        if (result.getDescription() != null && !result.getDescription().isEmpty()) {
            holder.textViewDescription.setVisibility(View.VISIBLE);
            holder.textViewDescription.setText(result.getDescription());
        } else {
            holder.textViewDescription.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return resultsList.size();
    }

    // Optional: Method to update data
    public void updateResults(List<SearchResult> newResults) {
        resultsList.clear();
        resultsList.addAll(newResults);
        notifyDataSetChanged();
    }
}
