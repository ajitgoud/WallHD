package com.android.blackgoku.wallhd.pagination_components.recents;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.DataSource;
//import android.arch.paging.PageKeyedDataSource;

import com.android.blackgoku.wallhd.model.unsplash_api.UnsplashApiModel;


public class UnsplashApiDataSourceFactory extends DataSource.Factory<Integer, UnsplashApiModel> {

    //private MutableLiveData<PageKeyedDataSource<Integer, UnsplashApiModel>> apiModelLiveDataSource = new MutableLiveData<>();
    private MutableLiveData<UnsplashApiDataSource> listApiModel = new MutableLiveData<>();

    @Override
    public DataSource<Integer, UnsplashApiModel> create() {

        UnsplashApiDataSource apiDataSource = new UnsplashApiDataSource();
        listApiModel.postValue(apiDataSource);

        return apiDataSource;

    }

    MutableLiveData<UnsplashApiDataSource> getListApiModel() {
        return listApiModel;
    }
}
