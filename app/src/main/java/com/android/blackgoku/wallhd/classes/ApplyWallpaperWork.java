package com.android.blackgoku.wallhd.classes;

import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;

import com.android.blackgoku.wallhd.activity.WallpaperViewActivity;

import java.io.IOException;

import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class ApplyWallpaperWork extends Worker {

    public static final String TAG = ApplyWallpaperWork.class.getSimpleName();

    public ApplyWallpaperWork(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        Data urlData = getInputData();

        String bitmapString = urlData.getString(WallpaperViewActivity.IMAGE_BITMAP_RESOURCE);
        Bitmap resource = BitmapHelper.deserializeFromJson(bitmapString);

        setBackWallpaper(resource);

        return Result.success();
    }

    private void setBackWallpaper(Bitmap resource) {

        final WallpaperManager wallpaperManager = (WallpaperManager) getApplicationContext().getSystemService(Context.WALLPAPER_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

           /* final ApplyDialogBox applyDialogBox = new ApplyDialogBox();
            applyDialogBox.show(getSupportFragmentManager(), "ApplyDialogBox");*/

            assert wallpaperManager != null;
            boolean isAllowed = wallpaperManager.isSetWallpaperAllowed();

            if (isAllowed) {

                try {
                    wallpaperManager.setBitmap(resource);
                    //applyDialogBox.dismiss();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }

        }


    }

}
