package com.test.feulmgmt.pojos.orders;

import com.google.gson.annotations.SerializedName;

public class Status{

	@SerializedName("id")
	private int id;

	@SerializedName("value")
	private String value;

	public int getId(){
		return id;
	}

	public String getValue(){
		return value;
	}
}