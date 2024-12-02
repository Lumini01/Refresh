package com.example.refresh.Model;

public class NotificationInstance {

    private int instanceID;
    private int templateID;
    private String time;

    public NotificationInstance(int instanceID, int templateID, String time) {
        this.instanceID = instanceID;
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
