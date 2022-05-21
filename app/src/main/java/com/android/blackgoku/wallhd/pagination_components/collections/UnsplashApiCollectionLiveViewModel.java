package com.android.blackgoku.wallhd.pagination_components.collections;

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


public class UnsplashApiCollectionLiveViewModel extends AndroidViewModel {

    private LiveData<PagedList<UnsplashApiModel>> apiModel;
   // public LiveData<PageKeyedDataSource<Integer, UnsplashApiModel>> apiModelLiveDataSource;
    private LiveData<NetworkState> networkStateLiveData;
    private LiveData<NetworkState> initialNetworkStateLiveData;

    UnsplashApiCollectionLiveViewModel(@NonNull Application application, String category) {

        super(application);
        UnsplashApiCollectionDataSourceFactory dataSourceFactory = new UnsplashApiCollectionDataSourceFactory(category);

        PagedList.Config config = new PagedList.Config.Builder()
                .setPageSize(WallHDConstants.PAGE_SIZE)
                .setInitialLoadSizeHint(WallHDConstants.PAGE_SIZE * 2)
                .build();

        networkStateLiveData = Transformations.switchMap(dataSourceFactory.getApiDataSource(), new Function<UnsplashApiCollectionDataSource, LiveData<NetworkState>>() {
            @Override
            public LiveData<NetworkState> apply(UnsplashApiCollectionDataSource source) {
                return source.getNetworkState();
            }
        });

        initialNetworkStateLiveData = Transformations.switchMap(dataSourceFactory.getApiDataSource(), new Function<UnsplashApiCollectionDataSource, LiveData<NetworkState>>() {
            @Override
            public LiveData<NetworkState> apply(UnsplashApiCollectionDataSource source) {
                return source.getInitialLoading();
            }
        });

        apiModel = new LivePagedListBuilder<>(dataSourceFactory, config)
                .setInitialLoadKey(1)
                .build();

    }

   public void setRefresh(){

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
