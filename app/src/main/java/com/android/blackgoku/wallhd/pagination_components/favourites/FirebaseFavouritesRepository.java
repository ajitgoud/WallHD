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

public class FirebaseFavouritesRepository {

    private SignedInUserDetails userDetails;

    public FirebaseFavouritesRepository() {
        userDetails = new SignedInUserDetails();
    }

    public void getFavouriteImages(final int startPos, final int listSize, @NonNull final ItemKeyedDataSource.LoadCallback<FavouriteModelClass> loadCallback) {

        FirebaseFirestoreUtilityClass.getFavouritesCollectionRoot()
                .document(userDetails.getUserUID())
                .collection(WallHDConstants.FAVOURITES_COLLECTION_ROOT_REF)
                .whereGreaterThan("timeStamp", startPos)
                .limit(listSize)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                        if (e != null | queryDocumentSnapshots == null) {

                            return;

                        }

                        List<FavouriteModelClass> modelClasses = new ArrayList<>();

                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                            modelClasses.add(documentSnapshot.toObject(FavouriteModelClass.class));

                        }

                        if (modelClasses.size() == 0)
                            return;

                        if (loadCallback instanceof ItemKeyedDataSource.LoadInitialCallback) {

                            ((ItemKeyedDataSource.LoadInitialCallback<FavouriteModelClass>) loadCallback).onResult(modelClasses, startPos, modelClasses.size());

                        } else {

                            loadCallback.onResult(modelClasses);

                        }

                    }
                });

    }


}
