package com.android.blackgoku.wallhd.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SearchEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewPropertyAnimator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.blackgoku.wallhd.BuildConfig;
import com.android.blackgoku.wallhd.Java.dialogBox.HelpAndFeedback;
import com.android.blackgoku.wallhd.adapter.HomeViewPagerAdapter;
import com.android.blackgoku.wallhd.Java.dialogBox.SignOutDialogBox;
import com.android.blackgoku.wallhd.R;
import com.android.blackgoku.wallhd.tabs.CategoryWallpaperTabFragment;
import com.android.blackgoku.wallhd.tabs.FavouriteWallpaperTabFragment;
import com.android.blackgoku.wallhd.tabs.LatestWallpaperTabFragment;
import com.android.blackgoku.wallhd.utility.FirebaseFirestoreUtilityClass;
import com.android.blackgoku.wallhd.utility.SignedInUserDetails;
import com.android.blackgoku.wallhd.utility.WallHDConstants;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, SignOutDialogBox.SignOutOnClick {

    private static final String notSignedInUserNameHeader = "Not signed in yet";
    private static final String TOOLBAR_TITLE = "Home";
    private TabLayout tabLayout;
    private ViewPager pager;
    private DrawerLayout drawerLayout;
    private ImageView headerUserImageView;
    private CardView internetStatusCardView;
    private AppCompatTextView headerUserNameTextView, headerUserEmailTextView, internetStatusAppCompatText;
    private NavigationView navigationDrawerView;
    private HomeBroadcastReceiver homeBroadcastReceiver;
    private CoordinatorLayout homeCoordinatorLayout;
    private HomeViewPagerAdapter pagerAdapter;
    private final static String CURRENT_TAB_POS = "current";
   // private MenuItem mSearchItem;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        setUpToolBarAndFindViewById();


        setTabs();

        homeBroadcastReceiver = new HomeBroadcastReceiver();
        registerReceiver(homeBroadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(homeBroadcastReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpNavigationHeaderData();
    }

    private void setTabs() {

        int currentTab = getIntent().getIntExtra(CURRENT_TAB_POS, 1);

        pagerAdapter = new HomeViewPagerAdapter(getSupportFragmentManager());

        pagerAdapter.addFragment(CategoryWallpaperTabFragment.getInstance(), CategoryWallpaperTabFragment.getFragmentName());
        pagerAdapter.addFragment(LatestWallpaperTabFragment.getInstance(), LatestWallpaperTabFragment.getFragmentName());
        pagerAdapter.addFragment(FavouriteWallpaperTabFragment.getInstance(), FavouriteWallpaperTabFragment.getFragmentName());

        pager.setAdapter(pagerAdapter);
        pager.setOffscreenPageLimit(pagerAdapter.getCount());
        tabLayout.setupWithViewPager(pager, false);
        pager.setCurrentItem(currentTab);


    }

    private void setUpToolBarAndFindViewById() {

        homeCoordinatorLayout = findViewById(R.id.homeCoordinatorLayout);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(TOOLBAR_TITLE);

        tabLayout = findViewById(R.id.tabLayout);
        pager = findViewById(R.id.viewpager);

        drawerLayout = findViewById(R.id.drawerLayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        toggle.setDrawerSlideAnimationEnabled(true);

        navigationDrawerView = findViewById(R.id.navigationDrawerView);
        navigationDrawerView.setNavigationItemSelectedListener(this);

        View navigationHeaderView = navigationDrawerView.getHeaderView(0);
        headerUserImageView = navigationHeaderView.findViewById(R.id.headerUserImageView);
        headerUserNameTextView = navigationHeaderView.findViewById(R.id.headerUserNameTextView);
        headerUserEmailTextView = navigationHeaderView.findViewById(R.id.headerUserEmailTextView);
        internetStatusAppCompatText = findViewById(R.id.internetStatusAppCompatText);

        internetStatusCardView = findViewById(R.id.internetStatusCardView);

        setUpNavigationHeaderData();
    }

    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {

            drawerLayout.closeDrawer(GravityCompat.START);

        } else {

            super.onBackPressed();

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.home_menu, menu);

        return true;

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if (FirebaseFirestoreUtilityClass.currentUserIsNotNull()) {

            menu.findItem(R.id.signInHome).setTitle("Sign out");

        } else {

            navigationDrawerView.getMenu().findItem(R.id.signOutNavMenu).setTitle("Sign in");
            menu.findItem(R.id.signInHome).setTitle("Sign in");

        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.searchBarHome:

                onSearchRequested();

                return true;

            case R.id.settingHome:
                Intent settingIntent = new Intent(HomeActivity.this, SettingsActivity.class);
                startActivity(settingIntent);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                return true;

            case R.id.signInHome:

                if (FirebaseFirestoreUtilityClass.currentUserIsNotNull()) {

                    SignOutDialogBox singOut = new SignOutDialogBox();
                    singOut.show(getSupportFragmentManager(), "SignOutDialogBox");
                    singOut.SignOutOnClickListener(this);

                } else {

                    Intent signInIntent = new Intent(HomeActivity.this, SignInActivity.class);
                    startActivity(signInIntent);
                    overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);

                }

                return true;

            default:
                return false;

        }

    }

    private void shareApplication() {
        ApplicationInfo app = getApplicationContext().getApplicationInfo();
        String filePath = app.sourceDir;

        Intent intent = new Intent(Intent.ACTION_SEND);

        intent.setType("*/*");

        File originalApk = new File(filePath);

        try {
            File tempFile = new File(getExternalCacheDir() + "/ExtractedApk");
            if (!tempFile.isDirectory())
                if (!tempFile.mkdirs())
                    return;
            tempFile = new File(tempFile.getPath() + "/" + getString(app.labelRes).replace(" ", "").toLowerCase() + ".apk");
            //If file doesn't exists create new
            if (!tempFile.exists()) {
                if (!tempFile.createNewFile()) {
                    return;
                }
            }
            InputStream in = new FileInputStream(originalApk);
            OutputStream out = new FileOutputStream(tempFile);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
            System.out.println("File copied.");
            intent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", tempFile));
            startActivity(Intent.createChooser(intent, "Share app via"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setUpNavigationHeaderData() {

        SignedInUserDetails userDetails = new SignedInUserDetails();

        if (userDetails.getPhotoUrl() != null) {

            GlideApp.with(this)
                    .load(userDetails.getPhotoUrl())
                    .placeholder(R.drawable.user_placeholder)
                    .centerCrop()
                    .into(headerUserImageView);

            headerUserNameTextView.setText(userDetails.getUserName());
            headerUserEmailTextView.setText(userDetails.getUserEmail());
            navigationDrawerView.getMenu().findItem(R.id.signOutNavMenu).setTitle("Sign out");

        } else {

            GlideApp.with(this)
                    .load(R.drawable.user_placeholder)
                    .centerCrop()
                    .into(headerUserImageView);

            headerUserNameTextView.setText(notSignedInUserNameHeader);
            headerUserEmailTextView.setText("");
            navigationDrawerView.getMenu().findItem(R.id.signOutNavMenu).setTitle("Sign in");

        }


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            case R.id.myAccountNavMenu:
                drawerLayout.closeDrawer(GravityCompat.START);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if (FirebaseFirestoreUtilityClass.currentUserIsNotNull()) {

                            startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
                            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);


                        } else {

                            Toasty.info(HomeActivity.this, "You have to signed in...", Toast.LENGTH_SHORT).show();

                        }

                    }
                }, WallHDConstants.NAVIGATION_DRAWER_CLOSING_TIME);


                break;

            case R.id.settingsNavMenu:
                drawerLayout.closeDrawer(GravityCompat.START);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(HomeActivity.this, SettingsActivity.class));
                        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                    }
                }, WallHDConstants.NAVIGATION_DRAWER_CLOSING_TIME);


                break;

            case R.id.helpAndFeedbackNavMenu:
                drawerLayout.closeDrawer(GravityCompat.START);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (FirebaseFirestoreUtilityClass.currentUserIsNotNull()) {

                            openDialog();


                        } else {

                            Toasty.info(HomeActivity.this, "You have to signed in first...", Toast.LENGTH_SHORT).show();

                        }
                    }
                }, WallHDConstants.NAVIGATION_DRAWER_CLOSING_TIME);

                break;

            case R.id.signOutNavMenu:
                drawerLayout.closeDrawer(GravityCompat.START);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (FirebaseFirestoreUtilityClass.currentUserIsNotNull()) {

                            SignOutDialogBox singOut = new SignOutDialogBox();
                            singOut.show(getSupportFragmentManager(), "SignOutDialogBox");
                            singOut.SignOutOnClickListener(HomeActivity.this);

                        } else {

                            Intent signInIntent = new Intent(HomeActivity.this, SignInActivity.class);
                            startActivity(signInIntent);
                            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);

                        }
                    }
                }, WallHDConstants.NAVIGATION_DRAWER_CLOSING_TIME);


                break;


            case R.id.shareAPK:

                drawerLayout.closeDrawer(GravityCompat.START);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        shareApplication();
                    }
                }, WallHDConstants.NAVIGATION_DRAWER_CLOSING_TIME);

                break;

        }

        return true;

    }

    private void openDialog() {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        //transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_bottom);
        HelpAndFeedback helpAndFeedbackDialog = new HelpAndFeedback();
        helpAndFeedbackDialog.show(transaction, "HelpAndFeedback");

    }

    @Override
    public void signOut() {

        FirebaseAuth.getInstance().signOut();

        setUpNavigationHeaderData();

        Toasty.success(getApplicationContext(), "You have successfully signed out", Toast.LENGTH_SHORT).show();

        Intent afterSignInIntent = new Intent(HomeActivity.this, HomeActivity.class);
        afterSignInIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        afterSignInIntent.putExtra(CURRENT_TAB_POS, tabLayout.getSelectedTabPosition());
        startActivity(afterSignInIntent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

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
