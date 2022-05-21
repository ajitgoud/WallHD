package com.android.blackgoku.wallhd.utility;

import static android.content.SearchRecentSuggestionsProvider.DATABASE_MODE_QUERIES;

public class WallHDConstants {

    //
    public static final int NAVIGATION_DRAWER_CLOSING_TIME = 300;

    //Intents
    public static final String CATEGORY_NAME = "com.android.blackgoku.wallhd.Tabs.CategoryWallpaperTabFragment";


    //Unsplash Api
    public static final String BASE_URL_UNSPLASH_API = "https://api.unsplash.com/";
    public static final int PAGE_SIZE = 20;
    public static final String CLIENT_ID = "8cfa7a88e89f6fa7c7327d56b630f4f2109abde0ae81f8e446bada7aca1bcef3";


    //Firebase Firestore
    static String HELP_COLLECTION_ROOT_REF = "Help";
    static String FEEDBACK_COLLECTION_ROOT_REF = "Feedback";
    static String CATEGORIES_COLLECTION_ROOT_REF = "Categories";
    static String USER_DETAIL_COLLECTION_ROOT_REF = "UserDetail";
    public static String FAVOURITES_COLLECTION_ROOT_REF = "favourites";


    //Suggestion providers
    public final static String AUTHORITY = "com.android.blackgoku.wallhd.classes.MySuggestionProvider";
    public final static int MODE = DATABASE_MODE_QUERIES;


    //Hotlink
    public static final String USER_PROFILE_IMAGE_HOTLINK = "user_profile_image";
    public static final String USER_USERNAME_HOTLINK = "user_name";
    public static final String USER_USERLOCATION_HOTLINK = "user_location";
    public static final String USER_INSTAGRAM = "user_instagram";
    public static final String USER_TWITTER = "user_twitter";
    public static final String USER_UNSPLASH = "user_unsplash";
    public static final String USER_BIO = "user_bio";

}
