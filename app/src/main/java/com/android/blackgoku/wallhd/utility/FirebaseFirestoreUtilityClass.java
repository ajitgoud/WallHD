package com.android.blackgoku.wallhd.utility;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

public class FirebaseFirestoreUtilityClass {

    private static FirebaseFirestore getFirebaseFirestoreInstance() {


        return FirebaseFirestore.getInstance();

    }

    public static FirebaseAuth getFirebaseAuth(){

        return FirebaseAuth.getInstance();

    }

    static FirebaseUser getCurrentSignedUser(){

        return getFirebaseAuth().getCurrentUser();

    }

    public static boolean currentUserIsNotNull(){

        return getCurrentSignedUser() != null;

    }
    public static CollectionReference getHelpCollectionRoot() {

        return getFirebaseFirestoreInstance().collection(WallHDConstants.HELP_COLLECTION_ROOT_REF);

    }



    public static CollectionReference getFeedbackCollectionRoot() {

        return getFirebaseFirestoreInstance().collection(WallHDConstants.FEEDBACK_COLLECTION_ROOT_REF);

    }

    public static CollectionReference getCategoriesCollectionRoot() {

        return getFirebaseFirestoreInstance().collection(WallHDConstants.CATEGORIES_COLLECTION_ROOT_REF);

    }

    public static CollectionReference getUserDetailCollectionRoot() {

        return getFirebaseFirestoreInstance().collection(WallHDConstants.USER_DETAIL_COLLECTION_ROOT_REF);

    }

    public static CollectionReference getFavouritesCollectionRoot() {

        return getUserDetailCollectionRoot();

    }


}
