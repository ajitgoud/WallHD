package com.android.blackgoku.wallhd.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.blackgoku.wallhd.model.UserDetail;
import com.android.blackgoku.wallhd.R;
import com.android.blackgoku.wallhd.utility.FirebaseFirestoreUtilityClass;
import com.android.blackgoku.wallhd.utility.SignedInUserDetails;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;

import java.util.Arrays;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

import static com.android.blackgoku.wallhd.tabs.LatestWallpaperTabFragment.WALLPAPER_VIEW_REVEAL_X;
import static com.android.blackgoku.wallhd.tabs.LatestWallpaperTabFragment.WALLPAPER_VIEW_REVEAL_Y;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int GOOGLE_SIGN_IN_REQUEST_CODE = 1;
    private CardView googleSignInCardView, facebookSignInCardView;
    private FirebaseAuth.AuthStateListener authStateListener;
    private GoogleSignInClient signInClient;
    private RelativeLayout rootLayout;
    private int revealX;
    private int revealY;

    private CallbackManager callbackManager;

    private CollectionReference rootUserCollectionLatestRef;

    private HomeBroadcastReceiver homeBroadcastReceiver;
    private CardView internetStatusCardView;
    private AppCompatTextView internetStatusAppCompatText;

    private boolean isConnectionNotAvailable = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        final Intent intent = getIntent();
        rootLayout = findViewById(R.id.signInRootLayout);

       /* if (savedInstanceState == null && intent.hasExtra(WALLPAPER_VIEW_REVEAL_X) && intent.hasExtra(WALLPAPER_VIEW_REVEAL_Y)) {

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

        }*/

        rootUserCollectionLatestRef = FirebaseFirestoreUtilityClass.getUserDetailCollectionRoot();

        setUpToolbarAndFindViewById();

        listenerForUserIsNullOrNot();

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
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(homeBroadcastReceiver);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    @Override
    public void onClick(View v) {

        String msg = "No internet connection, please try later...";

        switch (v.getId()) {

            case R.id.googleSignInCardView:

                if (isConnectionNotAvailable) {

                    Toasty.warning(getApplicationContext(), msg, Toast.LENGTH_LONG).show();

                } else {

                    setAllButtonDisabled();

                    googleSignInSetUp();

                }

                break;

            case R.id.facebookSignInCardView:

                if (isConnectionNotAvailable) {

                    Toasty.warning(getApplicationContext(), msg, Toast.LENGTH_LONG).show();

                } else {

                    setAllButtonDisabled();

                    facebookSignInSetup();
                }

                break;

        }

    }

    private void listenerForUserIsNullOrNot() {

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (FirebaseFirestoreUtilityClass.currentUserIsNotNull()) {

                    if (signInClient != null) {

                        signInClient.signOut();

                    }

                    // unRevealActivity();
                    finish();
                    overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);

//                    Intent afterSignInIntent = new Intent(SignInActivity.this, HomeActivity.class);
//                    afterSignInIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    startActivity(afterSignInIntent);
//                    overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);

                }

            }
        };

    }

    private void facebookSignInSetup() {

        callbackManager = CallbackManager.Factory.create();
        LoginManager instance = LoginManager.getInstance();
        instance.logInWithReadPermissions(SignInActivity.this, Arrays.asList("email", "public_profile"));
        instance.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                fireBaseAuthWithFacebook(loginResult.getAccessToken());

            }

            @Override
            public void onCancel() {

                Toasty.error(getApplicationContext(), "You denied sign in", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onError(FacebookException error) {

                Toasty.error(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });


    }

    private void fireBaseAuthWithFacebook(AccessToken accessToken) {

        AuthCredential authCredential = FacebookAuthProvider.getCredential(accessToken.getToken());
        signInWithCredentialMethod(authCredential, "facebook");

    }

    private void googleSignInSetUp() {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        signInClient = GoogleSignIn.getClient(this, gso);

        if (!FirebaseFirestoreUtilityClass.currentUserIsNotNull()) {

            Intent googleSignInIntent = signInClient.getSignInIntent();
            startActivityForResult(googleSignInIntent, GOOGLE_SIGN_IN_REQUEST_CODE);


        }

    }


    private void fireBaseAuthWithGoogle(GoogleSignInAccount account) {

        AuthCredential authCredential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        signInWithCredentialMethod(authCredential, "google");

    }


    private void signInWithCredentialMethod(AuthCredential credential, final String loginPlatform) {


        FirebaseFirestoreUtilityClass.getFirebaseAuth().signInWithCredential(credential).addOnCompleteListener(SignInActivity.this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            Toasty.success(SignInActivity.this, "Signed in using " + loginPlatform + " successfully", Toast.LENGTH_SHORT).show();
                            userDetails();

                        } else {

                            Toasty.error(SignInActivity.this, "Failed to sign in", Toast.LENGTH_SHORT).show();
                            setAllButtonEnabled();

                        }

                    }
                });


    }

    private void userDetails() {

        SignedInUserDetails userDetails = new SignedInUserDetails();
        if (userDetails.getUserEmail() == null)
            return;
        uploadUserDetails(userDetails.getUserUID(), userDetails.getUserName(), userDetails.getUserEmail(), userDetails.getAuthProvider(), userDetails.getAccountCreationDate());

    }

    private void uploadUserDetails(String userUID, String userName, String userEmail, String userProvider, String userAccountCreationDate) {

        UserDetail userDetail = new UserDetail(userName, userEmail, userProvider, userAccountCreationDate);

        rootUserCollectionLatestRef.document(userUID).set(userDetail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                Toasty.success(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GOOGLE_SIGN_IN_REQUEST_CODE && resultCode == RESULT_OK && data != null) {

            Task<GoogleSignInAccount> taskSignInAccount = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {

                GoogleSignInAccount account = taskSignInAccount.getResult(ApiException.class);
                assert account != null;
                fireBaseAuthWithGoogle(account);


            } catch (ApiException e) {
                e.printStackTrace();
                Toasty.error(SignInActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

            }

        } else {

            if (data != null && resultCode == RESULT_OK) {

                callbackManager.onActivityResult(requestCode, resultCode, data);

            }

        }

        setAllButtonEnabled();

    }


    private void setUpToolbarAndFindViewById() {

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);

            }
        });
        Objects.requireNonNull(getSupportActionBar()).setTitle("Sign In");

        googleSignInCardView = findViewById(R.id.googleSignInCardView);
        facebookSignInCardView = findViewById(R.id.facebookSignInCardView);

        internetStatusAppCompatText = findViewById(R.id.internetStatusAppCompatText);
        internetStatusCardView = findViewById(R.id.internetStatusCardView);

        googleSignInCardView.setOnClickListener(this);
        facebookSignInCardView.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseFirestoreUtilityClass.getFirebaseAuth().addAuthStateListener(authStateListener);

    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseFirestoreUtilityClass.getFirebaseAuth().removeAuthStateListener(authStateListener);
    }


    private void setAllButtonDisabled() {

        googleSignInCardView.setEnabled(false);
        facebookSignInCardView.setEnabled(false);

    }

    private void setAllButtonEnabled() {

        googleSignInCardView.setEnabled(true);
        facebookSignInCardView.setEnabled(true);

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
