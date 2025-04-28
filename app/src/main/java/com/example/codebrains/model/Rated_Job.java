package com.example.codebrains.model;

public class Rated_Job {


    long timestamp;
    String JobId;
    String id;
    String Feedback;

    float rating;
    String zip,freelancer,jobTitle,jobDescription;

    public Rated_Job() {
    }

    public String getFreelancer() {
        return freelancer;
    }

    public void setFreelancer(String freelancer) {
        this.freelancer = freelancer;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public Rated_Job(long timestamp, String jobId, String feedback, float rating, String zip, String id, String jobDescription, String jobTitle, String freelancer) {
        this.timestamp = timestamp;
        JobId = jobId;
        Feedback = feedback;
        this.rating=rating;
        this.zip=zip;
        this.id=id;
        this.freelancer=freelancer;
        this.jobDescription=jobDescription;
        this.jobTitle=jobTitle;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getJobId() {
        return JobId;
    }

    public void setJobId(String jobId) {
        JobId = jobId;
    }

    public String getFeedback() {
        return Feedback;
    }

    public void setFeedback(String feedback) {
        Feedback = feedback;
    }
}
