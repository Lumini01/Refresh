package com.example.refresh.Model;

import android.content.Context;

public class NotificationTemplate {

    private int templateID;
    private String category;
    private String title;
    private String message;
    private int iconID;
    private Class<?> activityClass;

    public NotificationTemplate(int templateID, String category, String title, String message, int iconID, Class<?> activityClass) {
        this.templateID = templateID;
        this.category = category;
        this.title = title;
        this.message = message;
        this.iconID = iconID;
        this.activityClass = activityClass;
    }

    public NotificationTemplate(int templateID, String category, String title, String message, String iconID, String activityClassName) {
        this.templateID = templateID;
        this.category = category;
        this.title = title;
        this.message = message;
        try {
            this.iconID = Integer.parseInt(iconID);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        try {
            this.activityClass = Class.forName(activityClassName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // Getters and Setters
    public int getTemplateID() {
        return templateID;
    }

    public void setTemplateID(int templateID) {
        this.templateID = templateID;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getIconID() {
        return iconID;
    }

    public String getIconIDName(Context context) {
        return context.getResources().getResourceEntryName(iconID);
    }

    public void setIconID(int iconID) {
        this.iconID = iconID;
    }

    public void setIconID(String iconID) {
        try {
            this.iconID = Integer.parseInt(iconID);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public Class<?> getActivityClass() {
        return activityClass;
    }

    public String getActivityClassName() {
        return activityClass.getName();
    }

    public void setActivityClass(Class<?> activityClass) {
        this.activityClass = activityClass;
    }

    public void setActivityClass(String activityClass) {
        try {
            this.activityClass = Class.forName(activityClass);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
