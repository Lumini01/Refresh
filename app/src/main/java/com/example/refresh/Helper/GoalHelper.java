package com.example.refresh.Helper;

import android.content.Context;

public class GoalHelper {
    private final UserInfoHelper userInfoHelper;

    public GoalHelper(Context context) {
        this.userInfoHelper = new UserInfoHelper(context);
    }

    /** Main entry: read inputs, compute all targets, and save them. */
    public void calculateDailyNutritionGoals() {
        // 1. Load raw inputs
        String gender        = userInfoHelper.getGender();
        int    age           = userInfoHelper.getAge();
        String goal          = userInfoHelper.getGoal();
        String activityLevel = userInfoHelper.getActivityLevel();
        float  weight        = userInfoHelper.getWeight();
        float  height        = userInfoHelper.getHeight();
        String dietType      = userInfoHelper.getDietType();

        // 2. Calculate BMR & TDEE
        double bmr  = calculateBMR(gender, weight, height, age);
        double tdee = bmr * getActivityMultiplier(activityLevel);

        // 3. Adjust calories for weight goal
        double calories = adjustCaloriesForGoal(tdee, goal);

        // 4. Determine macros (dynamic + diet-type)
        MacroRatios ratios = getMacroRatios(dietType, goal, weight, activityLevel, calories);

        double  carbs   = (calories * ratios.carbsRatio)   / 4; // 4 kcal per gram carb
        double  protein = (calories * ratios.proteinRatio) / 4; // 4 kcal per gram protein
        double  fats    = (calories * ratios.fatsRatio)    / 9; // 9 kcal per gram fat

        // 5. Water intake: 35 ml per kg body weight
        double waterMl = weight * 35;

        // 6. Save results
        userInfoHelper.setCalorieGoal((int) calories);
        userInfoHelper.setCarbGoal((int) carbs);
        userInfoHelper.setProteinGoal((int) protein);
        userInfoHelper.setFatGoal((int) fats);
        userInfoHelper.setWaterIntakeGoal((int) waterMl);
    }

    /** Mifflin–St Jeor BMR formula */
    private double calculateBMR(String gender, double weight, double height, int age) {
        double genderOffSet = gender.equalsIgnoreCase("male") ? +5 : -161;
        return 10 * weight    // weight in kg
                + 6.25 * height  // height in cm
                - 5 * age        // age in years
                + genderOffSet;
    }

    /** Maps activity level to standard multipliers */
    private double getActivityMultiplier(String level) {
        switch (level.toLowerCase()) {
            case "low":       return 1.2;   // sedentary
            case "medium":    return 1.55;  // lightly active
            case "high":      return 1.725; // very active
            case "very high": return 1.9;   // extra active
            default:            return 1.55;
        }
    }

    /** Adjusts TDEE up/down based on weight goal */
    private double adjustCaloriesForGoal(double tdee, String goal) {
        switch (goal.toLowerCase()) {
            case "lose":
                return tdee - 500;
            case "gain":
                return tdee + 500;
            case "gain muscle":
                return tdee + 300;
            default:
                return tdee;
        }
    }

    /**
     * Dynamic macro calculation that also respects diet type.
     * Blends 70% dynamic targets with 30% static diet splits.
     */
    private MacroRatios getMacroRatios(
            String dietType,
            String goal,
            double weightKg,
            String activityLevel,
            double totalCalories
    ) {
        // 1) Protein target (g per kg)
        double proteinPerKg;
        switch (goal.toLowerCase()) {
            case "gain muscle": proteinPerKg = 1.8; break;
            case "lose":        proteinPerKg = 2.2; break;
            default:             proteinPerKg = 1.5; break;
        }
        double proteinCals = proteinPerKg * weightKg * 4;

        // 2) Fat target (g per kg)
        double fatCals = 0.8 * weightKg * 9;

        // 3) Carb boost for high activity
        double carbBoost = 1.0;
        if (activityLevel.equalsIgnoreCase("very_high")) {
            carbBoost = 1.1;
        } else if (activityLevel.equalsIgnoreCase("high")) {
            carbBoost = 1.05;
        }

        // 4) Dynamic carb cals
        double dynamicCarbCals = (totalCalories - (proteinCals + fatCals)) * carbBoost;

        // 5) Convert dynamic → ratios
        double dynCarbsRatio   = dynamicCarbCals   / totalCalories;
        double dynProteinRatio = proteinCals / totalCalories;
        double dynFatsRatio    = fatCals     / totalCalories;

        // 6) Static diet split
        MacroRatios staticSplit = getStaticDietSplit(dietType);

        // 7) Blend: 70% dynamic, 30% static
        double carbsRatio   = dynCarbsRatio   * 0.7 + staticSplit.carbsRatio   * 0.3;
        double proteinRatio = dynProteinRatio * 0.7 + staticSplit.proteinRatio * 0.3;
        double fatsRatio    = dynFatsRatio    * 0.7 + staticSplit.fatsRatio    * 0.3;

        return new MacroRatios(carbsRatio, proteinRatio, fatsRatio);
    }

    /** Returns the original static splits for each diet type. */
    private MacroRatios getStaticDietSplit(String dietType) {
        switch (dietType.toLowerCase()) {
            case "carnivore":    return new MacroRatios(0.10, 0.50, 0.40);
            case "vegetarian":   return new MacroRatios(0.50, 0.25, 0.25);
            case "vegan":        return new MacroRatios(0.60, 0.20, 0.20);
            default:               return new MacroRatios(0.50, 0.30, 0.20);
        }
    }

    /** Simple holder for macro fractions */
    private static class MacroRatios {
        final double carbsRatio, proteinRatio, fatsRatio;
        MacroRatios(double c, double p, double f) {
            this.carbsRatio   = c;
            this.proteinRatio = p;
            this.fatsRatio    = f;
        }
    }
}
