package com.example.codebrains.model;

public class User {
    private String firstName;
    String id;
    private String lastName;
    private String profileImage; // Base64 encoded string
    private long timestamp;

    public User() {
    }

    // Constructor
    public User(String id,String firstName, String lastName, String profileImage, long timestamp) {
        this.id=id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.profileImage = profileImage;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // Getters & Setters
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
