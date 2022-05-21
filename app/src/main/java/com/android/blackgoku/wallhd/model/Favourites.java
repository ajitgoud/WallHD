package com.android.blackgoku.wallhd.model;

import java.util.List;

public class Favourites {

    private List<String> imageID;

    public Favourites() {

    }

    public Favourites(List<String> imageID) {
        this.imageID = imageID;
    }

    public List<String> getImageID() {
        return imageID;
    }

}
