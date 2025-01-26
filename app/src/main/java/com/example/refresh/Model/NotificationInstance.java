package com.example.refresh.Model;

// Notification Instance Model Class which represents a notification instance
public class NotificationInstance {

    // Attributes
    private int instanceID;
    private int templateID;
    private String time;

    // Constructors
    public NotificationInstance(int instanceID, int templateID, String time) {
        this.instanceID = instanceID;
        this.templateID = templateID;
        this.time = time;
    }

    public NotificationInstance(int templateID, String time) {
        this.instanceID = -1;
        this.templateID = templateID;
        this.time = time;
    }

    // Getters and Setters
    public int getInstanceID() {
        return instanceID;
    }

    public void setInstanceID(int instanceID) {
        this.instanceID = instanceID;
    }

    public int getTemplateID() {
        return templateID;
    }

    public void setTemplateID(int templateID) {
        this.templateID = templateID;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
