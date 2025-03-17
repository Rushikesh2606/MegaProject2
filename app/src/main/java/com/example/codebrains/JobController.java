package com.example.codebrains;


public class JobController {
    private String id;
    private String username;
    private String jobTitle;
    private String jobCategory;
    private String jobDescription;
    private String primarySkill;
    private String additionalSkills;
    private String experienceLevel;
    private double budget;
    private String deadline;
    private String attachments;
    private String additionalQuestions;
    private String status;
    private int noOfBidsReceived;
    private String postedDate;

    // Default constructor
    public JobController() {}

    // Parameterized constructor
    public JobController(String id, String username, String jobTitle, String jobCategory, String jobDescription,
               String primarySkill, String additionalSkills, String experienceLevel, double budget,
               String deadline, String attachments, String additionalQuestions, String status,
               int noOfBidsReceived, String postedDate) {
        this.id = id;
        this.username = username;
        this.jobTitle = jobTitle;
        this.jobCategory = jobCategory;
        this.jobDescription = jobDescription;
        this.primarySkill = primarySkill;
        this.additionalSkills = additionalSkills;
        this.experienceLevel = experienceLevel;
        this.budget = budget;
        this.deadline = deadline;
        this.attachments = attachments;
        this.additionalQuestions = additionalQuestions;
        this.status = status;
        this.noOfBidsReceived = noOfBidsReceived;
        this.postedDate = postedDate;
    }
    public JobController(String title, String date, String status, int bids) {
        this.jobTitle = title;
        this.postedDate = date;
        this.status = status;
        this.noOfBidsReceived = bids;
    }

    public JobController(String attachments, String jobDescription, String jobCategory, String jobTitle, String username, String id) {
        this.attachments = attachments;
        this.jobDescription = jobDescription;
        this.jobCategory = jobCategory;
        this.jobTitle = jobTitle;
        this.username = username;
        this.id = id;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getJobCategory() {
        return jobCategory;
    }

    public void setJobCategory(String jobCategory) {
        this.jobCategory = jobCategory;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public String getPrimarySkill() {
        return primarySkill;
    }

    public void setPrimarySkill(String primarySkill) {
        this.primarySkill = primarySkill;
    }

    public String getAdditionalSkills() {
        return additionalSkills;
    }

    public void setAdditionalSkills(String additionalSkills) {
        this.additionalSkills = additionalSkills;
    }

    public String getExperienceLevel() {
        return experienceLevel;
    }

    public void setExperienceLevel(String experienceLevel) {
        this.experienceLevel = experienceLevel;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getAttachments() {
        return attachments;
    }

    public void setAttachments(String attachments) {
        this.attachments = attachments;
    }

    public String getAdditionalQuestions() {
        return additionalQuestions;
    }

    public void setAdditionalQuestions(String additionalQuestions) {
        this.additionalQuestions = additionalQuestions;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getNoOfBidsReceived() {
        return noOfBidsReceived;
    }

    public void setNoOfBidsReceived(int noOfBidsReceived) {
        this.noOfBidsReceived = noOfBidsReceived;
    }

    public String getPostedDate() {
        return postedDate;
    }

    public void setPostedDate(String postedDate) {
        this.postedDate = postedDate;
    }
}