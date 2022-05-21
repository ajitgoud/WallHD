package com.android.blackgoku.wallhd.model;

import com.google.firebase.firestore.Exclude;

public class ImageCategory {

    private String cat_image_url;
    private String cat_name;
    private String mCategoryKey;

    public ImageCategory() {
    }

    public ImageCategory(String cat_image_url, String cat_name) {
        this.cat_image_url = cat_image_url;
        this.cat_name = cat_name;
    }

    @Exclude
    public String getCategoryKey() {
        return mCategoryKey;
    }

    public void setCategoryKey(String mCategoryKey) {
        this.mCategoryKey = mCategoryKey;
    }

    public String getCat_image_url() {
        return cat_image_url;
    }

    public String getCat_name() {
        return cat_name;
    }
}
