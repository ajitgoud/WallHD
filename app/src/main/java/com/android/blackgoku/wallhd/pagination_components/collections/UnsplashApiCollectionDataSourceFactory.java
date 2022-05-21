package com.android.blackgoku.wallhd.pagination_components.collections;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.DataSource;
//import android.arch.paging.PageKeyedDataSource;

import com.android.blackgoku.wallhd.model.unsplash_api.UnsplashApiModel;


public class UnsplashApiCollectionDataSourceFactory extends DataSource.Factory<Integer, UnsplashApiModel> {

    //private MutableLiveData<PageKeyedDataSource<Integer, UnsplashApiModel>> apiModelLiveDataSource = new MutableLiveData<>();
    private MutableLiveData<UnsplashApiCollectionDataSource> mApiDataSource = new MutableLiveData<>();
    private String category;

    UnsplashApiCollectionDataSourceFactory(String category) {
        this.category = category;
    }

    @Override
    public DataSource<Integer, UnsplashApiModel> create() {

        UnsplashApiCollectionDataSource apiDataSource = new UnsplashApiCollectionDataSource(category);
        mApiDataSource.postValue(apiDataSource);
        return apiDataSource;

    }

    MutableLiveData<UnsplashApiCollectionDataSource> getApiDataSource() {
        return mApiDataSource;
    }
}
