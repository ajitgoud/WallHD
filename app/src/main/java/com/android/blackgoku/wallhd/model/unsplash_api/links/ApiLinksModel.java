package com.android.blackgoku.wallhd.model.unsplash_api.links;

import android.os.Parcel;
import android.os.Parcelable;

public class ApiLinksModel implements Parcelable {

    private String self;
    private String html;
    private String download;
    private String download_location;

    public ApiLinksModel() {
    }

    protected ApiLinksModel(Parcel in) {
        self = in.readString();
        html = in.readString();
        download = in.readString();
        download_location = in.readString();
    }

    public static final Creator<ApiLinksModel> CREATOR = new Creator<ApiLinksModel>() {
        @Override
        public ApiLinksModel createFromParcel(Parcel in) {
            return new ApiLinksModel(in);
        }

        @Override
        public ApiLinksModel[] newArray(int size) {
            return new ApiLinksModel[size];
        }
    };

    public String getSelf() {
        return self;
    }

    public String getHtml() {
        return html;
    }

    public String getDownload() {
        return download;
    }

    public String getDownload_location() {
        return download_location;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(self);
        dest.writeString(html);
        dest.writeString(download);
        dest.writeString(download_location);
    }
}
