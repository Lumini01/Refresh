package com.example.refresh.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.refresh.Adapters.ResultsAdapter;
import com.example.refresh.Model.SearchResult;
import com.example.refresh.R;

import java.util.ArrayList;
import java.util.List;

public class SearchResultsFragment extends Fragment {

    private RecyclerView recyclerViewResults;
    private TextView textViewNoResults;
    private ResultsAdapter resultsAdapter;
    private List<SearchResult> searchResults;

    public SearchResultsFragment() {
        // Required empty public constructor
    }

    /**
     * Factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param results List of search results.
     * @return A new instance of fragment SearchResultsFragment.
     */
    public static SearchResultsFragment newInstance(ArrayList<SearchResult> results) {
        SearchResultsFragment fragment = new SearchResultsFragment();
        Bundle args = new Bundle();
        args.putSerializable("search_results", results);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_results, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        // Initialize Views
        recyclerViewResults = view.findViewById(R.id.recyclerViewResults);
        textViewNoResults = view.findViewById(R.id.textViewNoResults);

        // Initialize Search Results List
        if (getArguments() != null) {
            searchResults = (List<SearchResult>) getArguments().getSerializable("search_results");
        } else {
            searchResults = new ArrayList<>();
        }

        // Setup RecyclerView
        resultsAdapter = new ResultsAdapter(searchResults);
        recyclerViewResults.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewResults.setAdapter(resultsAdapter);

        // Update UI based on initial data
        updateUI();
    }

    /**
     * Updates the RecyclerView with new search results.
     *
     * @param newResults List of new search results.
     */
    public void updateResults(List<SearchResult> newResults) {
        searchResults.clear();
        searchResults.addAll(newResults);
        resultsAdapter.notifyDataSetChanged();
        updateUI();
    }

    /**
     * Shows or hides the RecyclerView and No Results TextView based on the data.
     */
    private void updateUI() {
        if (searchResults.isEmpty()) {
            recyclerViewResults.setVisibility(View.GONE);
            textViewNoResults.setVisibility(View.VISIBLE);
        } else {
            recyclerViewResults.setVisibility(View.VISIBLE);
            textViewNoResults.setVisibility(View.GONE);
        }
    }
}
