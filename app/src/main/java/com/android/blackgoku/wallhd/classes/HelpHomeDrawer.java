package com.android.blackgoku.wallhd.classes;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.blackgoku.wallhd.R;
import com.android.blackgoku.wallhd.model.UserHelp;
import com.android.blackgoku.wallhd.utility.FirebaseFirestoreUtilityClass;
import com.android.blackgoku.wallhd.utility.SignedInUserDetails;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class HelpHomeDrawer extends Fragment implements View.OnClickListener {

    private CollectionReference helpCollectionRef;
    private TextInputEditText titleInputEditText, descInputEditText;
    private MaterialButton sendHelpButton;
    private ProgressBar sendingProgressBar;
    private boolean isInProgress = false;

    public HelpHomeDrawer() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.help_home_drawer_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        findView(view);

        helpCollectionRef = FirebaseFirestoreUtilityClass.getHelpCollectionRoot();

    }

    private void findView(View view) {

        titleInputEditText = view.findViewById(R.id.titleInputEditText);
        descInputEditText = view.findViewById(R.id.descInputEditText);
        sendHelpButton = view.findViewById(R.id.sendHelpButton);
        sendingProgressBar = view.findViewById(R.id.sendingProgressBar);
        sendingProgressBar.setVisibility(View.GONE);

        sendHelpButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.sendHelpButton:
                if (!isInProgress) {
                    sendHelpMail();
                } else {

                    Toasty.info(Objects.requireNonNull(getContext()), "Already sending...", Toast.LENGTH_LONG).show();

                }

        }

    }

    private String getCurrentDate() {

        Date currentTime = Calendar.getInstance().getTime();
        return String.valueOf(currentTime);

    }

    private void reset(){

        isInProgress = false;
        sendingProgressBar.setVisibility(View.GONE);
        sendHelpButton.setText(R.string.send_help);

    }
    private void sendHelpMail() {

        isInProgress = true;
        sendHelpButton.setText(R.string.sending);
        sendingProgressBar.setVisibility(View.VISIBLE);
        assert getContext() != null;
        String titleText = String.valueOf(titleInputEditText.getText());
        String descText = String.valueOf(descInputEditText.getText());

        if (titleText.trim().equals("")) {

            Toasty.warning(getContext(), "Title can't be empty", Toast.LENGTH_LONG).show();
            reset();
            return;

        }

        if (descText.trim().equals("")) {

            Toasty.warning(getContext(), "Description can't be empty", Toast.LENGTH_LONG).show();
            reset();
            return;

        }

        SignedInUserDetails userDetails = new SignedInUserDetails();
        String userEmail = userDetails.getUserEmail();

        UserHelp help = new UserHelp(titleText, descText, userEmail, getCurrentDate());

        String timeStamp = String.valueOf(System.currentTimeMillis());

        helpCollectionRef.document(timeStamp)
                .set(help)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Toasty.success(Objects.requireNonNull(getContext()), "Sent, will get you soon...", Toast.LENGTH_LONG).show();
                        titleInputEditText.setText("");
                        descInputEditText.setText("");
                        reset();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toasty.error(Objects.requireNonNull(getContext()), e.getMessage(), Toast.LENGTH_LONG).show();
                        reset();


                    }
                });

    }
}
