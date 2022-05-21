package com.android.blackgoku.wallhd.pagination_components.collections;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.PageKeyedDataSource;
import android.support.annotation.NonNull;
import android.util.Log;

import com.android.blackgoku.wallhd.classes.RetrofitService;
import com.android.blackgoku.wallhd.interfaces.UnsplashApiListener;
import com.android.blackgoku.wallhd.model.NetworkState;
import com.android.blackgoku.wallhd.model.unsplash_api.UnsplashApiModel;
import com.android.blackgoku.wallhd.model.unsplash_images_collection.UnsplashImageCollection;
import com.android.blackgoku.wallhd.utility.WallHDConstants;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UnsplashApiCollectionDataSource extends PageKeyedDataSource<Integer, UnsplashApiModel> {

    private final String TAG = UnsplashApiCollectionDataSource.class.getSimpleName();
    private static final int FIRST_PAGE = 1;
    private String QUERY;
    private UnsplashApiListener apiListener;
    private MutableLiveData<NetworkState> networkState;
    private MutableLiveData<NetworkState> initialLoading;

    UnsplashApiCollectionDataSource(String category) {

        RetrofitService service = RetrofitService.getInstance();
        apiListener = service.getApiListener();
        networkState = new MutableLiveData<>();
        initialLoading = new MutableLiveData<>();
        QUERY = category;

    }

    MutableLiveData<NetworkState> getNetworkState() {
        return networkState;
    }

    MutableLiveData<NetworkState> getInitialLoading() {
        return initialLoading;
    }

    @Override
    public void loadInitial(@NonNull final LoadInitialParams<Integer> params, @NonNull final LoadInitialCallback<Integer, UnsplashApiModel> callback) {

        initialLoading.postValue(NetworkState.LOADING);


        apiListener.getImagesByCollection(FIRST_PAGE, WallHDConstants.PAGE_SIZE, QUERY, WallHDConstants.CLIENT_ID)
                .enqueue(new Callback<UnsplashImageCollection>() {
                    @Override
                    public void onResponse(@NonNull Call<UnsplashImageCollection> call, @NonNull Response<UnsplashImageCollection> response) {

                        if (response.isSuccessful() && response.body() != null) {

                            initialLoading.postValue(NetworkState.LOADED);
                            callback.onResult(response.body().getResults(), null, FIRST_PAGE + 1);


                        } else {

                            Log.e(TAG, "onResponse error " + response.message());
                            initialLoading.postValue(new NetworkState(NetworkState.Status.FAILED, response.message()));

                        }

                    }

                    @Override
                    public void onFailure(@NonNull Call<UnsplashImageCollection> call, @NonNull Throwable t) {

                        String errorMessage = t.getMessage();
                        Log.e(TAG, "onFailure: " + errorMessage);
                        initialLoading.postValue(new NetworkState(NetworkState.Status.FAILED, errorMessage));

                    }
                });

    }

    @Override
    public void loadBefore(@NonNull final LoadParams<Integer> params, @NonNull final LoadCallback<Integer, UnsplashApiModel> callback) {

        networkState.postValue(NetworkState.LOADING);

        apiListener.getImagesByCollection(params.key, WallHDConstants.PAGE_SIZE, QUERY, WallHDConstants.CLIENT_ID)
                .enqueue(new Callback<UnsplashImageCollection>() {
                    @Override
                    public void onResponse(@NonNull Call<UnsplashImageCollection> call, @NonNull Response<UnsplashImageCollection> response) {

                        if (response.isSuccessful() && response.body() != null) {

                            networkState.postValue(NetworkState.LOADED);

                            Integer key = (params.key > 1) ? params.key - 1 : null;
                            callback.onResult(response.body().getResults(), key);

                        } else {

                            Log.e(TAG, "onResponse error " + response.message());
                            networkState.postValue(new NetworkState(NetworkState.Status.FAILED, response.message()));

                        }

                    }

                    @Override
                    public void onFailure(@NonNull Call<UnsplashImageCollection> call, @NonNull Throwable t) {

                        String errorMessage = t.getMessage();
                        Log.e(TAG, "onFailure: "+errorMessage );
                        networkState.postValue(new NetworkState(NetworkState.Status.FAILED,errorMessage));

                    }
                });


    }

    @Override
    public void loadAfter(@NonNull final LoadParams<Integer> params, @NonNull final LoadCallback<Integer, UnsplashApiModel> callback) {

        networkState.postValue(NetworkState.LOADING);

        apiListener.getImagesByCollection(params.key, WallHDConstants.PAGE_SIZE, QUERY, WallHDConstants.CLIENT_ID)
                .enqueue(new Callback<UnsplashImageCollection>() {
                    @Override
                    public void onResponse(@NonNull Call<UnsplashImageCollection> call, @NonNull Response<UnsplashImageCollection> response) {

                        if (response.isSuccessful() && response.body() != null) {

                            networkState.postValue(NetworkState.LOADED);

                            Integer key = (params.key < response.body().getTotal_pages()) ? params.key + 1 : null;
                            callback.onResult(response.body().getResults(), key);

                        } else {

                            Log.e(TAG, "onResponse error " + response.message());
                            networkState.postValue(new NetworkState(NetworkState.Status.FAILED, response.message()));

                        }

                    }

                    @Override
                    public void onFailure(@NonNull Call<UnsplashImageCollection> call, @NonNull Throwable t) {

                        String errorMessage = t.getMessage();
                        Log.e(TAG, "onFailure: "+errorMessage );
                        networkState.postValue(new NetworkState(NetworkState.Status.FAILED,errorMessage));

                    }
                });

    }
}
