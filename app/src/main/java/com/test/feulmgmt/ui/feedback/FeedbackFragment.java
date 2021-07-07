package com.test.feulmgmt.ui.feedback;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.Task;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.test.feulmgmt.BuildConfig;
import com.test.feulmgmt.MainActivity;
import com.test.feulmgmt.R;
import com.test.feulmgmt.utils.ApiUrls;
import com.test.feulmgmt.utils.FuelPrefs;

public class FeedbackFragment extends Fragment {
    private AppCompatTextView txtTitle,txtCopyLink;
    private AppCompatImageView imgBack, imgDrawer;
    private AppCompatButton btnFeedback, btnShare;
    private AppCompatEditText edtFeedback;
    private FuelPrefs prefs;
    private ProgressBar progress_bar;
    private ReviewManager reviewManager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_feedback, container, false);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        reviewManager = ReviewManagerFactory.create(requireContext());
        txtTitle = view.findViewById(R.id.txtTitle);
        txtTitle.setText("Feedback");

        imgBack = view.findViewById(R.id.imgBack);
        imgDrawer = view.findViewById(R.id.imgDrawer);

        prefs = new FuelPrefs();
        progress_bar = view.findViewById(R.id.progress_bar);

        imgBack.setOnClickListener(view12 -> {
            ((MainActivity) getActivity()).onBackPressed();
        });

        imgDrawer.setOnClickListener(view1 -> ((MainActivity) getActivity()).showDrawer());

        btnFeedback = view.findViewById(R.id.btnFeedback);
        btnFeedback.setOnClickListener(view13 -> submitFeedback());

        edtFeedback = view.findViewById(R.id.edtFeedback);

        btnShare = view.findViewById(R.id.btnShare);
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,
                        "Hello Welcome to FULL!");
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });

        txtCopyLink = view.findViewById(R.id.txtCopyLink);
        txtCopyLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*ClipboardManager clipboard = (ClipboardManager) requireActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label", "text");
                clipboard.setPrimaryClip(clip);
                Toast.makeText(requireContext(), "Link Copied", Toast.LENGTH_SHORT).show();*/
                showRateApp();
            }
        });
    }

    public void showRateApp() {
        Task<ReviewInfo> request = reviewManager.requestReviewFlow();
        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // We can get the ReviewInfo object
                ReviewInfo reviewInfo = task.getResult();

                Task<Void> flow = reviewManager.launchReviewFlow(requireActivity(), reviewInfo);
                flow.addOnCompleteListener(task1 -> {
                    // The flow has finished. The API does not indicate whether the user
                    // reviewed or not, or even whether the review dialog was shown. Thus, no
                    // matter the result, we continue our app flow.
                    Toast.makeText(requireContext(), "App not published in play store.", Toast.LENGTH_SHORT).show();
                });
            } else {
                // There was some problem, continue regardless of the result.
                // show native rate app dialog on error
                showRateAppFallbackDialog();
            }
        });
    }

    /**
     * Showing native dialog with three buttons to review the app
     * Redirect user to playstore to review the app
     */
    private void showRateAppFallbackDialog() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Rate our app")
                .setMessage("Provide us with your best rating possible.")
                .setPositiveButton("OK", (dialog, which) -> {

                })
                .setNegativeButton("CANCEL",
                        (dialog, which) -> {
                        })
                .setNeutralButton("SKIP",
                        (dialog, which) -> {
                        })
                .setOnDismissListener(dialog -> {
                })
                .show();
    }

    private void submitFeedback() {
        if (!edtFeedback.getText().toString().isEmpty()) {
            progress_bar.setVisibility(View.VISIBLE);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("message", edtFeedback.getText().toString());
            Ion.with(requireContext()).load(ApiUrls.SUBMIT_FEEDBACK).setHeader("Authorization", "Bearer " + prefs.getUserData().getToken()).
                    setJsonObjectBody(jsonObject).asJsonObject().setCallback(new FutureCallback<JsonObject>() {
                @Override
                public void onCompleted(Exception e, JsonObject result) {
                    progress_bar.setVisibility(View.GONE);
                    Toast.makeText(requireContext(), result.get("meta").getAsJsonObject().get("message").getAsString(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(requireContext(), "Please enter the text", Toast.LENGTH_SHORT).show();
        }
    }

}