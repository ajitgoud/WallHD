package com.android.blackgoku.wallhd.model.unsplash_api.urls;

import android.os.Parcel;
import android.os.Parcelable;

public class ApiUrlsModel implements Parcelable {

    private String raw;
    private String full;
    private String regular;
    private String small;
    private String thumb;

    public ApiUrlsModel() {
    }

    protected ApiUrlsModel(Parcel in) {
        raw = in.readString();
        full = in.readString();
        regular = in.readString();
        small = in.readString();
        thumb = in.readString();
    }

    public static final Creator<ApiUrlsModel> CREATOR = new Creator<ApiUrlsModel>() {
        @Override
        public ApiUrlsModel createFromParcel(Parcel in) {
            return new ApiUrlsModel(in);
        }

        @Override
        public ApiUrlsModel[] newArray(int size) {
            return new ApiUrlsModel[size];
        }
    };

    public String getRaw() {
        return raw;
    }

    public String getFull() {
        return full;
    }

    public String getRegular() {
        return regular;
    }

    public String getSmall() {
        return small;
    }

    public String getThumb() {
        return thumb;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(raw);
        dest.writeString(full);
        dest.writeString(regular);
        dest.writeString(small);
        dest.writeString(thumb);
    }
}
