package com.example.codebrains.model;
public class Reevaluation {
    private String id;
    private boolean isPending;
    private long timestamp;
    private String reason;

    public Reevaluation(String id, boolean isPending, long timestamp, String reason) {
        this.id = id;
        this.isPending = isPending;
        this.timestamp = timestamp;
        this.reason = reason;
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
