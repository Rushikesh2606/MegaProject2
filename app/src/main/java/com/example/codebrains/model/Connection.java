package com.example.codebrains.model;

public class Connection {
    private String connectionId;
    private String client;
    private String freelancer;
    private String jobId;
    private String status;

    public Connection() {
    }

    public Connection(String connectionId, String client, String freelancer, String jobId, String status) {
        this.connectionId = connectionId;
        this.client = client;
        this.freelancer = freelancer;
        this.jobId = jobId;
        this.status = status;
    }

    // Getters and setters
    public String getConnectionId() {
        return connectionId;
    }

    public void setConnectionId(String connectionId) {
        this.connectionId = connectionId;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getFreelancer() {
        return freelancer;
    }

    public void setFreelancer(String freelancer) {
        this.freelancer = freelancer;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}