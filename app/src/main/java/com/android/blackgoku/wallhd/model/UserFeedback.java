package com.android.blackgoku.wallhd.model;

public class UserFeedback {

    private String rating;
    private String description;
    private String date;
    private String userEmail;

    public UserFeedback(String rating, String description, String userEmail, String date) {
        this.rating = rating;
        this.description = description;
        this.userEmail = userEmail;
        this.date = date;
    }

    public String getRating() {
        return rating;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public String getUserEmail() {
        return userEmail;
    }
}
