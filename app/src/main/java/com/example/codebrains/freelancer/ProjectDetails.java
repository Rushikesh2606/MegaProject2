package com.example.codebrains.freelancer;

import android.os.Parcel;
import android.os.Parcelable;

public class ProjectDetails implements Parcelable {
    private String title;
    private String description;
    private String skills;

    public ProjectDetails() {
        // Required empty constructor for Firebase
    }

    public ProjectDetails(String title, String description, String skills) {
        this.title = title;
        this.description = description;
        this.skills = skills;
    }

    protected ProjectDetails(Parcel in) {
        title = in.readString();
        description = in.readString();
        skills = in.readString();
    }

    public static final Creator<ProjectDetails> CREATOR = new Creator<ProjectDetails>() {
        @Override
        public ProjectDetails createFromParcel(Parcel in) {
            return new ProjectDetails(in);
        }

        @Override
        public ProjectDetails[] newArray(int size) {
            return new ProjectDetails[size];
        }
    };

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getSkills() { return skills; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(skills);
    }
}