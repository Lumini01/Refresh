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

    private Food food;
    private OnFoodInfoFragmentListener fragmentListener;
    private ArrayList<String> solidServingOptions = new ArrayList<>(Arrays.asList(
            "Standard Serving (100g)", "Teaspoon (5g)", "Tablespoon (15g)",
            "Cup (240g)", "Pint (473g)", "Quart (946g)", "Custom (grams)"
    ));

    private ArrayList<String> liquidsOptions = new ArrayList<>(Arrays.asList(
            "Standard Serving (100ml)", "Teaspoon (5ml)", "Tablespoon (15ml)", "Fluid Ounce (30ml)",
            "Cup (240ml)", "Pint (473ml)", "Quart (946ml)", "Custom (milliliters)"
    ));

    // Map for solids: Option string → Size in grams
    private static final Map<String, Integer> SOLID_SERVING_MAP = new HashMap<>();
    // Map for liquids: Option string → Size in milliliters
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

    private static int getSolidServingSize(String option) {
        return SOLID_SERVING_MAP.getOrDefault(option, -1);
    }

    private static int getLiquidServingSize(String option) {
        return LIQUID_SERVING_MAP.getOrDefault(option, -1);
    }

    // toolbar views
    private TextView tvTitle;
    private ImageButton backArrow;
    private ImageButton extraBtn;

    private TextView tvFoodName;
    private ImageButton favoriteBtn;
    private TextView tvFoodDescription;

    // Input views
    private EditText etServingCount;
    private Spinner spinnerServingOptions;

    // Nutrient circular progress bars and their overlay texts
    private CircularProgressBar progressBarCrabs;
    private TextView progressTextCarbs;

    private CircularProgressBar progressBarProtein;
    private TextView progressTextProtein;

    private CircularProgressBar progressBarFat;
    private TextView progressTextFat;

    private SeekBar seekBarCalories;

    // Nutrient detail text views
    private TextView tvCarbs;
    private TextView tvProtein;
    private TextView tvFat;

    // Calories per serving (assumed to have been added to the XML)
    private TextView tvCalories;

    // Bottom action button
    private Button btnAdd;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Make sure the hosting activity implements the interface.
        if (context instanceof FoodInfoFragment.OnFoodInfoFragmentListener) {
            fragmentListener = (FoodInfoFragment.OnFoodInfoFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
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

        tvTitle = view.findViewById(R.id.toolbarTitle);
        backArrow = view.findViewById(R.id.backArrow);
        extraBtn = view.findViewById(R.id.extra_button);

        tvFoodName = view.findViewById(R.id.tv_food_name);
        favoriteBtn = view.findViewById(R.id.favorite_btn);
        tvFoodDescription = view.findViewById(R.id.tv_food_description);
        etServingCount = view.findViewById(R.id.etServingCount);
        spinnerServingOptions = view.findViewById(R.id.spinnerServingOptions);
        progressBarCrabs = view.findViewById(R.id.progressBarCrabs);
        progressTextCarbs = view.findViewById(R.id.progressTextCarbs);
        tvCarbs = view.findViewById(R.id.tv_carbs);
        progressBarProtein = view.findViewById(R.id.progressBarProtein);
        progressTextProtein = view.findViewById(R.id.progressTextProtein);
        tvProtein = view.findViewById(R.id.tv_protein);
        progressBarFat = view.findViewById(R.id.progressBarFat);
        progressTextFat = view.findViewById(R.id.progressTextFat);
        tvFat = view.findViewById(R.id.tv_fat);
        tvCalories = view.findViewById(R.id.tv_calories);
        seekBarCalories = view.findViewById(R.id.calorie_seek_bar);
        btnAdd = view.findViewById(R.id.btn_add);
    }

    private void assignValuesToViews() {
        if (food == null) {
            return; // Optionally, you could show a default state or error message
        }

        tvTitle.setText("Food Info");
        extraBtn.setVisibility(View.GONE);


        // 1. Basic Food Details
        tvFoodName.setText(food.getName());
        tvFoodDescription.setText(food.getDescription());

        updateNutritionalValues();
        setNutrientProgressBars();

        // 4. Spinner for Serving Options
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                R.layout.custom_spinner_item, food.isLiquid() ? liquidsOptions : solidServingOptions);
        adapter.setDropDownViewResource(R.layout.custom_spinner_item);
        spinnerServingOptions.setAdapter(adapter);
        spinnerServingOptions.setSelection(0); // Set the default selection to the first item
    }

    public void updateNutritionalValues() {

        tvCalories.setText(food.getActualCalories() + " kcal");
        tvCarbs.setText("Carbs: " + food.getActualCarbs() + "g");
        tvProtein.setText("Protein: " + food.getActualProtein() + "g");
        tvFat.setText("Fat: " + food.getActualFat() + "g");

    }

    public void setNutrientProgressBars() {
        progressBarCrabs.setProgress(food.getCarbs());
        progressBarProtein.setProgress(food.getProtein());
        progressBarFat.setProgress(food.getFat());

        progressTextCarbs.setText(food.getCarbs() + "%");
        progressTextProtein.setText(food.getProtein() + "%");
        progressTextFat.setText(food.getFat() + "%");

        seekBarCalories.setProgress((food.getCalories() - 20) * 4);
    }

    private void setListeners() {
        backArrow.setOnClickListener(v -> {
            fragmentListener.onExitFoodInfoFragment();
        });

        spinnerServingOptions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int optionPosition = spinnerServingOptions.getSelectedItemPosition();
                spinnerServingOptions.setSelection(optionPosition);

                updateServingSize();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        etServingCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String txt = etServingCount.getText().toString();
                if (etServingCount.getText().toString().isEmpty())
                    updateServingSize();
                else if (Double.parseDouble(txt) <= 999.0 || String.valueOf(Integer.parseInt(txt)).length() <= 3)
                    updateServingSize();
                else if (Double.parseDouble(txt) <= 999.0 && String.valueOf(Integer.parseInt(txt)).length() >= 4) {
                    etServingCount.setText(String.valueOf(Double.parseDouble(txt)));
                }
                else {
                    etServingCount.setText("1");
                }
            }
        });

        btnAdd.setOnClickListener(v -> addFoodToSelectedFoods());

        seekBarCalories.setOnTouchListener((v, event) -> true);

    }

    private void updateServingSize() {
        String servingOption = spinnerServingOptions.getSelectedItem().toString();
        String etValue = etServingCount.getText().toString();

        int multiplier = etValue.isEmpty() ? 1 : Integer.parseInt(etValue);
        int servingSize = food.isLiquid() ? getLiquidServingSize(servingOption) : getSolidServingSize(servingOption);

        if (servingSize == -1) {
            return;
        }

        food.setServingSize(servingSize * multiplier);
        updateNutritionalValues();
    }

    public void addFoodToSelectedFoods() {
        ListItem<Food> addedFood = new ListItem<>(food);
        fragmentListener.onAddingToSelectedFoods(addedFood);
        fragmentListener.onExitFoodInfoFragment();
    }
}