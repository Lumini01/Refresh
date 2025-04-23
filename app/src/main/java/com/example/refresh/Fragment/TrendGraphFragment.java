package com.example.refresh.Fragment;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.refresh.Model.DaySummary;
import com.example.refresh.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;

public class TrendGraphFragment extends Fragment {

    private BarChart chart;

    public TrendGraphFragment() {
        // Required empty public constructor
    }

    public static TrendGraphFragment newInstance(ArrayList<DaySummary> daySummaries, int calorieGoal) {
        TrendGraphFragment fragment = new TrendGraphFragment();
        Bundle args = new Bundle();
        args.putSerializable("daySummaries", daySummaries);
        args.putInt("calorieGoal", calorieGoal);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_trend_graph, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        initializeUI(view);

        ArrayList<DaySummary> daySummaries = (ArrayList<DaySummary>) getArguments().getSerializable("daySummaries");
        int calorieGoal = getArguments().getInt("calorieGoal");
        if (daySummaries != null)
            updateLineChart(daySummaries, calorieGoal);
    }

    private void initializeUI(View view) {
        chart = view.findViewById(R.id.lineChart);
    }

    public void updateLineChart(ArrayList<DaySummary> daySummaries, int calorieGoal) {
        // 1. Prepare exactly 7 data entries
        ArrayList<BarEntry> entries = new ArrayList<>();
        float maxCalories = 0f;

        int size = daySummaries != null ? daySummaries.size() : 0;
        // Ensure we handle up to 7 days; if daySummaries has fewer than 7,
        // fill remaining days with 0.
        for (int i = 0; i < 7; i++) {
            float value = 0f;
            if (i < size) {
                value = daySummaries.get(i).getTotalCalories();
            }
            if (value > maxCalories) {
                maxCalories = value;
            }
            entries.add(new BarEntry(i, value));
        }

        if (entries.isEmpty()) {
            for (int i = 0; i < 7; i++) {
                entries.add(new BarEntry(i, 0f));
            }
        }

        // 2. Create a dataset and configure styling
        BarDataSet dataSet = new BarDataSet(entries, "Weekly Data");
        dataSet.setColor(Color.BLUE); // Modern flat color
        dataSet.setFormLineWidth(4f);
        dataSet.setDrawValues(false);

        // 3. Create BarData and set it to the chart
        BarData barData = new BarData(dataSet);
        chart.setData(barData);

        // 4. Customize the X-Axis
        XAxis xAxis = chart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setTextColor(Color.DKGRAY);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        final String[] days = new String[] {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                if (index >= 0 && index < days.length) {
                    return days[index];
                } else {
                    return "?";
                }
            }
        });

        // 5. Customize the Left Y-Axis
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setDrawAxisLine(false);
        leftAxis.setTextColor(Color.DKGRAY);

        // Ensure the minimum is 0
        leftAxis.setAxisMinimum(0f);

        // Multiply the max by 1.25 to leave extra space

        leftAxis.setAxisMaximum(Math.max(maxCalories * 1.4f, calorieGoal * 1.4f));

        // 6. Disable the Right Y-Axis
        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);

        // 7. Create a dashed limit line for the calorie goal
        LimitLine limitLine = new LimitLine(calorieGoal, "");
        limitLine.setLineWidth(2f);
        limitLine.setLineColor(Color.DKGRAY);
        limitLine.enableDashedLine(10f, 10f, 0f);
        leftAxis.addLimitLine(limitLine);

        if (calorieGoal < 0) {
            leftAxis.removeLimitLine(limitLine);
            Log.d("TrendGraphFragment", "Calorie Goal: " + calorieGoal);
        }

        // 8. Remove the description label
        Description description = new Description();
        description.setEnabled(false);
        chart.setDescription(description);

        // 9. Disable the legend for a minimalist look
        Legend legend = chart.getLegend();
        legend.setEnabled(false);

        // 10. Enable touch gestures (no pinch zoom for a bar chart, but you can enable if desired)
        chart.setTouchEnabled(true);
        chart.setPinchZoom(false);

        // 11. Set a transparent background for a modern feel
        chart.setBackgroundColor(Color.TRANSPARENT);

        // 12. Animate the chart horizontally
        if (daySummaries.size() >= 5)
            chart.animateX(500);
        else {
            chart.animateX(100);
        }

        // 13. Refresh the chart
        chart.invalidate();
    }
}