package com.example.codebrains.model;

public class Proposal {
    private String name;
    private String location;
    private float rating;
    private String description;
    private String price;
    private String jobId;
    private String proposalId;
    private String freelancerId;

    public Proposal(String name, String location, float rating, String description, String price, String jobId, String proposalId, String freelancerId) {
        this.name = name;
        this.location = location;
        this.rating = rating;
        this.description = description;
        this.price = price;
        this.jobId = jobId;
        this.proposalId = proposalId;
        this.freelancerId = freelancerId;
    }

    // Getters
    public String getName() { return name; }
    public String getLocation() { return location; }
    public float getRating() { return rating; }
    public String getDescription() { return description; }
    public String getPrice() { return price; }
    public String getJobId() { return jobId; }
    public String getProposalId() { return proposalId; }
    public String getFreelancerId() { return freelancerId; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setLocation(String location) { this.location = location; }
    public void setRating(float rating) { this.rating = rating; }
    public void setDescription(String description) { this.description = description; }
    public void setPrice(String price) { this.price = price; }
    public void setJobId(String jobId) { this.jobId = jobId; }
    public void setProposalId(String proposalId) { this.proposalId = proposalId; }
    public void setFreelancerId(String freelancerId) { this.freelancerId = freelancerId; }
}