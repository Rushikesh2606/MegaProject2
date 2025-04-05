package com.example.codebrains.model;

public class Rated_Job {


    long timestamp;
    String JobId;
    String Feedback;

    int rating;

    public Rated_Job() {
    }

    public Rated_Job(long timestamp, String jobId, String feedback,int rating) {
        this.timestamp = timestamp;
        JobId = jobId;
        Feedback = feedback;
        this.rating=rating;
    }

    public int getRating() {
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
