package com.example.codebrains.model;

public class User {
    private String id;
    private String firstName;
    private String lastName;
    private String username;
    private String contactNo;
    private String country;
    private String profileImage;
    private String password;
    private String email;
    private String gender;
    private String dob;
    private String profession;
    private long timestamp;
    private int total_jobs;
    private int completed;
    private int pending;
    private int in_progress;

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public User() {}

    public User(String id, String firstName, String lastName, String username,
                long timestamp,String country,String contactNo,String profileImage,String password,String email,String gender,String dob,int total_jobs,int completed,String profession,int pending,int in_progress) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.contactNo = contactNo;
        this.country = country;
        this.profileImage = profileImage;
        this.password = password;
        this.email = email;
        this.gender = gender;
        this.dob = dob;
        this.profession = profession;
        this.timestamp = timestamp;
        this.total_jobs = total_jobs;
        this.completed = completed;
        this.pending = pending;
        this.in_progress = in_progress;
        this.timestamp=timestamp;
    }
public User(String id, String firstName, String lastName, String username,
            long timestamp){
    this.id = id;
    this.firstName = firstName;
    this.lastName = lastName;
    this.username = username;

    this.timestamp=timestamp;
}
    // Getters and setters for all fields
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getContactNo() { return contactNo; }
    public void setContactNo(String contactNo) { this.contactNo = contactNo; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getProfileImage() { return profileImage; }
    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getDob() { return dob; }
    public void setDob(String dob) { this.dob = dob; }


    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public int getTotal_jobs() { return total_jobs; }
    public void setTotal_jobs(int total_jobs) { this.total_jobs = total_jobs; }

    public int getCompleted() { return completed; }
    public void setCompleted(int completed) { this.completed = completed; }

    public int getPending() { return pending; }
    public void setPending(int pending) { this.pending = pending; }

    public int getIn_progress() { return in_progress; }
    public void setIn_progress(int in_progress) { this.in_progress = in_progress; }
}