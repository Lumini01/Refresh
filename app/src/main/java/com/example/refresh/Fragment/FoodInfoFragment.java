package com.example.refresh.Fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.refresh.Model.Food;
import com.example.refresh.Model.ListItem;
import com.example.refresh.R;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FoodInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FoodInfoFragment extends Fragment {

    public interface OnFoodInfoFragmentListener {
        void onAddingToSelectedFoods(ListItem<Food> addedFood);
        void onExitFoodInfoFragment();
    }

    // Domain model
    private Food food;

    // Listener
    private OnFoodInfoFragmentListener fragmentListener;

    // Serving option lists
    private final ArrayList<String> solidServingOptions = new ArrayList<>(Arrays.asList(
            "Standard Serving (100g)", "Teaspoon (5g)", "Tablespoon (15g)",
            "Cup (240g)", "Pint (473g)", "Quart (946g)", "Custom (grams)"
    ));
    private final ArrayList<String> liquidServingOptions = new ArrayList<>(Arrays.asList(
            "Standard Serving (100ml)", "Teaspoon (5ml)", "Tablespoon (15ml)", "Fluid Ounce (30ml)",
            "Cup (240ml)", "Pint (473ml)", "Quart (946ml)", "Custom (milliliters)"
    ));

    // Static maps for serving sizes
    private static final Map<String, Integer> SOLID_SERVING_MAP = new HashMap<>();
    private static final Map<String, Integer> LIQUID_SERVING_MAP = new HashMap<>();

    static {
        // Define solids options
        SOLID_SERVING_MAP.put("Standard Serving (100g)", 100);
        SOLID_SERVING_MAP.put("Teaspoon (5g)", 5);
        SOLID_SERVING_MAP.put("Tablespoon (15g)", 15);
        SOLID_SERVING_MAP.put("Cup (240g)", 240);
        SOLID_SERVING_MAP.put("Pint (473g)", 473);
        SOLID_SERVING_MAP.put("Quart (946g)", 946);
        SOLID_SERVING_MAP.put("Custom (grams)", 1);

        // Define liquids options
        LIQUID_SERVING_MAP.put("Standard Serving (100ml)", 100);
        LIQUID_SERVING_MAP.put("Teaspoon (5ml)", 5);
        LIQUID_SERVING_MAP.put("Tablespoon (15ml)", 15);
        LIQUID_SERVING_MAP.put("Fluid Ounce (30ml)", 30);
        LIQUID_SERVING_MAP.put("Cup (240ml)", 240);
        LIQUID_SERVING_MAP.put("Pint (473ml)", 473);
        LIQUID_SERVING_MAP.put("Quart (946ml)", 946);
        LIQUID_SERVING_MAP.put("Custom (milliliters)", 1);
    }

    // Static helper methods
    private static int getSolidServingSize(String option) {
        return SOLID_SERVING_MAP.getOrDefault(option, -1);
    }
    private static int getLiquidServingSize(String option) {
        return LIQUID_SERVING_MAP.getOrDefault(option, -1);
    }

    // Toolbar views
    private TextView title;
    private ImageButton backBtn;
    private ImageButton extraBtn;

    // Food info header views
    private TextView foodNameTV;
    private ImageButton favoriteBtn;
    private TextView descriptionTV;

    // Input views
    private EditText servingCountET;
    private Spinner servingOptionSpinner;

    // Nutrient progress bars & overlays
    private CircularProgressBar carbsProgressBar;
    private TextView carbsProgressTV;
    private CircularProgressBar proteinProgressBar;
    private TextView proteinProgressTV;
    private CircularProgressBar fatProgressBar;
    private TextView fatProgressTV;
    private SeekBar caloriesSeekBar;

    // Nutrient detail text views
    private TextView carbsTV;
    private TextView proteinTV;
    private TextView fatTV;

    // Calories per serving view
    private TextView caloriesTV;

    // Bottom action button
    private Button addBtn;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Make sure the hosting activity implements the interface.
        if (context instanceof FoodInfoFragment.OnFoodInfoFragmentListener) {
            fragmentListener = (FoodInfoFragment.OnFoodInfoFragmentListener) context;
        } else {
            throw new RuntimeException(context
                    + " must implement OnFoodInfoFragmentListener");
        }
    }

    public FoodInfoFragment() {
        // Required empty public constructor
    }

    public static FoodInfoFragment newInstance(Food food) {
        FoodInfoFragment fragment = new FoodInfoFragment();
        Bundle args = new Bundle();
        args.putSerializable("food", food);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_food_info, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            try {
                food = (Food) getArguments().getSerializable("food");
            }
            catch (ClassCastException e) {
                e.printStackTrace();
                food = new Food();
            }
        }
        else food = new Food();

        initializeViews(view);

        assignValuesToViews();

        setListeners();
    }

    private void initializeViews(View view) {

        title = view.findViewById(R.id.toolbar_title_tv);
        backBtn = view.findViewById(R.id.back_btn);
        extraBtn = view.findViewById(R.id.extra_btn);

        foodNameTV = view.findViewById(R.id.food_title_tv);
        favoriteBtn = view.findViewById(R.id.favorite_btn);
        descriptionTV = view.findViewById(R.id.food_description_tv);
        servingCountET = view.findViewById(R.id.serving_count_et);
        servingOptionSpinner = view.findViewById(R.id.serving_options_spinner);
        carbsProgressBar = view.findViewById(R.id.progressBarCrabs);
        carbsProgressTV = view.findViewById(R.id.progressTextCarbs);
        carbsTV = view.findViewById(R.id.tv_carbs);
        proteinProgressBar = view.findViewById(R.id.progressBarProtein);
        proteinProgressTV = view.findViewById(R.id.progressTextProtein);
        proteinTV = view.findViewById(R.id.tv_protein);
        fatProgressBar = view.findViewById(R.id.progressBarFat);
        fatProgressTV = view.findViewById(R.id.progressTextFat);
        fatTV = view.findViewById(R.id.tv_fat);
        caloriesTV = view.findViewById(R.id.tv_calories);
        caloriesSeekBar = view.findViewById(R.id.calorie_seek_bar);
        addBtn = view.findViewById(R.id.add_btn);
    }

    private void assignValuesToViews() {
        if (food == null) {
            return; // Optionally, you could show a default state or error message
        }

        title.setText("Food Info");
        extraBtn.setVisibility(View.GONE);


        // 1. Basic Food Details
        foodNameTV.setText(food.getName());
        descriptionTV.setText(food.getDescription());

        updateNutritionalValues();
        setNutrientProgressBars();

        // 4. Spinner for Serving Options
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                R.layout.item_custom_spinner, food.isLiquid() ? liquidServingOptions : solidServingOptions);
        adapter.setDropDownViewResource(R.layout.item_custom_spinner);
        servingOptionSpinner.setAdapter(adapter);
        servingOptionSpinner.setSelection(0); // Set the default selection to the first item
    }

    private void updateNutritionalValues() {

        caloriesTV.setText(food.getActualCalories() + " kcal");
        carbsTV.setText("Carbs: " + food.getActualCarbs() + "g");
        proteinTV.setText("Protein: " + food.getActualProtein() + "g");
        fatTV.setText("Fat: " + food.getActualFat() + "g");

    }

    private void setNutrientProgressBars() {
        carbsProgressBar.setProgress(food.getCarbs());
        proteinProgressBar.setProgress(food.getProtein());
        fatProgressBar.setProgress(food.getFat());

        carbsProgressTV.setText(food.getCarbs() + "%");
        proteinProgressTV.setText(food.getProtein() + "%");
        fatProgressTV.setText(food.getFat() + "%");

        caloriesSeekBar.setProgress((food.getCalories() - 20) * 4);
    }

    private void setListeners() {
        backBtn.setOnClickListener(v -> {
            fragmentListener.onExitFoodInfoFragment();
        });

        servingOptionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int optionPosition = servingOptionSpinner.getSelectedItemPosition();
                servingOptionSpinner.setSelection(optionPosition);

                updateServingSize();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        servingCountET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String txt = servingCountET.getText().toString();
                if (servingCountET.getText().toString().isEmpty())
                    updateServingSize();
                else if (Double.parseDouble(txt) <= 999.0 || String.valueOf(Integer.parseInt(txt)).length() <= 3)
                    updateServingSize();
                else if (Double.parseDouble(txt) <= 999.0 && String.valueOf(Integer.parseInt(txt)).length() >= 4) {
                    servingCountET.setText(String.valueOf(Double.parseDouble(txt)));
                }
                else {
                    servingCountET.setText("1");
                }
            }
        });

        addBtn.setOnClickListener(v -> addFoodToSelectedFoods());

        caloriesSeekBar.setOnTouchListener((v, event) -> true);

    }

    private void updateServingSize() {
        String servingOption = servingOptionSpinner.getSelectedItem().toString();
        String etValue = servingCountET.getText().toString();

        int multiplier = etValue.isEmpty() ? 1 : Integer.parseInt(etValue);
        int servingSize = food.isLiquid() ? getLiquidServingSize(servingOption) : getSolidServingSize(servingOption);

        if (servingSize == -1) {
            return;
        }

        food.setServingSize(servingSize * multiplier);
        updateNutritionalValues();
    }

    private void addFoodToSelectedFoods() {
        ListItem<Food> addedFood = new ListItem<>(food);
        fragmentListener.onAddingToSelectedFoods(addedFood);
        fragmentListener.onExitFoodInfoFragment();
    }
}