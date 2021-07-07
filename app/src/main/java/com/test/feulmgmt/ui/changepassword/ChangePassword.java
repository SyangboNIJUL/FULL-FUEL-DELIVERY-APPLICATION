package com.test.feulmgmt.ui.changepassword;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.test.feulmgmt.MainActivity;
import com.test.feulmgmt.R;
import com.test.feulmgmt.utils.ApiUrls;
import com.test.feulmgmt.utils.FuelPrefs;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChangePassword#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChangePassword extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FuelPrefs prefs;

    public ChangePassword() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static ChangePassword newInstance() {
        ChangePassword fragment = new ChangePassword();

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
        return inflater.inflate(R.layout.fragment_change_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prefs = new FuelPrefs();

        AppCompatTextView txtTitle = view.findViewById(R.id.txtTitle);
        txtTitle.setText("Change Password");

        AppCompatImageView imgBack = view.findViewById(R.id.imgBack);
        AppCompatImageView imgDrawer = view.findViewById(R.id.imgDrawer);

        imgBack.setOnClickListener(view12 -> {
            ((MainActivity) getActivity()).onBackPressed();
        });

        imgDrawer.setOnClickListener(view1 -> ((MainActivity) getActivity()).showDrawer());

        TextInputEditText oldPassword = view.findViewById(R.id.edtOldPassword);
        TextInputEditText newPassword = view.findViewById(R.id.edtPassword);
        TextInputEditText confirmPassword = view.findViewById(R.id.edtCPassword);

        view.findViewById(R.id.btnSave).setOnClickListener(v -> {

            if (oldPassword.getText().length() <= 0) {
                Toast.makeText(getContext(), "Please enter your old Password", Toast.LENGTH_SHORT).show();
                return;
            }

            if (newPassword.getText().length() <= 0) {
                Toast.makeText(getContext(), "Please enter your New Password", Toast.LENGTH_SHORT).show();
                return;
            }

            if (confirmPassword.getText().length() <= 0) {
                Toast.makeText(getContext(), "Please enter your Confirm Password", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!confirmPassword.getText().toString().equals(newPassword.getText().toString())) {
                Toast.makeText(getContext(), "Passwords don't match", Toast.LENGTH_SHORT).show();
                return;
            }


            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("oldPassword", oldPassword.getText().toString());
            jsonObject.addProperty("newPassword", newPassword.getText().toString());
            jsonObject.addProperty("role", "User");


            Ion.with(requireContext()).load("PATCH", ApiUrls.CHANGE_PASSWORD)
                    .setHeader("Authorization", "Bearer " + prefs.getUserData().getToken())
                    .setJsonObjectBody(jsonObject)
                    .asJsonObject().setCallback(new FutureCallback<JsonObject>() {
                @Override
                public void onCompleted(Exception e, JsonObject result) {
                    if (result != null && result.get("meta").getAsJsonObject().get("success").getAsBoolean()) {
                        Toast.makeText(requireContext(), result.get("meta").getAsJsonObject().get("message").getAsString(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireContext(), result.get("meta").getAsJsonObject().get("message").getAsString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });
    }
}