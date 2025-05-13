package com.example.refresh.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.refresh.Helper.DatabaseHelper;
import com.example.refresh.R;
import com.example.refresh.Model.User;
import com.example.refresh.Model.ErrorMessage;
import com.example.refresh.Helper.ValidationHelper;

/**
 * Login Activity - This screen handles user login by allowing them to enter their email
 * and password. It validates the credentials, shows appropriate error messages, and
 * navigates to the HomeDashboard upon successful login. It also includes a sign-up button
 * to redirect to the registration screen and a logo that leads back to the Start activity.
 */
public class LoginActivity extends AppCompatActivity {

    // UI Components
    private EditText emailET;
    private EditText pwdET;
    private Button loginBtn;
    private CheckBox rememberMeCB;
    private TextView signUpBtn;
    private ImageView toggleBtn;

    // Database Helper
    private final DatabaseHelper dbHelper = new DatabaseHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set up edge-to-edge view and content
        setupEdgeToEdge();

        // Initialize UI components
        initUIComponents();

        // Set up listeners for buttons and logo
        setupListeners();

        // Hide the action bar for a clean UI
        hideActionBar();

        final WindowInsetsController controller = getWindow().getInsetsController();
        if (controller != null) {
            // hide both status bar & navigation bar
            controller.hide(WindowInsets.Type.navigationBars());
            // allow swipe to temporarily reveal
            controller.setSystemBarsBehavior(
                    WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            );
        }
    }

    /**
     * Set up the edge-to-edge view for the activity.
     */
    private void setupEdgeToEdge() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    /**
     * Initialize all UI components.
     */
    private void initUIComponents() {
        emailET = findViewById(R.id.email_login_et);
        pwdET = findViewById(R.id.pwd_login_et);
        loginBtn = findViewById(R.id.login_btn);
        rememberMeCB = findViewById(R.id.remember_me_cb);
        signUpBtn = findViewById(R.id.sign_up_tv);
        toggleBtn = findViewById(R.id.pwd_login_toggle_btn);

        String emailExtra = getIntent().getStringExtra("userEmail");
        String pwdExtra = getIntent().getStringExtra("userPwd");
        if (emailExtra != null && pwdExtra != null) {
            emailET.setText(emailExtra);
            pwdET.setText(pwdExtra);
        }
    }

    /**
     * Set up listeners for the login, signup, and logo buttons.
     */
    private void setupListeners() {
        // Login button listener
        loginBtn.setOnClickListener(view -> handleLogin());

        // Sign Up button listener
        signUpBtn.setOnClickListener(view -> navigateToSignUp());

        final boolean[] showing = {false};
        toggleBtn.setOnClickListener(v -> {
            if (showing[0]) {
                // show password
                pwdET.setTransformationMethod(PasswordTransformationMethod.getInstance());
                toggleBtn.setImageResource(R.drawable.ic_show);
            } else {
                // hide password
                pwdET.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                toggleBtn.setImageResource(R.drawable.ic_hide);
            }
            showing[0] = !showing[0];
            // move cursor to the end
            pwdET.setSelection(pwdET.getText().length());
        });
    }

    /**
     * Handle the login process.
     */
    private void handleLogin() {
        // Get user credentials from input fields
        String userEmail = emailET.getText().toString().toLowerCase();
        String userPwd = pwdET.getText().toString();
        boolean rememberMe = rememberMeCB.isChecked();

        if (userEmail.isEmpty() || userPwd.isEmpty()) {
            showToast("Invalid Credentials.");
            emailET.setError("Enter Email");
            pwdET.setError("Enter Password");
            return;
        }

        // Validate login credentials
        User validationUser = new User(this, userEmail, userPwd);
        ErrorMessage validationError = ValidationHelper.validateLogin(validationUser, dbHelper);

        if (validationError == null) {
            // Successful login
            if (rememberMe) {
                updateActiveUser(new User(this, userEmail, userPwd));
            }
            showToast("Login Successful!");
            clearErrors();
            navigateToHomeDashboard();
        } else {
            // Show validation error
            showValidationError(validationError);
        }
    }

    /**
     * Show a toast message.
     */
    private void showToast(String message) {
        Toast toast = Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 100);
        toast.show();
    }

    /**
     * Clear error indicators on the input fields.
     */
    private void clearErrors() {
        emailET.setError(null);
        pwdET.setError(null);
    }

    /**
     * Show error messages on the input fields based on validation failure.
     */
    private void showValidationError(ErrorMessage error) {
        switch (error.getField()) {
            case EMAIL:
                emailET.setError(error.getMessage());
                break;
            case PWD:
                pwdET.setError(error.getMessage());
                break;
        }
        showToast("Unable to Log In.");
    }

    /**
     * Navigate to the sign-up activity.
     */
    private void navigateToSignUp() {
        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(intent);
    }

    /**
     * Navigate to the home dashboard.
     */
    private void navigateToHomeDashboard() {
        Intent intent = new Intent(LoginActivity.this, HomeDashboardActivity.class);
        if (getIntent().getExtras() != null) {
            intent.putExtra("firstLog", getIntent().getBooleanExtra("firstLog", false));
        }
        startActivity(intent);
        finish();
    }

    /**
     * Update the active user in SharedPreferences.
     */
    private void updateActiveUser(User user) {
        SharedPreferences appPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = appPreferences.edit();
        editor.putInt("loggedUserID", user.getID());
        editor.apply();
        replaceUserSP(user);
    }

    /**
     * Replace the user's SharedPreferences with the logged-in user's information.
     */
    private void replaceUserSP(User user) {
        SharedPreferences appPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = appPreferences.edit();
        String loggedUserSPName = "User" + user.getID() + "Preferences";
        editor.putString("loggedUserSPName", loggedUserSPName);
        editor.putInt("loggedUserID", user.getID());
        editor.apply();
    }

    /**
     * Hide the action bar for a clean UI.
     */
    private void hideActionBar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }
}
