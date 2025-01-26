package com.example.refresh.Model;

import java.io.Serializable;

// Search Result Model Class which represents a search result
public class SearchResult implements Serializable {
    private String title;
    private String description;
    private String type;
    private String modelReference; // Optional: Add more fields as needed

    public SearchResult(String title) {
        this.title = title;
    }

    public SearchResult(String title, String description, String type, String modelReference) {
        this.title = title;
        this.description = description;
        this.type = type;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getModelReference() {
        return modelReference;
    }

    public void setModelReference(String modelReference) {
        this.modelReference = modelReference;
    }
}
