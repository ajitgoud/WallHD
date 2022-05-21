package com.android.blackgoku.wallhd.Java.dialogBox;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;

import com.android.blackgoku.wallhd.R;

import java.util.Objects;

public class ShareDialogBox extends AppCompatDialogFragment {


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater inflater = Objects.requireNonNull(getActivity()).getLayoutInflater();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.DialogTheme);

        builder.setView(inflater.inflate(R.layout.share_dialob_box, null));

        AlertDialog alertDialog = builder.create();
        Objects.requireNonNull(alertDialog.getWindow()).getAttributes().windowAnimations = R.style.ZoomIn_ZoomOut;
        return alertDialog;

    }
}
