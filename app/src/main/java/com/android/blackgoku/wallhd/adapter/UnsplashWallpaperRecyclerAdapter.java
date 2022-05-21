package com.android.blackgoku.wallhd.adapter;

import android.arch.paging.PagedListAdapter;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.widget.CircularProgressDrawable;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.blackgoku.wallhd.R;
import com.android.blackgoku.wallhd.activity.GlideApp;
import com.android.blackgoku.wallhd.model.unsplash_api.UnsplashApiModel;
import com.android.blackgoku.wallhd.model.unsplash_api.urls.ApiUrlsModel;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;


public class UnsplashWallpaperRecyclerAdapter extends PagedListAdapter<UnsplashApiModel, UnsplashWallpaperRecyclerAdapter.WallpaperViewHolder> {

    private Context mContext;
    private static final String TAG = UnsplashWallpaperRecyclerAdapter.class.getSimpleName();

    private UnsplashLatestWallpaperClickListener listener;

    private static DiffUtil.ItemCallback<UnsplashApiModel> DIFF_CALL_BACK = new DiffUtil.ItemCallback<UnsplashApiModel>() {
        @Override
        public boolean areItemsTheSame(@NonNull UnsplashApiModel oldApiModel, @NonNull UnsplashApiModel newApiModel) {
            Log.d("Id's", oldApiModel.getId() + " next " + newApiModel.getId());
            return oldApiModel.getId().equals(newApiModel.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull UnsplashApiModel oldApiModel, @NonNull UnsplashApiModel newApiModel) {
            return oldApiModel.equals(newApiModel);
        }
    };

    public UnsplashWallpaperRecyclerAdapter(Context context) {
        super(DIFF_CALL_BACK);
        mContext = context;
    }


    @NonNull
    @Override
    public WallpaperViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.wallpaper_recycler_item, viewGroup, false);
        return new WallpaperViewHolder(view, listener);

    }

    @Override
    public void onBindViewHolder(@NonNull final WallpaperViewHolder wallpaperViewHolder, int i) {

        UnsplashApiModel model = getItem(i);
        Log.d(TAG, "Running");
        if (model != null) {

            CircularProgressDrawable progressDrawable = new CircularProgressDrawable(mContext);
            progressDrawable.setBackgroundColor(R.color.iconColor);
            progressDrawable.setStyle(CircularProgressDrawable.LARGE);


            ApiUrlsModel urlsModel = model.getUrls();
            final String regularImageUrl = urlsModel.getSmall();

            RequestOptions requestOptions = new RequestOptions().centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).override(18,18);
            RequestBuilder<Drawable> requestBuilder = GlideApp.with(mContext).load(urlsModel.getThumb()).apply(requestOptions);



            GlideApp.with(mContext)
                    .load(regularImageUrl)
                    .thumbnail(requestBuilder)
                    .placeholder(progressDrawable)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
                    .into(wallpaperViewHolder.uploadImageView);
        }

    }

    public UnsplashApiModel getApiModel(int pos) {

        return getItem(pos);

    }

    class WallpaperViewHolder extends RecyclerView.ViewHolder {

        private AppCompatImageView uploadImageView;

        WallpaperViewHolder(@NonNull View itemView, final UnsplashLatestWallpaperClickListener listener) {
            super(itemView);

            uploadImageView = itemView.findViewById(R.id.uploadImageView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {

                        listener.LatestWallpaperClickListener(getAdapterPosition(), v);

                    }

                }
            });

        }
    }

    public interface UnsplashLatestWallpaperClickListener {

        void LatestWallpaperClickListener(int position, View view);

    }

    public void setOnUnsplashLatestWallpaperClickListener(UnsplashLatestWallpaperClickListener listener) {

        this.listener = listener;

    }

}
