package com.example.refresh.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.refresh.Database.NotificationInstancesTable;
import com.example.refresh.Helper.DatabaseHelper;
import com.example.refresh.Helper.NotificationHelper;
import com.example.refresh.Model.NotificationInstance;
import com.example.refresh.Notification.NotificationScheduler;
import com.example.refresh.R;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Random;


public class NotificationSettingsFragment extends Fragment {

    public interface OnNotificationSettingsListener {
        void hideNotificationSettings();
    }

    private NotificationSettingsFragment.OnNotificationSettingsListener fragmentListener;
    private DatabaseHelper dbHelper;
    private ArrayList<String> notificationSchedules;
    private TextView title;
    private ImageButton backBtn;
    private SwitchMaterial notificationsSwitch;
    private EditText breakfastET;
    private EditText launchET;
    private EditText dinnerET;
    private Button testBtn;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Make sure the hosting activity implements the interface.
        if (context instanceof OnNotificationSettingsListener) {
            fragmentListener = (OnNotificationSettingsListener) context;
        } else {
            throw new RuntimeException(context
                    + " must implement OnNotificationSettingsListener");
        }
    }

    public NotificationSettingsFragment() {
        // Required empty public constructor
    }

    public static NotificationSettingsFragment newInstance() {
        NotificationSettingsFragment fragment = new NotificationSettingsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new DatabaseHelper(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notification_settings, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);
        setupUI();
        setListeners();
    }

    private void initializeViews(View view) {
        title = view.findViewById(R.id.toolbar_title_tv);
        backBtn = view.findViewById(R.id.back_btn);
        ImageButton extra = view.findViewById(R.id.extra_btn);
        extra.setVisibility(View.GONE);

        notificationsSwitch = view.findViewById(R.id.notifications_switch);
        breakfastET = view.findViewById(R.id.breakfast_et);
        launchET = view.findViewById(R.id.launch_et);
        dinnerET = view.findViewById(R.id.dinner_et);
        testBtn = view.findViewById(R.id.test_btn);
    }

    private void setupUI() {
        String titleStr = "Notifications";
        title.setText(titleStr);

        notificationSchedules = new ArrayList<>();
        for (int i=1 ; i<=3 ; i++) {
        notificationSchedules.add(dbHelper.getFromRecordByIndex(DatabaseHelper.Tables.NOTIFICATION_INSTANCES,
                NotificationInstancesTable.Columns.TIME, i));
        }

        breakfastET.setText(notificationSchedules.get(0));
        launchET.setText(notificationSchedules.get(1));
        dinnerET.setText(notificationSchedules.get(2));
    }

    private void setListeners() {
        backBtn.setOnClickListener(v -> {
            for (int i=0 ; i<3 ; i++) {
                String time = notificationSchedules.get(i);
                NotificationInstance instance = new NotificationInstance(i+1, time);

            }
            // NotificationScheduler.cancelExistingAlarm(getActivity(), );
            fragmentListener.hideNotificationSettings();
        });

        testBtn.setOnClickListener(v -> {
            LocalTime time = LocalTime.now();
            String formattedTime = time.format(DateTimeFormatter.ofPattern("HH:mm"));

            NotificationInstance instance = new NotificationInstance(1, formattedTime);
            NotificationHelper.showNotification(getActivity(), instance);
        });

        breakfastET.setOnClickListener(v -> {
            handleTimeClick(LocalTime.of(5, 0), LocalTime.of(11, 59));
        });

        launchET.setOnClickListener(v -> {
            handleTimeClick(LocalTime.of(12, 0), LocalTime.of(17, 59));
        });

        dinnerET.setOnClickListener(v -> {
            handleTimeClick(LocalTime.of(18, 0), LocalTime.of(23, 59));
        });
    }

    private void handleTimeClick(LocalTime rangeStart, LocalTime rangeEnd) {
        // 1. Build the picker
        MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                .setTitleText("Select time")
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(LocalTime.now().getHour())
                .setMinute(LocalTime.now().getMinute())
                .build();

        // 2. Show it
        timePicker.show(
                getParentFragmentManager(),
                "TIME_PICKER"
        );

        // 3. Handle selection
        timePicker.addOnPositiveButtonClickListener(dialog -> {
            // a) Create a LocalTime right here
            LocalTime selectedTime = LocalTime.of(
                    timePicker.getHour(),
                    timePicker.getMinute()
            );
            if (validateTime(rangeStart, rangeEnd, selectedTime)) {
                // b) Format and display in your EditText
                String formatted = selectedTime.format(
                        DateTimeFormatter.ofPattern("HH:mm")
                );

                updateET(formatted, determineET(rangeStart));
            }
            else {
                String formattedStartTime = rangeStart.format(DateTimeFormatter.ofPattern("HH:mm"));
                String formattedEndTime = rangeEnd.format(DateTimeFormatter.ofPattern("HH:mm"));
                String toast = "Reminder Must Be Between " + formattedStartTime + " and " + formattedEndTime;

                Toast.makeText(getActivity(), toast, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateTime(LocalTime rangeStart, LocalTime rangeEnd, LocalTime selectedTime) {
        if (selectedTime.isAfter(rangeStart) && selectedTime.isBefore(rangeEnd)) return true;

        return false;
    }

    private EditText determineET(LocalTime rangeStart) {
        if (rangeStart.equals(LocalTime.of(5, 0))) return breakfastET;
        if (rangeStart.equals(LocalTime.of(12, 0))) return launchET;
        if (rangeStart.equals(LocalTime.of(18, 0))) return dinnerET;

        return null;
    }

    private void updateET(String time, EditText et) {
        et.setText(time);

        int index = -1;
        if (et.equals(breakfastET)) index = 0;
        if (et.equals(launchET)) index = 1;
        if (et.equals(dinnerET)) index = 2;

        notificationSchedules.set(0, time);
    }
}
