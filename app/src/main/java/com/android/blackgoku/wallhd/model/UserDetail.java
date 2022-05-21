package com.android.blackgoku.wallhd.model;

public class UserDetail {

    private String mUserName;
    private String mUserEmail;
    private String mUserAccountCreationDate;
    private String mUserSignInProvider;

    public UserDetail() {

    }


    public UserDetail(String mUserName, String mUserEmail, String mUserSignInProvider, String mUserAccountCreationDate) {

        this.mUserName = mUserName;
        this.mUserEmail = mUserEmail;
        this.mUserAccountCreationDate = mUserAccountCreationDate;
        this.mUserSignInProvider = mUserSignInProvider;
    }

    public String getUserName() {
        return mUserName;
    }

    public void setUserName(String mUserName) {
        this.mUserName = mUserName;
    }

    public String getUserEmail() {
        return mUserEmail;
    }

    public void setUserEmail(String mUserEmail) {
        this.mUserEmail = mUserEmail;
    }

    public String getUserAccountCreationDate() {
        return mUserAccountCreationDate;
    }

    public void setUserAccountCreationDate(String mUserAccountCreationDate) {
        this.mUserAccountCreationDate = mUserAccountCreationDate;
    }

    public String getUserSignInProvider() {
        return mUserSignInProvider;
    }

    public void setUserSignInProvider(String mUserSignInProvider) {
        this.mUserSignInProvider = mUserSignInProvider;
    }
}
