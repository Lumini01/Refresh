package com.example.refresh.Model;

public class SearchResult {
    private String title;
    private String description; // Optional: Add more fields as needed

    public SearchResult(String title) {
        this.title = title;
    }

    public SearchResult(String title, String description) {
        this.title = title;
        this.description = description;
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
}
