package com.android.blackgoku.wallhd.pagination_components.favourites;

import android.arch.paging.ItemKeyedDataSource;
import android.arch.paging.PageKeyedDataSource;
import android.support.annotation.NonNull;

import com.android.blackgoku.wallhd.model.FavouriteModelClass;
import com.android.blackgoku.wallhd.utility.FirebaseFirestoreUtilityClass;
import com.android.blackgoku.wallhd.utility.SignedInUserDetails;
import com.android.blackgoku.wallhd.utility.WallHDConstants;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class FavouriteModelClassDataSource extends ItemKeyedDataSource<Integer, FavouriteModelClass> {

    private FirebaseFavouritesRepository repository;
    private SignedInUserDetails userDetails;

    FavouriteModelClassDataSource() {

        repository = new FirebaseFavouritesRepository();
        userDetails = new SignedInUserDetails();

    }


    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull LoadInitialCallback<FavouriteModelClass> callback) {

        int startPos = 1;
        repository.getFavouriteImages(startPos, params.requestedLoadSize, callback);

    }

    @Override
    public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<FavouriteModelClass> callback) {

        repository.getFavouriteImages(params.key, params.requestedLoadSize, callback);

    }

    @Override
    public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<FavouriteModelClass> callback) {
        repository.getFavouriteImages(params.key, params.requestedLoadSize, callback);
    }

    @NonNull
    @Override
    public Integer getKey(@NonNull FavouriteModelClass item) {
        return item.getTimeStamp();
    }


}
