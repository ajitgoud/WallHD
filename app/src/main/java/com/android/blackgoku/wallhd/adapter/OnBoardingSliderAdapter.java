package com.android.blackgoku.wallhd.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.android.blackgoku.wallhd.R;

public class OnBoardingSliderAdapter extends PagerAdapter {

    private Context context;
    private AppCompatTextView onBoardingTextHeader, onBoardingTextDescription;
    private AppCompatImageView onBoardingImageView;

    public OnBoardingSliderAdapter(Context context) {
        this.context = context;
    }

    public static int sliderImages[] = {
            R.drawable.ic_photo_white,
            R.drawable.free_tag,
    };

    public static String sliderHeader[] = {

            "High quality walls",
            "Free to use",

    };

    public static String sliderDescription[] = {

            "Enjoy high quality wallpapers from unsplash",
            "All the wallpapers from are free to use, even commercially!",

    };

    @Override
    public int getCount() {
        return sliderImages.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        View view = inflater.inflate(R.layout.slider_layout, container, false);

        findViews(view, position);

        container.addView(view);

        return view;

    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

        container.removeView((RelativeLayout) object);

    }

    private void findViews(View view, int position) {

        onBoardingImageView = view.findViewById(R.id.onBoardingImageView);
        onBoardingTextHeader = view.findViewById(R.id.onBoardingTextHeader);
        onBoardingTextDescription = view.findViewById(R.id.onBoardingTextDescription);

        onBoardingImageView.setImageResource(sliderImages[position]);
        onBoardingTextHeader.setText(sliderHeader[position]);
        onBoardingTextDescription.setText(sliderDescription[position]);

    }
}
