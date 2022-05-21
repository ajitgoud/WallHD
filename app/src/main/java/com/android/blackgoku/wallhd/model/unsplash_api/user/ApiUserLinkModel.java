package com.android.blackgoku.wallhd.model.unsplash_api.user;

import android.os.Parcel;
import android.os.Parcelable;

public class ApiUserLinkModel implements Parcelable {

    private String self;
    private String html;
    private String photos;
    private String likes;
    private String portfolio;

    public ApiUserLinkModel() {
    }

    protected ApiUserLinkModel(Parcel in) {
        self = in.readString();
        html = in.readString();
        photos = in.readString();
        likes = in.readString();
        portfolio = in.readString();
    }

    public static final Creator<ApiUserLinkModel> CREATOR = new Creator<ApiUserLinkModel>() {
        @Override
        public ApiUserLinkModel createFromParcel(Parcel in) {
            return new ApiUserLinkModel(in);
        }

        @Override
        public ApiUserLinkModel[] newArray(int size) {
            return new ApiUserLinkModel[size];
        }
    };

    public String getSelf() {
        return self;
    }

    public String getHtml() {
        return html;
    }

    public String getPhotos() {
        return photos;
    }

    public String getLikes() {
        return likes;
    }

    public String getPortfolio() {
        return portfolio;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(self);
        dest.writeString(html);
        dest.writeString(photos);
        dest.writeString(likes);
        dest.writeString(portfolio);
    }
}
