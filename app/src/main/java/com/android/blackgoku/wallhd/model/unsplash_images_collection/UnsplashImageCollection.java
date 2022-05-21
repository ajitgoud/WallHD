package com.android.blackgoku.wallhd.model.unsplash_images_collection;

import com.android.blackgoku.wallhd.model.unsplash_api.UnsplashApiModel;

import java.util.List;

public class UnsplashImageCollection {

    private int total;
    private int total_pages;
    private List<UnsplashApiModel> results;

    public int getTotal() {
        return total;
    }

    public int getTotal_pages() {
        return total_pages;
    }

    public List<UnsplashApiModel> getResults() {
        return results;
    }
}
