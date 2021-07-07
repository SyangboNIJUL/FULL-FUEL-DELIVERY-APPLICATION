package com.test.feulmgmt.pojos.feedback;

import com.google.gson.annotations.SerializedName;

public class User{

	@SerializedName("country")
	private String country;

	@SerializedName("profilePicture")
	private String profilePicture;

	@SerializedName("role")
	private String role;

	@SerializedName("address")
	private String address;

	@SerializedName("phone")
	private String phone;

	@SerializedName("fullName")
	private String fullName;

	@SerializedName("email")
	private String email;

	public String getCountry(){
		return country;
	}

	public String getProfilePicture(){
		return profilePicture;
	}

	public String getRole(){
		return role;
	}

	public String getAddress(){
		return address;
	}

	public String getPhone(){
		return phone;
	}

	public String getFullName(){
		return fullName;
	}

	public String getEmail(){
		return email;
	}
}