package com.android.blackgoku.wallhd.utility;

import android.net.Uri;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUserMetadata;
import com.google.firebase.auth.UserInfo;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

public class SignedInUserDetails {

    private String userUID;
    private String userName;
    private String userEmail;
    private String authProvider;
    private String accountCreationDate;
    private Uri photoUrl;

    public SignedInUserDetails() {

        if (FirebaseFirestoreUtilityClass.currentUserIsNotNull()) {

            FirebaseUser user = FirebaseFirestoreUtilityClass.getCurrentSignedUser();

            if (user != null) {

                FirebaseUserMetadata metadata = user.getMetadata();
                assert metadata != null;
                long userAccountCreation = metadata.getCreationTimestamp();

                int style = DateFormat.MEDIUM;
                Date date = new Date(userAccountCreation);
                DateFormat df = DateFormat.getDateInstance(style, Locale.US);

                for (UserInfo info : user.getProviderData()) {
                    userEmail = info.getEmail();
                    authProvider = info.getProviderId();
                    photoUrl = info.getPhotoUrl();
                    userName = info.getDisplayName();
                }

                userUID = user.getUid();
                accountCreationDate = df.format(date);
            }
        }
    }

    public Uri getPhotoUrl() {
        return photoUrl;
    }

    public String getUserUID() {
        return userUID;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getAuthProvider() {
        return authProvider;
    }

    public String getAccountCreationDate() {
        return accountCreationDate;
    }
}
