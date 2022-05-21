package com.android.blackgoku.wallhd.model;

import com.android.blackgoku.wallhd.model.unsplash_api.UnsplashApiModel;

public class FavouriteModelClass {

    private UnsplashApiModel apiModel;
    private int timeStamp;

    public FavouriteModelClass(UnsplashApiModel apiModel, int timeStamp) {
        this.apiModel = apiModel;
        this.timeStamp = timeStamp;
    }

    public FavouriteModelClass() {
    }

    public UnsplashApiModel getApiModel() {
        return apiModel;
    }

    public int getTimeStamp() {
        return timeStamp;
    }
}
