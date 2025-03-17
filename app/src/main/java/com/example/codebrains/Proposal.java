package com.example.codebrains;

public class Proposal {
    private String name;
    private String location;
    private float rating;
    private String description;
    private String price;

    public Proposal(String name, String location, float rating, String description, String price) {
        this.name = name;
        this.location = location;
        this.rating = rating;
        this.description = description;
        this.price = price;
    }

    public String getName() { return name; }
    public String getLocation() { return location; }
    public float getRating() { return rating; }
    public String getDescription() { return description; }
    public String getPrice() { return price; }
}