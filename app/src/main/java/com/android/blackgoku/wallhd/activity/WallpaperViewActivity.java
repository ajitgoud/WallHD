package com.android.blackgoku.wallhd.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.DownloadManager;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.CircularProgressDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.blackgoku.wallhd.BuildConfig;
import com.android.blackgoku.wallhd.Java.dialogBox.ApplyDialogBox;
import com.android.blackgoku.wallhd.Java.dialogBox.ShareDialogBox;
import com.android.blackgoku.wallhd.Java.dialogBox.UserProfileHotlink;
import com.android.blackgoku.wallhd.R;
import com.android.blackgoku.wallhd.broadcast_receiver.AppBroadcastReceiver;
import com.android.blackgoku.wallhd.classes.RetrofitService;
import com.android.blackgoku.wallhd.interfaces.UnsplashApiListener;
import com.android.blackgoku.wallhd.model.DownloadUrlLocation;
import com.android.blackgoku.wallhd.model.FavouriteModelClass;
import com.android.blackgoku.wallhd.model.unsplash_api.UnsplashApiModel;
import com.android.blackgoku.wallhd.model.unsplash_api.urls.ApiUrlsModel;
import com.android.blackgoku.wallhd.model.unsplash_api.user.ApiUserModel;
import com.android.blackgoku.wallhd.utility.FirebaseFirestoreUtilityClass;
import com.android.blackgoku.wallhd.utility.SignedInUserDetails;
import com.android.blackgoku.wallhd.utility.WallHDConstants;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.android.blackgoku.wallhd.tabs.LatestWallpaperTabFragment.WALLPAPER_VIEW_INTENT_UNSPLASH_API_MODEL;
import static com.android.blackgoku.wallhd.tabs.LatestWallpaperTabFragment.WALLPAPER_VIEW_REVEAL_X;
import static com.android.blackgoku.wallhd.tabs.LatestWallpaperTabFragment.WALLPAPER_VIEW_REVEAL_Y;

public class WallpaperViewActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = WallpaperViewActivity.class.getSimpleName();
    private static final int DOWNLOAD = 1;
    private static final int NEED_SIGN = 2;
    public static final String IMAGE_URL = "image_url";
    public static final String IMAGE_BITMAP_RESOURCE = "image_bitmap_resource";
    public static final String IMAGE_ID = "image_id";
    private static final int STORAGE_PERMISSIONS = 106;

    private boolean isStoragePermissionGranted = false;

    private CollectionReference userReference;

    private UnsplashApiModel model;
    private ApiUrlsModel urlsModel;

    private AppCompatImageView wallpaperViewImageView, finishActivityImageView;
    private AppCompatTextView descriptionTextView, photographerHotlinkTextView, widthAndHeightTextView;
    private LinearLayout viewLinearLayout, applyLinearLayout, saveLinearLayout, shareLinearLayout;
    private Context context;
    private long referenceId;
    private FloatingActionButton addFavFloatingActionButton;
    private boolean isApplyingAsWall = false;
    private boolean isMakingReadyForShare = false;
    private boolean isDownloading = false;
    private boolean inFavourites;
    private AppBroadcastReceiver broadcastReceiver;
    private HomeBroadcastReceiver homeBroadcastReceiver;
    private CardView internetStatusCardView;
    private AppCompatTextView internetStatusAppCompatText;
    private ProgressBar wallpaperImageLoadProgressBar;
    private View rootLayout;
    private int revealX;
    private int revealY;

    private boolean UserDownloadedWalls = false;

    private int count = 1;

    private String filePath;
    private DownloadManager downloadManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_wallpaper_view);
        final Intent intent = getIntent();
        rootLayout = findViewById(R.id.wallpaperViewCoordinatorLayout);

        if (savedInstanceState == null && intent.hasExtra(WALLPAPER_VIEW_REVEAL_X) && intent.hasExtra(WALLPAPER_VIEW_REVEAL_Y)) {

            rootLayout.setVisibility(View.INVISIBLE);

            revealX = intent.getIntExtra(WALLPAPER_VIEW_REVEAL_X, 0);
            revealY = intent.getIntExtra(WALLPAPER_VIEW_REVEAL_Y, 0);

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


/*
        Explode explode = new Explode();
        explode.setDuration(700);
        getWindow().setEnterTransition(explode);
        getWindow().setReturnTransition(new Explode().setDuration(50));
        getWindow().setAllowReturnTransitionOverlap(false);
        getWindow().setAllowReturnTransitionOverlap(false);*/


        inFavourites = false;

        if (FirebaseFirestoreUtilityClass.currentUserIsNotNull()) {

            firebaseSetUp();

        }

        setUpFindViewByIdAndToolbar();

        setImagesAndOtherDetails();

        setOnClickListeners();

        if (FirebaseFirestoreUtilityClass.currentUserIsNotNull()) {
            determineAddedToFavOrNot();
        } else {

            addFavFloatingActionButton.setImageResource(R.drawable.ic_heart_black_24dp);

        }

        homeBroadcastReceiver = new HomeBroadcastReceiver();
        registerReceiver(homeBroadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

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
        circularAnim.setDuration(300);
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

    private void firebaseSetUp() {

        CollectionReference rootCollectionRef = FirebaseFirestoreUtilityClass.getFavouritesCollectionRoot();
        SignedInUserDetails userDetails = new SignedInUserDetails();
        userReference = rootCollectionRef.document(userDetails.getUserUID())
                .collection(WallHDConstants.FAVOURITES_COLLECTION_ROOT_REF);

    }

    private void determineAddedToFavOrNot() {

        userReference.document(model.getId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        if (documentSnapshot.exists()) {

                            inFavourites = true;
                            addFavFloatingActionButton.setImageResource(R.drawable.ic_heart_red_24dp);

                        } else {

                            inFavourites = false;
                            addFavFloatingActionButton.setImageResource(R.drawable.ic_heart_black_24dp);

                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        determineAddedToFavOrNot();

                    }
                });

    }

    private void setOnClickListeners() {

        applyLinearLayout.setOnClickListener(this);
        saveLinearLayout.setOnClickListener(this);
        shareLinearLayout.setOnClickListener(this);
        viewLinearLayout.setOnClickListener(this);
        addFavFloatingActionButton.setOnClickListener(this);
        photographerHotlinkTextView.setOnClickListener(this);
        finishActivityImageView.setOnClickListener(this);

    }

    private void setUpFindViewByIdAndToolbar() {

        context = getApplicationContext();
        Toolbar toolbar = findViewById(R.id.toolbarWallpaperView);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setTitle("");

        internetStatusAppCompatText = findViewById(R.id.internetStatusAppCompatText);
        internetStatusCardView = findViewById(R.id.internetStatusCardView);

        wallpaperViewImageView = findViewById(R.id.wallpaperViewImageView);

        wallpaperImageLoadProgressBar = findViewById(R.id.wallpaperImageLoadProgressBar);

        descriptionTextView = findViewById(R.id.descriptionTextView);
        photographerHotlinkTextView = findViewById(R.id.photographerHotlinkTextView);
        widthAndHeightTextView = findViewById(R.id.widthAndHeightTextView);

        finishActivityImageView = findViewById(R.id.finishActivityImageView);
        addFavFloatingActionButton = findViewById(R.id.addFavFloatingActionButton);
        applyLinearLayout = findViewById(R.id.applyLinearLayout);
        saveLinearLayout = findViewById(R.id.saveLinearLayout);
        shareLinearLayout = findViewById(R.id.shareLinearLayout);
        viewLinearLayout = findViewById(R.id.viewLinearLayout);

    }

    private void setImagesAndOtherDetails() {

        Intent wallpaperViewIntent = getIntent();

        CircularProgressDrawable progressDrawable = new CircularProgressDrawable(this);
        progressDrawable.setBackgroundColor(R.color.iconColor);
        progressDrawable.setStyle(CircularProgressDrawable.LARGE);
        progressDrawable.setStartEndTrim(1,100);
        model = wallpaperViewIntent.getParcelableExtra(WALLPAPER_VIEW_INTENT_UNSPLASH_API_MODEL);
        urlsModel = model.getUrls();
        ApiUserModel user = model.getUser();

        RequestOptions requestOptions = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).override(48,48).centerCrop();
        RequestBuilder<Drawable> requestBuilder = GlideApp.with(this).load(urlsModel.getSmall()).apply(requestOptions);

        GlideApp.with(context)
                .load(urlsModel.getRegular())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        wallpaperImageLoadProgressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        wallpaperImageLoadProgressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .thumbnail(requestBuilder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(progressDrawable)
                .into(wallpaperViewImageView);

        if (model.getDescription() != null) {

            descriptionTextView.setText(model.getDescription());

        } else {

            descriptionTextView.setVisibility(View.GONE);

        }

        String hotlink = user.getName() + " on Unsplash";
        photographerHotlinkTextView.setPaintFlags(photographerHotlinkTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        photographerHotlinkTextView.setText(hotlink);
        String resolution = model.getWidth() + " x " + model.getHeight();
        widthAndHeightTextView.setText(resolution);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.viewLinearLayout:

                Intent wallpaperFullView = new Intent(this, ImageViewActivity.class);
                wallpaperFullView.putExtra(IMAGE_URL, urlsModel.getRegular());
                startActivity(wallpaperFullView);
                overridePendingTransition(R.anim.zoom_in, 0);

                break;

            case R.id.addFavFloatingActionButton:

                if (!FirebaseFirestoreUtilityClass.currentUserIsNotNull()) {

                    String infoToUser = "You need to sign in first...";
                    callSnackbar(infoToUser, NEED_SIGN);

                } else {

                    if (inFavourites) {

                        removeFromFavWalls();

                    } else {

                        addToFavWalls();
                    }
                }

                break;


            case R.id.applyLinearLayout:

                if (isApplyingAsWall) {

                    String infoToUser = "Already applying, Please wait...";
                    callSnackbar(infoToUser);

                } else {

                    isApplyingAsWall = true;
                    setBackWallpaper();
                }

                break;

            case R.id.saveLinearLayout:

                if (isDownloading) {

                    String infoToUser = "Already downloading... ";
                    callSnackbar(infoToUser, DOWNLOAD);

                } else {

                    isDownloading = true;
                    String infoToUser = "Added to downloads... ";
                    callSnackbar(infoToUser);
                    downloadWallpaper();

                }

                break;

            case R.id.shareLinearLayout:

                if (isMakingReadyForShare) {

                    String infoToUser = "Already in progress, Please wait...";
                    callSnackbar(infoToUser);

                } else {

                    isMakingReadyForShare = true;
                    shareImages();

                }

                break;

            case R.id.photographerHotlinkTextView:

                showUserProfileHotlink();
                break;

            case R.id.finishActivityImageView:

                unRevealActivity();


        }

    }

    private void showUserProfileHotlink() {

        UserProfileHotlink hotlinkDialog = new UserProfileHotlink();
        hotlinkDialog.setArguments(getBundleData());
        hotlinkDialog.setCancelable(false);
        hotlinkDialog.show(getSupportFragmentManager(), "UserProfileHotlink");


    }

    private Bundle getBundleData() {

        if (model == null) {
            return null;
        }

        ApiUserModel userModel = model.getUser();

        Bundle unsplashDataBundle = new Bundle();
        unsplashDataBundle.putString(WallHDConstants.USER_PROFILE_IMAGE_HOTLINK, userModel.getProfile_image().getLarge());
        unsplashDataBundle.putString(WallHDConstants.USER_USERNAME_HOTLINK, userModel.getName());
        unsplashDataBundle.putString(WallHDConstants.USER_USERLOCATION_HOTLINK, userModel.getLocation());
        unsplashDataBundle.putString(WallHDConstants.USER_INSTAGRAM, userModel.getInstagram_username());
        unsplashDataBundle.putString(WallHDConstants.USER_TWITTER, userModel.getTwitter_username());
        unsplashDataBundle.putString(WallHDConstants.USER_UNSPLASH, userModel.getLinks().getHtml());
        unsplashDataBundle.putString(WallHDConstants.USER_BIO, userModel.getBio());
        return unsplashDataBundle;

    }

    private void removeFromFavWalls() {

        addFavFloatingActionButton.setImageResource(R.drawable.ic_heart_black_24dp);

        userReference.document(model.getId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        String toUser = "Removed from favourites";
                        callSnackbar(toUser);
                        inFavourites = false;

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        String toUser = e.getMessage();
                        callSnackbar(toUser);
                        inFavourites = true;

                    }
                });


    }

    private void addToFavWalls() {

        addFavFloatingActionButton.setImageResource(R.drawable.ic_heart_red_24dp);
        userReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                if (queryDocumentSnapshots == null) {
                    count = 0;
                    return;

                }

                for (DocumentSnapshot doc : queryDocumentSnapshots) {

                    count = count + 1;

                }

                addDataToFirebase(count);
            }
        });


    }

    private void addDataToFirebase(int count) {

        String docPath = model.getId();

        Log.d("Test_", "initial" + String.valueOf(count));

        FavouriteModelClass modelClass = new FavouriteModelClass(model, count);

        Log.d("Test_", "After" + String.valueOf(count));

        userReference.document(docPath)
                .set(modelClass)
                .addOnSuccessListener(WallpaperViewActivity.this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        String toUser = "Added to favourites";
                        callSnackbar(toUser);
                        inFavourites = true;

                    }
                })
                .addOnFailureListener(WallpaperViewActivity.this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        String toUser = e.getMessage();
                        callSnackbar(toUser);

                    }
                });


    }

    private void shareImages() {

        if (urlsModel == null) {

            return;

        }

        final ShareDialogBox shareDialogBox = new ShareDialogBox();
        shareDialogBox.show(getSupportFragmentManager(), getString(R.string.apply_dialog_box));

        GlideApp.with(getApplicationContext())
                .asBitmap()
                .load(urlsModel.getRegular())
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("image/*");
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(Intent.EXTRA_STREAM, getLocalBitmapUri(resource, model.getId()));
                        shareDialogBox.dismiss();
                        Intent intentChooser = Intent.createChooser(intent, "Open with");
                        intentChooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intentChooser);
                        isMakingReadyForShare = false;

                    }
                });


    }

    private Uri getLocalBitmapUri(Bitmap resource, String imageId) {

        Uri bitmapUri = null;
        try {

            File image = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/Wall HD/"), imageId + ".jpg");

            FileOutputStream outputStream = new FileOutputStream(image);
            resource.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
            outputStream.close();
            bitmapUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", image);
            Log.d("MY_URI", ": " + bitmapUri);


        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmapUri;

    }

    private void setBackWallpaper() {

        final ApplyDialogBox dialogBox = new ApplyDialogBox();
        dialogBox.show(getSupportFragmentManager(), getString(R.string.apply_dialog_box));

        final WallpaperManager wallpaperManager = (WallpaperManager) getApplicationContext().getSystemService(Context.WALLPAPER_SERVICE);

        assert wallpaperManager != null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            boolean isAllowed = wallpaperManager.isSetWallpaperAllowed();

            if (!isAllowed) {
                String msg = "Setting wallpaper is not allowed";
                Toasty.error(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                return;
            }

        }

        GlideApp.with(context)
                .asBitmap()
                .load(urlsModel.getRegular())
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                        try {
                            wallpaperManager.setBitmap(resource);
                            callSnackbar("Applied as wallpaper");
                            dialogBox.dismiss();
                            isApplyingAsWall = false;

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                });


    }

    private void callSnackbar(String info, int id) {

        CoordinatorLayout coordinatorLayout = findViewById(R.id.wallpaperViewCoordinatorLayout);
        Snackbar snackbar = null;

        switch (id) {

            case DOWNLOAD:

                snackbar = Snackbar.make(coordinatorLayout, info, Snackbar.LENGTH_SHORT)
                        .setAction(getString(R.string.download_again), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                downloadWallpaper();

                            }
                        })
                        .setActionTextColor(Color.WHITE);

                break;

            case NEED_SIGN:

                snackbar = Snackbar.make(coordinatorLayout, info, Snackbar.LENGTH_SHORT)
                        .setAction(getString(R.string.sign_in), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                startActivity(new Intent(WallpaperViewActivity.this, SignInActivity.class));

                            }
                        })
                        .setActionTextColor(Color.WHITE);

                break;

        }

        assert snackbar != null;
        snackbar.show();

    }

    private void callSnackbar(String info) {

        CoordinatorLayout coordinatorLayout = findViewById(R.id.wallpaperViewCoordinatorLayout);
        Snackbar snackbar;

        snackbar = Snackbar.make(coordinatorLayout, info, Snackbar.LENGTH_SHORT);

        snackbar.show();

    }


    private void downloadWallpaper() {

        if (!isStoragePermissionGranted) {

            checkForStoragePermission();

        } else {


            if (model == null) {
                return;
            }

            String imageID = model.getId();

            RetrofitService service = RetrofitService.getInstance();
            UnsplashApiListener apiListener = service.getApiListener();

            Call<DownloadUrlLocation> urlLocationCall = apiListener.getDownloadLocationUrl(imageID, WallHDConstants.CLIENT_ID);

            urlLocationCall.enqueue(new Callback<DownloadUrlLocation>() {
                @Override
                public void onResponse(@NonNull Call<DownloadUrlLocation> call, @NonNull Response<DownloadUrlLocation> response) {

                    if (!response.isSuccessful()) {

                        Toasty.error(getApplicationContext(), response.code() + " error", Toast.LENGTH_LONG).show();

                        return;

                    }

                    DownloadUrlLocation urlLocation = response.body();

                    assert urlLocation != null;
                    Uri imageURI = Uri.parse(urlLocation.getUrl());

                    String imageName = model.getId() + ".jpg";
                    String mainFilePath = Environment.DIRECTORY_PICTURES + "/Wall HD/";

                    downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

                    DownloadManager.Request request = new DownloadManager.Request(imageURI);

                    request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                            .setAllowedOverRoaming(false)
                            .setTitle("Wall HD")
                            .setDescription("Downloading in progress")
                            .setVisibleInDownloadsUi(true)
                            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                            .setDestinationInExternalPublicDir(mainFilePath, imageName)
                            .allowScanningByMediaScanner();

                    assert downloadManager != null;
                    referenceId = downloadManager.enqueue(request);

                    broadcastReceiver = new AppBroadcastReceiver(imageName);
                    registerReceiver(broadcastReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                    UserDownloadedWalls = true;

                }

                @Override
                public void onFailure(@NonNull Call<DownloadUrlLocation> call, @NonNull Throwable t) {

                    Toasty.error(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();

                }
            });


        }

    }


    private void checkForStoragePermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                isStoragePermissionGranted = true;
                downloadWallpaper();

            } else {

                if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {


                    Toast.makeText(this, "App needs to save walls", Toast.LENGTH_SHORT).show();

                }
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSIONS);

            }

        } else {

            isStoragePermissionGranted = true;
            downloadWallpaper();

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        switch (requestCode) {
            case STORAGE_PERMISSIONS:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    isStoragePermissionGranted = true;
                    downloadWallpaper();

                }
                break;

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }

    @Override
    public void onBackPressed() {

        unRevealActivity();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (UserDownloadedWalls) {
            unregisterReceiver(broadcastReceiver);
        }
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
