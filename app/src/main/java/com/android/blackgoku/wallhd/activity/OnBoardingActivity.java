package com.android.blackgoku.wallhd.activity;

import android.content.Intent;
import android.support.design.button.MaterialButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.text.Html;
import android.transition.Explode;
import android.transition.Fade;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

import com.android.blackgoku.wallhd.R;
import com.android.blackgoku.wallhd.adapter.OnBoardingSliderAdapter;

public class OnBoardingActivity extends AppCompatActivity implements View.OnClickListener {

    private ViewPager onBoardingViewPager;
    private LinearLayout dotIndicatorLinearLayout;
    private OnBoardingSliderAdapter sliderAdapter;
    private AppCompatTextView dots[];
    private MaterialButton navigateBackMaterialButton, navigateNextMaterialButton, skipMaterialButton;
    private int currentPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_boarding);

        currentPage = 0;

        findView();

        setViewPager();

        setClickListeners();

        createDotIndicators(0);

    }


    private void setClickListeners() {

        navigateBackMaterialButton.setOnClickListener(this);
        navigateNextMaterialButton.setOnClickListener(this);
        skipMaterialButton.setOnClickListener(this);

    }

    private void setViewPager() {

        sliderAdapter = new OnBoardingSliderAdapter(this);

        onBoardingViewPager.setAdapter(sliderAdapter);
        onBoardingViewPager.addOnPageChangeListener(pageChangeListener);
        onBoardingViewPager.setPageTransformer(true, new ZoomOutPageTransformer());


    }

    private void createDotIndicators(int position) {

        dots = new AppCompatTextView[OnBoardingSliderAdapter.sliderImages.length];
        dotIndicatorLinearLayout.removeAllViews();

        for (int i = 0; i < dots.length; i++) {

            dots[i] = new AppCompatTextView(this);
            dots[i].setText(Html.fromHtml("&#8226"));
            dots[i].setTextSize(TypedValue.COMPLEX_UNIT_SP, 36);
            dots[i].setTextColor(getResources().getColor(R.color.colorTransparentWhite));
            dotIndicatorLinearLayout.addView(dots[i]);

        }

        if (dots.length > 0) {

            dots[position].setTextColor(getResources().getColor(R.color.colorPrimary));

        }

    }

    private void findView() {

        onBoardingViewPager = findViewById(R.id.onBoardingViewPager);
        dotIndicatorLinearLayout = findViewById(R.id.dotIndicatorLinearLayout);
        navigateBackMaterialButton = findViewById(R.id.navigateBackMaterialButton);
        navigateNextMaterialButton = findViewById(R.id.navigateNextMaterialButton);
        skipMaterialButton = findViewById(R.id.skipMaterialButton);

    }

    ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int i, float v, int i1) {

        }

        @Override
        public void onPageSelected(int i) {

            createDotIndicators(i);

            currentPage = i;

            if (i == 0) {

                navigateBackMaterialButton.setEnabled(false);
                navigateBackMaterialButton.setVisibility(View.GONE);

            } else if (i == dots.length - 1) {

                navigateNextMaterialButton.setEnabled(false);
                navigateNextMaterialButton.setVisibility(View.GONE);
                skipMaterialButton.setText(R.string.get_started);

            }else{

                navigateBackMaterialButton.setEnabled(true);
                navigateBackMaterialButton.setVisibility(View.VISIBLE);
                navigateNextMaterialButton.setEnabled(true);
                navigateNextMaterialButton.setVisibility(View.VISIBLE);
                skipMaterialButton.setText(R.string.skip);

            }

        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    };

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.navigateBackMaterialButton:
                goBack();
                break;

            case R.id.navigateNextMaterialButton:
                goNext();
                break;

            case R.id.skipMaterialButton:
                skip();
                break;

        }

    }

    private void skip() {

        startActivity(new Intent(this, HomeActivity.class));
        finish();

    }

    private void goNext() {

        onBoardingViewPager.setCurrentItem(currentPage + 1);

    }

    private void goBack() {

        onBoardingViewPager.setCurrentItem(currentPage - 1);

    }
/*
    public class DepthPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.75f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0f);

            } else if (position <= 0) { // [-1,0]
                // Use the default slide transition when moving to the left page
                view.setAlpha(1f);
                view.setTranslationX(0f);
                view.setScaleX(1f);
                view.setScaleY(1f);

            } else if (position <= 1) { // (0,1]
                // Fade the page out.
                view.setAlpha(1 - position);

                // Counteract the default slide transition
                view.setTranslationX(pageWidth * -position);

                // Scale the page down (between MIN_SCALE and 1)
                float scaleFactor = MIN_SCALE
                        + (1 - MIN_SCALE) * (1 - Math.abs(position));
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0f);
            }
        }
    }*/

    public class ZoomOutPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.70f;
        private static final float MIN_ALPHA = 0.3f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();
            int pageHeight = view.getHeight();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0f);

            } else if (position <= 1) { // [-1,1]
                //(horzMargin - vertMargin / 2)
                //(-horzMargin + vertMargin / 2)
                // Modify the default slide transition to shrink the page as well
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                if (position < 0) {
                    view.setTranslationX(horzMargin - vertMargin / 2);
                } else {
                    view.setTranslationX(-horzMargin + vertMargin / 2);
                }


                // Scale the page down (between MIN_SCALE and 1)
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

                // Fade the page relative to its size.
                view.setAlpha(MIN_ALPHA +
                        (scaleFactor - MIN_SCALE) /
                                (1 - MIN_SCALE) * (1 - MIN_ALPHA));

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0f);
            }
        }
    }



}
