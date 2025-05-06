package com.example.refresh.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.refresh.Helper.DailySummaryHelper;
import com.example.refresh.Helper.UserInfoHelper;
import com.example.refresh.Model.DaySummary;
import com.example.refresh.R;

// Macronutrient Fragment which displays the macronutrients in the meal log activity
public class DailyProgressFragment extends Fragment {

    private DaySummary daySummary;
    private UserInfoHelper userInfoHelper;

    private ProgressBar caloriesProgress;
    private ProgressBar carbsProgress;
    private ProgressBar proteinProgress;
    private ProgressBar fatProgress;
    private ProgressBar waterProgress;
    private TextView caloriesText;
    private TextView carbsText;
    private TextView proteinText;
    private TextView fatText;
    private TextView waterText;

    private int totalCalories;
    private int totalCarbs;
    private int totalProtein;
    private int totalFat;
    private int totalWater;

    private int calorieGoal;
    private int carbGoal;
    private int proteinGoal;
    private int fatGoal;
    private int waterGoal;

    public DailyProgressFragment() {}

    public static DailyProgressFragment newInstance(DaySummary daySummary, UserInfoHelper userInfoHelper) {
        DailyProgressFragment fragment = new DailyProgressFragment();
        fragment.daySummary = daySummary;
        fragment.userInfoHelper = userInfoHelper;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_daily_progress, container, false);

        return view;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        intiViews(view);

        totalCalories = daySummary.getTotalCalories();
        totalCarbs = daySummary.getTotalCarbs();
        totalProtein = daySummary.getTotalProtein();
        totalFat = daySummary.getTotalFat();
        totalWater = daySummary.getTotalWater();

        calorieGoal = userInfoHelper.getCalorieGoal();
        carbGoal = userInfoHelper.getCarbGoal();
        proteinGoal = userInfoHelper.getProteinGoal();
        fatGoal = userInfoHelper.getFatGoal();
        waterGoal = userInfoHelper.getWaterIntakeGoal();

        updateMacros();

    }

    private void intiViews(View view) {
        caloriesProgress = view.findViewById(R.id.calories_progress);
        carbsProgress = view.findViewById(R.id.carbs_progress);
        proteinProgress = view.findViewById(R.id.protein_progress);
        fatProgress = view.findViewById(R.id.fat_progress);
        waterProgress = view.findViewById(R.id.water_progress);

        caloriesText = view.findViewById(R.id.calories_text);
        carbsText = view.findViewById(R.id.carbs_text);
        proteinText = view.findViewById(R.id.protein_text);
        fatText = view.findViewById(R.id.fat_text);
        waterText = view.findViewById(R.id.water_text);
    }

    // Update the progress bars and text
    public void updateMacros() {
        caloriesProgress.setMax(calorieGoal);
        caloriesProgress.setProgress(totalCalories);
        caloriesText.setText(totalCalories + "/" + calorieGoal + " kcal");

        carbsProgress.setMax(carbGoal);
        carbsProgress.setProgress(totalCarbs);
        carbsText.setText(totalCarbs + "/" + carbGoal + " g");

        proteinProgress.setMax(proteinGoal);
        proteinProgress.setProgress(totalProtein);
        proteinText.setText(totalProtein + "/" + proteinGoal + " g");

        fatProgress.setMax(fatGoal);
        fatProgress.setProgress(totalFat);
        fatText.setText(totalFat + "/" + fatGoal + " g");

        waterProgress.setMax(waterGoal);
        waterProgress.setProgress(totalWater);
        waterText.setText(totalWater + "/" + waterGoal + " ml");
    }
}
