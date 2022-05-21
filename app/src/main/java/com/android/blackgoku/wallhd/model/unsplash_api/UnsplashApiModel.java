package com.android.blackgoku.wallhd.model.unsplash_api;


import android.os.Parcel;
import android.os.Parcelable;

import com.android.blackgoku.wallhd.model.unsplash_api.links.ApiLinksModel;
import com.android.blackgoku.wallhd.model.unsplash_api.urls.ApiUrlsModel;
import com.android.blackgoku.wallhd.model.unsplash_api.user.ApiUserModel;

public class UnsplashApiModel implements Parcelable {

    private String id;
    private String description;
    private ApiUrlsModel urls;
    private Integer width;
    private Integer height;
    private ApiLinksModel links;
    private ApiUserModel user;

    public UnsplashApiModel() {
    }

    protected UnsplashApiModel(Parcel in) {
        id = in.readString();
        description = in.readString();
        urls = in.readParcelable(ApiUrlsModel.class.getClassLoader());
        if (in.readByte() == 0) {
            width = null;
        } else {
            width = in.readInt();
        }
        if (in.readByte() == 0) {
            height = null;
        } else {
            height = in.readInt();
        }
        links = in.readParcelable(ApiLinksModel.class.getClassLoader());
        user = in.readParcelable(ApiUserModel.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(description);
        dest.writeParcelable(urls, flags);
        if (width == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(width);
        }
        if (height == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(height);
        }
        dest.writeParcelable(links, flags);
        dest.writeParcelable(user, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UnsplashApiModel> CREATOR = new Creator<UnsplashApiModel>() {
        @Override
        public UnsplashApiModel createFromParcel(Parcel in) {
            return new UnsplashApiModel(in);
        }

        @Override
        public UnsplashApiModel[] newArray(int size) {
            return new UnsplashApiModel[size];
        }
    };

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public ApiUrlsModel getUrls() {
        return urls;
    }

    public Integer getWidth() {
        return width;
    }

    public Integer getHeight() {
        return height;
    }

    public ApiLinksModel getLinks() {
        return links;
    }

    public ApiUserModel getUser() {
        return user;
    }

}
