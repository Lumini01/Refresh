package com.example.refresh.Fragment;

import static com.example.refresh.Fragment.UserInfoFragment.States.ACCOUNT_DETAILS;
import static com.example.refresh.Fragment.UserInfoFragment.States.ADJUST_GOAL;
import static com.example.refresh.Fragment.UserInfoFragment.States.ALL;
import static com.example.refresh.Fragment.UserInfoFragment.States.FIRST_LOG;
import static com.example.refresh.Fragment.UserInfoFragment.States.LIFESTYLE_GOAL;
import static com.example.refresh.Fragment.UserInfoFragment.States.MULTIPLE;
import static com.example.refresh.Fragment.UserInfoFragment.States.PERSONAL_INFO;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.refresh.Database.UsersTable;
import com.example.refresh.Helper.DatabaseHelper;
import com.example.refresh.Helper.GoalHelper;
import com.example.refresh.Helper.UserInfoHelper;
import com.example.refresh.Model.User;
import com.example.refresh.R;

import java.util.ArrayList;
import java.util.Arrays;

public class NotificationSettingsFragment extends Fragment {

    public interface OnNotificationSettingsListener {
        void hideNotificationSettings();
    }

    private NotificationSettingsFragment.OnNotificationSettingsListener fragmentListener;

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

    public static UserInfoFragment newInstance() {
        UserInfoFragment fragment = new UserInfoFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        assignValuesToViews();
    }

    private void initializeViews(View view) {
    }

    private void assignValuesToViews() {

    }
}
