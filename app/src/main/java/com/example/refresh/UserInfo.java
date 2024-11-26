package com.example.refresh;

/**
 * UserInfo - This class is used to store and manage user information, including
 * their name, email, phone number, and password. It provides methods to access
 * and modify these details, and includes constructors for creating user objects
 * with different combinations of the user data.
 */
public class UserInfo {

    private String userName;   // Stores the user's name
    private String userEmail;  // Stores the user's email
    private String userPhone;  // Stores the user's phone number
    private String userPwd;    // Stores the user's password

    /**
     * Default constructor for creating a UserInfo object with no initial values.
     */
    public UserInfo() {}

    /**
     * Constructor to create a UserInfo object with all user details (name, email, phone, and password).
     *
     * @param userName The user's name
     * @param userEmail The user's email
     * @param userPhone The user's phone number
     * @param userPwd The user's password
     */
    public UserInfo(String userName, String userEmail, String userPhone, String userPwd) {
        setUserName(userName);
        setUserEmail(userEmail);
        setUserPhone(userPhone);
        setUserPwd(userPwd);
    }

    /**
     * Constructor to create a UserInfo object with only email and password.
     *
     * @param userEmail The user's email
     * @param userPwd The user's password
     */
    public UserInfo(String userEmail, String userPwd) {
        setUserName(null);  // User's name is optional
        setUserPwd(userPwd);
        setUserEmail(userEmail);
        setUserPhone(null);  // User's phone is optional
    }

    /**
     * Returns the user's name.
     *
     * @return The user's name
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Returns the user's email.
     *
     * @return The user's email
     */
    public String getUserEmail() {
        return userEmail;
    }

    /**
     * Returns the user's phone number.
     *
     * @return The user's phone number
     */
    public String getUserPhone() {
        return userPhone;
    }

    /**
     * Returns the user's password.
     *
     * @return The user's password
     */
    public String getUserPwd() {
        return userPwd;
    }

    /**
     * Sets the user's name.
     *
     * @param userName The name to be set
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Sets the user's email.
     *
     * @param userEmail The email to be set
     */
    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    /**
     * Sets the user's phone number.
     *
     * @param userPhone The phone number to be set
     */
    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    /**
     * Sets the user's password.
     *
     * @param userPwd The password to be set
     */
    public void setUserPwd(String userPwd) {
        this.userPwd = userPwd;
    }
}
