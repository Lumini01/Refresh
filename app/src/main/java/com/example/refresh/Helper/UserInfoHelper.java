package com.example.refresh.Helper;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.refresh.MyApplication;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class UserInfoHelper {
    private int userId;
    private final Context context;
    private SharedPreferences userPreferences;

    private static final String KEY_GENDER         = "gender";           // "male" or "female"
    private static final String KEY_AGE            = "age";              // int, years
    private static final String KEY_DATE_OF_BIRTH  = "dateOfBirth";      // "dd/MM/yyyy"
    private static final String KEY_GOAL           = "goal";             // "lose", "maintain", "gain", "gain_muscle"
    private static final String KEY_ACTIVITY_LEVEL = "activityLevel";   // "low", "medium", "high", "very_high"
    private static final String KEY_WEIGHT         = "weight";           // float, in kg
    private static final String KEY_TARGET_WEIGHT  = "targetWeight";     // float, in kg
    private static final String KEY_HEIGHT         = "height";           // float, in cm
    private static final String KEY_DIET_TYPE      = "dietType";        // "carnivore", "vegetarian", "vegan"
    private static final String KEY_START_DATE     = "startDate";        // "dd/MM/yyyy"
    private static final String KEY_START_WEIGHT   = "startWeight";      // float, in kg
    private static final String KEY_STREAK         = "streak";           // int, days

    // Output keys
    private static final String KEY_DAILY_CALORIES = "calorieGoal";
    private static final String KEY_CARBS          = "carbGoal";       // grams
    private static final String KEY_PROTEIN        = "proteinGoal";     // grams
    private static final String KEY_FATS           = "fatGoal";        // grams
    private static final String KEY_WATER          = "waterIntakeGoal";       // ml

    public UserInfoHelper(Context context) {
        this.context = context;
        userId = MyApplication.getInstance().getLoggedUserID();
        userPreferences = context.getSharedPreferences(
                MyApplication.getInstance().getLoggedUserSPName(),
                MODE_PRIVATE);
    }

    public UserInfoHelper(Context context, int userId) {
        this.context = context;
        this.userId = userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
        userPreferences = context.getSharedPreferences(
                MyApplication.getInstance().getLoggedUserSPName(),
                MODE_PRIVATE);
    }

    public boolean setGender(String gender) {
        if (gender.equalsIgnoreCase("male") ||
                gender.equalsIgnoreCase("female") ||
                gender.equalsIgnoreCase("other")) {
            userPreferences.edit().putString("gender", gender).apply();
            return true;
        } else {
            Toast.makeText(context, "Invalid Gender.", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public boolean setDateOfBirth(LocalDate dateOfBirth) {
        if (dateOfBirth.isBefore(LocalDate.now()) &&
                calculateAge(dateOfBirth) < 120 &&
                calculateAge(dateOfBirth) >= 16) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String formattedDateOfBirth = dateOfBirth.format(formatter);
            userPreferences.edit().putString("dateOfBirth", formattedDateOfBirth).apply();
            setAge(calculateAge(dateOfBirth));
            return true;
        } else {
            Toast.makeText(context, "Invalid Date of Birth.", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private boolean setAge(int age) {
        if (age >= 0) { // you can add more validations if necessary
            userPreferences.edit().putInt("age", age).apply();
            return true;
        } else {
            Toast.makeText(context, "Invalid Age.", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    // Height in cm
    public boolean setHeight(int height) {
        if (height >= 100 && height <= 220) {
            userPreferences.edit().putInt("height", height).apply();
            return true;
        } else {
            Toast.makeText(context, "Invalid Height.", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    // Weight in kg
    public boolean setWeight(int weight) {
        if (weight >= 40 && weight <= 250) {
            userPreferences.edit().putInt("weight", weight).apply();
            return true;
        } else {
            Toast.makeText(context, "Invalid Weight.", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public boolean setGoal(String goal) {
        if (goal.equalsIgnoreCase("lose") ||
                goal.equalsIgnoreCase("maintain") ||
                goal.equalsIgnoreCase("gain") ||
                goal.equalsIgnoreCase("gain muscle")) {
            userPreferences.edit().putString("goal", goal).apply();
            return true;
        } else {
            Toast.makeText(context, "Invalid Goal.", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public boolean setTargetWeight(int targetWeight) {
        if (targetWeight >= 40 && targetWeight <= 250) {
            userPreferences.edit().putInt("targetWeight", targetWeight).apply();
            return true;
        } else {
            Toast.makeText(context, "Invalid Target Weight.", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public boolean setActivityLevel(String activityLevel) {
       if (activityLevel.equalsIgnoreCase("low") ||
                activityLevel.equalsIgnoreCase("medium") ||
                activityLevel.equalsIgnoreCase("high") ||
                activityLevel.equalsIgnoreCase("very high")) {
            userPreferences.edit().putString("activityLevel", activityLevel).apply();
            return true;
        } else {
            Toast.makeText(context, "Invalid Activity Level.", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public boolean setDietType(String dietType) {
        if (dietType.equalsIgnoreCase("vegan") ||
                dietType.equalsIgnoreCase("vegetarian") ||
                dietType.equalsIgnoreCase("carnivore")) {
            userPreferences.edit().putString("dietType", dietType).apply();
            return true;
        } else {
            Toast.makeText(context, "Invalid Diet Type.", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private boolean setStreak(int streak) {
        if (streak >= 0) {
            userPreferences.edit().putInt("streak", streak).apply();
            return true;
        } else {
            Toast.makeText(context, "Invalid Streak.", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void incrementStreak() {
        setStreak(userPreferences.getInt("streak", 0) + 1);
    }

    public boolean setCalorieGoal(int calorieGoal) {
       if (calorieGoal > 0 && calorieGoal < 10000) {
            userPreferences.edit().putInt("calorieGoal", calorieGoal).apply();
            return true;
        } else {
            Toast.makeText(context, "Invalid Calorie Goal.", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public boolean setCarbGoal(int carbGoal) {
        if (carbGoal > 0 && carbGoal < 1000) {
            userPreferences.edit().putInt("carbGoal", carbGoal).apply();
            return true;
        } else {
            Toast.makeText(context, "Invalid Carb Goal.", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public boolean setProteinGoal(int proteinGoal) {
        if (proteinGoal > 0 && proteinGoal < 1000) {
            userPreferences.edit().putInt("proteinGoal", proteinGoal).apply();
            return true;
        } else {
            Toast.makeText(context, "Invalid Protein Goal.", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public boolean setFatGoal(int fatGoal) {
        if (fatGoal > 0 && fatGoal < 1000) {
            userPreferences.edit().putInt("fatGoal", fatGoal).apply();
            return true;
        } else {
            Toast.makeText(context, "Invalid Fat Goal.", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public boolean setWaterIntakeGoal(int waterIntakeGoal) {
        if (waterIntakeGoal > 0 && waterIntakeGoal < 10000) {
            userPreferences.edit().putInt("waterIntakeGoal", waterIntakeGoal).apply();
            return true;
        } else {
            Toast.makeText(context, "Invalid Water Intake goal.", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public boolean setStartDate(LocalDate startDate) {
        if (startDate.isBefore(LocalDate.now().plusDays(1))) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String formattedStartDate = startDate.format(formatter);
            userPreferences.edit().putString("startDate", formattedStartDate).apply();
            return true;
        } else {
            Toast.makeText(context, "Invalid Start Date.", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public SharedPreferences getUserPreferences() {
        return userPreferences;
    }

    // Get User ID
    public int getUserId() {
        return userId;
    }

    // Get Gender
    public String getGender() {
        return userPreferences.getString("gender", "");
    }

    // Get Date of Birth as LocalDate
    public LocalDate getDateOfBirth() {
        String dobStr = userPreferences.getString("dateOfBirth", null);
        if (dobStr != null) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                return LocalDate.parse(dobStr, formatter);
            } catch (DateTimeParseException e) {
                // Log error or handle parsing failure as needed
            }
        }
        return null;
    }

    public String getParsedDateOfBirth() {
        LocalDate date = getDateOfBirth();
        if (date != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            return date.format(formatter);
        }
        return "";
    }

    public int getAge() {
        return userPreferences.getInt("age", 0);
    }

    // Get Height in centimeters
    public int getHeight() {
        return userPreferences.getInt("height", 0);
    }

    // Get Weight in kilograms
    public int getWeight() {
        return userPreferences.getInt("weight", 0);
    }

    // Get Goal (e.g., "lose", "maintain", "gain", "gain muscle")
    public String getGoal() {
        return userPreferences.getString("goal", "");
    }

    // Get Target Weight in kilograms
    public int getTargetWeight() {
        return userPreferences.getInt("targetWeight", 0);
    }

    // Get Activity Level (e.g., "low", "medium", "high", "very high")
    public String getActivityLevel() {
        return userPreferences.getString("activityLevel", "");
    }

    // Get Diet Type (e.g., "vegan", "vegetarian", "carnivore")
    public String getDietType() {
        return userPreferences.getString("dietType", "");
    }

    public int getStreak() {
        return userPreferences.getInt("streak", 0);
    }

    public LocalDate getStartDate() {
        String dobStr = userPreferences.getString("startDate", null);
        if (dobStr != null) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                return LocalDate.parse(dobStr, formatter);
            } catch (DateTimeParseException e) {
                Toast.makeText(context, "Invalid start date.", Toast.LENGTH_SHORT).show();
            }
        }
        return null;
    }

    public int getStartWeight() {
        return userPreferences.getInt("startWeight", 0);
    }

    // Get Calorie Goal
    public int getCalorieGoal() {
        return userPreferences.getInt("calorieGoal", 0);
    }

    // Get Carbs Goal
    public int getCarbGoal() {
        return userPreferences.getInt("carbGoal", 0);
    }

    // Get Protein Goal
    public int getProteinGoal() {
        return userPreferences.getInt("proteinGoal", 0);
    }

    // Get Fat Goal
    public int getFatGoal() {
        return userPreferences.getInt("fatGoal", 0);
    }

    // Get Water Intake Goal
    public int getWaterIntakeGoal() {
        return userPreferences.getInt("waterIntakeGoal", 0);
    }

    // Calculate age from date of birth
    public static int calculateAge(LocalDate dateOfBirth) {
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
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
            case "low": return 1f;
            case "medium": return 1.2f;
            case "high": return 1.5f;
            case "very high": return 1.7f;
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
}
