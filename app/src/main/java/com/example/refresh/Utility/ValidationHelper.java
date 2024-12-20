package com.example.refresh.Utility;

import static com.example.refresh.Database.DatabaseHelper.Tables.*;
import static com.example.refresh.Database.Tables.UsersTable.Columns.*;

import android.util.Patterns;
import android.telephony.PhoneNumberUtils;

import com.example.refresh.Database.DatabaseHelper;
import com.example.refresh.Model.UserInfo;

/**
 * Utility class to handle validation logic for user signup and login.
 */
public class ValidationHelper {

    /**
     * Validates the user name during registration.
     * @param name The name to validate.
     * @return Error message if validation fails, null otherwise.
     */
    public static String registerName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "Name is required!";
        } else if (name.length() < 2) {
            return "Invalid Name.";
        }
        return null;
    }

    /**
     * Validates the user name during login.
     * @param name The name to validate.
     * @param databaseHelper The database helper.
     * @return Error message if validation fails, null otherwise.
     */
    public static String validateName(String name, DatabaseHelper databaseHelper) {
        if (name == null || name.trim().isEmpty()) {
            return "Name is required!";
        } else if (name.length() < 2) {
            return "Invalid Name.";
        } else if (databaseHelper.existsInDB(USERS, NAME, name) == -1) {
            return "Name not found.";
        }
        return null;
    }

    /**
     * Validates the email during registration.
     * @param email The email to validate.
     * @param databaseHelper The database helper.
     * @return Error message if validation fails, null otherwise.
     */
    public static String registerEmail(String email, DatabaseHelper databaseHelper) {
        if (email == null || email.isEmpty()) {
            return "Email is required!";
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return "Invalid email format!";
        } else if (databaseHelper.existsInDB(USERS, EMAIL, email) != -1) {
            return "Email is already in use!";
        }
        return null;
    }

    /**
     * Validates the email during login.
     * @param email The email to validate.
     * @param databaseHelper The database helper.
     * @return Error message if validation fails, null otherwise.
     */
    public static String validateEmail(String email, DatabaseHelper databaseHelper) {
        if (email == null || email.isEmpty()) {
            return "Email is required!";
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return "Invalid email format!";
        } else if (databaseHelper.existsInDB(USERS, EMAIL, email) == -1) {
            return "Account not found.";
        }
        return null;
    }

    /**
     * Validates the phone number during registration.
     * @param phone The phone number to validate.
     * @param databaseHelper The database helper.
     * @return Error message if validation fails, null otherwise.
     */
    public static String registerPhone(String phone, DatabaseHelper databaseHelper) {
        if (phone == null || phone.isEmpty()) {
            return "Phone number must be at least 10 digits!";
        } else if (!PhoneNumberUtils.isGlobalPhoneNumber(phone)) {
            return "Invalid phone number format.";
        } else if (databaseHelper.existsInDB(USERS, PHONE, phone) != -1) {
            return "Phone number is already in use!";
        }
        return null;
    }

    /**
     * Validates the phone number during login.
     * @param phone The phone number to validate.
     * @param databaseHelper The database helper.
     * @return Error message if validation fails, null otherwise.
     */
    public static String validatePhone(String phone, DatabaseHelper databaseHelper) {
        if (phone == null || phone.isEmpty()) {
            return "Phone number must be at least 10 digits!";
        } else if (PhoneNumberUtils.isGlobalPhoneNumber(phone)) {
            return "Invalid phone number format.";
        } else if (databaseHelper.existsInDB(USERS, PHONE, phone) == -1) {
            return "Phone number not found.";
        }
        return null;
    }

    /**
     * Validates the password during registration.
     * @param pwd The password to validate.
     * @param pwdConf The confirmed password to check against.
     * @return Error message if validation fails, null otherwise.
     */
    public static String registerPwd(String pwd, String pwdConf) {
        if (pwd == null || pwd.isEmpty()) {
            return "Password is required!";
        } else if (pwd.length() < 6) {
            return "Password must be at least 6 characters!";
        } else if (!pwd.equals(pwdConf)) {
            return "Passwords Do Not Match.";
        }
        return null;
    }

    /**
     * Validates the password during login.
     * @param pwd The password to validate.
     * @param email The email to validate against.
     * @param databaseHelper The database helper.
     * @return Error message if validation fails, null otherwise.
     */
    public static String validatePwd(String pwd, String email, DatabaseHelper databaseHelper) {
        int index = databaseHelper.existsInDB(USERS, EMAIL, email);

        if (pwd == null || pwd.isEmpty()) {
            return "Password is required!";
        } else if (pwd.length() < 6) {
            return "Password must be at least 6 characters!";
        } else if (index == -1) {
            return "Wrong password!";
        } else if (!pwd.equals(databaseHelper.getFromRecord(USERS, PWD, index))) {
            return "Wrong password!";
        }
        return null;
    }

    /**
     * Validates the login credentials.
     * @param user The user info to validate.
     * @param databaseHelper The database helper.
     * @return Error message if validation fails, null otherwise.
     */
    public static ErrorMessage validateLogin(UserInfo user, DatabaseHelper databaseHelper) {
        String emailError = validateEmail(user.getEmail(), databaseHelper);
        if (emailError != null) {
            return new ErrorMessage(EMAIL, emailError);
        }
        String pwdError = validatePwd(user.getPwd(), user.getEmail(), databaseHelper);
        if (pwdError != null) {
            return new ErrorMessage(PWD, pwdError);
        }
        return null;
    }

    /**
     * Validates the signup information.
     * @param user The user info to validate.
     * @param pwdConf The confirmed password to check against.
     * @param databaseHelper The database helper.
     * @return Error message if validation fails, null otherwise.
     */
    public static ErrorMessage validateSignUp(UserInfo user, String pwdConf, DatabaseHelper databaseHelper) {
        String nameError = registerName(user.getName());
        if (nameError != null) {
            return new ErrorMessage(NAME, nameError);
        }
        String emailError = registerEmail(user.getEmail(), databaseHelper);
        if (emailError != null) {
            return new ErrorMessage(EMAIL, emailError);
        }
        String phoneError = registerPhone(user.getPhone(), databaseHelper);
        if (phoneError != null) {
            return new ErrorMessage(PHONE, phoneError);
        }
        String pwdError = registerPwd(user.getPwd(), pwdConf);
        if (pwdError != null) {
            if (!pwdError.equals("Passwords Do Not Match.")) {
                return new ErrorMessage(PWD, pwdError);
            }
            return new ErrorMessage(PWD_CONFIRM, pwdError);
        }
        return null;
    }
}
