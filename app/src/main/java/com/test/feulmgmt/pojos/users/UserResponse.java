package com.test.feulmgmt.pojos.users;

import com.google.gson.annotations.SerializedName;

public class UserResponse{

	@SerializedName("data")
	private UserData userData;

	@SerializedName("meta")
	private Meta meta;

	public UserData getUserData(){
		return userData;
	}

	public Meta getMeta(){
		return meta;
	}
}