package com.example.refresh.Database;

// Notification class to store notification details
public class Notification {
    private int notificationID;
    private String notificationTitle;
    private String notificationMessage;
    private String notificationIcon;

    public Notification(int notificationID, String notificationTitle, String notificationMessage, String notificationIcon) {
        this.notificationID = notificationID;
        this.notificationTitle = notificationTitle;
        this.notificationMessage = notificationMessage;
        this.notificationIcon = notificationIcon;
    }

    public void setNotificationID(int notificationID) {
        this.notificationID = notificationID;
    }

    public void setNotificationTitle(String notificationTitle) {
        this.notificationTitle = notificationTitle;
    }

    public void setNotificationMessage(String notificationMessage) {
        this.notificationMessage = notificationMessage;
    }

    public void setNotificationIcon(String notificationIcon) {
        this.notificationIcon = notificationIcon;
    }

    public int getNotificationID() {
        return notificationID;
    }

    public String getNotificationTitle() {
        return notificationTitle;
    }

    public String getNotificationMessage() {
        return notificationMessage;
    }

    public String getNotificationIcon() {
        return notificationIcon;
    }
}
