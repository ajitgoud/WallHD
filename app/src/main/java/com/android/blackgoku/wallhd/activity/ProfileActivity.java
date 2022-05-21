package com.android.blackgoku.wallhd.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.android.blackgoku.wallhd.R;
import com.android.blackgoku.wallhd.utility.SignedInUserDetails;
import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {

    private AppCompatTextView profileActivityUserNameTextView, profileActivityUserEmailTextView,
            profileActivityUserSinceTextView, profileActivityProviderTextView;

    private ImageView profileActivityUserImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        setUpToolBar();

        setUpFindViewById();

        setUpUserdata();

    }

    private void setUpUserdata() {

        SignedInUserDetails userDetails = new SignedInUserDetails();

        if(userDetails.getPhotoUrl() == null)
            return;

        GlideApp.with(this)
                .load(userDetails.getPhotoUrl())
                .placeholder(R.drawable.user_placeholder)
                .centerCrop()
                .into(profileActivityUserImageView);

        profileActivityUserNameTextView.setText(userDetails.getUserName());
        profileActivityUserEmailTextView.setText(userDetails.getUserEmail());
        profileActivityUserSinceTextView.setText(userDetails.getAccountCreationDate());
        profileActivityProviderTextView.setText(userDetails.getAuthProvider());


    }


    private void setUpFindViewById() {

        profileActivityUserNameTextView = findViewById(R.id.profileActivityUserNameTextView);

        profileActivityUserEmailTextView = findViewById(R.id.profileActivityUserEmailTextView);

        profileActivityUserImageView = findViewById(R.id.profileActivityUserImageView);

        profileActivityProviderTextView = findViewById(R.id.profileActivityProviderTextView);

        profileActivityUserSinceTextView = findViewById(R.id.profileActivityUserSinceTextView);

    }

    private void setUpToolBar() {

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);

            }
        });
        Objects.requireNonNull(getSupportActionBar()).setTitle("Profile");

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }
}
