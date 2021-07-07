package com.test.feulmgmt.pojos.orders;

import com.google.gson.annotations.SerializedName;

public class Meta{

	@SerializedName("success")
	private boolean success;

	@SerializedName("message")
	private String message;

	public boolean isSuccess(){
		return success;
	}

	public String getMessage(){
		return message;
	}
}