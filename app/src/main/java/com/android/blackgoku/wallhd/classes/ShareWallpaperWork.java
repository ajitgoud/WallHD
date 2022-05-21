package com.android.blackgoku.wallhd.classes;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;

import com.android.blackgoku.wallhd.BuildConfig;
import com.android.blackgoku.wallhd.activity.GlideApp;
import com.android.blackgoku.wallhd.activity.WallpaperViewActivity;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class ShareWallpaperWork extends Worker {

    private Context context;

    public ShareWallpaperWork(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {

        Data urlData = getInputData();
        String imageId = urlData.getString(WallpaperViewActivity.IMAGE_ID);
        String bitmapString = urlData.getString(WallpaperViewActivity.IMAGE_BITMAP_RESOURCE);
        Bitmap resource = BitmapHelper.deserializeFromJson(bitmapString);

        shareImages( imageId, resource);
        return Result.success();
    }

    private void shareImages(final String imageId, Bitmap resource) {

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_STREAM, getLocalBitmapUri(resource, imageId));
        context.startActivity(Intent.createChooser(intent, "Wall HD"));

    }

    private Uri getLocalBitmapUri(Bitmap resource, String imageId) {

        Uri bitmapUri = null;
        try {

            File image = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/Wall HD/"), imageId + ".jpg");

            FileOutputStream outputStream = new FileOutputStream(image);
            resource.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
            outputStream.close();
            bitmapUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", image);


        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmapUri;

    }

}

