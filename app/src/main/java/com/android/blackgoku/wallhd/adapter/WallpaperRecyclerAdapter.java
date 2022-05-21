package com.android.blackgoku.wallhd.adapter;

import android.arch.paging.PagedListAdapter;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.blackgoku.wallhd.R;
import com.android.blackgoku.wallhd.activity.GlideApp;
import com.android.blackgoku.wallhd.model.unsplash_api.UnsplashApiModel;
import com.bumptech.glide.load.engine.DiskCacheStrategy;


public class WallpaperRecyclerAdapter extends PagedListAdapter<UnsplashApiModel,WallpaperRecyclerAdapter.WallpaperViewHolder> {

    private Context mContext;
    private UnsplashWallpaperClickListener listener;

    public WallpaperRecyclerAdapter(Context context) {
        super(DIFF_UTIL);
        mContext = context;

    }

    private static DiffUtil.ItemCallback<UnsplashApiModel> DIFF_UTIL = new DiffUtil.ItemCallback<UnsplashApiModel>() {
        @Override
        public boolean areItemsTheSame(@NonNull UnsplashApiModel oldModel, @NonNull UnsplashApiModel newModel) {
            return oldModel.getId().equals(newModel.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull UnsplashApiModel oldModel, @NonNull UnsplashApiModel newModel) {
            return oldModel.equals(newModel);
        }
    };


    @NonNull
    @Override
    public WallpaperViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.wallpaper_recycler_item, viewGroup, false);
        super.getItem(i);
        return new WallpaperViewHolder(view, listener);

    }

    @Override
    public void onBindViewHolder(@NonNull WallpaperViewHolder wallpaperViewHolder, int i) {

        UnsplashApiModel apiModel = getItem(i);

        if(apiModel != null) {

            String imageURL = apiModel.getUrls().getSmall();

            GlideApp.with(mContext)
                    .load(imageURL)
                    .placeholder(R.drawable.placeholder)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(wallpaperViewHolder.uploadImageView);

        }else{

            Toast.makeText(mContext, "Null", Toast.LENGTH_SHORT).show();

        }

    }

    public UnsplashApiModel getApiModel(int pos) {

        return getItem(pos);

    }

    class WallpaperViewHolder extends RecyclerView.ViewHolder {

        private AppCompatImageView uploadImageView;

        WallpaperViewHolder(@NonNull View itemView, final UnsplashWallpaperClickListener listener) {
            super(itemView);

            uploadImageView = itemView.findViewById(R.id.uploadImageView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {

                        listener.wallpaperClickListener(getAdapterPosition(), v);

                    }

                }
            });

        }
    }

    public interface UnsplashWallpaperClickListener{

        void wallpaperClickListener(int position, View view);

    }

    public void setOnUnsplashWallpaperClickListener(UnsplashWallpaperClickListener listener){

        this.listener = listener;

    }

}
