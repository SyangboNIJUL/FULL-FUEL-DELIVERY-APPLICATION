package com.test.feulmgmt.pojos.users;

import com.google.gson.annotations.SerializedName;

public class UserDetail {

    @SerializedName("phone")
    private String phone;

    @SerializedName("fullName")
    private String fullName;

    @SerializedName("email")
    private String email;

    @SerializedName("role")
    private String role;

    @SerializedName("address")
    private String address;

    @SerializedName("country")
    private String country;

    @SerializedName("profilePicture")
    private String profilePicture;

    public String getProfilePicture() {
        return profilePicture;
    }

    public String getAddress() {
        return address;
    }

    public String getCountry() {
        return country;
    }

    public String getRole() {
        return role;
    }

    public String getPhone() {
        return phone;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public UserDetail(String phone, String fullName, String email, String role, String address, String country, String profilePicture) {
        this.phone = phone;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.address = address;
        this.country = country;
        this.profilePicture = profilePicture;
    }
}