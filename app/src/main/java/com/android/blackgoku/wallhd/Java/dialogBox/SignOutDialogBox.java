package com.android.blackgoku.wallhd.Java.dialogBox;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.View;
import android.view.ViewAnimationUtils;

import com.android.blackgoku.wallhd.R;

import java.util.Objects;

public class SignOutDialogBox extends AppCompatDialogFragment {

    private static final String title = "Sign out";
    private static final String msg = "Are you sure?";
    private static final String positiveButton = "Sign out";
    private static final String negativeButton = "Cancel";
    private SignOutOnClick listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()), R.style.DialogTheme);
        builder.setTitle(title)
                .setMessage(msg)
                .setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (listener != null) {

                            listener.signOut();

                        }
                    }
                })

                .setNegativeButton(negativeButton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dismiss();

                    }
                });

        AlertDialog alertDialog = builder.create();
        Objects.requireNonNull(alertDialog.getWindow()).getAttributes().windowAnimations = R.style.ZoomIn_ZoomOut;

        return alertDialog;
    }

    public interface SignOutOnClick {

        void signOut();

    }


    private void showCircularReveal(View view) {

        int cx = view.getWidth() / 2;
        int cy = view.getHeight() / 2;
        float finalRadius = (float) Math.hypot(cx, cy);

        Animator anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, finalRadius);
        anim.setDuration(500);
        anim.start();

    }

    private void hideCircularReveal(View view) {

        int cx = view.getWidth() / 2;
        int cy = view.getHeight() / 2;
        float finalRadius = (float) Math.hypot(cx, cy);

        Animator anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, finalRadius, 0);
        anim.setDuration(500);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                dismiss();
            }
        });
        anim.start();

    }

    public void SignOutOnClickListener(SignOutOnClick listener) {

        this.listener = listener;

    }
}
