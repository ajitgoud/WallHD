package com.android.blackgoku.wallhd.model.unsplash_api.user;

import android.os.Parcel;
import android.os.Parcelable;

public class ApiUserModel implements Parcelable {

    private String id;
    private String username;
    private String name;
    private String first_name;
    private String last_name;
    private String twitter_username;
    private String portfolio_url;
    private String bio;
    private String location;
    private String instagram_username;
    private ApiUserLinkModel links;
    private ApiUserProfileModel profile_image;

    public ApiUserModel() {
    }

    protected ApiUserModel(Parcel in) {
        id = in.readString();
        username = in.readString();
        name = in.readString();
        first_name = in.readString();
        last_name = in.readString();
        twitter_username = in.readString();
        portfolio_url = in.readString();
        bio = in.readString();
        location = in.readString();
        instagram_username = in.readString();
        links = in.readParcelable(ApiUserLinkModel.class.getClassLoader());
        profile_image = in.readParcelable(ApiUserProfileModel.class.getClassLoader());
    }

    public static final Creator<ApiUserModel> CREATOR = new Creator<ApiUserModel>() {
        @Override
        public ApiUserModel createFromParcel(Parcel in) {
            return new ApiUserModel(in);
        }

        @Override
        public ApiUserModel[] newArray(int size) {
            return new ApiUserModel[size];
        }
    };

    public String getInstagram_username() {
        return instagram_username;
    }
    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getTwitter_username() {
        return twitter_username;
    }

    public String getPortfolio_url() {
        return portfolio_url;
    }

    public String getBio() {
        return bio;
    }

    public String getLocation() {
        return location;
    }

    public ApiUserLinkModel getLinks() {
        return links;
    }

    public ApiUserProfileModel getProfile_image() {
        return profile_image;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(username);
        dest.writeString(name);
        dest.writeString(first_name);
        dest.writeString(last_name);
        dest.writeString(twitter_username);
        dest.writeString(portfolio_url);
        dest.writeString(bio);
        dest.writeString(location);
        dest.writeString(instagram_username);
        dest.writeParcelable(links, flags);
        dest.writeParcelable(profile_image, flags);
    }
}
