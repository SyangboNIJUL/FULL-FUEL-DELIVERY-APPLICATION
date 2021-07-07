package com.test.feulmgmt.pojos.feedback;

import com.google.gson.annotations.SerializedName;

public class FeedbacksItem{

	@SerializedName("id")
	private int id;

	@SerializedName("message")
	private String message;

	@SerializedName("user")
	private User user;

	public int getId(){
		return id;
	}

	public String getMessage(){
		return message;
	}

	public User getUser(){
		return user;
	}
}