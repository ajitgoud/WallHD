package com.android.blackgoku.wallhd.pagination_components.favourites;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.support.annotation.NonNull;

import com.android.blackgoku.wallhd.model.FavouriteModelClass;
import com.android.blackgoku.wallhd.utility.WallHDConstants;

import java.util.List;

public class FavouritesViewModel extends AndroidViewModel {

    private LiveData<PagedList<FavouriteModelClass>> favouriteLiveModel;
    private MutableLiveData<List<FavouriteModelClass>> getList;

    public FavouritesViewModel(@NonNull Application application) {
        super(application);

        FavouriteModelClassDataSourceFactory dataSourceFactory = new FavouriteModelClassDataSourceFactory();

        /*PagedList.Config config = (new PagedList.Config.Builder())
                .setEnablePlaceholders(false)
                .setPageSize(10)
                .setInitialLoadSizeHint(10)
                .build();

        favouriteLiveModel = new LivePagedListBuilder<>(dataSourceFactory, config)
                .build();*/


        getList = new FirebaseQueryLiveData();
    }

    public LiveData<PagedList<FavouriteModelClass>> getFavouriteLiveModel() {

        return favouriteLiveModel;

    }

    public MutableLiveData<List<FavouriteModelClass>> getGetList() {
        return getList;
    }
}
