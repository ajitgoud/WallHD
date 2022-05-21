package com.android.blackgoku.wallhd.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.view.View;

import com.android.blackgoku.wallhd.BuildConfig;
import com.android.blackgoku.wallhd.R;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;
import java.io.FileOutputStream;

import static com.android.blackgoku.wallhd.broadcast_receiver.AppBroadcastReceiver.IMAGE_URI;

public class NotificationResultActivity extends AppCompatActivity {

    private PhotoView fullImageView;
    private AppCompatImageView goBackImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_result);

        fullImageView = findViewById(R.id.fullImageView);

        goBackImageView = findViewById(R.id.goBackImageView);
        goBackImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
                overridePendingTransition(0, R.anim.zoom_out);

            }
        });

        onNewIntent(getIntent());

    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Bundle extras = intent.getExtras();

        if (extras != null) {

            String filePath = extras.getString(IMAGE_URI);
            assert filePath != null;
            String mainFilePath = Environment.DIRECTORY_PICTURES + "/Wall HD/";
            File rootPath = Environment.getExternalStoragePublicDirectory(mainFilePath);
            File image = new File(rootPath,filePath);
            Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath());
            GlideApp.with(this)
                    .load(bitmap)
                    .into(fullImageView);

        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(0, R.anim.zoom_out);
    }
}
