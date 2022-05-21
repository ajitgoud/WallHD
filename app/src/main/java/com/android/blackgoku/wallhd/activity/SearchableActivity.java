package com.android.blackgoku.wallhd.activity;

import android.animation.ObjectAnimator;
import android.app.ActivityOptions;
import android.app.SearchManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.provider.SearchRecentSuggestions;
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
import android.util.Pair;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.blackgoku.wallhd.R;
import com.android.blackgoku.wallhd.adapter.WallpaperRecyclerAdapter;
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

public class SearchableActivity extends AppCompatActivity {

    private ProgressBar wallpaperProgressBar;
    private RecyclerView recyclerViewWallpaper;
    private WallpaperRecyclerAdapter adapter;
    private CardView internetStatusCardView;
    private AppCompatTextView internetStatusAppCompatText;
    private HomeBroadcastReceiver homeBroadcastReceiver;
    private SwipeRefreshLayout swipeRefreshLayout;
    private UnsplashApiCollectionLiveViewModel viewModel;
    private LinearLayout noConnectionLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);

        handleIntent(getIntent());

        setToolbar();

        setFindViews();

        setRecyclerView();

        setListeners();


        homeBroadcastReceiver = new HomeBroadcastReceiver();
        registerReceiver(homeBroadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));


    }

    private void setListeners() {

        adapter.setOnUnsplashWallpaperClickListener(new WallpaperRecyclerAdapter.UnsplashWallpaperClickListener() {
            @Override
            public void wallpaperClickListener(int position, View view) {

                UnsplashApiModel model = adapter.getApiModel(position);

                Intent wallpaperViewIntent = new Intent(SearchableActivity.this, WallpaperViewActivity.class);
                wallpaperViewIntent.putExtra(WALLPAPER_VIEW_INTENT_UNSPLASH_API_MODEL, model);

                int revealX = (int) (view.getX() + (view.getWidth() / 2));
                int revealY = (int) (view.getY() + (view.getHeight() - view.getHeight() / 6));

                wallpaperViewIntent.putExtra(WALLPAPER_VIEW_REVEAL_X, revealX);
                wallpaperViewIntent.putExtra(WALLPAPER_VIEW_REVEAL_Y, revealY);

                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(SearchableActivity.this, view, "transition");

                startActivity(wallpaperViewIntent, options.toBundle());

            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                viewModel.setRefresh();
                noConnectionLinearLayout.setVisibility(View.GONE);
                ViewPropertyAnimator animator = noConnectionLinearLayout.animate().alpha(0).setDuration(300);
                animator.start();

            }
        });


    }

    private void setRecyclerView() {

        recyclerViewWallpaper.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        adapter = new WallpaperRecyclerAdapter(this);

        recyclerViewWallpaper.setAdapter(adapter);

    }

    private void setFindViews() {

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.iconColor));

        noConnectionLinearLayout = findViewById(R.id.noConnectionLinearLayout);

        internetStatusCardView = findViewById(R.id.internetStatusCardView);

        internetStatusAppCompatText = findViewById(R.id.internetStatusAppCompatText);

        wallpaperProgressBar = findViewById(R.id.wallpaperProgressBarCenter);
        wallpaperProgressBar.setVisibility(View.INVISIBLE);

        recyclerViewWallpaper = findViewById(R.id.recyclerViewWallpaper);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    private void setToolbar() {

        Toolbar toolbar = findViewById(R.id.toolbarWallpaper);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);

            }
        });
        Objects.requireNonNull(getSupportActionBar()).setTitle("Search Results");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(homeBroadcastReceiver);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }


    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {

            String query = intent.getStringExtra(SearchManager.QUERY);

            viewModel = ViewModelProviders.of(SearchableActivity.this, new ViewModelFactory(getApplication(), query)).get(UnsplashApiCollectionLiveViewModel.class);

            viewModel.getApiModels().observe(SearchableActivity.this, new Observer<PagedList<UnsplashApiModel>>() {
                @Override
                public void onChanged(@Nullable PagedList<UnsplashApiModel> unsplashApiModels) {

                    adapter.submitList(unsplashApiModels);

                }
            });

            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(SearchableActivity.this, WallHDConstants.AUTHORITY, WallHDConstants.MODE);
            suggestions.saveRecentQuery(query, null);

            viewModel.getInitialNetworkStateLiveData().observe(this, new Observer<NetworkState>() {
                @Override
                public void onChanged(@Nullable NetworkState networkState) {

                    if (networkState == NetworkState.LOADING) {

                        wallpaperProgressBar.setVisibility(View.VISIBLE);
                        ViewPropertyAnimator animator = noConnectionLinearLayout.animate().alpha(0).setDuration(1000);
                        animator.start();
                        noConnectionLinearLayout.setVisibility(View.GONE);

                    } else {

                        if (networkState == NetworkState.LOADED) {

                            wallpaperProgressBar.setVisibility(View.GONE);
                            swipeRefreshLayout.setRefreshing(false);

                        } else {

                            assert networkState != null;
                            if (networkState.getStatus() == NetworkState.Status.FAILED) {

                                swipeRefreshLayout.setRefreshing(false);
                                wallpaperProgressBar.setVisibility(View.GONE);
                                noConnectionLinearLayout.setVisibility(View.VISIBLE);
                                ViewPropertyAnimator animator = noConnectionLinearLayout.animate().alpha(1).setDuration(1000);
                                animator.start();

                            }

                        }

                    }

                }
            });

            viewModel.getNetworkStateLiveData().observe(this, new Observer<NetworkState>() {
                @Override
                public void onChanged(@Nullable NetworkState networkState) {

                    assert networkState != null;
                    if (networkState == NetworkState.LOADING) {

                        //wallpaperProgressBarBelowRV.setVisibility(View.VISIBLE);

                    } else {

                        if (networkState == NetworkState.LOADED) {

                            wallpaperProgressBar.setVisibility(View.GONE);

                        } else {

                            if (networkState.getStatus() == NetworkState.Status.FAILED) {

                                Toasty.error(getApplicationContext(), networkState.getMsg(), Toast.LENGTH_SHORT).show();

                            }

                        }

                    }
                }
            });

        }

    }

    class HomeBroadcastReceiver extends BroadcastReceiver {
        ObjectAnimator objectAnimator;

        final String animationProperty = "translationY";

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
                    objectAnimator = ObjectAnimator.ofFloat(internetStatusCardView, animationProperty, 0);
                    objectAnimator.setDuration(1000);
                    objectAnimator.start();

                } else {

                    if (!isFirst) {

                        internetStatusAppCompatText.setText(getString(R.string.connection_available));
                        internetStatusAppCompatText.setTextColor(Color.GREEN);
                        objectAnimator = ObjectAnimator.ofFloat(internetStatusCardView, animationProperty, size);
                        objectAnimator.setStartDelay(800);
                        objectAnimator.setDuration(1000);
                        objectAnimator.start();

                    }
                }

            }

        }

    }

}
