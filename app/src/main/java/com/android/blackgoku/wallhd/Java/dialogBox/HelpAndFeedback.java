package com.android.blackgoku.wallhd.Java.dialogBox;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;

import com.android.blackgoku.wallhd.R;
import com.android.blackgoku.wallhd.adapter.HomeViewPagerAdapter;
import com.android.blackgoku.wallhd.classes.FeedbackHomeDrawer;
import com.android.blackgoku.wallhd.classes.HelpHomeDrawer;

import java.util.Objects;

public class HelpAndFeedback extends AppCompatDialogFragment {

    private ViewPager viewpagerHelpAndFeedback;
    private TabLayout tabLayoutHelpAndFeedback;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(AppCompatDialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.help_and_feedback_layout, container, false);

        findViews(view);

        setUpViewPagerAndTab();

        return view;

    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {

                v.removeOnLayoutChangeListener(this);
                showCircularReveal(view);

            }
        });

        //Objects.requireNonNull(getDialog().getWindow()).getAttributes().windowAnimations = R.style.PauseDialogAnimation;
    }

    private void showCircularReveal(View view) {

        float finalRadius = (float) Math.hypot(view.getWidth(), view.getHeight());

        Animator anim = ViewAnimationUtils.createCircularReveal(view, 0, 0, 0, finalRadius);
        anim.setDuration(700);
        anim.start();

    }

    private void hideCircularReveal(View view) {

        float finalRadius = (float) Math.hypot(view.getWidth(), view.getHeight());

        Animator anim = ViewAnimationUtils.createCircularReveal(view, 0, 0, finalRadius, 0);
        anim.setDuration(700);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                dismiss();
            }
        });
        anim.start();

    }

    private void setUpViewPagerAndTab() {

        assert getActivity() != null;

        HomeViewPagerAdapter pagerAdapter = new HomeViewPagerAdapter(getChildFragmentManager());

        pagerAdapter.addFragment(new HelpHomeDrawer(), "Help");

        pagerAdapter.addFragment(new FeedbackHomeDrawer(), "Feedback");

        viewpagerHelpAndFeedback.setAdapter(pagerAdapter);
        tabLayoutHelpAndFeedback.setupWithViewPager(viewpagerHelpAndFeedback, true);

    }

    private void findViews(final View view) {

        viewpagerHelpAndFeedback = view.findViewById(R.id.viewpagerHelpAndFeedback);
        tabLayoutHelpAndFeedback = view.findViewById(R.id.tabLayoutHelpAndFeedback);
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Help and feedback");
        toolbar.setNavigationIcon(R.drawable.dismiss_black);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hideCircularReveal(view);

            }
        });

    }


}
