package com.test.feulmgmt.ui.profile;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.async.http.body.MultipartFormDataBody;
import com.koushikdutta.ion.Ion;
import com.squareup.picasso.Picasso;
import com.test.feulmgmt.MainActivity;
import com.test.feulmgmt.R;
import com.test.feulmgmt.pojos.users.UserData;
import com.test.feulmgmt.pojos.users.UserDetail;
import com.test.feulmgmt.pojos.users.UserResponse;
import com.test.feulmgmt.utils.ApiUrls;
import com.test.feulmgmt.utils.FuelPrefs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {
    private AppCompatTextView txtTitle;
    private AppCompatImageView imgBack, imgDrawer, imgCamera, imgEdit,imgPencil;
    private CircleImageView imgUser;
    private AppCompatButton btnSave;
    private TextInputEditText edtName, edtEmail, edtPhone, edtAddress, edtCountry,edtPass;
    private TextInputLayout txtName, txtEmail, txtPhone, txtAddress, txtCountry,txtPass;
    private FuelPrefs prefs;
    private Bitmap imageBitmap = null;
    private ProgressBar progressBar;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prefs = new FuelPrefs();
        progressBar = view.findViewById(R.id.progress_bar);
        txtTitle = view.findViewById(R.id.txtTitle);
        txtTitle.setText("Edit Profile");

        imgBack = view.findViewById(R.id.imgBack);
        imgDrawer = view.findViewById(R.id.imgDrawer);

        imgPencil = view.findViewById(R.id.imgPencil);
        imgPencil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchGallery();
            }
        });

        imgBack.setOnClickListener(view12 -> {
            ((MainActivity) getActivity()).onBackPressed();
        });
        imgCamera = view.findViewById(R.id.imgCamera);
        imgDrawer.setOnClickListener(view1 -> ((MainActivity) getActivity()).showDrawer());
        imgCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchCamera();
            }
        });
        imgUser = view.findViewById(R.id.imgUser);

        btnSave = view.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(imageBitmap != null)
                    saveUserDatasWithFile();
                else
                    saveUserDatas();
            }
        });

        edtName = view.findViewById(R.id.edtName);
        edtEmail = view.findViewById(R.id.edtEmail);
        edtPhone = view.findViewById(R.id.edtPhone);
        edtAddress = view.findViewById(R.id.edtAddress);
        edtCountry = view.findViewById(R.id.edtCountry);

        txtName = view.findViewById(R.id.txtName);
        txtEmail = view.findViewById(R.id.txtEmail);
        txtPhone = view.findViewById(R.id.txtPhone);
        txtAddress = view.findViewById(R.id.txtAddress);
        txtCountry = view.findViewById(R.id.txtCountry);

        if (prefs.getUserData() != null) {
            try {
                edtName.setText(Editable.Factory.getInstance().newEditable(prefs.getUserData().getUserDetail().getFullName()));
                edtEmail.setText(Editable.Factory.getInstance().newEditable(prefs.getUserData().getUserDetail().getEmail()));
                edtPhone.setText(Editable.Factory.getInstance().newEditable(prefs.getUserData().getUserDetail().getPhone()));
                edtCountry.setText(Editable.Factory.getInstance().newEditable(prefs.getUserData().getUserDetail().getCountry()));
                edtAddress.setText(Editable.Factory.getInstance().newEditable(prefs.getUserData().getUserDetail().getAddress()));
                if (prefs.getUserData().getUserDetail().getProfilePicture() != null)
                    Picasso.get().load(ApiUrls.USER_IMAGE + prefs.getUserData().getUserDetail().getProfilePicture()).into(imgUser);
            } catch (Exception e) {

            }
        }
    }

    private void launchGallery() {
        ((MainActivity)getActivity()).launchGallery(this);
    }

    private void saveUserDatas() {
        if (checkDetails()) {
            progressBar.setVisibility(View.VISIBLE);
            Ion.with(requireContext()).
                    load("PUT", ApiUrls.UPDATE_USER).
                    setHeader("Authorization", "Bearer " + prefs.getUserData().getToken()).
                    setMultipartParameter("fullName", edtName.getText().toString()).
                    setMultipartParameter("email", edtEmail.getText().toString()).
                    setMultipartParameter("phone", edtPhone.getText().toString()).
                    setMultipartParameter("address", edtAddress.getText().toString()).
                    setMultipartParameter("country", edtCountry.getText().toString()).
                    asJsonObject().
                    setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            progressBar.setVisibility(View.GONE);
                            if (result.get("meta").getAsJsonObject().get("success").getAsBoolean()) {
                                UserData userdata = prefs.getUserData();
                                JsonObject data = result.get("data").getAsJsonObject().get("user").getAsJsonObject();
                                userdata.setUserDetail(new UserDetail(data.get("phone").getAsString(), data.get("fullName").getAsString(), data.get("email").getAsString(),
                                        data.get("role").getAsString(), data.get("address").getAsString(), data.get("country").getAsString(), data.get("profilePicture").getAsString()));
                                prefs.saveUserResponse(userdata);
                                Toast.makeText(requireContext(), result.get("meta").getAsJsonObject().get("message").getAsString(), Toast.LENGTH_SHORT).show();
                                ((MainActivity) getActivity()).onBackPressed();
                            } else {
                                Toast.makeText(requireContext(), result.get("meta").getAsJsonObject().get("message").getAsString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void saveUserDatasWithFile() {
        if (checkDetails()) {
            progressBar.setVisibility(View.VISIBLE);
            Ion.with(requireContext()).
                    load("PUT", ApiUrls.UPDATE_USER).
                    setHeader("Authorization", "Bearer " + prefs.getUserData().getToken()).
                    setMultipartParameter("fullName", edtName.getText().toString()).
                    setMultipartParameter("email", edtEmail.getText().toString()).
                    setMultipartParameter("phone", edtPhone.getText().toString()).
                    setMultipartParameter("address", edtAddress.getText().toString()).
                    setMultipartParameter("country", edtCountry.getText().toString()).
                    setMultipartFile("image", persistImage(imageBitmap, "myImage")).
                    asJsonObject().
                    setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            progressBar.setVisibility(View.GONE);
                            if (result.get("meta").getAsJsonObject().get("success").getAsBoolean()) {
                                UserData userdata = prefs.getUserData();
                                JsonObject data = result.get("data").getAsJsonObject().get("user").getAsJsonObject();
                                userdata.setUserDetail(new UserDetail(data.get("phone").getAsString(), data.get("fullName").getAsString(), data.get("email").getAsString(),
                                        data.get("role").getAsString(), data.get("address").getAsString(), data.get("country").getAsString(), data.get("profilePicture").getAsString()));
                                prefs.saveUserResponse(userdata);
                                Toast.makeText(requireContext(), result.get("meta").getAsJsonObject().get("message").getAsString(), Toast.LENGTH_SHORT).show();
                                ((MainActivity) getActivity()).onBackPressed();
                            } else {
                                Toast.makeText(requireContext(), result.get("meta").getAsJsonObject().get("message").getAsString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private File persistImage(Bitmap bitmap, String name) {
        File filesDir = requireContext().getFilesDir();
        File imageFile = new File(filesDir, name + ".jpg");

        OutputStream os;
        try {
            os = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
            return imageFile;
        } catch (Exception e) {
            Log.e("Error writing bitmap", e.toString());
            return null;
        }
    }

    public boolean checkDetails() {
        if (edtEmail.getText().toString().isEmpty()) {
            edtEmail.setError(null);
            txtEmail.setError("Email field is required");
            return false;
        } else if (edtName.getText().toString().isEmpty()) {
            edtName.setError(null);
            txtName.setError("Name field is required");
            return false;
        } else if (edtPhone.getText().toString().isEmpty()) {
            edtPhone.setError(null);
            txtPhone.setError("Phone field is required");
            return false;
        } else if (edtAddress.getText().toString().isEmpty()) {
            edtAddress.setError(null);
            txtAddress.setError("Address field is required");
            return false;
        } else if (edtCountry.getText().toString().isEmpty()) {
            edtCountry.setError(null);
            txtCountry.setError("Country field is required");
            return false;
        } else {
            return true;
        }
    }

    private void launchCamera() {
        ((MainActivity) getActivity()).launchCamera(this);
    }

    public void updateImage(Bitmap filestring) {
        imageBitmap = filestring;
        imgUser.setImageBitmap(filestring);
    }
}