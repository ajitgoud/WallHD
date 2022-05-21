package com.android.blackgoku.wallhd.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.ActivityOptions;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.util.Pair;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.blackgoku.wallhd.adapter.WallpaperRecyclerAdapter;
import com.android.blackgoku.wallhd.R;
import com.android.blackgoku.wallhd.model.NetworkState;
import com.android.blackgoku.wallhd.model.unsplash_api.UnsplashApiModel;
import com.android.blackgoku.wallhd.pagination_components.collections.UnsplashApiCollectionLiveViewModel;
import com.android.blackgoku.wallhd.pagination_components.collections.ViewModelFactory;
import com.android.blackgoku.wallhd.utility.WallHDConstants;

import java.util.Objects;

import es.dmoral.toasty.Toasty;

import static com.android.blackgoku.wallhd.tabs.LatestWallpaperTabFragment.SHARED_TRANSITION_NAME;
import static com.android.blackgoku.wallhd.tabs.LatestWallpaperTabFragment.WALLPAPER_VIEW_INTENT_UNSPLASH_API_MODEL;
import static com.android.blackgoku.wallhd.tabs.LatestWallpaperTabFragment.WALLPAPER_VIEW_REVEAL_X;
import static com.android.blackgoku.wallhd.tabs.LatestWallpaperTabFragment.WALLPAPER_VIEW_REVEAL_Y;

public class WallpaperActivity extends AppCompatActivity {

    private RecyclerView mRecyclerViewWallpaper;
    private WallpaperRecyclerAdapter mWallpaperRecyclerAdapter;
    private ProgressBar wallpaperProgressBarCenter, wallpaperProgressBarBelowRV;
    private CardView internetStatusCardView;
    private AppCompatTextView internetStatusAppCompatText;
    private HomeBroadcastReceiver homeBroadcastReceiver;
    private SwipeRefreshLayout swipeRefreshLayout;
    private UnsplashApiCollectionLiveViewModel viewModel;
    private LinearLayout noConnectionLinearLayout;
    private String category_Name;
    private View rootLayout;
    private int revealX;
    private int revealY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_wallpaper);


        //getWindow().setReenterTransition(new Explode().setDuration(500));

        Intent receivedIntent = getIntent();
        category_Name = receivedIntent.getStringExtra(WallHDConstants.CATEGORY_NAME);

        rootLayout = findViewById(R.id.wallpaperActivityRootLayout);

        if (savedInstanceState == null && receivedIntent.hasExtra(WALLPAPER_VIEW_REVEAL_X) && receivedIntent.hasExtra(WALLPAPER_VIEW_REVEAL_Y)) {

            rootLayout.setVisibility(View.INVISIBLE);

            revealX = receivedIntent.getIntExtra(WALLPAPER_VIEW_REVEAL_X, 0);
            revealY = receivedIntent.getIntExtra(WALLPAPER_VIEW_REVEAL_Y, 0);

            ViewTreeObserver viewTreeObserver = rootLayout.getViewTreeObserver();
            if (viewTreeObserver.isAlive()) {

                viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {

                        revealActivity(revealX, revealY);
                        rootLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                    }
                });

            }

        } else {

            rootLayout.setVisibility(View.VISIBLE);

        }

        setToolbar();

        findViews();

        setRecyclerView();

        setViewModel();

        setListeners();

        setBroadcasters();

    }

    private void revealActivity(int revealX, int revealY) {

        float finalRadius = (float) Math.hypot(rootLayout.getWidth(), rootLayout.getHeight());

        Animator circularAnim = ViewAnimationUtils.createCircularReveal(rootLayout, revealX, revealY, 0, finalRadius);
        circularAnim.setDuration(500);
        circularAnim.setInterpolator(new AccelerateInterpolator());
        rootLayout.setVisibility(View.VISIBLE);
        circularAnim.start();

    }

    private void unRevealActivity() {

        float finalRadius = (float) Math.hypot(rootLayout.getWidth(), rootLayout.getHeight());

        Animator circularAnim = ViewAnimationUtils.createCircularReveal(rootLayout, revealX, revealY, finalRadius, 0);
        circularAnim.setDuration(500);
        circularAnim.setInterpolator(new DecelerateInterpolator());
        circularAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                rootLayout.setVisibility(View.INVISIBLE);
                finishAfterTransition();

            }
        });

        circularAnim.start();

    }

    @Override
    public void onBackPressed() {

        unRevealActivity();

    }

    private void setBroadcasters() {

        homeBroadcastReceiver = new HomeBroadcastReceiver();
        registerReceiver(homeBroadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

    }

    private void setListeners() {

        swipeRefreshLayout.setOnRefreshListener(refreshListener);
        mWallpaperRecyclerAdapter.setOnUnsplashWallpaperClickListener(unsplashWallpaperClickListener);

    }

    private void setViewModel() {

        viewModel = ViewModelProviders.of(this, new ViewModelFactory(getApplication(), category_Name)).get(UnsplashApiCollectionLiveViewModel.class);

        viewModel.getApiModels().observe(this, apiModelObserver);

        viewModel.getInitialNetworkStateLiveData().observe(this, initialNetworkState);

        viewModel.getNetworkStateLiveData().observe(this, networkState);

    }

    private void setRecyclerView() {

        mRecyclerViewWallpaper.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        mWallpaperRecyclerAdapter = new WallpaperRecyclerAdapter(this);

        mRecyclerViewWallpaper.setAdapter(mWallpaperRecyclerAdapter);

    }

    private void findViews() {

        noConnectionLinearLayout = findViewById(R.id.noConnectionLinearLayout);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.iconColor));

        internetStatusCardView = findViewById(R.id.internetStatusCardView);

        internetStatusAppCompatText = findViewById(R.id.internetStatusAppCompatText);

        wallpaperProgressBarCenter = findViewById(R.id.wallpaperProgressBarCenter);
        wallpaperProgressBarBelowRV = findViewById(R.id.wallpaperProgressBarBelowRV);
        wallpaperProgressBarCenter.setVisibility(View.VISIBLE);
        wallpaperProgressBarBelowRV.setVisibility(View.INVISIBLE);

        mRecyclerViewWallpaper = findViewById(R.id.recyclerViewWallpaper);

    }

    private void setToolbar() {

        Toolbar toolbar = findViewById(R.id.toolbarWallpaper);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);

        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                unRevealActivity();

//                finish();
//                overridePendingTransition(R.anim.grow_in, R.anim.grow_out);
            }
        });
        Objects.requireNonNull(getSupportActionBar()).setTitle(category_Name);

    }

    private Observer<PagedList<UnsplashApiModel>> apiModelObserver = new Observer<PagedList<UnsplashApiModel>>() {
        @Override
        public void onChanged(@Nullable PagedList<UnsplashApiModel> unsplashApiModels) {

            mWallpaperRecyclerAdapter.submitList(unsplashApiModels);

        }
    };


    private Observer<NetworkState> initialNetworkState = new Observer<NetworkState>() {
        @Override
        public void onChanged(@Nullable NetworkState networkState) {

            if (networkState == NetworkState.LOADING) {

                wallpaperProgressBarCenter.setVisibility(View.VISIBLE);
                ViewPropertyAnimator animator = noConnectionLinearLayout.animate().alpha(0).setDuration(1000);
                animator.start();
                noConnectionLinearLayout.setVisibility(View.GONE);

            } else {

                if (networkState == NetworkState.LOADED) {

                    wallpaperProgressBarCenter.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                } else {

                    assert networkState != null;
                    if (networkState.getStatus() == NetworkState.Status.FAILED) {

                        swipeRefreshLayout.setRefreshing(false);
                        wallpaperProgressBarCenter.setVisibility(View.GONE);
                        noConnectionLinearLayout.setVisibility(View.VISIBLE);
                        ViewPropertyAnimator animator = noConnectionLinearLayout.animate().alpha(1).setDuration(1000);
                        animator.start();

                    }

                }

            }

        }
    };


    private Observer<NetworkState> networkState = new Observer<NetworkState>() {
        @Override
        public void onChanged(@Nullable NetworkState networkState) {

            assert networkState != null;
            if (networkState == NetworkState.LOADING) {

                //wallpaperProgressBarBelowRV.setVisibility(View.VISIBLE);

            } else {

                if (networkState == NetworkState.LOADED) {

                    wallpaperProgressBarBelowRV.setVisibility(View.INVISIBLE);

                } else {

                    if (networkState.getStatus() == NetworkState.Status.FAILED) {

                        Toasty.error(getApplicationContext(), networkState.getMsg(), Toast.LENGTH_SHORT).show();

                    }

                }

            }
        }
    };

    private WallpaperRecyclerAdapter.UnsplashWallpaperClickListener unsplashWallpaperClickListener = new WallpaperRecyclerAdapter.UnsplashWallpaperClickListener() {
        @Override
        public void wallpaperClickListener(int position, View view) {

            UnsplashApiModel model = mWallpaperRecyclerAdapter.getApiModel(position);
            Intent wallpaperViewIntent = new Intent(WallpaperActivity.this, WallpaperViewActivity.class);
            wallpaperViewIntent.putExtra(WALLPAPER_VIEW_INTENT_UNSPLASH_API_MODEL, model);

            int revealX = (int) (view.getX() + (view.getWidth() / 2));
            int revealY = (int) (view.getY() + (view.getHeight() - view.getHeight() / 6));

            wallpaperViewIntent.putExtra(WALLPAPER_VIEW_REVEAL_X, revealX);
            wallpaperViewIntent.putExtra(WALLPAPER_VIEW_REVEAL_Y, revealY);

            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(WallpaperActivity.this, view, "transition");


            /*Pair<View, String>[] pairs = new Pair[1];
            pairs[0] = new Pair<>(view, SHARED_TRANSITION_NAME);

            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(WallpaperActivity.this, pairs);*/

            startActivity(wallpaperViewIntent, options.toBundle());

        }
    };

    private SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {

            viewModel.setRefresh();
            noConnectionLinearLayout.setVisibility(View.GONE);
            ViewPropertyAnimator animator = noConnectionLinearLayout.animate().alpha(0).setDuration(300);
            animator.start();

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(homeBroadcastReceiver);
    }

    class HomeBroadcastReceiver extends BroadcastReceiver {
        ObjectAnimator objectAnimator;

        float size = getResources().getDimensionPixelSize(R.dimen.distance);
        boolean isFirst = true;

        @Override
        public void onReceive(Context context, Intent intent) {

            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {


                boolean isConnectionNotAvailable = intent.getBooleanExtra(

                        ConnectivityManager.EXTRA_NO_CONNECTIVITY, false

                );

                if (isConnectionNotAvailable) {

                    isFirst = false;

                    internetStatusAppCompatText.setText(getString(R.string.no_connection));
                    internetStatusAppCompatText.setTextColor(Color.RED);
                    objectAnimator = ObjectAnimator.ofFloat(internetStatusCardView, "translationY", 0);
                    objectAnimator.setDuration(1000);
                    objectAnimator.start();

                } else {

                    if (!isFirst) {

                        internetStatusAppCompatText.setText(getString(R.string.connection_available));
                        internetStatusAppCompatText.setTextColor(Color.GREEN);
                        objectAnimator = ObjectAnimator.ofFloat(internetStatusCardView, "translationY", size);
                        objectAnimator.setStartDelay(800);
                        objectAnimator.setDuration(1000);
                        objectAnimator.start();

                    }
                }

            }

        }
    }

}
