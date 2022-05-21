package com.android.blackgoku.wallhd.pagination_components.favourites;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.DataSource;

import com.android.blackgoku.wallhd.model.FavouriteModelClass;

public class FavouriteModelClassDataSourceFactory extends DataSource.Factory<Integer, FavouriteModelClass> {

    private MutableLiveData<FavouriteModelClassDataSource> mutableLiveData = new MutableLiveData<>();

    @Override
    public DataSource<Integer, FavouriteModelClass> create() {

        FavouriteModelClassDataSource dataSource = new FavouriteModelClassDataSource();
        mutableLiveData.postValue(dataSource);

        return dataSource;
    }
}
