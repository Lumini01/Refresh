package com.example.refresh.Model;

import android.content.Context;

import com.example.refresh.R;

import java.util.HashMap;
import java.util.Map;

public class NotificationTemplate {

    private int templateID;
    private String category;
    private String title;
    private String message;
    private int iconID;
    private Class<?> activityClass;

    // Create a map of resource names to resource IDs
    private static final Map<String, Integer> iconMap = new HashMap<>();

    static {
        iconMap.put("ic_placeholder", R.drawable.ic_placeholder);
        iconMap.put("ic_example", R.drawable.ic_today);
        // Add more mappings as needed
    }

    public NotificationTemplate(int templateID, String category, String title, String message, int iconID, Class<?> activityClass) {
        this.templateID = templateID;
        this.category = category;
        this.title = title;
        this.message = message;
        this.iconID = iconID;
        this.activityClass = activityClass;
    }

    public NotificationTemplate(Context context, int templateID, String category, String title, String message, String iconID, String activityClassName) {
        this.templateID = templateID;
        this.category = category;
        this.title = title;
        this.message = message;

        setIconID(context, iconID);
        setActivityClass(activityClassName);
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

    public void setIconID(Context context, String iconID) {
        Integer resId = iconMap.get(iconID);
        if (resId != null) {
            this.iconID = resId;  // Set the resource ID
        }
        else {
            try {
                this.iconID = context.getResources().getIdentifier(iconID, "drawable", context.getPackageName());
                if (this.iconID == 0) {
                    // Handle the case where the resource is not found
                    this.iconID = R.drawable.ic_placeholder; // Or some default icon
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
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
