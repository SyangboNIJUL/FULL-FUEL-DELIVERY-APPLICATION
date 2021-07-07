package com.test.feulmgmt.pojos.orders;

import com.google.gson.annotations.SerializedName;

public class User{

	@SerializedName("profilePicture")
	private String profilePicture;

	@SerializedName("password")
	private String password;

	@SerializedName("role")
	private String role;

	@SerializedName("phone")
	private String phone;

	@SerializedName("fullName")
	private String fullName;

	@SerializedName("id")
	private int id;

	@SerializedName("email")
	private String email;

	public String getProfilePicture(){
		return profilePicture;
	}

	public String getPassword(){
		return password;
	}

	public String getRole(){
		return role;
	}

	public String getPhone(){
		return phone;
	}

	public String getFullName(){
		return fullName;
	}

	public int getId(){
		return id;
	}

	public String getEmail(){
		return email;
	}
}