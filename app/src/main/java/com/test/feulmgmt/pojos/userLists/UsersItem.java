package com.test.feulmgmt.pojos.userLists;

import com.google.gson.annotations.SerializedName;

public class UsersItem{

	@SerializedName("profilePicture")
	private String profilePicture;

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