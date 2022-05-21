package com.android.blackgoku.wallhd.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.android.blackgoku.wallhd.Java.dialogBox.ClearHistoryDialogue;
import com.android.blackgoku.wallhd.R;
import com.android.blackgoku.wallhd.utility.FirebaseFirestoreUtilityClass;

import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    private CardView myProfileSettingsCardView, clearHistorySetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setUpToolBar();

        setUpFindViewById();

        setUpClickListeners();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    private void setUpClickListeners() {
        myProfileSettingsCardView.setOnClickListener(this);
        clearHistorySetting.setOnClickListener(this);
    }

    private void setUpFindViewById() {

        myProfileSettingsCardView = findViewById(R.id.myProfileSettingsCardView);
        clearHistorySetting = findViewById(R.id.clearHistorySetting);

    }

    private void setUpToolBar() {

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);

            }
        });
        Objects.requireNonNull(getSupportActionBar()).setTitle("Settings");

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.myProfileSettingsCardView:

                if (FirebaseFirestoreUtilityClass.currentUserIsNotNull()) {
                    startActivity(new Intent(this, ProfileActivity.class));
                    overridePendingTransition(R.anim.slide_to_left, R.anim.slide_from_right);
                } else {

                    Toasty.info(this, "You haven't signed in", Toast.LENGTH_LONG).show();

                }

                break;

            case R.id.clearHistorySetting:

                ClearHistoryDialogue dialogue = new ClearHistoryDialogue();
                dialogue.show(getSupportFragmentManager(), "ClearHistoryDialogue");

                break;

        }
    }
}
