package com.test.feulmgmt.pojos.users;

import com.google.gson.annotations.SerializedName;

public class UserData {

	@SerializedName(value = "userDetail",alternate = {"user"})
	private UserDetail userDetail;

	@SerializedName("token")
	private String token;

	public UserDetail getUserDetail(){
		return userDetail;
	}

	public String getToken(){
		return token;
	}

	public void setUserDetail(UserDetail userDetail) {
		this.userDetail = userDetail;
	}
}