package com.test.feulmgmt.ui.feedback;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.test.feulmgmt.MainActivity;
import com.test.feulmgmt.R;
import com.test.feulmgmt.pojos.feedback.FeedbackResponse;
import com.test.feulmgmt.utils.ApiUrls;
import com.test.feulmgmt.utils.FuelPrefs;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FeedbackListsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FeedbackListsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private AppCompatTextView txtTitle;
    private ProgressBar progress_bar;
    private AppCompatImageView imgBack, imgDrawer;
    private RecyclerView feedbackList;
    private FuelPrefs prefs;

    public FeedbackListsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FeedbackListsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FeedbackListsFragment newInstance(String param1, String param2) {
        FeedbackListsFragment fragment = new FeedbackListsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_feedback_lists, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prefs = new FuelPrefs();
        txtTitle = view.findViewById(R.id.txtTitle);
        txtTitle.setText("Feedback Lists");
        progress_bar = view.findViewById(R.id.progress_bar);

        imgBack = view.findViewById(R.id.imgBack);
        imgDrawer = view.findViewById(R.id.imgDrawer);

        imgBack.setOnClickListener(view12 -> {
            ((MainActivity) getActivity()).onBackPressed();
        });

        imgDrawer.setOnClickListener(view1 -> ((MainActivity) getActivity()).showDrawer());

        feedbackList = view.findViewById(R.id.feedbackList);
        feedbackList.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));

        fetchFeedbackLists();
    }

    private void fetchFeedbackLists() {
        progress_bar.setVisibility(View.VISIBLE);
        Ion.with(requireContext()).load(ApiUrls.FEEDBACK_LISTS).setHeader("Authorization", "Bearer " + prefs.getUserData().getToken()).asJsonObject().setCallback(new FutureCallback<JsonObject>() {
            @Override
            public void onCompleted(Exception e, JsonObject result) {
                progress_bar.setVisibility(View.GONE);
                if (result.get("meta").getAsJsonObject().get("success").getAsBoolean()) {
                    FeedbackResponse response = new Gson().fromJson(result, FeedbackResponse.class);
                    feedbackList.setAdapter(new FeedBackAdapter(response.getData().getFeedbacks()));
                } else {
                    Toast.makeText(requireContext(), result.get("meta").getAsJsonObject().get("message").getAsString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}