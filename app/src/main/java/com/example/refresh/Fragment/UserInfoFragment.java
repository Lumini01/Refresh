package com.example.refresh.Fragment;

import static com.example.refresh.Fragment.UserInfoFragment.States.ACCOUNT_DETAILS;
import static com.example.refresh.Fragment.UserInfoFragment.States.ADJUST_GOAL;
import static com.example.refresh.Fragment.UserInfoFragment.States.ALL;
import static com.example.refresh.Fragment.UserInfoFragment.States.FIRST_LOG;
import static com.example.refresh.Fragment.UserInfoFragment.States.LIFESTYLE_GOAL;
import static com.example.refresh.Fragment.UserInfoFragment.States.MULTIPLE;
import static com.example.refresh.Fragment.UserInfoFragment.States.PERSONAL_INFO;

import android.app.DatePickerDialog;
import android.content.Context;
import android.icu.util.Calendar;
import android.icu.util.TimeZone;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.refresh.Database.UsersTable;
import com.example.refresh.Helper.DatabaseHelper;
import com.example.refresh.Helper.GoalHelper;
import com.example.refresh.Helper.UserInfoHelper;
import com.example.refresh.Model.User;
import com.example.refresh.R;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class UserInfoFragment extends Fragment {

    public interface OnUserInfoFragmentListener {
        void hideUserInfo();
    }

    public enum States {
        ALL("all"),
        FIRST_LOG("first_log"),
        PERSONAL_INFO("personal_info"),
        ACCOUNT_DETAILS("account_details"),
        LIFESTYLE_GOAL("life_style_goal"),
        ADJUST_GOAL("adjust_goal"),
        MULTIPLE("multiple");

        private final String stateName;

        States(String stateName) {
            this.stateName = stateName;
        }

        public String getStateName() {
            return stateName;
        }
    }

    // State & domain
    private String state;
    private int userId;
    private User user;
    private OnUserInfoFragmentListener fragmentListener;
    private ArrayList<String> displayedSections;

    // Helpers & persistence
    private UserInfoHelper userInfoHelper;
    private DatabaseHelper dbHelper;
    private GoalHelper goalHelper;

    // Immutable option lists
    private final ArrayList<String> genderOptions = new ArrayList<>(Arrays.asList(
            "Select Gender", "Male", "Female", "Other"
    ));
    private final ArrayList<String> activityLevelOptions = new ArrayList<>(Arrays.asList(
            "Select Level", "Low", "Medium", "High", "Very High"
    ));

    // Layout containers
    private LinearLayout personalInfoLayout;
    private LinearLayout accountDetailsLayout;
    private LinearLayout lifestyleGoalLayout;
    private LinearLayout adjustGoalLayout;
    private LinearLayout targetWeightLayout;

    // Toolbar
    private TextView title;
    private ImageButton saveBtn;
    private ImageButton backBtn;

    // — Personal Info Section —
    private EditText nameET;
    private EditText weightET;
    private EditText heightET;
    private EditText dateOfBirthET;
    private Spinner genderSpinner;
    private TextView genderErrorTV;

    // — Account Details Section —
    private EditText emailET;
    private EditText phoneET;
    private EditText pwdET;
    private EditText pwdConfirmET;

    // — Goals & Lifestyle Section —
    private RadioGroup goalRG;
    private RadioButton loseWeightRB;
    private RadioButton maintainWeightRB;
    private RadioButton gainWeightRB;
    private RadioButton gainMuscleRB;
    private TextView goalErrorTV;

    private EditText targetWeightET;

    private Spinner activityLevelSpinner;
    private TextView activityLevelErrorTV;

    private RadioGroup dietTypeRG;
    private RadioButton carnivoreRB;
    private RadioButton vegetarianRB;
    private RadioButton veganRB;
    private TextView dietTypeErrorTV;

    // — Adjustment Inputs Section —
    private EditText adjustCaloriesET;
    private EditText adjustCarbsET;
    private EditText adjustProteinET;
    private EditText adjustFatET;
    private EditText adjustWaterIntakeET;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Make sure the hosting activity implements the interface.
        if (context instanceof OnUserInfoFragmentListener) {
            fragmentListener = (OnUserInfoFragmentListener) context;
        } else {
            throw new RuntimeException(context
                    + " must implement OnUserInfoFragmentListener");
        }
    }

    public UserInfoFragment() {
        // Required empty public constructor
    }
    public static UserInfoFragment newInstance(String state, int userId) {
        UserInfoFragment fragment = new UserInfoFragment();
        Bundle args = new Bundle();
        args.putString("state", state);
        args.putInt("userId", userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            state = getArguments().getString("state");
            userId = getArguments().getInt("userId");
        }
        else {
            state = ALL.getStateName();
            userId = -1;
        }

        dbHelper = new DatabaseHelper(requireContext());
        goalHelper = new GoalHelper(requireContext());

        if (userId != -1) user = dbHelper.getRecord(
                DatabaseHelper.Tables.USERS,
                UsersTable.Columns.ID,
                new String[]{userId + ""}
        );
        else user = new User();
        dbHelper.close();

        userInfoHelper = new UserInfoHelper(requireContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_info, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);

        configureFragmentState();

        assignValuesToViews();

        setListeners();
    }

    private void initializeViews(View view) {
        personalInfoLayout = view.findViewById(R.id.personal_info_layout);
        accountDetailsLayout = view.findViewById(R.id.account_details_layout);
        lifestyleGoalLayout = view.findViewById(R.id.lifestyle_goal_layout);
        adjustGoalLayout = view.findViewById(R.id.goal_adjustment_layout);

        nameET = view.findViewById(R.id.name_et);
        weightET = view.findViewById(R.id.weight_et);
        heightET = view.findViewById(R.id.height_et);
        dateOfBirthET = view.findViewById(R.id.date_of_birth_et);
        genderSpinner = view.findViewById(R.id.gender_spinner);
        genderErrorTV = view.findViewById(R.id.gender_error_tv);

        emailET = view.findViewById(R.id.email_et);
        phoneET = view.findViewById(R.id.phone_et);
        pwdET = view.findViewById(R.id.pwd_et);
        pwdConfirmET = view.findViewById(R.id.pwd_conf_et);

        goalRG = view.findViewById(R.id.goal_rg);
        loseWeightRB = view.findViewById(R.id.lose_rBtn);
        maintainWeightRB = view.findViewById(R.id.maintain_rBtn);
        gainWeightRB = view.findViewById(R.id.gain_rBtn);
        gainMuscleRB = view.findViewById(R.id.gain_muscle_rBtn);
        goalErrorTV = view.findViewById(R.id.goal_error_tv);

        targetWeightLayout = view.findViewById(R.id.target_weight_layout);
        targetWeightET = view.findViewById(R.id.target_weight_et);

        activityLevelSpinner = view.findViewById(R.id.activity_level_spinner);
        activityLevelErrorTV = view.findViewById(R.id.activity_level_error_tv);

        dietTypeRG = view.findViewById(R.id.diet_type_rg);
        carnivoreRB = view.findViewById(R.id.carnivore_rBtn);
        vegetarianRB = view.findViewById(R.id.vegetarian_rBtn);
        veganRB = view.findViewById(R.id.vegan_rBtn);
        dietTypeErrorTV = view.findViewById(R.id.diet_type_error_tv);

        adjustCaloriesET = view.findViewById(R.id.adjust_calories_et);
        adjustCarbsET = view.findViewById(R.id.adjust_carbs_et);
        adjustProteinET = view.findViewById(R.id.adjust_protein_et);
        adjustFatET = view.findViewById(R.id.adjust_fat_et);
        adjustWaterIntakeET = view.findViewById(R.id.adjust_water_intake_et);

        backBtn = view.findViewById(R.id.back_btn);
        saveBtn = view.findViewById(R.id.extra_btn);
        saveBtn.setImageResource(R.drawable.ic_save);
        title = view.findViewById(R.id.toolbar_title_tv);
    }

    private void configureFragmentState() {
        if (state.equals(MULTIPLE.getStateName())) {
            if (getArguments() != null) displayedSections = getArguments().getStringArrayList("displayedSections");
            else displayedSections = new ArrayList<>();
        }

        if (!state.equals(ALL.getStateName())) {
            if (state.equals(FIRST_LOG.getStateName())) {
                accountDetailsLayout.setVisibility(View.GONE);
                adjustGoalLayout.setVisibility(View.GONE);
            }
            else if (state.equals(PERSONAL_INFO.getStateName())) {
                accountDetailsLayout.setVisibility(View.GONE);
                lifestyleGoalLayout.setVisibility(View.GONE);
                adjustGoalLayout.setVisibility(View.GONE);
            }
            else if (state.equals(ACCOUNT_DETAILS.getStateName())) {
                personalInfoLayout.setVisibility(View.GONE);
                lifestyleGoalLayout.setVisibility(View.GONE);
                adjustGoalLayout.setVisibility(View.GONE);
            }
            else if (state.equals(LIFESTYLE_GOAL.getStateName())) {
                personalInfoLayout.setVisibility(View.GONE);
                accountDetailsLayout.setVisibility(View.GONE);
                adjustGoalLayout.setVisibility(View.GONE);
            }
            else if (state.equals(ADJUST_GOAL.getStateName())) {
                personalInfoLayout.setVisibility(View.GONE);
                accountDetailsLayout.setVisibility(View.GONE);
                lifestyleGoalLayout.setVisibility(View.GONE);
            }
            else if (state.equals(MULTIPLE.getStateName())) {
                personalInfoLayout.setVisibility(View.GONE);
                accountDetailsLayout.setVisibility(View.GONE);
                lifestyleGoalLayout.setVisibility(View.GONE);
                adjustGoalLayout.setVisibility(View.GONE);

                for (String section : displayedSections) {
                    switch (section) {
                        case "personal_info":
                            personalInfoLayout.setVisibility(View.VISIBLE);
                            break;
                        case "account_details":
                            accountDetailsLayout.setVisibility(View.VISIBLE);
                            break;
                        case "lifestyle_goal":
                            lifestyleGoalLayout.setVisibility(View.VISIBLE);
                            break;
                        case "adjust_goal":
                            adjustGoalLayout.setVisibility(View.VISIBLE);
                            break;
                        default:
                            throw new IllegalStateException("Unexpected value: " + section);
                    }
                }
            }
            else throw new IllegalStateException("Unexpected value: " + state);
        }
    }

    private void assignValuesToViews() {
        title.setText(R.string.user_info);
        setValuesByState();
    }

    private void setValuesByState() {
        if (state.equals(ALL.getStateName())) setValuesAll();
        else if (state.equals(FIRST_LOG.getStateName())) setValuesFirstLog();
        else if (state.equals(MULTIPLE.getStateName())) setValuesMultiple();
        else if (state.equals(PERSONAL_INFO.getStateName())) setValuesPersonalInfo();
        else if (state.equals(ACCOUNT_DETAILS.getStateName())) setValuesAccountDetails();
        else if (state.equals(LIFESTYLE_GOAL.getStateName())) setValuesLifestyleGoal();
        else if (state.equals(ADJUST_GOAL.getStateName())) setValuesAdjustGoal();
        else throw new IllegalStateException("Unexpected value: " + state);
    }

    private void setValuesAll() {
        setValuesPersonalInfo();
        setValuesAccountDetails();
        setValuesLifestyleGoal();
        setValuesAdjustGoal();
    }

    private void setValuesFirstLog() {
        backBtn.setVisibility(View.GONE);
        nameET.setText(user.getName());
        nameET.setEnabled(false);

        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(requireContext(),
                R.layout.item_custom_spinner, genderOptions);
        genderAdapter.setDropDownViewResource(R.layout.item_custom_spinner);
        genderSpinner.setAdapter(genderAdapter);
        genderSpinner.setSelection(0); // Set the default selection to the first item
        activityLevelSpinner.setPopupBackgroundDrawable(null);

        ArrayAdapter<String> activityLevelAdapter = new ArrayAdapter<>(requireContext(),
                R.layout.item_custom_spinner, activityLevelOptions);
        activityLevelAdapter.setDropDownViewResource(R.layout.item_custom_spinner);
        activityLevelSpinner.setAdapter(activityLevelAdapter);
        activityLevelSpinner.setSelection(activityLevelOptions.indexOf(0)); // Set the default selection to the first item
        activityLevelSpinner.setPopupBackgroundDrawable(null);
    }

    private void setValuesMultiple() {
        for (String section : displayedSections) {
            switch (section) {
                case "personal_info":
                    setValuesPersonalInfo();
                    break;
                case "account_details":
                    setValuesAccountDetails();
                    break;
                case "lifestyle_goal":
                    setValuesLifestyleGoal();
                    break;
                case "adjust_goal":
                    setValuesAdjustGoal();
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + section);
            }
        }
    }

    private void setValuesPersonalInfo() {
        nameET.setText(user != null ? user.getName() : "");
        nameET.setEnabled(false);

        String weight = userInfoHelper.getWeight() + "";
        weightET.setText(weight);

        String height = userInfoHelper.getHeight() + "";
        heightET.setText(height);

        String birthday = userInfoHelper.getParsedDateOfBirth();
        dateOfBirthET.setText(birthday);

        String gender = userInfoHelper.getGender();
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(requireContext(),
                R.layout.item_custom_spinner, genderOptions);
        genderAdapter.setDropDownViewResource(R.layout.item_custom_spinner);
        genderSpinner.setAdapter(genderAdapter);
        genderSpinner.setSelection(genderOptions.indexOf(gender)); // Set the default selection to the first item
        activityLevelSpinner.setPopupBackgroundDrawable(null);
    }

    private void setValuesAccountDetails() {
        emailET.setText(user != null ? user.getEmail() : "");
        phoneET.setText(user != null ? user.getPhone() : "");
        pwdET.setText(user != null ? user.getPwd() : "");
    }

    private void setValuesLifestyleGoal() {
        int goalId = -1;
        String goal = userInfoHelper.getGoal();
        String targetWeight = "";

        if (goal.equals("lose")) {
            goalId = loseWeightRB.getId();
            targetWeight = userInfoHelper.getTargetWeight() + "";
        }
        else if (goal.equals("maintain")) goalId = maintainWeightRB.getId();
        else if (goal.equals("gain")) {
            goalId = gainWeightRB.getId();
            targetWeight = userInfoHelper.getTargetWeight() + "";
        }
        else if (goal.equals("gain muscle")) goalId = gainMuscleRB.getId();

        goalRG.check(goalId);
        targetWeightET.setText(targetWeight);
        if (!targetWeight.isEmpty())
            targetWeightLayout.setVisibility(View.VISIBLE);

        String activityLevel = userInfoHelper.getActivityLevel();
        ArrayAdapter<String> activityLevelAdapter = new ArrayAdapter<>(requireContext(),
                R.layout.item_custom_spinner, activityLevelOptions);
        activityLevelAdapter.setDropDownViewResource(R.layout.item_custom_spinner);
        activityLevelSpinner.setAdapter(activityLevelAdapter);
        activityLevelSpinner.setSelection(activityLevelOptions.indexOf(activityLevel)); // Set the default selection to the first item
        activityLevelSpinner.setPopupBackgroundDrawable(null);

        int dietTypeId = -1;
        String dietType = userInfoHelper.getDietType();
        if (dietType.equals("carnivore")) dietTypeId = carnivoreRB.getId();
        else if (dietType.equals("vegetarian")) dietTypeId = vegetarianRB.getId();
        else if (dietType.equals("vegan")) dietTypeId = veganRB.getId();

        dietTypeRG.check(dietTypeId);
    }

    private void setValuesAdjustGoal() {
        String calories = userInfoHelper.getCalorieGoal() + "";
        adjustCaloriesET.setText(calories);

        String carbs = userInfoHelper.getCarbGoal() + "";
        adjustCarbsET.setText(carbs);

        String protein = userInfoHelper.getProteinGoal() + "";
        adjustProteinET.setText(protein);

        String fat = userInfoHelper.getFatGoal() + "";
        adjustFatET.setText(fat);

        String waterIntake = userInfoHelper.getWaterIntakeGoal() + "";
        adjustWaterIntakeET.setText(waterIntake);
    }

    private void setListeners() {
        // Button Listeners:

        backBtn.setOnClickListener(v -> {
            dbHelper.close();
            fragmentListener.hideUserInfo();
        });

        saveBtn.setOnClickListener(v -> {
            onSave();
        });

        // Spinner Listeners:
        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int optionPosition = genderSpinner.getSelectedItemPosition();
                genderSpinner.setSelection(optionPosition);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        activityLevelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int optionPosition = activityLevelSpinner.getSelectedItemPosition();
                activityLevelSpinner.setSelection(optionPosition);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // 1. Add this dependency to your module’s build.gradle if you haven’t already:
// implementation 'com.google.android.material:material:1.9.0'

        dateOfBirthET.setOnClickListener(v -> {
            // 2. Build the MaterialDatePicker
            MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder
                    .datePicker()
                    .setTitleText("Select your birth date")
                    // optional: set a default selection (today) so the header shows a date
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .build();

            // 3. Show it
            datePicker.show(((AppCompatActivity) requireContext()).getSupportFragmentManager(), "DOB_PICKER");

            // 4. Listen for positive selection
            datePicker.addOnPositiveButtonClickListener(selection -> {
                // selection is the UTC milliseconds of the chosen date
                // convert to Calendar to format as dd/MM/yyyy
                Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Jerusalem"));
                cal.setTimeInMillis(selection);
                int year  = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH) + 1; // month is 0-based
                int day   = cal.get(Calendar.DAY_OF_MONTH);

                String formattedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", day, month, year);
                dateOfBirthET.setText(formattedDate);
            });
        });


        goalRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (loseWeightRB.isChecked()) {
                    targetWeightLayout.setVisibility(View.VISIBLE);
                }
                else if (maintainWeightRB.isChecked()) {
                    targetWeightET.setText("");
                    targetWeightLayout.setVisibility(View.GONE);
                }
                else if (gainWeightRB.isChecked()) {
                    targetWeightET.setText("");
                    targetWeightLayout.setVisibility(View.VISIBLE);
                }
                else if (gainMuscleRB.isChecked()) {
                    targetWeightLayout.setVisibility(View.GONE);
                }
            }
        });
    }

    private void onSave() {
        if (saveByState()) {
            if (!state.equals(ADJUST_GOAL.getStateName()))
                goalHelper.calculateDailyNutritionGoals();
            fragmentListener.hideUserInfo();
            dbHelper.close();
            Toast.makeText(dbHelper.getContext(), "Saved!", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean saveByState() {
        if (state.equals(ALL.getStateName())) return saveStateAll();
        else if (state.equals(FIRST_LOG.getStateName())) return saveStateFirstLog();
        else if (state.equals(PERSONAL_INFO.getStateName())) return saveStatePersonalInfo();
        else if (state.equals(ACCOUNT_DETAILS.getStateName())) return saveStateAccountDetails();
        else if (state.equals(LIFESTYLE_GOAL.getStateName())) return saveStateLifestyleGoal();
        else if (state.equals(ADJUST_GOAL.getStateName())) return saveStateAdjustGoal();
        else if (state.equals(MULTIPLE.getStateName())) return saveStateMultiple();
        else throw new IllegalStateException("Unexpected value: " + state);
    }

    private boolean saveStateAll() {
        boolean flag = saveStatePersonalInfo();
        flag &= saveStateAccountDetails();
        flag &= saveStateLifestyleGoal();
        flag &= saveStateAdjustGoal();

        return flag;
    }

    private boolean saveStateFirstLog() {
        boolean flag = saveStatePersonalInfo();
        flag &= saveStateLifestyleGoal();

        if (validatePersonalInfo() && userInfoHelper.getUserPreferences() != null) {
            userInfoHelper.getUserPreferences()
                    .edit()
                    .putInt("startWeight", Integer.parseInt(weightET.getText().toString()))
                    .apply();
            userInfoHelper.setStartDate(LocalDate.now());
        }
        else flag = false;

        return flag;
    }

    private boolean saveStatePersonalInfo() {
        if (validatePersonalInfo()) {
            user.setName(nameET.getText().toString().trim());
            dbHelper.editRecord(DatabaseHelper.Tables.USERS, user, UsersTable.Columns.ID, new String[]{user.getID() + ""});

            userInfoHelper.setWeight(Integer.parseInt(weightET.getText().toString()));
            userInfoHelper.setHeight(Integer.parseInt(heightET.getText().toString()));

            String dateOfBirthStr = (dateOfBirthET.getText() != null) ? dateOfBirthET.getText().toString().trim() : "";
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate dateOfBirth = LocalDate.parse(dateOfBirthStr, formatter);
            userInfoHelper.setDateOfBirth(dateOfBirth);

            userInfoHelper.setGender(genderSpinner.getSelectedItem().toString());

            return true;
        }

        return false;
    }

    private boolean saveStateAccountDetails() {
        if (validateAccountDetails()) {
            if (!pwdET.getText().toString().isEmpty()) {
                user.setPwd(pwdET.getText().toString());
                dbHelper.editRecord(
                        DatabaseHelper.Tables.USERS,
                        user,
                        UsersTable.Columns.ID,
                        new String[]{user.getID() + ""}
                );
            }

            // user.setEmail(emailET.getText().toString());
            // user.setPhone(phoneET.getText().toString());

            return true;
        }

        return false;
    }

    private boolean saveStateLifestyleGoal() {
        if (validateLifestyleGoal()) {

            String goalStr = "";
            if (loseWeightRB.isChecked()) goalStr = "lose";
            else if (maintainWeightRB.isChecked()) goalStr = "maintain";
            else if (gainWeightRB.isChecked()) goalStr = "gain";
            else if (gainMuscleRB.isChecked()) goalStr = "gain muscle";

            userInfoHelper.setGoal(goalStr);

            if (goalStr.equals("lose") || goalStr.equals("gain")) {
                userInfoHelper.setTargetWeight(Integer.parseInt(targetWeightET.getText().toString()));
            }

            userInfoHelper.setActivityLevel(activityLevelSpinner.getSelectedItem().toString());

            String dietTypeStr = "";
            if (carnivoreRB.isChecked()) dietTypeStr = "carnivore";
            else if (vegetarianRB.isChecked()) dietTypeStr = "vegetarian";
            else if (veganRB.isChecked()) dietTypeStr = "vegan";

            userInfoHelper.setDietType(dietTypeStr);

            return true;
        }

        return false;
    }

    private boolean saveStateAdjustGoal() {
        if (validateAdjustGoal()) {
            userInfoHelper.setCalorieGoal(Integer.parseInt(adjustCaloriesET.getText().toString()));
            userInfoHelper.setCarbGoal(Integer.parseInt(adjustCarbsET.getText().toString()));
            userInfoHelper.setProteinGoal(Integer.parseInt(adjustProteinET.getText().toString()));
            userInfoHelper.setFatGoal(Integer.parseInt(adjustFatET.getText().toString()));
            userInfoHelper.setWaterIntakeGoal(Integer.parseInt(adjustWaterIntakeET.getText().toString()));

            return true;
        }

        return false;
    }

    private boolean saveStateMultiple() {

        boolean flag = true;

        for (String section : displayedSections) {
            switch (section) {
                case "personal_info":
                    flag = flag && saveStatePersonalInfo();
                    break;
                case "account_details":
                    flag = flag && saveStateAccountDetails();
                    break;
                case "lifestyle_goal":
                    flag = flag && saveStateLifestyleGoal();
                    break;
                case "adjust_goal":
                    flag = flag && saveStateAdjustGoal();
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + section);
            }
        }

        return flag;
    }

    private boolean validatePersonalInfo() {
        boolean valid = true;

        // --- Validate Name ---
        String name = (nameET.getText() != null) ? nameET.getText().toString().trim() : "";
        if (name.isEmpty()) {
            nameET.setError("Name is required");
            valid = false;
        } else if (!name.contains(" ") || name.length() < 6) {
            nameET.setError("Invalid name (must include a space and be at least 6 characters)");
            valid = false;
        } else {
            nameET.setError(null);
        }

        // --- Validate Weight ---
        String weightStr = (weightET.getText() != null) ? weightET.getText().toString().trim() : "";
        if (weightStr.isEmpty()) {
            weightET.setError("Weight is required");
            valid = false;
        } else {
            try {
                int weight = Integer.parseInt(weightStr);
                if (weight < 40 || weight > 250) {
                    weightET.setError("Weight must be between 40-250 kg");
                    valid = false;
                } else {
                    weightET.setError(null);
                }
            } catch (NumberFormatException e) {
                weightET.setError("Invalid weight format");
                valid = false;
            }
        }

        // --- Validate Height ---
        String heightStr = (heightET.getText() != null) ? heightET.getText().toString().trim() : "";
        if (heightStr.isEmpty()) {
            heightET.setError("Height is required");
            valid = false;
        } else {
            try {
                int height = Integer.parseInt(heightStr);
                if (height < 100 || height > 220) {
                    heightET.setError("Height must be between 100-220 cm");
                    valid = false;
                } else {
                    heightET.setError(null);
                }
            } catch (NumberFormatException e) {
                heightET.setError("Invalid height format");
                valid = false;
            }
        }

        // --- Validate Date of Birth ---
        String dobStr = (dateOfBirthET.getText() != null) ? dateOfBirthET.getText().toString().trim() : "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        if (dobStr.isEmpty()) {
            dateOfBirthET.setError("Date of birth is required");
            valid = false;
        } else {
            try {
                LocalDate dob = LocalDate.parse(dobStr, formatter);
                if (dob.isAfter(LocalDate.now())) {
                    dateOfBirthET.setError("Date of birth must be in the past");
                    valid = false;
                }
                else if (LocalDate.now().getYear() - dob.getYear() < 16 || LocalDate.now().getYear() - dob.getYear() > 120) {
                    dateOfBirthET.setError("This app is for ages 16-120");
                    valid = false;
                }
                else {
                    dateOfBirthET.setError(null);
                }
            } catch (DateTimeParseException e) {
                dateOfBirthET.setError("Invalid date format (dd/MM/yyyy)");
                valid = false;
            }
        }


        // --- Validate Gender Spinner ---
        if (genderSpinner.getSelectedItemPosition() == 0) {
            genderErrorTV.setVisibility(View.VISIBLE);
            valid = false;
        } else {
            genderErrorTV.setVisibility(View.GONE);
        }

        return valid;
    }

    private boolean validateAccountDetails() {
        boolean valid = true;

        // Get and trim the text for each field
        String email = (emailET.getText() != null) ? emailET.getText().toString().trim() : "";
        String phone = (phoneET.getText() != null) ? phoneET.getText().toString().trim() : "";
        String pwd = (pwdET.getText() != null) ? pwdET.getText().toString().trim() : "";
        String pwdConfirm = (pwdConfirmET.getText() != null) ? pwdConfirmET.getText().toString().trim() : "";

        // Validate Email
        if (email.isEmpty()) {
            emailET.setError("Email is required");
            valid = false;
        } else {
            emailET.setError(null);
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                 emailET.setError("Enter a valid email address");
                 valid = false;
            }
        }

        // Validate Phone
        if (phone.isEmpty()) {
            phoneET.setError("Phone is required");
            valid = false;
        } else {
            phoneET.setError(null);
            // Additional phone validation rules can be added here if needed.
        }

        // Validate Password
        if (pwd.isEmpty()) {
            pwdET.setError("Password is required");
            valid = false;
        } else if (!pwdConfirm.isEmpty()) {
            if (!pwd.equals(pwdConfirm)) {
                pwdET.setError("Passwords do not match");
                pwdConfirmET.setError("Passwords do not match");
                valid = false;
            } else {
                pwdET.setError(null);
                pwdConfirmET.setError(null);
            }
        } else {
            pwdET.setError(null);
            pwdConfirmET.setError(null);
            // Additional password validation (e.g., length or complexity) can be added here.
        }

        return valid;
    }

    private boolean validateLifestyleGoal() {
        boolean valid = true;

        // Validate Goal RadioGroup (check that a radio button is selected)
        if (goalRG.getCheckedRadioButtonId() == -1) {
            goalErrorTV.setVisibility(View.VISIBLE);
            valid = false;
        } else {
            goalErrorTV.setVisibility(View.GONE);
        }

        // Validate Target Weight EditText If Needed
        if (loseWeightRB.isChecked() || gainWeightRB.isChecked()) {
            String targetWeightText = targetWeightET.getText() != null ? targetWeightET.getText().toString().trim() : "";
            if (targetWeightText.isEmpty()) {
                targetWeightET.setError("Target weight is required");
                valid = false;
            } else {
                targetWeightET.setError(null);
                try {
                    float targetWeight = Float.parseFloat(targetWeightText);
                    if (targetWeight < 40 || targetWeight > 250) {
                        targetWeightET.setError("Target weight must be between 40 and 300 kg");
                        valid = false;
                    }
                    else {
                        float weight;
                        if (state.equals(FIRST_LOG.getStateName()))
                            weight = Float.parseFloat(weightET.getText().toString());
                        else weight = userInfoHelper.getWeight();

                        if (loseWeightRB.isChecked() && targetWeight > weight) {
                            targetWeightET.setError("Target weight must be less than current weight");
                            valid = false;
                        }
                        else if (gainWeightRB.isChecked() && targetWeight < weight) {
                            targetWeightET.setError("Target weight must be greater than current weight");
                            valid = false;
                        }
                    }
                } catch (NumberFormatException e) {
                    targetWeightET.setError("Enter a valid number");
                    valid = false;
                }
            }
        }

        // Validate Activity Level Spinner
        if (activityLevelSpinner.getSelectedItemPosition() == 0) { // assuming position 0 is the default "Select..." entry
            activityLevelErrorTV.setVisibility(View.VISIBLE);
            valid = false;
        } else {
            activityLevelErrorTV.setVisibility(View.GONE);
        }

        // Validate Diet Type RadioGroup
        if (dietTypeRG.getCheckedRadioButtonId() == -1) {
            dietTypeErrorTV.setVisibility(View.VISIBLE);
            valid = false;
        } else {
            dietTypeErrorTV.setVisibility(View.GONE);
        }

        return valid;
    }

    private boolean validateAdjustGoal() {
        boolean valid = true;

        // Validate Adjust Calories
        String caloriesStr = adjustCaloriesET.getText() != null ? adjustCaloriesET.getText().toString().trim() : "";
        if (caloriesStr.isEmpty()) {
            adjustCaloriesET.setError("Adjust calories is required");
            valid = false;
        } else {
            try {
                int calories = Integer.parseInt(caloriesStr);
                if (calories < 0 || calories > 10000) {
                    adjustCaloriesET.setError("Calories must be between 0 and 10,000");
                    valid = false;
                }
            } catch (NumberFormatException e) {
                adjustCaloriesET.setError("Invalid number");
                valid = false;
            }
        }

        // Validate Adjust Carbs
        String carbsStr = adjustCarbsET.getText() != null ? adjustCarbsET.getText().toString().trim() : "";
        if (carbsStr.isEmpty()) {
            adjustCarbsET.setError("Adjust carbs is required");
            valid = false;
        } else {
            try {
                int carbs = Integer.parseInt(carbsStr);
                if (carbs < 0 || carbs > 1000) {
                    adjustCarbsET.setError("Carbs must be between 0 and 1000");
                    valid = false;
                }
            } catch (NumberFormatException e) {
                adjustCarbsET.setError("Invalid number");
                valid = false;
            }
        }

        // Validate Adjust Protein
        String proteinStr = adjustProteinET.getText() != null ? adjustProteinET.getText().toString().trim() : "";
        if (proteinStr.isEmpty()) {
            adjustProteinET.setError("Adjust protein is required");
            valid = false;
        } else {
            try {
                int protein = Integer.parseInt(proteinStr);
                if (protein < 0 || protein > 1000) {
                    adjustProteinET.setError("Protein must be between 0 and 1000");
                    valid = false;
                }
            } catch (NumberFormatException e) {
                adjustProteinET.setError("Invalid number");
                valid = false;
            }
        }

        // Validate Adjust Fat
        String fatStr = adjustFatET.getText() != null ? adjustFatET.getText().toString().trim() : "";
        if (fatStr.isEmpty()) {
            adjustFatET.setError("Adjust fat is required");
            valid = false;
        } else {
            try {
                int fat = Integer.parseInt(fatStr);
                if (fat < 0 || fat > 1000) {
                    adjustFatET.setError("Fat must be between 0 and 500");
                    valid = false;
                }
            } catch (NumberFormatException e) {
                adjustFatET.setError("Invalid number");
                valid = false;
            }
        }

        // Validate Adjust Water Intake
        String waterStr = adjustWaterIntakeET.getText() != null ? adjustWaterIntakeET.getText().toString().trim() : "";
        if (waterStr.isEmpty()) {
            adjustWaterIntakeET.setError("Adjust water intake is required");
            valid = false;
        } else {
            try {
                int water = Integer.parseInt(waterStr);
                if (water < 0 || water > 10000) {
                    adjustWaterIntakeET.setError("Water intake must be between 0 and 5000");
                    valid = false;
                }
            } catch (NumberFormatException e) {
                adjustWaterIntakeET.setError("Invalid number");
                valid = false;
            }
        }

        return valid;
    }

}