package com.android.blackgoku.wallhd.model.unsplash_api.user;

import android.os.Parcel;
import android.os.Parcelable;

public class ApiUserProfileModel implements Parcelable {

    private String small;
    private String medium;
    private String large;

    public ApiUserProfileModel() {
    }

    protected ApiUserProfileModel(Parcel in) {
        small = in.readString();
        medium = in.readString();
        large = in.readString();
    }

    public static final Creator<ApiUserProfileModel> CREATOR = new Creator<ApiUserProfileModel>() {
        @Override
        public ApiUserProfileModel createFromParcel(Parcel in) {
            return new ApiUserProfileModel(in);
        }

        @Override
        public ApiUserProfileModel[] newArray(int size) {
            return new ApiUserProfileModel[size];
        }
    };

    public String getSmall() {
        return small;
    }

    public String getMedium() {
        return medium;
    }

    public String getLarge() {
        return large;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(small);
        dest.writeString(medium);
        dest.writeString(large);
    }
}
