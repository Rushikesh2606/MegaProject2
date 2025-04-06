package com.example.codebrains.model;

import java.util.HashMap;
import java.util.Map;

public class Freelancer {
    // Fields
    String id;
    String profession;
    String desc;
    String passout;
    String degree;
    String language;
    String institute;
    String availability;
    String skills;
    String tagLine;
    String tools;
    String yearsOfExperience;
    String firstName;
    String lastName;
    String email;
    String password;
    String country;
    String username;
    String dob;
    String gender;
    String contactNo;
    int pending;
    int in_progress;
    int completed;
    int total_jobs;
    long timestamp;
    float rating;
    String profileImage;

    // Default constructor
    public Freelancer() {}
    // Add this inside your Freelancer.java
    public Map<String, Object> toMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("passout", passout);
        map.put("degree", degree);
        map.put("language", language);
        map.put("institute", institute);
        map.put("availability", availability);
        return map;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public int getPending() {
        return pending;
    }

    public void setPending(int pending) {
        this.pending = pending;
    }

    public int getIn_progress() {
        return in_progress;
    }

    public void setIn_progress(int in_progress) {
        this.in_progress = in_progress;
    }

    public int getCompleted() {
        return completed;
    }

    public void setCompleted(int completed) {
        this.completed = completed;
    }

    public int getTotal_jobs() {
        return total_jobs;
    }

    public void setTotal_jobs(int total_jobs) {
        this.total_jobs = total_jobs;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    // Constructor for initial registration data
    public Freelancer(String id, String firstName, String lastName, String email, String password, String country, String username, String dob, String gender, String contactNo, String profileImage) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.country = country;
        this.username = username;
        this.dob = dob;
        this.gender = gender;
        this.contactNo = contactNo;
        this.profileImage = profileImage;
    }

    // Constructor for additional data from freelancer_form1
    public Freelancer( String desc, String yearsOfExperience, String tools, String skills, String tagLine) {

        this.desc = desc;
        this.yearsOfExperience = yearsOfExperience;
        this.tools = tools;
        this.skills = skills;
        this.tagLine = tagLine;
    }

    // Constructor for final data from freelancer_register
    public Freelancer(String id, String passout, String degree, String language, String institute, String availability) {
        this.id = id;
        this.passout = passout;
        this.degree = degree;
        this.language = language;
        this.institute = institute;
        this.availability = availability;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getPassout() {
        return passout;
    }

    public void setPassout(String passout) {
        this.passout = passout;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getInstitute() {
        return institute;
    }

    public void setInstitute(String institute) {
        this.institute = institute;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public String getTagLine() {
        return tagLine;
    }

    public void setTagLine(String tagLine) {
        this.tagLine = tagLine;
    }

    public String getTools() {
        return tools;
    }

    public void setTools(String tools) {
        this.tools = tools;
    }

    public String getYearsOfExperience() {
        return yearsOfExperience;
    }

    public void setYearsOfExperience(String yearsOfExperience) {
        this.yearsOfExperience = yearsOfExperience;
    }

    public String getFirstName() {
        return firstName;
        // Other getters and setters...
    }
}