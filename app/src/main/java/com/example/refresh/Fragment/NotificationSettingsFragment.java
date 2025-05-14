package com.example.refresh.Fragment;

import android.Manifest;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.refresh.Database.NotificationInstancesTable;
import com.example.refresh.Helper.DatabaseHelper;
import com.example.refresh.Helper.NotificationHelper;
import com.example.refresh.Model.NotificationInstance;
import com.example.refresh.MyApplication;
import com.example.refresh.Notification.NotificationScheduler;
import com.example.refresh.R;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.time.LocalTime;
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
    private ActivityResultLauncher<String> requestNotifPermissionLauncher;
    private SharedPreferences userPreferences;
    private LinearLayout settingsLayout;
    private TextView title;
    private ImageButton backBtn;
    private SwitchMaterial notificationsSwitch;
    private EditText breakfastET;
    private EditText launchET;
    private EditText dinnerET;
    private EditText waterET;
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
        userPreferences = requireContext().getSharedPreferences(MyApplication.getInstance().getLoggedUserSPName(), Context.MODE_PRIVATE);

        requestNotifPermissionLauncher =
                registerForActivityResult(
                        new ActivityResultContracts.RequestPermission(),
                        isGranted -> {
                            if (isGranted) {
                                enableNotifications();
                            }
                            else {
                                showToast("Cannot Enable Notifications Without Permissions.");
                                revertSwitchOff();
                            }
                        }
                );
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

        settingsLayout = view.findViewById(R.id.settings_layout);
        notificationsSwitch = view.findViewById(R.id.notifications_switch);
        breakfastET = view.findViewById(R.id.breakfast_et);
        launchET = view.findViewById(R.id.launch_et);
        dinnerET = view.findViewById(R.id.dinner_et);
        waterET = view.findViewById(R.id.water_et);
        testBtn = view.findViewById(R.id.test_btn);
    }

    private void setupUI() {
        String titleStr = "Notifications";
        title.setText(titleStr);

        int status = ContextCompat.checkSelfPermission(
                getActivity(),
                Manifest.permission.POST_NOTIFICATIONS
        );

        if (status == PackageManager.PERMISSION_GRANTED) {
            notificationsSwitch.setChecked(true);
            userPreferences.edit().putBoolean("notificationsEnabled", true).apply();
        }
        else {
            notificationsSwitch.setChecked(false);
            userPreferences.edit().putBoolean("notificationsEnabled", true).apply();
        }


        notificationSchedules = NotificationInstancesTable.getUserDefaultNotificationTimes(
                requireContext(),
                MyApplication.getInstance().getLoggedUserID());


        breakfastET.setText(notificationSchedules.get(0));
        launchET.setText(notificationSchedules.get(1));
        dinnerET.setText(notificationSchedules.get(2));
        waterET.setText(notificationSchedules.get(3));
    }

    private void setListeners() {
        backBtn.setOnClickListener(v -> {
            ArrayList<Integer> templateIDs = new ArrayList<>();
            templateIDs.add(2);
            templateIDs.add(3);
            templateIDs.add(4);
            templateIDs.add(6);

            NotificationScheduler.updateDefaultNotifications(
                    requireContext(), templateIDs,
                    notificationSchedules,
                    MyApplication.getInstance().getLoggedUserID()
            );

            fragmentListener.hideNotificationSettings();
        });

        notificationsSwitch.setOnCheckedChangeListener(this::onNotificationsToggled);

        testBtn.setOnClickListener(v -> {
            LocalTime time = LocalTime.now();
            String formattedTime = time.format(DateTimeFormatter.ofPattern("HH:mm"));

            Random rnd = new Random();
            NotificationInstance instance = new NotificationInstance(rnd.nextInt(25) + 1);
            NotificationHelper.showNotification(getActivity(), instance);
        });

        breakfastET.setOnClickListener(v -> {
            handleTimeClick(LocalTime.of(5, 0),
                    LocalTime.of(11, 59));
        });

        launchET.setOnClickListener(v -> {
            handleTimeClick(LocalTime.of(12, 0),
                    LocalTime.of(17, 59));
        });

        dinnerET.setOnClickListener(v -> {
            handleTimeClick(LocalTime.of(18, 0),
                    LocalTime.of(23, 59));
        });

        waterET.setOnClickListener(v -> {
            handleTimeClick(LocalTime.of(5, 0),
                    LocalTime.of(23, 59));
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

                updateET(formatted, determineET(rangeStart, rangeEnd));
            }
            else {
                String formattedStartTime = rangeStart.format(DateTimeFormatter.ofPattern("HH:mm"));
                String formattedEndTime = rangeEnd.format(DateTimeFormatter.ofPattern("HH:mm"));
                String toast = "Reminder Must Be Between " + formattedStartTime + " and " + formattedEndTime;

                showToast(toast);
            }
        });
    }

    private boolean validateTime(LocalTime rangeStart, LocalTime rangeEnd, LocalTime selectedTime) {
        if (selectedTime.isAfter(rangeStart) && selectedTime.isBefore(rangeEnd)) return true;

        return false;
    }

    private EditText determineET(LocalTime rangeStart, LocalTime rangeEnd) {
        if (rangeStart.equals(LocalTime.of(5, 0))
                && rangeEnd.equals(LocalTime.of(11, 59))) return breakfastET;
        if (rangeStart.equals(LocalTime.of(12, 0))) return launchET;
        if (rangeStart.equals(LocalTime.of(18, 0))) return dinnerET;
        if (rangeStart.equals(LocalTime.of(5, 0))) return waterET;

        return null;
    }

    private void updateET(String time, EditText et) {
        et.setText(time);

        int index = -1;
        if (et.equals(breakfastET)) index = 0;
        if (et.equals(launchET)) index = 1;
        if (et.equals(dinnerET)) index = 2;
        if (et.equals(waterET)) index = 3;

        notificationSchedules.set(index, time);
    }

    private void onNotificationsToggled(CompoundButton button, boolean isChecked) {
        if (!isChecked) {
            // User turned OFF
            userPreferences.edit()
                    .putBoolean("notificationsEnabled", false)
                    .apply();

            showToast("Notifications OFF");
            setOpacity();
            return;
        }

        // User turned ON
        if (hasNotificationPermission()) {
            enableNotifications();
        } else {
            requestNotifPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
        }
    }

    private boolean hasNotificationPermission() {
        return ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED;
    }

    private void enableNotifications() {
        userPreferences.edit().putBoolean("notificationsEnabled", true).apply();
        showToast("Notifications ON");
        setOpacity();
        ensureExactAlarmsAllowed();
    }

    private void disableNotifications() {
        userPreferences.edit().putBoolean("notificationsEnabled", false).apply();
        showToast("Notifications OFF");
        setOpacity();
    }

    private void revertSwitchOff() {
        // Temporarily detach listener so setChecked(false) doesn't re-trigger it
        notificationsSwitch.setOnCheckedChangeListener(null);
        notificationsSwitch.setChecked(false);
        notificationsSwitch.setOnCheckedChangeListener(this::onNotificationsToggled);
    }

    private void ensureExactAlarmsAllowed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager am = requireContext().getSystemService(AlarmManager.class);
            if (am != null && !am.canScheduleExactAlarms()) {
                startActivity(new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM));
            }
        }
    }

    private void setOpacity() {
        if (notificationsSwitch.isChecked()) {
            settingsLayout.setAlpha(1f);
            breakfastET.setEnabled(true);
            launchET.setEnabled(true);
            dinnerET.setEnabled(true);
            waterET.setEnabled(true);
            testBtn.setEnabled(true);
        }
        else {
            settingsLayout.setAlpha(0.5f);
            breakfastET.setEnabled(false);
            launchET.setEnabled(false);
            dinnerET.setEnabled(false);
            waterET.setEnabled(false);
            testBtn.setEnabled(false);
        }

    }

    private void showToast(String msg) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
