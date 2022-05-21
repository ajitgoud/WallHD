package com.android.blackgoku.wallhd.pagination_components.favourites;

import android.arch.lifecycle.MutableLiveData;
import android.os.Handler;
import android.util.Log;

import com.android.blackgoku.wallhd.model.FavouriteModelClass;
import com.android.blackgoku.wallhd.utility.FirebaseFirestoreUtilityClass;
import com.android.blackgoku.wallhd.utility.SignedInUserDetails;
import com.android.blackgoku.wallhd.utility.WallHDConstants;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;


public class FirebaseQueryLiveData extends MutableLiveData<List<FavouriteModelClass>> {

    private Query query;
    private ListenerRegistration listenerRegistration;
    private MyValueListener listener;
    private boolean listenerRemovePending = false;
    private final Handler handler = new Handler();

    private List<FavouriteModelClass> modelClasses = new ArrayList<>();

    FirebaseQueryLiveData() {

        listener = new MyValueListener();
        SignedInUserDetails userDetails = new SignedInUserDetails();
        query = FirebaseFirestoreUtilityClass.getFavouritesCollectionRoot()
                .document(userDetails.getUserUID())
                .collection(WallHDConstants.FAVOURITES_COLLECTION_ROOT_REF);

    }


    private final Runnable removeListener = new Runnable() {
        @Override
        public void run() {
            listenerRegistration.remove();
            listenerRemovePending = false;
        }
    };

    @Override
    protected void onActive() {
        super.onActive();

        if (listenerRemovePending) {
            handler.removeCallbacks(removeListener);
        } else {
            listenerRegistration = query.orderBy("timeStamp", Query.Direction.DESCENDING).addSnapshotListener(listener);
        }
        listenerRemovePending = false;

        //listenerRegistration = query.orderBy("timeStamp", Query.Direction.DESCENDING).addSnapshotListener(listener);
    }

    @Override
    protected void onInactive() {
        super.onInactive();
        //listenerRegistration.remove();
        handler.postDelayed(removeListener, 2000);
        listenerRemovePending = true;

    }

    class MyValueListener implements EventListener<QuerySnapshot> {

        @Override
        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

            if (e != null | queryDocumentSnapshots == null) {
                return;
            }

            modelClasses.clear();
            Log.d("OnActivity", "FirebaseQuery");

            for (DocumentSnapshot doc : queryDocumentSnapshots) {

                modelClasses.add(doc.toObject(FavouriteModelClass.class));

            }

            setValue(modelClasses);

        }
    }

}
