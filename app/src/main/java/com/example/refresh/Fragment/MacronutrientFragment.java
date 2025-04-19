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
import com.example.refresh.R;

// Macronutrient Fragment which displays the macronutrients in the meal log activity
public class MacronutrientFragment extends Fragment {

    private ProgressBar caloriesProgress, carbsProgress, proteinProgress, fatProgress;
    private TextView caloriesText, carbsText, proteinText, fatText;

    private final int totalCalories = 2000;
    private final int totalCarbs = 400;
    private final int totalProtein = 400;
    private final int totalFat = 400;

    public MacronutrientFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_macronutrient, container, false);

        // Initialize Views
        caloriesProgress = view.findViewById(R.id.calories_progress);
        carbsProgress = view.findViewById(R.id.carbs_progress);
        proteinProgress = view.findViewById(R.id.protein_progress);
        fatProgress = view.findViewById(R.id.fat_progress);

        caloriesText = view.findViewById(R.id.calories_text);
        carbsText = view.findViewById(R.id.carbs_text);
        proteinText = view.findViewById(R.id.protein_text);
        fatText = view.findViewById(R.id.fat_text);

        // Set Default Progress
        updateMacros(0, 0, 0, 0);

        return view;
    }

    // Update the progress bars and text
    public void updateMacros(int calories, int carbs, int protein, int fat) {
        caloriesProgress.setProgress(calories);
        carbsProgress.setProgress(carbs);
        proteinProgress.setProgress(protein);
        fatProgress.setProgress(fat);

        caloriesText.setText(calories + "/" + totalCalories);
        carbsText.setText(carbs + "/" + totalCarbs);
        proteinText.setText(protein + "/" + totalProtein);
        fatText.setText(fat + "/" + totalFat);
    }
}
