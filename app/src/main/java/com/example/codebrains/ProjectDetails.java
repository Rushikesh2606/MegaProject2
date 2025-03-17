package com.example.codebrains;

public class ProjectDetails {
    private String title;
    private String description;
    private String skills;

    public ProjectDetails(String title, String description, String skills) {
        this.title = title;
        this.description = description;
        this.skills = skills;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getSkills() { return skills; }
}