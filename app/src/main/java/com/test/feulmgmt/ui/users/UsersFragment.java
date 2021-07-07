package com.test.feulmgmt.ui.users;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
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
import com.test.feulmgmt.pojos.userLists.UserListResponse;
import com.test.feulmgmt.pojos.userLists.UsersItem;
import com.test.feulmgmt.utils.ApiUrls;
import com.test.feulmgmt.utils.FuelPrefs;
import com.test.feulmgmt.utils.SwipeHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UsersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UsersFragment extends Fragment {

    private AppCompatTextView txtTitle;
    private AppCompatImageView imgBack, imgDrawer;
    private RecyclerView usersList;
    private FuelPrefs prefs;
    private ProgressBar progressBar;
    List<UsersItem> items;
    UsersAdapter usersAdapter;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UsersFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UsersFragment newInstance(String param1, String param2) {
        UsersFragment fragment = new UsersFragment();
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
        return inflater.inflate(R.layout.fragment_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        items = new ArrayList<>();
        prefs = new FuelPrefs();
        txtTitle = view.findViewById(R.id.txtTitle);
        txtTitle.setText("Users List");

        progressBar = view.findViewById(R.id.progress_bar);
        imgBack = view.findViewById(R.id.imgBack);
        imgDrawer = view.findViewById(R.id.imgDrawer);

        imgBack.setOnClickListener(view12 -> {
            ((MainActivity) getActivity()).onBackPressed();
        });

        imgDrawer.setOnClickListener(view1 -> ((MainActivity) getActivity()).showDrawer());

        usersList = view.findViewById(R.id.usersList);
        usersList.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        usersAdapter = new UsersAdapter(items);
        usersList.setAdapter(usersAdapter);

        fetchUsersList();
        itemSwipe();
    }

    private void fetchUsersList() {
        progressBar.setVisibility(View.VISIBLE);
        Ion.with(requireContext()).load(ApiUrls.USERS_LIST).
                setHeader("Authorization", "Bearer " + prefs.getUserData().getToken()).
                asJsonObject().
                setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        progressBar.setVisibility(View.GONE);
                        if (result != null && result.get("meta").getAsJsonObject().get("success").getAsBoolean()) {
                            Toast.makeText(requireContext(), result.get("meta").getAsJsonObject().get("message").getAsString(), Toast.LENGTH_SHORT).show();
                            UserListResponse userListsResponse = new Gson().fromJson(result, UserListResponse.class);
                            items.clear();
                            items.addAll(userListsResponse.getData().getUsers());
                            usersAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(requireContext(), result.get("meta").getAsJsonObject().get("message").getAsString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void itemSwipe() {
        SwipeHelper swipeHelper = new SwipeHelper(requireContext(), usersList, items) {
            @Override
            public void instantiateUnderlayButton(RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons) {

                underlayButtons.add(new UnderlayButton(requireContext(),
                        "Delete",
                        Color.parseColor("#172B4D"),
                        Color.parseColor("#172B4D"),
                        R.drawable.ic_trash,
                        Color.parseColor("#EEEEEE"),
                        (UnderlayButtonClickListener) pos -> {
                            deleteUser(items.get(pos).getId());
                        }
                ));
            }
        };
    }

    private void deleteUser(int id) {

        Ion.with(this).load("DELETE",ApiUrls.DELETE_FUEL + id)
                .setHeader("Authorization", "Bearer " + prefs.getUserData().getToken())
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (result != null && result.get("meta").getAsJsonObject().get("success").getAsBoolean()) {
                            Toast.makeText(requireContext(), "User deleted successfully", Toast.LENGTH_SHORT).show();
                            fetchUsersList();
                        } else {
                            Toast.makeText(requireContext(), "User could not be deleted", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}