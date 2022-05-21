package com.android.blackgoku.wallhd.model;


import com.google.firebase.firestore.Exclude;

public class ImagesUpload {

    private String imageURL;
    private String authorName;
    private String imageKey;
    private String timeStamp;

    public ImagesUpload() {
    }

    public ImagesUpload(String authorName, String imageURL, String timeStamp) {

        this.authorName = authorName;
        this.imageURL = imageURL;
        this.timeStamp = timeStamp;

    }

    public String getImageURL() {
        return imageURL;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    @Exclude
    public String getImageKey() {
        return imageKey;
    }

    @Exclude
    public void setImageKey(String mKey) {
        this.imageKey = mKey;
    }
}
