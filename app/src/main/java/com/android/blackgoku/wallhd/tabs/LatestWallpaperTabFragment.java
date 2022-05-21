package com.android.blackgoku.wallhd.tabs;

import android.app.ActivityOptions;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.transition.Explode;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.blackgoku.wallhd.activity.HomeActivity;
import com.android.blackgoku.wallhd.adapter.UnsplashWallpaperRecyclerAdapter;
import com.android.blackgoku.wallhd.model.NetworkState;
import com.android.blackgoku.wallhd.pagination_components.recents.UnsplashApiLiveViewModel;
import com.android.blackgoku.wallhd.activity.WallpaperViewActivity;
import com.android.blackgoku.wallhd.R;
import com.android.blackgoku.wallhd.model.unsplash_api.UnsplashApiModel;

import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class LatestWallpaperTabFragment extends Fragment {

    public static final String WALLPAPER_VIEW_INTENT_UNSPLASH_API_MODEL = "UnsplashApiModel";
    public static final String WALLPAPER_VIEW_REVEAL_X = "reveal_x";
    public static final String WALLPAPER_VIEW_REVEAL_Y = "reveal_y";
    public static final String SHARED_TRANSITION_NAME = "imageView";
    private ProgressBar wallpaperProgressBarCenter, wallpaperProgressBarBelowRV;
    private UnsplashWallpaperRecyclerAdapter unsplashWallpaperRecyclerAdapter;
    private RecyclerView recyclerViewUnsplashWallpaper;
    private LinearLayout noConnectionLinearLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Context context;
    private UnsplashApiLiveViewModel liveViewModel;

    public LatestWallpaperTabFragment() {

    }

    public static LatestWallpaperTabFragment getInstance(){

        return new LatestWallpaperTabFragment();

    }

    public static String getFragmentName(){

        return "Recent";

    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.latest_wallpaper_tab_fragment, container, false);
        setUpRecyclerViewAndOther(view);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /*Explode explode = new Explode();
        explode.setDuration(400);
        assert  getActivity() != null;

        Window window = getActivity().getWindow();
        window.setExitTransition(explode);
        window.setReenterTransition(explode);
        window.setAllowReturnTransitionOverlap(false);
        window.setAllowEnterTransitionOverlap(false);*/


        context = getContext();

    }


    private void setUpRecyclerViewAndOther(final View v) {

        findViews(v);

        setRecyclerView();

        setViewModel();

        setListeners();

    }

    private void setListeners() {

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                liveViewModel.swipeToRefresh();
                noConnectionLinearLayout.setVisibility(View.GONE);
                ViewPropertyAnimator animator = noConnectionLinearLayout.animate().alpha(0).setDuration(300);
                animator.start();


            }
        });

        unsplashWallpaperRecyclerAdapter.setOnUnsplashLatestWallpaperClickListener(new UnsplashWallpaperRecyclerAdapter.UnsplashLatestWallpaperClickListener() {
            @Override
            public void LatestWallpaperClickListener(int position, View view) {

                UnsplashApiModel model = unsplashWallpaperRecyclerAdapter.getApiModel(position);

                int revealX = (int) (view.getX() + (view.getWidth() / 2));
                int revealY = (int) (view.getY() + (view.getHeight() - view.getHeight() / 6));



                Intent wallpaperViewIntent = new Intent(context, WallpaperViewActivity.class);
                wallpaperViewIntent.putExtra(WALLPAPER_VIEW_INTENT_UNSPLASH_API_MODEL, model);

                wallpaperViewIntent.putExtra(WALLPAPER_VIEW_REVEAL_X, revealX);
                wallpaperViewIntent.putExtra(WALLPAPER_VIEW_REVEAL_Y, revealY);
                /*Pair<View, String>[] pairs = new Pair[1];
                pairs[0] = new Pair<>(view, SHARED_TRANSITION_NAME);

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getActivity(), pairs);
                startActivity(wallpaperViewIntent, options.toBundle());*/

                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(Objects.requireNonNull(getActivity()), view, "transition");

                startActivity(wallpaperViewIntent, options.toBundle());

            }
        });


    }

    private void setViewModel() {

        liveViewModel = ViewModelProviders.of(this).get(UnsplashApiLiveViewModel.class);


        liveViewModel.getApiModels().observe(this, new Observer<PagedList<UnsplashApiModel>>() {
            @Override
            public void onChanged(@Nullable PagedList<UnsplashApiModel> unsplashApiModels) {
                unsplashWallpaperRecyclerAdapter.submitList(unsplashApiModels);

            }
        });

        liveViewModel.getInitialNetworkStateLiveData().observe(this, new Observer<NetworkState>() {
            @Override
            public void onChanged(@Nullable NetworkState networkState) {
                assert networkState != null;
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

                        if (networkState.getStatus() == NetworkState.Status.FAILED) {

                            swipeRefreshLayout.setRefreshing(false);
                            wallpaperProgressBarCenter.setVisibility(View.INVISIBLE);
                            noConnectionLinearLayout.setVisibility(View.VISIBLE);
                            ViewPropertyAnimator animator = noConnectionLinearLayout.animate().alpha(1).setDuration(1000);
                            animator.start();

                        }

                    }

                }
            }
        });

        liveViewModel.getNetworkStateLiveData().observe(this, new Observer<NetworkState>() {
            @Override
            public void onChanged(@Nullable NetworkState networkState) {

                assert networkState != null;
                if (networkState == NetworkState.LOADING) {

                    wallpaperProgressBarBelowRV.setVisibility(View.VISIBLE);

                } else {

                    if (networkState == NetworkState.LOADED) {

                        wallpaperProgressBarBelowRV.setVisibility(View.GONE);

                    } else {

                        if (networkState.getStatus() == NetworkState.Status.FAILED) {

                            Toasty.error(context, networkState.getMsg(), Toast.LENGTH_SHORT).show();

                        }

                    }

                }

            }
        });


    }

    private void setRecyclerView() {

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);

        recyclerViewUnsplashWallpaper.setLayoutManager(new GridLayoutManager(getActivity(), 2, GridLayoutManager.VERTICAL, false));//new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        unsplashWallpaperRecyclerAdapter = new UnsplashWallpaperRecyclerAdapter(getContext());

        recyclerViewUnsplashWallpaper.setAdapter(unsplashWallpaperRecyclerAdapter);

    }

    private void findViews(View v) {

        noConnectionLinearLayout = v.findViewById(R.id.noConnectionLinearLayout);
        wallpaperProgressBarCenter = v.findViewById(R.id.wallpaperProgressBarCenter);
        wallpaperProgressBarBelowRV = v.findViewById(R.id.wallpaperProgressBarBelowRV);
        swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.iconColor));

        wallpaperProgressBarCenter.setVisibility(View.VISIBLE);
        wallpaperProgressBarBelowRV.setVisibility(View.GONE);

        recyclerViewUnsplashWallpaper = v.findViewById(R.id.recyclerViewUnsplashWallpaper);

    }


}
