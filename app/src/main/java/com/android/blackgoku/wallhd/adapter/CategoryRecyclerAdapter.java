package com.android.blackgoku.wallhd.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.blackgoku.wallhd.activity.GlideApp;
import com.android.blackgoku.wallhd.model.ImageCategory;
import com.android.blackgoku.wallhd.R;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

public class CategoryRecyclerAdapter extends RecyclerView.Adapter<CategoryRecyclerAdapter.CategoryViewHolder> {


    private Context mContext;
    private List<ImageCategory> categoryImages;
    private OnItemClickListener mListener;

    public CategoryRecyclerAdapter(Context mContext, List<ImageCategory> categoryImages) {
        this.mContext = mContext;
        this.categoryImages = categoryImages;
    }


    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.recycler_item_category, viewGroup, false);
        return new CategoryViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder categoryViewHolder, int i) {

        ImageCategory imageCategory = categoryImages.get(i);

        categoryViewHolder.categoryTextView.setText(imageCategory.getCat_name());
        GlideApp.with(mContext)
                .load(imageCategory.getCat_image_url())
                .placeholder(R.drawable.placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(categoryViewHolder.categoryImageView);

    }

    @Override
    public int getItemCount() {
        return categoryImages.size();
    }

    class CategoryViewHolder extends  RecyclerView.ViewHolder{

        private AppCompatTextView categoryTextView;
        private AppCompatImageView categoryImageView;

        CategoryViewHolder(@NonNull View itemView) {
            super(itemView);

            categoryTextView = itemView.findViewById(R.id.categoryTextView);
            categoryImageView = itemView.findViewById(R.id.categoryImageView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int pos = getAdapterPosition();

                    if(mListener != null && pos != RecyclerView.NO_POSITION ){

                        mListener.onItemClick(pos, v);

                    }

                }
            });

        }
    }

    public interface OnItemClickListener{

        void onItemClick(int i, View view);

    }

    public void setOnItemClickListener(OnItemClickListener listener){

        mListener = listener;

    }

}
