package com.test.feulmgmt.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.test.feulmgmt.FuelApplication;
import com.test.feulmgmt.pojos.users.UserData;

public class FuelPrefs {

    private final SharedPreferences prefs;
    private String user = "user";
    private String isLoggedIn = "is_logged_in";
    private String fbToken = "fb_token";

    public FuelPrefs(){
        prefs = FuelApplication.getContext().getSharedPreferences("fuel_prefs", Context.MODE_PRIVATE);
    }

    public void saveUserResponse(UserData userData){
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(userData);
        prefs.edit().putString(user, json).apply();
    }

    public UserData getUserData(){
        return new Gson().fromJson(prefs.getString(user,""),UserData.class);
    }

    public void setUserLoggedIn(boolean b) {
        prefs.edit().putBoolean(isLoggedIn,b).apply();
    }

    public boolean isUserLoggedIn(){
        return  prefs.getBoolean(isLoggedIn,false);
    }

    public void saveFirebaseToken(String s) {
        prefs.edit().putString(fbToken,s).apply();
    }

    public String getFirebaseToken(){
        return prefs.getString(fbToken,"");
    }
}
