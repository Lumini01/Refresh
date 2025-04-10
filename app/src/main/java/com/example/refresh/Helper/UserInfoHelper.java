package com.example.refresh.Helper;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.refresh.MyApplication;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class UserInfoHelper {
    private int userId;
    private Context context;
    private SharedPreferences userPreferences;

    public UserInfoHelper(Context context) {
        this.context = context;
        userId = MyApplication.getInstance().getLoggedUserID();
        userPreferences = context.getSharedPreferences(
                MyApplication.getInstance().getLoggedUserSPName(),
                MODE_PRIVATE);
    }

    public void setUserId(int userId) {
        this.userId = userId;
        userPreferences = context.getSharedPreferences(
                MyApplication.getInstance().getLoggedUserSPName(),
                MODE_PRIVATE);
    }

    public void setGender(String gender) {
        if (gender.equalsIgnoreCase("male") || gender.equalsIgnoreCase("female") || gender.equalsIgnoreCase("other"))
            userPreferences.edit().putString("gender", gender).apply();
        else Toast.makeText(context, "Invalid gender.", Toast.LENGTH_SHORT).show();
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        if (dateOfBirth.isBefore(LocalDate.now()) && calculateAge(dateOfBirth) < 120 && calculateAge(dateOfBirth) >= 16) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String formattedDateOfBirth = dateOfBirth.format(formatter);
            userPreferences.edit().putString("dateOfBirth", formattedDateOfBirth).apply();
            setAge(calculateAge(dateOfBirth));
        }
        else Toast.makeText(context, "Invalid date of birth.", Toast.LENGTH_SHORT).show();
    }

    private void setAge(int age) {
        userPreferences.edit().putInt("age", age).apply();
    }

    // Height in cm
    public void setHeight(int height) {
        if (height >= 120 && height <= 220)
            userPreferences.edit().putInt("height", height).apply();
        else Toast.makeText(context, "Invalid height.", Toast.LENGTH_SHORT).show();
    }

    // Weight in kg
    public void setWeight(int weight) {
        if (weight >= 40 && weight <= 200)
            userPreferences.edit().putInt("weight", weight).apply();
        else Toast.makeText(context, "Invalid weight.", Toast.LENGTH_SHORT).show();
    }

    public void setGoal(String goal) {
        if (goal.equalsIgnoreCase("lose") || goal.equalsIgnoreCase("gain") || goal.equalsIgnoreCase("maintain") || goal.equalsIgnoreCase("gain muscle"))
            userPreferences.edit().putString("goal", goal).apply();
        else Toast.makeText(context, "Invalid Goal.", Toast.LENGTH_SHORT).show();
    }

    public void setActivityLevel(String activityLevel) {
        if (activityLevel.equalsIgnoreCase("low") || activityLevel.equalsIgnoreCase("medium") || activityLevel.equalsIgnoreCase("high"))
            userPreferences.edit().putString("activityLevel", activityLevel).apply();
        else Toast.makeText(context, "Invalid activity level.", Toast.LENGTH_SHORT).show();
    }

    public void setDietType(String dietType) {
        if (dietType.equalsIgnoreCase("vegan") || dietType.equalsIgnoreCase("vegetarian") || dietType.equalsIgnoreCase("carnivore"))
            userPreferences.edit().putString("dietType", dietType).apply();
        else Toast.makeText(context, "Invalid diet type.", Toast.LENGTH_SHORT).show();
    }

    private void setStreak(int streak) {
        if (streak >= 0)
            userPreferences.edit().putInt("streak", streak).apply();
        else Toast.makeText(context, "Invalid streak.", Toast.LENGTH_SHORT).show();
    }

    private void incrementStreak() {
        setStreak(userPreferences.getInt("streak", 0) + 1);
    }

    private LocalDate getStartDate() {
        // TODO: Implement this method to retrieve the start date from SharedPreferences
        return LocalDate.now();
    }


    // TODO: Review these methods and add more if needed
    public static boolean isValidHeight(float heightCm) {
        return heightCm >= 50 && heightCm <= 300;
    }

    // Process or validate weight (in kg)
    public static boolean isValidWeight(float weightKg) {
        return weightKg >= 20 && weightKg <= 300;
    }

    // Calculate age from date of birth
    public static int calculateAge(LocalDate dateOfBirth) {
        return LocalDate.now().getYear() - dateOfBirth.getYear(); // Simple version
    }

    // Generate a summary string of user info
    public static String generateUserSummary(
            LocalDate dob,
            String gender,
            float heightCm,
            float weightKg,
            String activityLevel,
            String dietType,
            String goal,
            int streak,
            LocalDate joinDate
    ) {
        return "User Summary:\n" +
                "DOB: " + dob + "\n" +
                "Gender: " + gender + "\n" +
                "Height: " + heightCm + " cm\n" +
                "Weight: " + weightKg + " kg\n" +
                "Activity Level: " + activityLevel + "\n" +
                "Diet Type: " + dietType + "\n" +
                "Goal: " + goal + "\n" +
                "Streak: " + streak + " days\n" +
                "Joined: " + joinDate;
    }

    // Example: Suggest calorie adjustment factor based on activity level
    public static float getActivityMultiplier(String activityLevel) {
        switch (activityLevel.toLowerCase()) {
            case "low": return 1.2f;
            case "medium": return 1.5f;
            case "high": return 1.8f;
            default: return 1.0f;
        }
    }

    // Example: Suggestion based on goal
    public static float adjustCaloriesForGoal(float baseCalories, String goal) {
        switch (goal.toLowerCase()) {
            case "lose": return baseCalories - 500;
            case "gain": return baseCalories + 500;
            default: return baseCalories; // "maintain"
        }
    }

    // Example: Validate gender input
    public static boolean isValidGender(String gender) {
        return gender != null && (gender.equalsIgnoreCase("male") || gender.equalsIgnoreCase("female") || gender.equalsIgnoreCase("other"));
    }

    // Example: Validate date of birth
    public static boolean isValidDOB(LocalDate dob) {
        return dob != null && dob.isBefore(LocalDate.now()) && calculateAge(dob) < 120;
    }

    // More processing or transformation methods can be added here
}
