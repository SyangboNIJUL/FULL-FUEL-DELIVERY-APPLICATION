package com.test.feulmgmt.ui.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.test.feulmgmt.MainActivity;
import com.test.feulmgmt.R;
import com.test.feulmgmt.pojos.users.UserResponse;
import com.test.feulmgmt.utils.FuelPrefs;
import com.test.feulmgmt.utils.LocationUtils;
import com.test.feulmgmt.utils.NotificationSender;
import com.test.feulmgmt.utils.PermissionUtilities;
import com.test.feulmgmt.utils.ApiUrls;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText edtEmail, edtPass;
    private FuelPrefs prefs;
    private TextInputLayout txtPass, txtEmail;
    private ProgressBar progress_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        PermissionUtilities.requestPermission(this);
        prefs = new FuelPrefs();
        edtEmail = findViewById(R.id.edtEmail);
        edtPass = findViewById(R.id.edtPass);
        progress_bar = findViewById(R.id.progress_bar);

        txtEmail = findViewById(R.id.txtEmail);
        txtPass = findViewById(R.id.txtPass);

        if (prefs.isUserLoggedIn()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        edtPass.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    edtPass.setTransformationMethod(null);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    edtPass.setTransformationMethod(new PasswordTransformationMethod());
                }
                return false;
            }
        });

    }

    public void onLogin(View view) {
        if (checkDetails()) {
            progress_bar.setVisibility(View.VISIBLE);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("email", edtEmail.getText().toString());
            jsonObject.addProperty("password", edtPass.getText().toString());
            Ion.with(this).load(ApiUrls.LOGIN).setJsonObjectBody(jsonObject).asJsonObject().setCallback(new FutureCallback<JsonObject>() {
                @Override
                public void onCompleted(Exception e, JsonObject result) {
                    progress_bar.setVisibility(View.GONE);
                    try {
                        if (result.get("meta").getAsJsonObject().get("success").getAsBoolean()) {
                            LocationUtils locationUtils = new LocationUtils();
                            locationUtils.fetchLocation();
                            UserResponse userResponse = new Gson().fromJson(result, UserResponse.class);
                            prefs.saveUserResponse(userResponse.getUserData());
                            prefs.setUserLoggedIn(true);
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, result.get("meta").getAsJsonObject().get("message").getAsString(), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e1) {
                        Toast.makeText(LoginActivity.this, "Unable to perfom login.", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }
    }

    public void onSignUpSelected(View view) {
        startActivity(new Intent(this, RegisterActivity.class));
    }

    public boolean checkDetails() {
        if (edtEmail.getText().toString().isEmpty()) {
            edtEmail.setError(null);
            txtEmail.setError("Email field is required");
            return false;
        } else if (edtPass.getText().toString().isEmpty()) {
            edtPass.setError(null);
            txtPass.setError("Password field is required");
            return false;
        } else {
            return true;
        }
    }
}