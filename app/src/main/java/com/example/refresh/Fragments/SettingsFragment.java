package com.example.refresh.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.example.refresh.AccountSettingsFragment;
import com.example.refresh.R;

// Settings Fragment which displays the settings in the main activity
public class SettingsFragment extends Fragment {
    private LinearLayout accountSettingsField;
    private ImageButton backArrow;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.extra_button).setVisibility(View.GONE);

        accountSettingsField = view.findViewById(R.id.accountSettings);

        if (accountSettingsField != null) {
            accountSettingsField.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Navigate to AccountSettingsFragment
                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new AccountSettingsFragment())
                            .addToBackStack(null) // Add this transaction to the back stack
                            .commit();
                }
            });
        }

        backArrow = view.findViewById(R.id.backArrow);

        // Set click listener for Back Button
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){

                view.findViewById(R.id.fragment_container).setVisibility(View.GONE); // Ensure visibility
                if (getActivity() != null) {
                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .hide(SettingsFragment.this)
                            .commit();
                }
            }
        });

    }
}