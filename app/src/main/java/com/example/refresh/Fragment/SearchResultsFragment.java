package com.example.refresh.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.refresh.Adapter.SearchResultsAdapter;
import com.example.refresh.Model.Food;
import com.example.refresh.Model.ListItem;
import com.example.refresh.R;

import java.util.ArrayList;
import java.util.List;

// Search Results Fragment which displays the search results in the search activity
public class SearchResultsFragment extends Fragment {

    public interface OnSearchResultsFragmentListener {
        void onAddingToSelectedFoods(ListItem<Food> addedFood);
        void onNavigateToFoodInfo(Food food);
    }

    private ArrayList<ListItem<Food>> searchResults;
    private OnSearchResultsFragmentListener fragmentListener;
    private SearchResultsAdapter searchResultsAdapter;
    private RecyclerView resultsRV;
    private TextView noResultsTV;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Make sure the hosting activity implements the interface.
        if (context instanceof OnSearchResultsFragmentListener) {
            fragmentListener = (OnSearchResultsFragmentListener) context;
        } else {
            throw new RuntimeException(context
                    + " must implement OnSearchResultsFragmentListener");
        }
    }

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
    public static SearchResultsFragment newInstance(ArrayList<ListItem<Food>> results) {
        SearchResultsFragment fragment = new SearchResultsFragment();
        Bundle args = new Bundle();
        args.putSerializable("search_results", results);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_results, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        // Initialize Views

        resultsRV = view.findViewById(R.id.recyclerViewResults);
        noResultsTV = view.findViewById(R.id.textViewNoResults);

        // Initialize Search Results List
        if (getArguments() != null) {
            Object obj = getArguments().getSerializable("search_results");

            if (obj instanceof ArrayList<?>) {
                try {
                    searchResults = (ArrayList<ListItem<Food>>) obj;
                } catch (ClassCastException e) {
                    e.printStackTrace();
                    searchResults = new ArrayList<>();  // Fallback to empty list
                }
            }
            else {
                searchResults = new ArrayList<>();  // Fallback if not an ArrayList
            }
        }
        else {
            searchResults = new ArrayList<>();
        }

        // Setup RecyclerView
        searchResultsAdapter = new SearchResultsAdapter(searchResults, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        resultsRV.setLayoutManager(layoutManager);
        resultsRV.setAdapter(searchResultsAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(resultsRV.getContext(),
                layoutManager.getOrientation()
        );
        resultsRV.addItemDecoration(dividerItemDecoration);

        // Update UI based on initial data
        updateUI();
    }


    /**
     * Shows or hides the RecyclerView and No Results TextView based on the data.
     */
    private void updateUI() {
        if (searchResults.isEmpty()) {
            resultsRV.setVisibility(View.GONE);
            noResultsTV.setVisibility(View.VISIBLE);
        } else {
            resultsRV.setVisibility(View.VISIBLE);
            noResultsTV.setVisibility(View.GONE);
        }
    }

    public void addFoodToSelectedFoods(ListItem<Food> food) {
        fragmentListener.onAddingToSelectedFoods(food);
    }

    public void navigateToFoodInfo(Food food) {
        fragmentListener.onNavigateToFoodInfo(food);
    }
}
