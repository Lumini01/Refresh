package com.example.refresh.Helper;

import android.content.Context;

import com.example.refresh.Database.MealsTable;
import com.example.refresh.Model.Meal;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WaterLogHelper {
    private final DatabaseHelper dbHelper; // Your database access object
    private final Context context;

    public WaterLogHelper(Context context) {
        dbHelper = new DatabaseHelper(context);
        this.context = context;
    }

    public void logWater(int amount) {
        editWaterRecord(LocalDate.now(),
                getMealInstance(amount, LocalDate.now())
        );
    }

    public void logWater(int amount, LocalDate date) {
        editWaterRecord(date, getMealInstance(amount, date));
    }

    public void logWaterCup() {
        logWater(250);
    }

    public void logWaterCup(LocalDate date) {
        logWater(250, date);
    }

    private Meal getMealInstance(int amount, LocalDate date) {
        return new Meal(
                date,
                LocalTime.of(0, 1),
                "waterIntake",
                "",
                new ArrayList<>(Arrays.asList(144)),
                new ArrayList<>(Arrays.asList(amount))
        );
    }

    private void editWaterRecord(LocalDate date, Meal waterIntake) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formattedDate = date.format(formatter);

        Meal loggedIntake = dbHelper.getRecord(DatabaseHelper.Tables.MEALS,
                new Enum<?>[]{MealsTable.Columns.TYPE, MealsTable.Columns.DATE},
                new String[]{"waterIntake", formattedDate});

        if (loggedIntake == null)
        dbHelper.insert(DatabaseHelper.Tables.MEALS, waterIntake);
        else {
            waterIntake.editServingSize(0, waterIntake.getServingSize(0) + loggedIntake.getServingSize(0));
            dbHelper.editRecord(DatabaseHelper.Tables.MEALS, waterIntake, MealsTable.Columns.MEAL_ID, new String[]{loggedIntake.getId() + ""});
        }
    }
}
