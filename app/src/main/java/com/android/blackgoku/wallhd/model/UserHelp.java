package com.android.blackgoku.wallhd.model;

public class UserHelp {

    private String title;
    private String description;
    private String date;
    private String userEmail;

    public UserHelp(String title, String description, String userEmail, String date) {
        this.title = title;
        this.description = description;
        this.userEmail = userEmail;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }
}
