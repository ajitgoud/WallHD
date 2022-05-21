package com.android.blackgoku.wallhd.pagination_components.collections;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

public class ViewModelFactory extends ViewModelProvider.AndroidViewModelFactory {

    private Application application;
    private String category;

    /**
     * Creates a {@code AndroidViewModelFactory}
     *
     * @param application an application to pass in {@link AndroidViewModel}
     */

    public ViewModelFactory(@NonNull Application application, String category) {
        super(application);
        this.application = application;
        this.category = category;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new UnsplashApiCollectionLiveViewModel(application, category);
    }
}
