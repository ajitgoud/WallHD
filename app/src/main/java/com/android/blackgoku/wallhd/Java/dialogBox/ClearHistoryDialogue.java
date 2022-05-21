package com.android.blackgoku.wallhd.Java.dialogBox;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.widget.Toast;

import com.android.blackgoku.wallhd.R;
import com.android.blackgoku.wallhd.utility.WallHDConstants;

import java.util.Objects;

import es.dmoral.toasty.Toasty;


public class ClearHistoryDialogue extends AppCompatDialogFragment {

    private static final String DIALOGUE_MSG = "Clear search history ?";
    private static final String POSITIVE_BUTTON = "Clear";
    private static final String NEGATIVE_BUTTON = "Cancel";

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()), R.style.DialogTheme);
        builder.setMessage(DIALOGUE_MSG)
                .setPositiveButton(POSITIVE_BUTTON, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        SearchRecentSuggestions suggestions = new SearchRecentSuggestions(getActivity(), WallHDConstants.AUTHORITY, WallHDConstants.MODE);
                        suggestions.clearHistory();
                        Toasty.success(Objects.requireNonNull(getActivity()), "Search history cleared", Toast.LENGTH_SHORT).show();

                    }
                })
                .setNegativeButton(NEGATIVE_BUTTON, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                });

        AlertDialog alertDialog = builder.create();
        Objects.requireNonNull(alertDialog.getWindow()).getAttributes().windowAnimations = R.style.ZoomIn_ZoomOut;
        return alertDialog;

    }
}
