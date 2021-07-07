package com.test.feulmgmt.ui.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
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
import com.test.feulmgmt.utils.ApiUrls;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText edtName, edtEmail, edtPass, edtConfirmPass;
    private TextInputLayout txtName, txtEmail, txtPass, txtVerify;
    private FuelPrefs prefs;
    private ProgressBar progress_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        prefs = new FuelPrefs();
        edtName = findViewById(R.id.edtName);
        edtPass = findViewById(R.id.edtPass);
        edtEmail = findViewById(R.id.edtEmail);
        edtConfirmPass = findViewById(R.id.edtVerify);

        txtName = findViewById(R.id.txtName);
        txtEmail = findViewById(R.id.txtEmail);
        txtPass = findViewById(R.id.txtPass);
        txtVerify = findViewById(R.id.txtVerify);

        progress_bar = findViewById(R.id.progress_bar);

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

        edtConfirmPass.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    edtConfirmPass.setTransformationMethod(null);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    edtConfirmPass.setTransformationMethod(new PasswordTransformationMethod());
                }
                return false;
            }
        });
    }

    public void onLoginSelected(View view) {
        startActivity(new Intent(this, LoginActivity.class));
    }

    public void onSignUp(View view) {
        if (checkDetails()) {
            progress_bar.setVisibility(View.VISIBLE);
            JsonObject jsonObject = new JsonObject();
            try {
                Ion.with(this).
                        load("POST", ApiUrls.REGISTER).
                        setMultipartParameter("fullName", edtName.getText().toString()).
                        setMultipartParameter("email", edtEmail.getText().toString()).
                        setMultipartParameter("password", edtPass.getText().toString()).
                        asJsonObject().
                        setCallback(new FutureCallback<JsonObject>() {
                            @Override
                            public void onCompleted(Exception e, JsonObject result) {
                                progress_bar.setVisibility(View.GONE);
                                if (result.get("meta").getAsJsonObject().get("success").getAsBoolean()) {
                                    UserResponse userResponse = new Gson().fromJson(result, UserResponse.class);
                                    prefs.saveUserResponse(userResponse.getUserData());
                                    prefs.setUserLoggedIn(true);
                                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                    finish();
                                } else {
                                    Toast.makeText(RegisterActivity.this, result.get("meta").getAsJsonObject().get("message").getAsString(), Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
            }catch (Exception e){
                Toast.makeText(this, "Unable to perform Sign up.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public boolean checkDetails() {
        if (edtName.getText().toString().isEmpty()) {
            edtName.setError(null);
            txtName.setError("Name field is required");
            return false;
        } else if (edtEmail.getText().toString().isEmpty()) {
            edtEmail.setError(null);
            txtEmail.setError("Email field is required");
            return false;
        } else if (edtPass.getText().toString().isEmpty()) {
            edtPass.setError(null);
            txtPass.setError("Password field is required");
            return false;
        } else if (edtConfirmPass.getText().toString().isEmpty()) {
            edtConfirmPass.setError(null);
            txtVerify.setError("Please verify your password");
            return false;
        } else if (!edtPass.getText().toString().equals(edtConfirmPass.getText().toString())) {
            edtConfirmPass.setError(null);
            txtVerify.setError("Passwords do not match");
            return false;
        } else {
            return true;
        }
    }
}