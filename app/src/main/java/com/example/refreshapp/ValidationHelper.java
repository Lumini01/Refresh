package com.example.refreshapp;

import android.util.Patterns;
import android.telephony.PhoneNumberUtils;

public class ValidationHelper {
//    public static String validateName(String name) {
//        if (name == null || name.trim().isEmpty()) {
//            return "Name is required!";
//        }
//        else if (name.length() < 2) {
//            return "Invalid Name";
//        }
//        return null;
//    }

    public static String validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "Name is required!";
        }
        else if (name.length() < 2) {
            return "Invalid Name";
        }
        return null;
    }

    public static String validatePwd(String pwdInput) {
        if (pwdInput == null || pwdInput.isEmpty()) {
            return "Password is required!";
        }
        else if (pwdInput.length() < 6) {
            return "Password must be at least 6 characters!";
        }
        return null;
    }

    public static String validatePwd(String pwdInput, String pwdActual) {
        if (pwdInput == null || pwdInput.isEmpty()) {
            return "Password is required!";
        }
        else if (pwdInput.length() < 6) {
            return "Password must be at least 6 characters!";
        }
        else if (!pwdInput.equals(pwdActual)) {
            return "Wrong password!";
        }
        return null;
    }

//    public static String validateEmail(String email) {
//        if (email == null || email.isEmpty()) {
//            return "Email is required!";
//        }
//        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//            return "Invalid email format!";
//        }
//        return null;
//    }

    public static String validateEmail(String email) {
        if (email == null || email.isEmpty()) {
            return "Email is required!";
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return "Invalid email format!";
        }
//        else if (helperDB.isEmailInUse(email)) {
//            return "Email is already in use!";
//        }
        return null;
    }

//    public static String validatePhone(String phone) {
//        if (phone == null || phone.isEmpty()) {
//            return "Phone number must be at least 10 digits!";
//        }
//        else if (PhoneNumberUtils.isGlobalPhoneNumber(phone)) {
//            return "Invalid phone number format";
//        }
//        return null;
//    }
//
//    public static String validatePhone(String phone) {
//        if (phone == null || phone.isEmpty()) {
//            return "Phone number must be at least 10 digits!";
//        }
//        else if (PhoneNumberUtils.isGlobalPhoneNumber(phone)) {
//            return "Invalid phone number format";
//        }
//        else if (helperDB.isPhoneInUse(phone)) {
//            return "Phone number is already in use!";
//        }
//        return null;
//    }

//    public static String validateLogin(String email, String pwdInput, String pwdActual) {
//        String emailError = validateEmail(email);
//        if (emailError != null) {
//            return emailError;
//        }
//        String passwordError = validatePwd(pwdInput, pwdActual);
//        if (passwordError != null) {
//            return passwordError;
//        }
//        return null;
//    }
//
//    public static String validateSignUp(String name, String password, String email, String phone, HelperDB helperDB) {
//        String nameError = validateName(name);
//        if (nameError != null) {
//            return nameError;
//        }
//        String passwordError = validatePwd(password);
//        if (passwordError != null) {
//            return passwordError;
//        }
//        String emailError = validateEmail(email);
//        if (emailError != null) {
//            return emailError;
//        }
//        String phoneError = validatePhone(phone);
//        if (phoneError != null) {
//            return phoneError;
//        }
//        return null;
//    }
}
