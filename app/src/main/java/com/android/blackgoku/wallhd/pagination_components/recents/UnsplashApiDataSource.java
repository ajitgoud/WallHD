package com.android.blackgoku.wallhd.pagination_components.recents;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.PageKeyedDataSource;
import android.support.annotation.NonNull;
import android.util.Log;

import com.android.blackgoku.wallhd.classes.RetrofitService;
import com.android.blackgoku.wallhd.interfaces.UnsplashApiListener;
import com.android.blackgoku.wallhd.model.NetworkState;
import com.android.blackgoku.wallhd.model.unsplash_api.UnsplashApiModel;
import com.android.blackgoku.wallhd.utility.WallHDConstants;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UnsplashApiDataSource extends PageKeyedDataSource<Integer, UnsplashApiModel> {

    private final String TAG = UnsplashApiDataSource.class.getSimpleName();
    private static final int FIRST_PAGE = 1;
    private UnsplashApiListener apiListener;
    private MutableLiveData<NetworkState> networkState;
    private MutableLiveData<NetworkState> initialLoading;

    UnsplashApiDataSource() {

        RetrofitService service = RetrofitService.getInstance();
        apiListener = service.getApiListener();
        networkState = new MutableLiveData<>();
        initialLoading = new MutableLiveData<>();

    }

    MutableLiveData<NetworkState> getNetworkState() {
        return networkState;
    }

    MutableLiveData<NetworkState> getInitialLoading() {
        return initialLoading;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull final LoadInitialCallback<Integer, UnsplashApiModel> callback) {

        initialLoading.postValue(NetworkState.LOADING);

        apiListener.getDailyImages(FIRST_PAGE, WallHDConstants.PAGE_SIZE, WallHDConstants.CLIENT_ID)
                .enqueue(new Callback<List<UnsplashApiModel>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<UnsplashApiModel>> call, @NonNull Response<List<UnsplashApiModel>> response) {

                        if (response.isSuccessful() && response.body() != null) {

                            initialLoading.postValue(NetworkState.LOADED);

                            callback.onResult(response.body(), null, FIRST_PAGE + 1);

                        } else {

                            Log.e(TAG, "onResponse error " + response.message());
                            initialLoading.postValue(new NetworkState(NetworkState.Status.FAILED, response.message()));

                        }

                    }

                    @Override
                    public void onFailure(@NonNull Call<List<UnsplashApiModel>> call, @NonNull Throwable t) {

                        String errorMessage = t.getMessage();
                        Log.e(TAG, "onFailure: " + errorMessage);
                        initialLoading.postValue(new NetworkState(NetworkState.Status.FAILED, errorMessage));

                    }
                });

    }

    @Override
    public void loadBefore(@NonNull final LoadParams<Integer> params, @NonNull final LoadCallback<Integer, UnsplashApiModel> callback) {

        networkState.postValue(NetworkState.LOADING);

        apiListener.getDailyImages(params.key, WallHDConstants.PAGE_SIZE, WallHDConstants.CLIENT_ID)
                .enqueue(new Callback<List<UnsplashApiModel>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<UnsplashApiModel>> call, @NonNull Response<List<UnsplashApiModel>> response) {

                        if (response.isSuccessful() && response.body() != null) {

                            networkState.postValue(NetworkState.LOADED);

                            Integer key = (params.key > 1) ? params.key - 1 : null;
                            callback.onResult(response.body(), key);

                        } else {

                            Log.e(TAG, "onResponse error " + response.message());
                            networkState.postValue(new NetworkState(NetworkState.Status.FAILED, response.message()));

                        }

                    }

                    @Override
                    public void onFailure(@NonNull Call<List<UnsplashApiModel>> call, @NonNull Throwable t) {

                        String errorMessage = t.getMessage();
                        Log.e(TAG, "onFailure: " + errorMessage);
                        networkState.postValue(new NetworkState(NetworkState.Status.FAILED, errorMessage));

                    }
                });


    }

    @Override
    public void loadAfter(@NonNull final LoadParams<Integer> params, @NonNull final LoadCallback<Integer, UnsplashApiModel> callback) {

        networkState.postValue(NetworkState.LOADING);

        apiListener.getDailyImages(params.key, WallHDConstants.PAGE_SIZE, WallHDConstants.CLIENT_ID)
                .enqueue(new Callback<List<UnsplashApiModel>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<UnsplashApiModel>> call, @NonNull Response<List<UnsplashApiModel>> response) {

                        if (response.isSuccessful() && response.body() != null) {

                            networkState.postValue(NetworkState.LOADED);

                            callback.onResult(response.body(), params.key + 1);

                        } else {

                            Log.e(TAG, "onResponse error " + response.message());
                            networkState.postValue(new NetworkState(NetworkState.Status.FAILED, response.message()));

                        }

                    }

                    @Override
                    public void onFailure(@NonNull Call<List<UnsplashApiModel>> call, @NonNull Throwable t) {

                        String errorMessage = t.getMessage();
                        Log.e(TAG, "onFailure: " + errorMessage);
                        networkState.postValue(new NetworkState(NetworkState.Status.FAILED, errorMessage));

                    }
                });

    }

}
