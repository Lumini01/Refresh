package com.example.refresh.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.refresh.R;
import com.example.refresh.Adapters.FoodsAdapter;

import java.util.ArrayList;
import java.util.List;

public class SelectedFoodsFragment extends Fragment {

    private RecyclerView recyclerView;
    private FoodsAdapter adapter;
    private List<String> selectedFoodsList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_selected_foods, container, false);

        recyclerView = view.findViewById(R.id.rv_foods);

        // Sample data
        selectedFoodsList = new ArrayList<>();
        selectedFoodsList.add("Apple");
        selectedFoodsList.add("Banana");
        selectedFoodsList.add("Carrot");
        selectedFoodsList.add("Avocado");

        setupRecyclerView();

        return view;
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new FoodsAdapter(selectedFoodsList);
        recyclerView.setAdapter(adapter);
    }
}
