package com.android.blackgoku.wallhd.interfaces;

import com.android.blackgoku.wallhd.model.DownloadUrlLocation;
import com.android.blackgoku.wallhd.model.unsplash_api.UnsplashApiModel;
import com.android.blackgoku.wallhd.model.unsplash_images_collection.UnsplashImageCollection;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UnsplashApiListener {

    @GET("photos")
    Call<List<UnsplashApiModel>> getDailyImages(

            @Query("page") Integer page,
            @Query("per_page") Integer per_page,
            @Query("client_id") String client_id

    );

    @GET("photos/{ID}")
    Call<UnsplashApiModel> getSingleImage(

            @Path("ID") String imageId,
            @Query("client_id") String client_id

    );

    @GET("search/photos")
    Call<UnsplashImageCollection> getImagesByCollection(

            @Query("page") Integer page,
            @Query("per_page") Integer per_page,
            @Query("query") String query,
            @Query("client_id") String client_id

    );

    @GET("photos/{ID}/download")
    Call<DownloadUrlLocation> getDownloadLocationUrl(

            @Path("ID") String imageId,
            @Query("client_id") String client_id

    );

}
