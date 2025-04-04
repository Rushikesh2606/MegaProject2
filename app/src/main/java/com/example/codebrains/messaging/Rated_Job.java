package com.example.codebrains.messaging;

public class Rated_Job {
    long timestamp;
    String JobId;
    String Feedback;

    public Rated_Job() {
    }

    public Rated_Job(long timestamp, String jobId, String feedback) {
        this.timestamp = timestamp;
        JobId = jobId;
        Feedback = feedback;
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
