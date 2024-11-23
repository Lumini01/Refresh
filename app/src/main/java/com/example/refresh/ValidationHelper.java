package com.example.refresh;

import static com.example.refresh.UserCols.*;

import android.database.sqlite.SQLiteDatabase;
import android.util.Patterns;
import android.telephony.PhoneNumberUtils;

public class ValidationHelper {
    public static String registerName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "Name is required!";
        }
        else if (name.length() < 2) {
            return "Invalid Name.";
        }
        return null;
    }

    public static String validateName(String name, HelperDB helperDB, SQLiteDatabase db) {
        if (name == null || name.trim().isEmpty()) {
            return "Name is required!";
        }
        else if (name.length() < 2) {
            return "Invalid Name.";
        }
        else if (helperDB.existsInDB(NAME, name, db) == -1) {
            return "Name not found.";
        }
        return null;
    }

    public static String registerEmail(String email, HelperDB helperDB, SQLiteDatabase db) {
        if (email == null || email.isEmpty()) {
            return "Email is required!";
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return "Invalid email format!";
        }
        else if (helperDB.existsInDB(EMAIL, email, db) != -1) {
            return "Email is already in use!";
        }
        return null;
    }

    public static String validateEmail(String email, HelperDB helperDB, SQLiteDatabase db) {
        if (email == null || email.isEmpty()) {
            return "Email is required!";
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return "Invalid email format!";
        }
        else if (helperDB.existsInDB(EMAIL, email, db) == -1) {
            return "Account not found.";
        }
        return null;
    }

    public static String registerPhone(String phone, HelperDB helperDB, SQLiteDatabase db) {
        if (phone == null || phone.isEmpty()) {
            return "Phone number must be at least 10 digits!";
        }
        else if (!PhoneNumberUtils.isGlobalPhoneNumber(phone)) {
            return "Invalid phone number format.";
        }
        else if (helperDB.existsInDB(PHONE, phone, db) != -1) {
            return "Phone number is already in use!";
        }
        return null;
    }

    public static String validatePhone(String phone, HelperDB helperDB, SQLiteDatabase db) {
        if (phone == null || phone.isEmpty()) {
            return "Phone number must be at least 10 digits!";
        }
        else if (PhoneNumberUtils.isGlobalPhoneNumber(phone)) {
            return "Invalid phone number format.";
        }
        else if (helperDB.existsInDB(PHONE, phone, db) == -1) {
            return "Phone number not found.";
        }
        return null;
    }

    public static String registerPwd(String pwd, String pwdConf) {
        if (pwd == null || pwd.isEmpty()) {
            return "Password is required!";
        }
        else if (pwd.length() < 6) {
            return "Password must be at least 6 characters!";
        }
        else if (!pwd.equals(pwdConf)) {
            return "Passwords Do Not Match.";
        }
        return null;
    }

    public static String validatePwd(String pwd, String email, HelperDB helperDB, SQLiteDatabase db) {
        if (pwd == null || pwd.isEmpty()) {
            return "Password is required!";
        }
        else if (pwd.length() < 6) {
            return "Password must be at least 6 characters!";
        }
        else if (helperDB.existsInDB(PWD, pwd, db) == -1) {
            return "Wrong password!";
        }
        else if (!pwd.equals(helperDB.getFromRecord(helperDB.existsInDB(PWD, pwd, db), PWD, db))) {
            return "Wrong password!";
        }
        return null;
    }

    public static ErrorMessage validateLogin(UserInfo user, HelperDB helperDB, SQLiteDatabase db) {
        String emailError = validateEmail(user.getUserEmail(), helperDB, db);
        if (emailError != null) {
            return new ErrorMessage(EMAIL, emailError);
        }
        String pwdError = validatePwd(user.getUserPwd(), user.getUserEmail(), helperDB, db);
        if (pwdError != null) {
            return new ErrorMessage(PWD, pwdError);
        }
        return null;
    }

    public static ErrorMessage validateSignUp(UserInfo user, String pwdConf, HelperDB helperDB, SQLiteDatabase db) {
        String nameError = registerName(user.getUserName());
        if (nameError != null) {
            return new ErrorMessage(NAME, nameError);
        }
        String emailError = registerEmail(user.getUserEmail(), helperDB, db);
        if (emailError != null) {
            return new ErrorMessage(EMAIL, emailError);
        }
        String phoneError = registerPhone(user.getUserPhone(), helperDB, db);
        if (phoneError != null) {
            return new ErrorMessage(PHONE, phoneError);
        }
        String pwdError = registerPwd(user.getUserPwd(), pwdConf);
        if (pwdError != null) {
            if (!pwdError.equals("Passwords Do Not Match.")) {
                return new ErrorMessage(PWD, pwdError);
            }
            return new ErrorMessage(PWDCONF, pwdError);
        }
        return null;
    }
}
