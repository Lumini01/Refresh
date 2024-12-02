package com.example.refresh.Model;

public class NotificationTemplate {

    private int templateID;
    private String category;
    private String title;
    private String message;
    private String icon;
    private String color;

    public NotificationTemplate(int templateID, String category, String title, String message, String icon, String color) {
        this.templateID = templateID;
        this.category = category;
        this.title = title;
        this.message = message;
        this.icon = icon;
        this.color = color;
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

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
