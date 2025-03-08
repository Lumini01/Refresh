package com.example.refresh.Model;

import java.io.Serializable;

// Search Result Model Class which represents a search result
public class ListItem<T> implements Serializable {
    private String title;
    private String description;

    // Means - an actual object saved
    private T model; // Optional: Add more fields as needed

    public ListItem(String title) {
        this.title = title;
    }

    public ListItem(String title, String description, T model) {
        this.title = title;
        this.description = description;
        this.model = model;
    }

    public ListItem(T model) {
        this.model = model;
    }

    // Getter and Setter for Title
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    // Getter and Setter for Description
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public T getModel() {
        return model;
    }

    public void setModel(T model) {
        this.model = model;
    }
}
