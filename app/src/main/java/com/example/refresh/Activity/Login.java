package com.example.refresh.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.refresh.Helper.DatabaseHelper;
import com.example.refresh.R;
import com.example.refresh.Start;
import com.example.refresh.Model.UserInfo;
import com.example.refresh.Model.ErrorMessage;
import com.example.refresh.Helper.ValidationHelper;

/**
 * Login Activity - This screen handles user login by allowing them to enter their email
 * and password. It validates the credentials, shows appropriate error messages, and
 * navigates to the HomeDashboard upon successful login. It also includes a sign-up button
 * to redirect to the registration screen and a logo that leads back to the Start activity.
 */

public class Login extends AppCompatActivity {

    // UI Components
    private TextView title;
    private EditText etEmailLogin;
    private EditText etPwdLogin;
    private Button btLogin;
    private CheckBox rememberMeCheckbox;
    private TextView btSignUp;
    private ImageView logo;

    // Database Helper
    private UserInfo user;
    private DatabaseHelper databaseHelper = new DatabaseHelper(this);
    private SQLiteDatabase db;

    /**
     * Initializes the activity, sets up UI components, and handles login and sign-up button clicks.
     * @param savedInstanceState Bundle containing the saved instance state for restoring the previous activity state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up edge-to-edge view and content
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // Handle window insets for proper padding on devices with system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI components
        initUIComponents();

        // Set up the actions for each button and image view
        setupListeners();

        // Hide the action bar for a clean UI
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    /**
     * Initialize all UI components.
     */
    private void initUIComponents() {
        etEmailLogin = findViewById(R.id.etEmailLogin);
        etPwdLogin = findViewById(R.id.etPwdLogin);
        btLogin = findViewById(R.id.btLogin);
        rememberMeCheckbox = findViewById(R.id.rememberMeCheckbox);
        btSignUp = findViewById(R.id.btSignUp);
        logo = findViewById(R.id.logo);
    }

    /**
     * Set up listeners for the login, signup, and logo buttons.
     */
    private void setupListeners() {
        // Login button listener
        btLogin.setOnClickListener(view -> handleLogin());

        // Sign Up button listener
        btSignUp.setOnClickListener(view -> navigateToSignUp());

        // Logo click listener
        logo.setOnClickListener(view -> navigateToStart());
    }

    /**
     * Handle the login process.
     */
    private void handleLogin() {
        // Set user credentials from the input fields

        UserInfo validationUser = new UserInfo();
        String userEmail = etEmailLogin.getText().toString();
        String userPwd = etPwdLogin.getText().toString();

        validationUser.setEmail(userEmail);
        validationUser.setPwd(userPwd);
        boolean rememberMe = rememberMeCheckbox.isChecked();

        // Validate login credentials
        ErrorMessage validationError = ValidationHelper.validateLogin(validationUser, databaseHelper);

        if (validationError == null) {
            // Successful login
            // Save user email to SharedPreferences
            user = new UserInfo(this ,userEmail, userPwd);
            if (rememberMe) {
                updateActiveUser();
            }

            replaceUserSP();

            showToast("Login Successful!");
            clearErrors();
            navigateToHomeDashboard();
        }
        else {
            // Show error messages based on validation failure
            showValidationError(validationError);
        }
    }

    /**
     * Show a toast message.
     */
    private void showToast(String message) {
        Toast toast = Toast.makeText(Login.this, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 100);
        toast.show();
    }

    /**
     * Clear error indicators on the input fields.
     */
    private void clearErrors() {
        etEmailLogin.setError(null);
        etPwdLogin.setError(null);
    }

    /**
     * Show error messages on the input fields based on validation failure.
     */
    private void showValidationError(ErrorMessage error) {
        switch (error.getField()) {
            case EMAIL:
                etEmailLogin.setError(error.getMessage());
                break;
            case PWD:
                etPwdLogin.setError(error.getMessage());
                break;
        }

        showToast("Unable to Log In.");
    }

    /**
     * Navigate to the sign-up activity.
     */
    private void navigateToSignUp() {
        Intent intent = new Intent(Login.this, SignUp.class);
        startActivity(intent);
    }

    /**
     * Navigate to the home dashboard.
     */
    private void navigateToHomeDashboard() {
        Intent intent = new Intent(Login.this, HomeDashboard.class);
        startActivity(intent);
        finish();
    }

    /**
     * Navigate to the start activity.
     */
    private void navigateToStart() {
        Intent intent = new Intent(Login.this, Start.class);
        startActivity(intent);
    }

    private void updateActiveUser() {
        SharedPreferences appPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = appPreferences.edit();

        editor.putInt("loggedUserID", user.getID());
        editor.apply();
    }

    private void replaceUserSP() {
        SharedPreferences appPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = appPreferences.edit();

        String loggedUserSPName = "User" + user.getID() + "Preferences";
        editor.putString("loggedUserSPName", loggedUserSPName);
        editor.putInt("loggedUserID", user.getID());
        editor.apply();
    }
}