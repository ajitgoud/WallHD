package com.android.blackgoku.wallhd.tabs;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Explode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.blackgoku.wallhd.activity.WallpaperActivity;
import com.android.blackgoku.wallhd.adapter.CategoryRecyclerAdapter;
import com.android.blackgoku.wallhd.model.ImageCategory;
import com.android.blackgoku.wallhd.R;
import com.android.blackgoku.wallhd.utility.FirebaseFirestoreUtilityClass;
import com.android.blackgoku.wallhd.utility.WallHDConstants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.android.blackgoku.wallhd.tabs.LatestWallpaperTabFragment.WALLPAPER_VIEW_REVEAL_X;
import static com.android.blackgoku.wallhd.tabs.LatestWallpaperTabFragment.WALLPAPER_VIEW_REVEAL_Y;

public class CategoryWallpaperTabFragment extends Fragment {

    private ProgressBar progressBar;
    private RecyclerView recyclerViewCategory;
    private CategoryRecyclerAdapter categoryRecyclerAdapter;
    private List<ImageCategory> categoryImages;

    private CollectionReference categoryCollectionRef;

    private SwipeRefreshLayout swipeRefreshLayout;

    public CategoryWallpaperTabFragment() {

    }

    public static CategoryWallpaperTabFragment getInstance(){

        return new CategoryWallpaperTabFragment();

    }

    public static String getFragmentName(){

        return "Categories";

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.category_wallpaper_tab_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        categoryCollectionRef = FirebaseFirestoreUtilityClass.getCategoriesCollectionRoot();
        categoryImages = new ArrayList<>();

        findViews(view);
        setRecyclerView();

        loadCategoryDataFromFireStore();

        setListener();

    }


    private void setListener() {

        categoryRecyclerAdapter.setOnItemClickListener(new CategoryRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int i, View view) {

                ImageCategory upload = categoryImages.get(i);
                String categoryName = upload.getCat_name();

                Intent wallpaperActivityIntent = new Intent(getContext(), WallpaperActivity.class);
                wallpaperActivityIntent.putExtra(WallHDConstants.CATEGORY_NAME, categoryName);

                //Objects.requireNonNull(getActivity()).overridePendingTransition(R.anim.grow_in, R.anim.grow_out);

                int revealX = (int) (view.getX() + (view.getWidth() / 2));
                int revealY = (int) (view.getY() + (view.getHeight() - view.getHeight() / 20));

                wallpaperActivityIntent.putExtra(WALLPAPER_VIEW_REVEAL_X, revealX);
                wallpaperActivityIntent.putExtra(WALLPAPER_VIEW_REVEAL_Y, revealY);

                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(Objects.requireNonNull(getActivity()), view, "transition");

                startActivity(wallpaperActivityIntent, options.toBundle());

            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                categoryImages.clear();
                loadCategoryDataFromFireStore();

            }
        });

    }

    private void setRecyclerView() {

        recyclerViewCategory.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerViewCategory.setHasFixedSize(true);
        categoryRecyclerAdapter = new CategoryRecyclerAdapter(getContext(), categoryImages);
        recyclerViewCategory.setAdapter(categoryRecyclerAdapter);

    }

    private void findViews(View view) {

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.iconColor));
        progressBar = view.findViewById(R.id.progress_horizontal);
        progressBar.setVisibility(View.VISIBLE);
        recyclerViewCategory = view.findViewById(R.id.recyclerViewCategory);

    }

    private void loadCategoryDataFromFireStore() {

        categoryCollectionRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        progressBar.setVisibility(View.GONE);
                        swipeRefreshLayout.setRefreshing(false);
                        if (queryDocumentSnapshots != null) {

                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                                ImageCategory imageCategory = documentSnapshot.toObject(ImageCategory.class);

                                imageCategory.setCategoryKey(documentSnapshot.getId());

                                categoryImages.add(imageCategory);

                            }

                            categoryRecyclerAdapter.notifyDataSetChanged();

                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

    }

}
