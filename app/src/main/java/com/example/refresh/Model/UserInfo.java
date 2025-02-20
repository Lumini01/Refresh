package com.example.refresh.Model;

import android.content.Context;

import com.example.refresh.Helper.DatabaseHelper;
import com.example.refresh.Helper.DatabaseHelper.Tables;
import com.example.refresh.Database.UsersTable.Columns;

/**
 * UserInfo - This class is used to store and manage user information, including
 * their name, email, phone number, and password. It provides methods to access
 * and modify these details, and includes constructors for creating user objects
 * with different combinations of the user data.
 */
public class UserInfo {

    private int id;        // Stores the user's unique identifier
    private String name;   // Stores the user's name
    private String email;  // Stores the user's email
    private String phone;  // Stores the user's phone number
    private String pwd;    // Stores the user's password

    /**
     * Default constructor for creating a UserInfo object with no initial values.
     */
    public UserInfo() {}

    /**
     * Constructor to create a UserInfo object with all user details (name, email, phone, and password).
     *
     * @param name The user's name
     * @param email The user's email
     * @param phone The user's phone number
     * @param pwd The user's password
     */
    public UserInfo(int id, String name, String email, String phone, String pwd) {
        setID(id);
        setName(name);
        setEmail(email);
        setPhone(phone);
        setPwd(pwd);
    }

    public UserInfo( String name, String email, String phone, String pwd) {
        id = -1;
        setName(name);
        setEmail(email);
        setPhone(phone);
        setPwd(pwd);
    }

    /**
     * Constructor to create a UserInfo object with only email and password.
     *
     * @param email The user's email
     * @param pwd The user's password
     */
    public UserInfo(Context context, String email, String pwd) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);

        setID(dbHelper.getFromRecordByValue(Tables.USERS, Columns.ID, Columns.EMAIL, email));
        setName(dbHelper.getFromRecordByValue(Tables.USERS, Columns.NAME, Columns.EMAIL, email));  // User's name is optional
        setPwd(pwd);
        setEmail(email);
        setPhone(dbHelper.getFromRecordByValue(Tables.USERS, Columns.PHONE, Columns.EMAIL, email));  // User's phone is optional
    }

    public int getID() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }

    /**
     * Returns the user's name.
     *
     * @return The user's name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the user's email.
     *
     * @return The user's email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Returns the user's phone number.
     *
     * @return The user's phone number
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Returns the user's password.
     *
     * @return The user's password
     */
    public String getPwd() {
        return pwd;
    }

    /**
     * Sets the user's name.
     *
     * @param name The name to be set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the user's email.
     *
     * @param email The email to be set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Sets the user's phone number.
     *
     * @param phone The phone number to be set
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Sets the user's password.
     *
     * @param pwd The password to be set
     */
    public void setPwd(String pwd) {
        this.pwd = pwd;
    }
}
