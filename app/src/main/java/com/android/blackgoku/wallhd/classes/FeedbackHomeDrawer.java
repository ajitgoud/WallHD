package com.android.blackgoku.wallhd.classes;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatRatingBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.blackgoku.wallhd.R;
import com.android.blackgoku.wallhd.model.UserFeedback;
import com.android.blackgoku.wallhd.utility.FirebaseFirestoreUtilityClass;
import com.android.blackgoku.wallhd.utility.SignedInUserDetails;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class FeedbackHomeDrawer extends Fragment implements View.OnClickListener {

    private CollectionReference feedbackCollectionRef;
    private AppCompatRatingBar ratingBar;
    private TextInputEditText descInputEditText;
    private MaterialButton sendFeedbackButton;
    private ProgressBar submittingProgressBar;
    private boolean isInProgress = false;

    public FeedbackHomeDrawer() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.feedback_home_drawer_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        feedbackCollectionRef = FirebaseFirestoreUtilityClass.getFeedbackCollectionRoot();

        findViews(view);
        setClicks();
    }

    private void setClicks() {

        sendFeedbackButton.setOnClickListener(this);

    }

    private void findViews(View view) {

        ratingBar = view.findViewById(R.id.ratingBar);
        descInputEditText = view.findViewById(R.id.descInputEditText);
        sendFeedbackButton = view.findViewById(R.id.sendFeedbackButton);
        submittingProgressBar = view.findViewById(R.id.submittingProgressBar);
        submittingProgressBar.setVisibility(View.GONE);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.sendFeedbackButton:

                if (!isInProgress) {
                    sendFeedback();
                } else {

                    Toasty.info(Objects.requireNonNull(getContext()), "Already submitting...", Toast.LENGTH_LONG).show();

                }


        }

    }

    private String getCurrentDate() {

        Date currentTime = Calendar.getInstance().getTime();
        return String.valueOf(currentTime);

    }

    private void reset(){

        isInProgress = false;
        submittingProgressBar.setVisibility(View.GONE);
        sendFeedbackButton.setText(getString(R.string.submit));

    }

    private void sendFeedback() {

        isInProgress = true;
        sendFeedbackButton.setText(getString(R.string.submitting));
        submittingProgressBar.setVisibility(View.VISIBLE);

        String rating = String.valueOf(ratingBar.getRating());
        String descText = String.valueOf(descInputEditText.getText());

        if (rating.equals("0")) {

            Toasty.warning(Objects.requireNonNull(getContext()), "Please rate...", Toast.LENGTH_SHORT).show();
            reset();
            return;
        }

        SignedInUserDetails userDetails = new SignedInUserDetails();
        String userEmail = userDetails.getUserEmail();

        UserFeedback feedback = new UserFeedback(rating, descText, userEmail, getCurrentDate());

        String timeStamp = String.valueOf(System.currentTimeMillis());

        feedbackCollectionRef.document(timeStamp)
                .set(feedback)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Toasty.success(Objects.requireNonNull(getContext()), "Thank you for feedback!", Toast.LENGTH_LONG).show();
                        ratingBar.setRating(0f);
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
