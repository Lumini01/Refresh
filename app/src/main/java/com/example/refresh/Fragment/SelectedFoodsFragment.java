package com.example.refresh.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.refresh.Adapter.FoodSelectionsAdapter;
import com.example.refresh.Model.Food;
import com.example.refresh.Model.ListItem;
import com.example.refresh.R;

import java.util.ArrayList;

// Selected Foods Fragment which displays the selected foods in the meal log activity
public class SelectedFoodsFragment extends Fragment {

    public interface OnSelectedFoodsFragmentListener {
        void onNavigateToFoodInfo(Food food);
    }
    private ArrayList<ListItem<Food>> selectedFoods;
    private OnSelectedFoodsFragmentListener fragmentListener;
    private FoodSelectionsAdapter foodsAdapter;
    private RecyclerView foodsRV;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Make sure the hosting activity implements the interface.
        if (context instanceof SelectedFoodsFragment.OnSelectedFoodsFragmentListener) {
            fragmentListener = (SelectedFoodsFragment.OnSelectedFoodsFragmentListener) context;
        } else {
            throw new RuntimeException(context
                    + " must implement OnSelectedFoodsFragmentListener");
        }
    }

    public SelectedFoodsFragment() {}

    public static SelectedFoodsFragment newInstance(ArrayList<ListItem<Food>> selectedFoods) {
        SelectedFoodsFragment fragment = new SelectedFoodsFragment();
        Bundle args = new Bundle();
        args.putSerializable("selected_foods", selectedFoods);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_selected_foods, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        foodsRV = view.findViewById(R.id.rv_foods);

        // Initialize the list and adapter
        if (getArguments() != null) {
            Object obj = getArguments().getSerializable("selected_foods");

            if (obj instanceof ArrayList<?>) {
                try {
                    selectedFoods = (ArrayList<ListItem<Food>>) obj;
                } catch (ClassCastException e) {
                    e.printStackTrace();
                    selectedFoods = new ArrayList<>();  // Fallback to empty list
                }
            } else {
                selectedFoods = new ArrayList<>();  // Fallback if not an ArrayList
            }
        } else {
            selectedFoods = new ArrayList<>();
        }

        setupRecyclerView();
    }

    public ArrayList<ListItem<Food>> getSelectedFoods() {
        return selectedFoods;
    }

    private void setupRecyclerView() {
        foodsAdapter = new FoodSelectionsAdapter(selectedFoods, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        foodsRV.setLayoutManager(layoutManager);
        foodsRV.setAdapter(foodsAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(foodsRV.getContext(),
                layoutManager.getOrientation()
        );
        foodsRV.addItemDecoration(dividerItemDecoration);
    }

    // Add food to the selected foods list

    public void addFoodToSelectedFoods(ListItem<Food> food) {
        int foodIndex = -1;

        for (ListItem<Food> foodItem : selectedFoods) {
            if (foodItem.getModel().getId() == food.getModel().getId()) {
                foodIndex = selectedFoods.indexOf(foodItem);
                break;
            }
        }

        if (foodIndex == -1) {
            selectedFoods.add(food);  // Update the list in fragment
            foodsAdapter.addItem(food, selectedFoods.size());  // Notify adapter
        }
        else {
            selectedFoods.set(foodIndex, food);  // Update the list in fragment
            foodsAdapter.removeItem(foodIndex);
            foodsAdapter.addItem(food, selectedFoods.size());  // Notify adapter
        }
    }
    // Remove food from the selected foods list

    public void removeFoodFromSelectedFoods(int position) {
        if (position >= 0 && position < selectedFoods.size()) {
            selectedFoods.remove(position);  // Update the list in fragment
            foodsAdapter.removeItem(position);  // Notify adapter to remove item
        }
    }
    public void removeFoodFromSelectedFoodsByFoodID(int foodID) {
        int position = 0;

        for (ListItem<Food> foodItem : selectedFoods) {
            if (foodItem.getModel().getId() == foodID) {
                break;
            }
            position++;
        }

        selectedFoods.remove(position);  // Update the list in fragment
        foodsAdapter.removeItem(position);  // Notify adapter to remove item
    }

    public void clearSelectedFoods() {
        selectedFoods.clear();
        foodsAdapter.clearAll();
    }

    public void navigateToFoodInfo(Food food) {
        fragmentListener.onNavigateToFoodInfo(food);
    }
}