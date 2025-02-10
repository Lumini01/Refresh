package com.example.refresh.Model;

import java.io.Serializable;

// Search Result Model Class which represents a search result
public class SearchResult implements Serializable {
    private String title;
    private String description;
    private String model;

    // Means - an actual object saved
    // TODO: make the refernce model general
    private int modelReference; // Optional: Add more fields as needed

    public SearchResult(String title) {
        this.title = title;
    }

    public SearchResult(String title, String description, String model, int modelReference) {
        this.title = title;
        this.description = description;
        this.model = model;
        this.modelReference = modelReference;
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

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getModelReference() {
        return modelReference;
    }

    public void setModelReference(int modelReference) {
        this.modelReference = modelReference;
    }
}
