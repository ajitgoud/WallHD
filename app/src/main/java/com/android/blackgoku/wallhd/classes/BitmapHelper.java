package com.android.blackgoku.wallhd.classes;

import android.graphics.Bitmap;

import com.google.gson.Gson;

public class BitmapHelper {

    public static String serializeToJson(Bitmap bmp) {

        Gson gson = new Gson();
        return gson.toJson(bmp);

    }

    public static Bitmap deserializeFromJson(String jsonString) {

        Gson gson = new Gson();
        return gson.fromJson(jsonString, Bitmap.class);

    }

}
