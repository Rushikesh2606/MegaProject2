package com.example.codebrains.freelancer;

public class Job {
    private String category;
    private String title;
    private String description;
    private String completedDate;

    public Job(String category, String title, String description, String completedDate) {
        this.category = category;
        this.title = title;
        this.description = description;
        this.completedDate = completedDate;
    }

    // Getters
    public String getCategory() { return category; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getCompletedDate() { return completedDate; }
}