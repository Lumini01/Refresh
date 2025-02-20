package com.example.refresh.Fragment;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.refresh.Adapter.SearchResultsAdapter;
import com.example.refresh.Model.Food;
import com.example.refresh.Model.ListItem;
import com.example.refresh.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;

public class TrendGraphFragment extends Fragment {

    private LineChart chart;


    public TrendGraphFragment() {
        // Required empty public constructor
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

        setupLineChart();
    }

    public void initializeUI(View view) {
        chart = view.findViewById(R.id.lineChart);
    }

    public void setupLineChart() {
        // Prepare sample data entries
        ArrayList<Entry> entries = new ArrayList<>();
        entries.add(new Entry(0f, 1f));
        entries.add(new Entry(1f, 3f));
        entries.add(new Entry(2f, 2f));
        entries.add(new Entry(3f, 5f));
        entries.add(new Entry(4f, 4f));

        // Create a dataset and configure styling for a modern look
        LineDataSet dataSet = new LineDataSet(entries, "Health Data");
        dataSet.setColor(Color.parseColor("#4CAF50")); // Modern flat color
        dataSet.setLineWidth(2f);
        dataSet.setDrawCircles(false);
        dataSet.setDrawValues(false);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER); // Smooth, curved lines
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(Color.parseColor("#4CAF50"));
        dataSet.setFillAlpha(80); // Adjust transparency

        // Create a LineData object with the dataset
        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);

        // Customize the X-Axis
        XAxis xAxis = chart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setTextColor(Color.DKGRAY);

        // Customize the Left Y-Axis
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setDrawAxisLine(false);
        leftAxis.setTextColor(Color.DKGRAY);

        // Disable the Right Y-Axis
        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);

        // Remove the description label
        Description description = new Description();
        description.setEnabled(false);
        chart.setDescription(description);

        // Disable the legend for a minimalist look
        Legend legend = chart.getLegend();
        legend.setEnabled(false);

        // Enable touch gestures and pinch zoom
        chart.setTouchEnabled(true);
        chart.setPinchZoom(true);

        // Set a transparent background for a modern feel
        chart.setBackgroundColor(Color.TRANSPARENT);

        // Animate the chart horizontally
        chart.animateX(1500);

        // Refresh the chart
        chart.invalidate();
    }
}