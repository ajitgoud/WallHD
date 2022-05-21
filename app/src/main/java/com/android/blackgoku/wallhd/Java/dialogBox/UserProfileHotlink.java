package com.android.blackgoku.wallhd.Java.dialogBox;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.android.blackgoku.wallhd.R;
import com.android.blackgoku.wallhd.activity.GlideApp;
import com.android.blackgoku.wallhd.utility.WallHDConstants;

import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileHotlink extends AppCompatDialogFragment implements View.OnClickListener {

    private AppCompatImageView squareProfileImage, locationImageView;
    private CircleImageView circularProfileImage;
    private AppCompatTextView userNameTextView, userLocationTextView, bioTextView;
    private LinearLayout linearLayout1, linearLayout2, linearLayout3;
    private FloatingActionButton dismissDialogFAB;
    private String userProfileImage, userName, userLocation, userInstagram, userTwitter, userUnsplash, userBio;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        assert getArguments() != null;

        userProfileImage = getArguments().getString(WallHDConstants.USER_PROFILE_IMAGE_HOTLINK);
        userName = getArguments().getString(WallHDConstants.USER_USERNAME_HOTLINK);
        userLocation = getArguments().getString(WallHDConstants.USER_USERLOCATION_HOTLINK);
        userInstagram = getArguments().getString(WallHDConstants.USER_INSTAGRAM);
        userTwitter = getArguments().getString(WallHDConstants.USER_TWITTER);
        userUnsplash = getArguments().getString(WallHDConstants.USER_UNSPLASH);
        userBio = getArguments().getString(WallHDConstants.USER_BIO);

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater inflater = Objects.requireNonNull(getActivity()).getLayoutInflater();
        View view = inflater.inflate(R.layout.user_profile_hotlink, null);

        findViews(view);
        setOnClickListeners();
        setImages(userProfileImage);
        setUserName(userName);
        setUserLocation(userLocation);
        setUserBio(userBio);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.DialogTheme);

        builder.setView(view);
        AlertDialog alertDialog = builder.create();
        Objects.requireNonNull(alertDialog.getWindow()).getAttributes().windowAnimations = R.style.ZoomIn_ZoomOut;

        return alertDialog;

    }

    private void setOnClickListeners() {

        linearLayout1.setOnClickListener(this);
        linearLayout2.setOnClickListener(this);
        linearLayout3.setOnClickListener(this);
        dismissDialogFAB.setOnClickListener(this);

    }

    private void setUserBio(String bioText) {

        if (bioText != null) {

            bioTextView.setText(bioText);

        } else {

            bioTextView.setVisibility(View.GONE);

        }

    }

    private void setUserLocation(String userLocation) {

        if (userLocation != null) {

            userLocationTextView.setText(userLocation);

        } else {

            userLocationTextView.setVisibility(View.GONE);
            locationImageView.setVisibility(View.GONE);

        }

    }

    private void setUserName(String userName) {

        if (userName != null) {

            userNameTextView.setText(userName);

        } else {

            userNameTextView.setVisibility(View.GONE);

        }

    }

    private void setImages(String userProfileImage) {

        if (userProfileImage != null) {

            GlideApp.with(Objects.requireNonNull(getContext()))
                    .load(userProfileImage)
                    .centerCrop()
                    .into(squareProfileImage);

            GlideApp.with(Objects.requireNonNull(getContext()))
                    .load(userProfileImage)
                    .centerCrop()
                    .into(circularProfileImage);


        }
    }

    private void findViews(View view) {

        squareProfileImage = view.findViewById(R.id.squareProfileImage);
        locationImageView = view.findViewById(R.id.locationImageView);
        circularProfileImage = view.findViewById(R.id.circularProfileImage);
        userNameTextView = view.findViewById(R.id.userNameTextView);
        userLocationTextView = view.findViewById(R.id.userLocationTextView);
        bioTextView = view.findViewById(R.id.bioTextView);
        dismissDialogFAB = view.findViewById(R.id.dismissDialogFAB);

        linearLayout1 = view.findViewById(R.id.linearLayout1);
        linearLayout2 = view.findViewById(R.id.linearLayout2);
        linearLayout3 = view.findViewById(R.id.linearLayout3);

        if (userInstagram == null) {
            linearLayout3.setVisibility(View.GONE);
        }

        if (userUnsplash == null) {
            linearLayout1.setVisibility(View.GONE);
        }

        if (userTwitter == null) {
            linearLayout2.setVisibility(View.GONE);
        }


    }

    @Override
    public void onClick(View v) {

        assert getContext() != null;

        switch (v.getId()) {

            case R.id.linearLayout1:

                openUnsplash(userUnsplash);

                break;

            case R.id.linearLayout2:

                openTwitter(userTwitter);

                break;

            case R.id.linearLayout3:

                openInstagram(userInstagram);

                break;


            case R.id.dismissDialogFAB:

                dismiss();

                break;

        }

    }

    private void openUnsplash(String userUnsplash) {

        String hotlinkUserUrl = userUnsplash + "?utm_source=wall_hd&utm_medium=referral";

        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(hotlinkUserUrl)));

    }

    private void openTwitter(String userTwitter) {

        String twitterUserProfileLink = "twitter://user?screen_name=" + userTwitter;
        Uri intentURI = Uri.parse(twitterUserProfileLink);

        Intent twitterIntent = new Intent(Intent.ACTION_VIEW, intentURI);

        twitterIntent.setPackage("com.twitter.android");

        if(isIntentAvailable(twitterIntent)){

            startActivity(twitterIntent);


        }else{

            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/" + userInstagram)));

        }


    }

    private void openInstagram(String userInstagram) {

        String instaUserProfileLink = "http://instagram.com/_u/" + userInstagram;

        Uri intentURI = Uri.parse(instaUserProfileLink);

        Intent instaIntent = new Intent(Intent.ACTION_VIEW, intentURI);
        instaIntent.setPackage("com.instagram.android");

        if(isIntentAvailable(instaIntent)){

            startActivity(instaIntent);

        }else{

            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://instagram.com/" + userInstagram)));

        }


    }

    private boolean isIntentAvailable(Intent intent) {

        assert getContext() != null;
        final PackageManager packageManager = getContext().getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;

    }

}
