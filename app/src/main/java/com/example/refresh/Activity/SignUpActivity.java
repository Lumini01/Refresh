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
import android.widget.EditText;
import android.widget.ImageButton;
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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * SignUp Activity - Handles user registration. Allows users to enter details and validates inputs.
 * If valid, registers the user and navigates to the login screen.
 */
public class SignUpActivity extends AppCompatActivity {

    // UI Elements
    private EditText nameET;
    private EditText emailET;
    private EditText phoneET;
    private EditText pwdET;
    private EditText pwdConfET;
    private Button signUpBtn;
    private TextView loginBtn;
    private ImageButton pwdToggleBtn;
    private ImageButton pwdConfToggleBtn;

    // User object and DB helper
    private final User user = new User();
    private final DatabaseHelper dbHelper = new DatabaseHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize UI components
        initializeUI();

        // Adjust layout for system bars
        applyWindowInsets();

        // SetUp listeners for buttons
        setUpListeners();

        // Hide the action bar for a clean UI
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

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

    // UI Initialization
    private void initializeUI() {
        nameET = findViewById(R.id.name_sign_up_et);
        emailET = findViewById(R.id.email_sign_up_et);
        phoneET = findViewById(R.id.phone_sign_up_et);
        pwdET = findViewById(R.id.pwd_sign_up_et);
        pwdConfET = findViewById(R.id.pwd_conf_sign_up_et);
        signUpBtn = findViewById(R.id.sign_up_btn);
        loginBtn = findViewById(R.id.login_tv);
        pwdToggleBtn = findViewById(R.id.pwd_sign_up_toggle_btn);
        pwdConfToggleBtn = findViewById(R.id.pwd_conf_sign_up_toggle_btn);
    }

    // Window insets for system bars
    private void applyWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Set up listener for the Sign Up button
    private void setUpListeners() {
        signUpBtn.setOnClickListener(view -> {
            collectUserInput();
            ErrorMessage validationError = validateInput();

            if (validationError == null) {
                handleSuccessfulSignUp();
            } else {
                handleValidationError(validationError);
            }
        });

        loginBtn.setOnClickListener(view -> navigateToLogin(false));

        final boolean[] showingPwd = {false};
        pwdToggleBtn.setOnClickListener(v -> {
            if (showingPwd[0]) {
                // show password
                pwdET.setTransformationMethod(PasswordTransformationMethod.getInstance());
                pwdToggleBtn.setImageResource(R.drawable.ic_show);
            } else {
                // hide password
                pwdET.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                pwdToggleBtn.setImageResource(R.drawable.ic_hide);
            }
            showingPwd[0] = !showingPwd[0];
            // move cursor to the end
            pwdET.setSelection(pwdET.getText().length());
        });

        final boolean[] showingPwdConf = {false};
        pwdConfToggleBtn.setOnClickListener(v -> {
            if (showingPwdConf[0]) {
                // show password
                pwdConfET.setTransformationMethod(PasswordTransformationMethod.getInstance());
                pwdConfToggleBtn.setImageResource(R.drawable.ic_show);
            } else {
                // hide password
                pwdConfET.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                pwdConfToggleBtn.setImageResource(R.drawable.ic_hide);
            }
            showingPwdConf[0] = !showingPwdConf[0];
            // move cursor to the end
            pwdConfET.setSelection(pwdConfET.getText().length());
        });
    }

    // Collect user input from form fields
    private void collectUserInput() {
        user.setName(nameET.getText().toString());
        user.setEmail(emailET.getText().toString().toLowerCase());
        user.setPhone(phoneET.getText().toString());
        user.setPwd(pwdET.getText().toString());
    }
    // Validate input fields

    private ErrorMessage validateInput() {
        String pwdConf = pwdConfET.getText().toString();
        return ValidationHelper.validateSignUp(user, pwdConf, dbHelper);
    }
    // Handle successful sign-up

    private void handleSuccessfulSignUp() {
        int id = dbHelper.insert(DatabaseHelper.Tables.USERS, user);
        if (id != -1) {
            showToast("Signup Successful!");

            user.setID(id);
            clearInputErrors();
            createUserSP();
            navigateToLogin(true);
        } else {
            showToast("Unexpected Signup Error.");
        }
    }
    // Clear error messages from input fields

    private void clearInputErrors() {
        nameET.setError(null);
        emailET.setError(null);
        phoneET.setError(null);
        pwdET.setError(null);
    }
    // Show a toast message

    private void showToast(String message) {
        Toast toast = Toast.makeText(SignUpActivity.this, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 100);
        toast.show();
    }
    // Handle validation errors

    private void handleValidationError(ErrorMessage validate) {
        switch (validate.getField()) {
            case NAME:
                nameET.setError(validate.getMessage());
                break;
            case EMAIL:
                emailET.setError(validate.getMessage());
                break;
            case PHONE:
                phoneET.setError(validate.getMessage());
                break;
            case PWD:
                pwdET.setError(validate.getMessage());
                break;
            case PWD_CONFIRM:
                pwdET.setError(validate.getMessage());
                pwdConfET.setError(validate.getMessage());
                break;
        }
        showToast("Input Error - Signup Not Completed.");
    }
    // Set up listener for the Login button

    // Navigate to the Login screen
    private void navigateToLogin(Boolean signedUp) {
        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
        if (signedUp) {
            intent.putExtra("firstLog", true);
            intent.putExtra("userEmail", user.getEmail());
            intent.putExtra("userPwd", user.getPwd());
        }

        startActivity(intent);
        finish();
    }

    // Create SharedPreferences for the new user
    private void createUserSP() {
        String userSPName = "User" + user.getID() + "Preferences";

        SharedPreferences userPreferences = getSharedPreferences(userSPName, MODE_PRIVATE);
        SharedPreferences.Editor userEditor = userPreferences.edit();

        userEditor.putInt("userID", user.getID());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formattedDate = LocalDate.now().format(formatter);
        userEditor.putString("startDate", formattedDate);
        userEditor.apply();
    }
}
