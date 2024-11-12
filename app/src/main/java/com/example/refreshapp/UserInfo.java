package com.example.refreshapp;

public class UserInfo {
    private String userName;
    private String userPwd;
    private String userEmail;
    private String userTel;


    public UserInfo() {}
    public UserInfo(String userName, String userPwd, String userEmail, String userTel) {
        setUserName(userName);
        setUserPwd(userPwd);
        setUserEmail(userEmail);
        setUserTel(userTel);
    }

    public UserInfo(String userEmail, String userPwd) {
        setUserName(null);
        setUserPwd(userPwd);
        setUserEmail(userEmail);
        setUserTel(null);
    }

    public String getUserName() {
        return userName;
    }

    public String getUserPwd() {
        return userPwd;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserTel() {
        return userTel;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
    public void setUserPwd(String userPwd) {
        this.userPwd = userPwd;
    }
    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
    public void setUserTel(String userTel) {
        this.userTel = userTel;
    }
}
