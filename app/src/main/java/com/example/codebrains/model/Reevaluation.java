package com.example.codebrains.model;
public class Reevaluation {
    private String id;
    private boolean isPending;
    private long timestamp;
    private String reason;
    String jobId,freelancer;
    String Analyzer_reasons;

    public Reevaluation() {
    }

    public Reevaluation(String id, boolean isPending, long timestamp, String reason, String freelancer, String jobId, String Analyzer_reasons) {
        this.id = id;
        this.isPending = isPending;
        this.timestamp = timestamp;
        this.reason = reason;
        this.freelancer=freelancer;
        this.jobId=jobId;
        this.Analyzer_reasons=Analyzer_reasons;
    }

    public String getAnalyzer_reasons() {
        return Analyzer_reasons;
    }

    public void setAnalyzer_reasons(String analyzer_reasons) {
        Analyzer_reasons = analyzer_reasons;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getFreelancer() {
        return freelancer;
    }

    public void setFreelancer(String freelancer) {
        this.freelancer = freelancer;
    }

    // Getters and setters for each field
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isPending() {
        return isPending;
    }

    public void setPending(boolean isPending) {
        this.isPending = isPending;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
