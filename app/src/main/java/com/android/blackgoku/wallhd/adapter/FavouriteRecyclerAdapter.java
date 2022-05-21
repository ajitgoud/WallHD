package com.android.blackgoku.wallhd.adapter;

import android.arch.paging.PagedListAdapter;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.blackgoku.wallhd.R;
import com.android.blackgoku.wallhd.activity.GlideApp;
import com.android.blackgoku.wallhd.model.FavouriteModelClass;
import com.android.blackgoku.wallhd.model.unsplash_api.UnsplashApiModel;

public class FavouriteRecyclerAdapter extends ListAdapter<FavouriteModelClass, FavouriteRecyclerAdapter.FavouriteViewHolder> {

    private Context context;
    private FavWallpaperClickListener listener;

    public FavouriteRecyclerAdapter(Context context) {
        super(DIFF_CALLBACK);
        this.context = context;
    }

    private static DiffUtil.ItemCallback<FavouriteModelClass> DIFF_CALLBACK = new DiffUtil.ItemCallback<FavouriteModelClass>() {
        @Override
        public boolean areItemsTheSame(@NonNull FavouriteModelClass oldModel, @NonNull FavouriteModelClass newModel) {
            return oldModel.getApiModel().getId().equals(newModel.getApiModel().getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull FavouriteModelClass oldModel, @NonNull FavouriteModelClass newModel) {
            return oldModel.getApiModel().equals(newModel.getApiModel()) && oldModel.getTimeStamp() == newModel.getTimeStamp();
        }
    };


    @NonNull
    @Override
    public FavouriteViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(context).inflate(R.layout.wallpaper_recycler_item, viewGroup, false);

        return new FavouriteViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull FavouriteViewHolder favouriteViewHolder, int i) {

        FavouriteModelClass model = getItem(i);

        UnsplashApiModel apiModel = model.getApiModel();

        if (apiModel != null) {

            GlideApp.with(context)
                    .load(apiModel.getUrls().getRegular())
                    .placeholder(R.drawable.placeholder)
                    .into(favouriteViewHolder.uploadImageView);

        }


    }

    public UnsplashApiModel getItemAtPosition(int i){

        return getItem(i).getApiModel();
    }


    class FavouriteViewHolder extends RecyclerView.ViewHolder {

        AppCompatImageView uploadImageView;

        public FavouriteViewHolder(@NonNull final View itemView, final FavWallpaperClickListener listener) {
            super(itemView);

            uploadImageView = itemView.findViewById(R.id.uploadImageView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(listener != null && getAdapterPosition() != RecyclerView.NO_POSITION){

                        listener.wallpaperClickListener(getAdapterPosition(), itemView);

                    }


                }
            });

        }

    }

    public interface FavWallpaperClickListener{

        void wallpaperClickListener(int position, View view);

    }

    public void setOnFavWallpaperClickListener(FavWallpaperClickListener listener){

        this.listener = listener;

    }

}
