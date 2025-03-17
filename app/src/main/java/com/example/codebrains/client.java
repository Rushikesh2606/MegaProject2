package com.example.codebrains;

public class client {
    String firstName;
    String lastName;
    String email;
String profession;
    String password;
    String country;
    String username;
    String dob;
    int total_jobs, in_progress, completed, pending;

    public int getTotal_jobs() {
        return total_jobs;
    }

    public void setTotal_jobs(int total_jobs) {
        this.total_jobs = total_jobs;
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

    public int getPending() {
        return pending;
    }

    public void setPending(int pending) {
        this.pending = pending;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    String contactNo;
    String gender;
    String profileImage;
    String profileImageUri;

    public String getFirstName() {
        return firstName;
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

    public client(String firstName, String lastName, String email, String password, String country, String username, String contactNo, String gender, String Profession,String profileImage, String dob,int pending, int in_progress, int completed, int total_jobs) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.country = country;
        this.username = username;
        this.contactNo = contactNo;
        this.gender = gender;
        this.profileImage = profileImage;
        this.dob = dob;
        this.profession=Profession;
        this.pending = pending;
        this.in_progress = in_progress;
        this.completed = completed;
        this.total_jobs = total_jobs;
    }

    public client() {
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

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getProfileImageUri() {
        return profileImageUri;
    }

    public void setProfileImageUri(String profileImageUri) {
        this.profileImageUri = profileImageUri;
    }
}
