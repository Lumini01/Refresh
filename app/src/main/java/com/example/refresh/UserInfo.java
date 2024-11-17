package com.example.refresh;

public class UserInfo {
    private String userName;
    private String userEmail;
    private String userPhone;
    private String userPwd;


    public UserInfo() {}
    public UserInfo(String userName, String userEmail, String userPhone, String userPwd) {
        setUserName(userName);
        setUserEmail(userEmail);
        setUserPhone(userPhone);
        setUserPwd(userPwd);
    }

    public UserInfo(String userEmail, String userPwd) {
        setUserName(null);
        setUserPwd(userPwd);
        setUserEmail(userEmail);
        setUserPhone(null);
    }

    public String getUserName() {
        return userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public String getUserPwd() {
        return userPwd;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }
    public void setUserPwd(String userPwd) {
        this.userPwd = userPwd;
    }
}
