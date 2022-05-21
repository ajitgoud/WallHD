package com.android.blackgoku.wallhd.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;

import com.android.blackgoku.wallhd.R;
import com.github.chrisbanes.photoview.PhotoView;

public class ImageViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        Intent wallpaperViewIntent = getIntent();
        String imageUrl = wallpaperViewIntent.getStringExtra(WallpaperViewActivity.IMAGE_URL);

        PhotoView fullImageView = findViewById(R.id.fullImageView);

        GlideApp.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder)
                .into(fullImageView);

        AppCompatImageView goBackImageView = findViewById(R.id.goBackImageView);
        goBackImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
                overridePendingTransition(0, R.anim.zoom_out);

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(0, R.anim.zoom_out);
    }
}
