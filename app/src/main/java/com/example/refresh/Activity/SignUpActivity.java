package com.example.refresh.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
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
import com.example.refresh.StartActivity;
import com.example.refresh.Model.User;
import com.example.refresh.Model.ErrorMessage;
import com.example.refresh.Helper.ValidationHelper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * SignUp Activity - This screen handles user registration by allowing them to enter
 * their name, email, phone number, and password. It validates the input fields,
 * shows appropriate error messages, and registers the user upon successful validation.
 * It also includes a login button to redirect to the login screen and a logo that
 * leads back to the Start activity.
 */
public class SignUpActivity extends AppCompatActivity {

    // UI Elements
    private TextView title;
    private EditText etNameSignUp, etEmailSignUp, etPhoneSignUp, etPwdSignUp, etPwdConfSignUp;
    private Button btSignUp;
    private TextView btLogin;
    private ImageView logo;

    // User object and DB helper
    private final User user = new User();
    private final DatabaseHelper databaseHelper = new DatabaseHelper(this);
    private SQLiteDatabase db;

    /**
     * Initialize UI components and set up listeners.
     *
     * @param savedInstanceState Contains activity state data.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);

        initializeUI();
        applyWindowInsets();
        setUpSignUpButton();
        setUpLoginButton();
        setUpLogoClickListener();

        // Hide the action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    /**
     * Initialize all UI components.
     */
    private void initializeUI() {
        etNameSignUp = findViewById(R.id.name_sign_up_et);
        etEmailSignUp = findViewById(R.id.email_sign_up_et);
        etPhoneSignUp = findViewById(R.id.phone_sign_up_et);
        etPwdSignUp = findViewById(R.id.pwd_sign_up_et);
        etPwdConfSignUp = findViewById(R.id.pwd_conf_sign_up_et);
        btSignUp = findViewById(R.id.sign_up_btn);
        btLogin = findViewById(R.id.login_tv);
        logo = findViewById(R.id.logo);
    }

    /**
     * Adjust the layout to avoid UI overlap with system bars.
     */
    private void applyWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    /**
     * Set up the sign-up button listener to handle user input and registration.
     */
    private void setUpSignUpButton() {
        btSignUp.setOnClickListener(view -> {
            collectUserInput();
            ErrorMessage validationError = validateInput();

            if (validationError == null) {
                handleSuccessfulSignUp();
            } else {
                handleValidationError(validationError);
            }
        });
    }

    /**
     * Collect user input from the form fields.
     */
    private void collectUserInput() {
        user.setName(etNameSignUp.getText().toString());
        user.setEmail(etEmailSignUp.getText().toString());
        user.setPhone(etPhoneSignUp.getText().toString());
        user.setPwd(etPwdSignUp.getText().toString());
    }

    /**
     * Validate the user input.
     *
     * @return Validation error or null if input is valid.
     */
    private ErrorMessage validateInput() {
        String pwdConf = etPwdConfSignUp.getText().toString();
        return ValidationHelper.validateSignUp(user, pwdConf, databaseHelper);
    }

    /**
     * Handle successful sign-up and navigate to the login screen.
     */
    private void handleSuccessfulSignUp() {
        if (databaseHelper.insert(DatabaseHelper.Tables.USERS, user) != -1) {
            showToast("Signup Successful!");

            // Clear input errors and navigate to Login activity
            clearInputErrors();
            createUserSP();
            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
            intent.putExtra("firstLog", true);
            startActivity(intent);
        } else {
            showToast("Unexpected Signup Error.");
        }
    }

    /**
     * Clear any displayed error messages on input fields.
     */
    private void clearInputErrors() {
        etNameSignUp.setError(null);
        etEmailSignUp.setError(null);
        etPhoneSignUp.setError(null);
        etPwdSignUp.setError(null);
    }

    /**
     * Show a toast message with the provided text.
     *
     * @param message Message to display in the toast.
     */
    private void showToast(String message) {
        Toast toast = Toast.makeText(SignUpActivity.this, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 100);
        toast.show();
    }

    /**
     * Handle validation errors by displaying the corresponding error message.
     *
     * @param validate The validation error.
     */
    private void handleValidationError(ErrorMessage validate) {
        switch (validate.getField()) {
            case NAME:
                etNameSignUp.setError(validate.getMessage());
                break;
            case EMAIL:
                etEmailSignUp.setError(validate.getMessage());
                break;
            case PHONE:
                etPhoneSignUp.setError(validate.getMessage());
                break;
            case PWD:
                etPwdSignUp.setError(validate.getMessage());
                break;
            case PWD_CONFIRM:
                etPwdSignUp.setError(validate.getMessage());
                etPwdConfSignUp.setError(validate.getMessage());
                break;
        }
        showToast("Input Error - Signup Not Completed.");
    }

    /**
     * Set up the login button listener to navigate to the Login screen.
     */
    private void setUpLoginButton() {
        btLogin.setOnClickListener(view -> {
            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            finish();
        });
    }

    /**
     * Set up the logo click listener to navigate to the Start screen.
     */
    private void setUpLogoClickListener() {
        logo.setOnClickListener(view -> startActivity(new Intent(SignUpActivity.this, StartActivity.class)));
    }

    private void createUserSP() {
        String userSPName = "User" + user.getID() + "Preferences";

        // Create a new SharedPreferences file for the logged-in user
        SharedPreferences userPreferences = getSharedPreferences(userSPName, MODE_PRIVATE);

        SharedPreferences.Editor userEditor = userPreferences.edit();
        userEditor.putInt("userID", user.getID());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formattedDate = LocalDate.now().format(formatter);
        userEditor.putString("startDate", formattedDate);
        userEditor.apply();
    }
}
