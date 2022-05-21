package com.android.blackgoku.wallhd.classes;

import com.android.blackgoku.wallhd.interfaces.UnsplashApiListener;
import com.android.blackgoku.wallhd.utility.WallHDConstants;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitService {

    private Retrofit retrofit;
    private static RetrofitService retrofitService;

    private RetrofitService() {

        retrofit = new Retrofit.Builder()
                .baseUrl(WallHDConstants.BASE_URL_UNSPLASH_API)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

    }

    public UnsplashApiListener getApiListener() {

        return retrofit.create(UnsplashApiListener.class);

    }

    public static synchronized RetrofitService getInstance() {

        if (retrofitService == null) {

            retrofitService = new RetrofitService();

        }

        return retrofitService;
    }



}

