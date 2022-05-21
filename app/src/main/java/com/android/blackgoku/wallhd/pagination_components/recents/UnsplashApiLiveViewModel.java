package com.android.blackgoku.wallhd.pagination_components.recents;

import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.support.annotation.NonNull;

import com.android.blackgoku.wallhd.model.NetworkState;
import com.android.blackgoku.wallhd.model.unsplash_api.UnsplashApiModel;
import com.android.blackgoku.wallhd.utility.WallHDConstants;

import java.util.Objects;


public class UnsplashApiLiveViewModel extends AndroidViewModel {

    private LiveData<PagedList<UnsplashApiModel>> apiModel;
    private LiveData<NetworkState> networkStateLiveData;
    private LiveData<NetworkState> initialNetworkStateLiveData;

    public UnsplashApiLiveViewModel(@NonNull Application application) {
        super(application);

        UnsplashApiDataSourceFactory dataSourceFactory = new UnsplashApiDataSourceFactory();

        PagedList.Config config = (new PagedList.Config.Builder())
                .setEnablePlaceholders(false)
                .setPageSize(WallHDConstants.PAGE_SIZE)
                .setInitialLoadSizeHint(WallHDConstants.PAGE_SIZE * 2)
                .build();

        networkStateLiveData = Transformations.switchMap(dataSourceFactory.getListApiModel(), new Function<UnsplashApiDataSource, LiveData<NetworkState>>() {
            @Override
            public LiveData<NetworkState> apply(UnsplashApiDataSource source) {
                return source.getNetworkState();
            }
        });

        initialNetworkStateLiveData = Transformations.switchMap(dataSourceFactory.getListApiModel(), new Function<UnsplashApiDataSource, LiveData<NetworkState>>() {
            @Override
            public LiveData<NetworkState> apply(UnsplashApiDataSource source) {
                return source.getInitialLoading();
            }
        });



        apiModel = new LivePagedListBuilder<>(dataSourceFactory, config)
                .build();

    }

    public void swipeToRefresh(){

            Objects.requireNonNull(apiModel.getValue()).getDataSource().invalidate();
    }

    public LiveData<PagedList<UnsplashApiModel>> getApiModels() {

        return apiModel;

    }

    public LiveData<NetworkState> getNetworkStateLiveData() {
        return networkStateLiveData;
    }

    public LiveData<NetworkState> getInitialNetworkStateLiveData() {
        return initialNetworkStateLiveData;
    }
}
