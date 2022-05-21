package com.android.blackgoku.wallhd.tabs;

import android.app.ActivityOptions;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.android.blackgoku.wallhd.R;
import com.android.blackgoku.wallhd.activity.SignInActivity;
import com.android.blackgoku.wallhd.activity.WallpaperViewActivity;
import com.android.blackgoku.wallhd.adapter.FavouriteRecyclerAdapter;
import com.android.blackgoku.wallhd.model.FavouriteModelClass;
import com.android.blackgoku.wallhd.model.unsplash_api.UnsplashApiModel;
import com.android.blackgoku.wallhd.pagination_components.favourites.FavouritesViewModel;
import com.android.blackgoku.wallhd.utility.FirebaseFirestoreUtilityClass;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.android.blackgoku.wallhd.tabs.LatestWallpaperTabFragment.WALLPAPER_VIEW_INTENT_UNSPLASH_API_MODEL;
import static com.android.blackgoku.wallhd.tabs.LatestWallpaperTabFragment.WALLPAPER_VIEW_REVEAL_X;
import static com.android.blackgoku.wallhd.tabs.LatestWallpaperTabFragment.WALLPAPER_VIEW_REVEAL_Y;

public class FavouriteWallpaperTabFragment extends Fragment {

    private RecyclerView recyclerViewWallpaper;
    private FavouriteRecyclerAdapter recyclerAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FavouriteWallpaperTabFragment fragment;
    private AppCompatTextView emptyFavListTextView;
    private RelativeLayout signedInRelativeLayout, notSignedInRelativeLayout;

    public FavouriteWallpaperTabFragment() {

    }

    public static FavouriteWallpaperTabFragment getInstance() {

        return new FavouriteWallpaperTabFragment();

    }

    public static String getFragmentName() {

        return "Favourites";

    }


    @Override
    public void onStart() {
        super.onStart();


    }

    @Override
    public void onResume() {
        super.onResume();

        if (FirebaseFirestoreUtilityClass.currentUserIsNotNull()) {

            signedInRelativeLayout.setVisibility(View.VISIBLE);
            notSignedInRelativeLayout.setVisibility(View.GONE);
            callWalls();

        } else {

            signedInRelativeLayout.setVisibility(View.GONE);
            notSignedInRelativeLayout.setVisibility(View.VISIBLE);

        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.favourite_wallpaper_tab_fragment, container, false);
        signedInRelativeLayout = view.findViewById(R.id.signedInRelativeLayout);
        notSignedInRelativeLayout = view.findViewById(R.id.notSignedInRelativeLayout);

        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpRecyclerViewAndAll(view);
        setUpSignInButton(view);
        if (FirebaseFirestoreUtilityClass.currentUserIsNotNull()) {

            signedInRelativeLayout.setVisibility(View.VISIBLE);
            callWalls();

        } else {

            notSignedInRelativeLayout.setVisibility(View.VISIBLE);

        }

    }

    private void callWalls() {

        final int startPos = 0;

        FavouritesViewModel viewModel = ViewModelProviders.of(this).get(FavouritesViewModel.class);

        viewModel.getGetList().observe(this, new Observer<List<FavouriteModelClass>>() {
            @Override
            public void onChanged(@Nullable final List<FavouriteModelClass> favouriteModelClasses) {

                if (favouriteModelClasses != null) {

                    if (favouriteModelClasses.size() == 0) {

                        recyclerAdapter.submitList(new ArrayList<>(favouriteModelClasses));
                        recyclerViewWallpaper.scrollToPosition(startPos);
                        recyclerViewIsEmpty(true);

                        return;
                    }

                    recyclerViewIsEmpty(false);
                    recyclerAdapter.submitList(new ArrayList<>(favouriteModelClasses));
                    recyclerViewWallpaper.scrollToPosition(startPos);

                }

            }
        });

        /*viewModel.getFavouriteLiveModel().observe(this, new Observer<PagedList<FavouriteModelClass>>() {
            @Override
            public void onChanged(@Nullable PagedList<FavouriteModelClass> favouriteModelClasses) {

                recyclerAdapter.submitList(favouriteModelClasses);

            }
        });*/

    }


    private void recyclerViewIsEmpty(boolean isEmpty) {

        if (isEmpty) {

            emptyFavListTextView.setVisibility(View.VISIBLE);

        } else {

            emptyFavListTextView.setVisibility(View.GONE);

        }

    }

    private void setUpSignInButton(View view) {

        final MaterialButton signInButtonFav = view.findViewById(R.id.signInButtonFav);

        signInButtonFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent signInIntent = new Intent(getContext(), SignInActivity.class);
/*

                int revealX = (int) (view.getX() + view.getWidth() / 2);
                int revealY = (int) (view.getY() + view.getHeight());

                signInIntent.putExtra(WALLPAPER_VIEW_REVEAL_X, revealX);
                signInIntent.putExtra(WALLPAPER_VIEW_REVEAL_Y, revealY);

                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(Objects.requireNonNull(getActivity()), view, "transition");
*/

                startActivity(signInIntent);
                Objects.requireNonNull(getActivity()).overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);

            }
        });

    }

    private void setUpRecyclerViewAndAll(View view) {

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.iconColor));

        recyclerViewWallpaper = view.findViewById(R.id.recyclerViewWallpaper);
        emptyFavListTextView = view.findViewById(R.id.emptyFavListTextView);
        emptyFavListTextView.setVisibility(View.GONE);

        ProgressBar wallpaperProgressBarCenter = view.findViewById(R.id.wallpaperProgressBarCenter);
        ProgressBar wallpaperProgressBarBelowRV = view.findViewById(R.id.wallpaperProgressBarBelowRV);
        wallpaperProgressBarCenter.setVisibility(View.GONE);
        wallpaperProgressBarBelowRV.setVisibility(View.GONE);
        recyclerViewWallpaper.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        recyclerAdapter = new FavouriteRecyclerAdapter(getContext());
        recyclerViewWallpaper.setAdapter(recyclerAdapter);
        setListeners();

    }

    private void setListeners() {

        recyclerAdapter.setOnFavWallpaperClickListener(new FavouriteRecyclerAdapter.FavWallpaperClickListener() {
            @Override
            public void wallpaperClickListener(int position, View view) {

                /*itemPos = position;
                totalItemCount = recyclerAdapter.getItemCount();*/

                UnsplashApiModel model = recyclerAdapter.getItemAtPosition(position);

                int revealX = (int) (view.getX() + (view.getWidth() / 2));
                int revealY = (int) (view.getY() + (view.getHeight() - view.getHeight() / 6));

                Intent wallpaperViewIntent = new Intent(getContext(), WallpaperViewActivity.class);
                wallpaperViewIntent.putExtra(WALLPAPER_VIEW_INTENT_UNSPLASH_API_MODEL, model);
                wallpaperViewIntent.putExtra(WALLPAPER_VIEW_REVEAL_X, revealX);
                wallpaperViewIntent.putExtra(WALLPAPER_VIEW_REVEAL_Y, revealY);

                /*Pair<View, String>[] pairs = new Pair[1];
                pairs[0] = new Pair<>(view, SHARED_TRANSITION_NAME);

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getActivity(), pairs);*/
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(Objects.requireNonNull(getActivity()), view, "transition");

                startActivity(wallpaperViewIntent, options.toBundle());

            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                recyclerViewWallpaper.invalidate();
                callWalls();
                swipeRefreshLayout.setRefreshing(false);

            }
        });

    }
}
