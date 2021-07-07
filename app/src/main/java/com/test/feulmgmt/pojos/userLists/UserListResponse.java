package com.test.feulmgmt.pojos.userLists;

import com.google.gson.annotations.SerializedName;

public class UserListResponse{

	@SerializedName("data")
	private Data data;

	@SerializedName("meta")
	private Meta meta;

	public Data getData(){
		return data;
	}

	public Meta getMeta(){
		return meta;
	}
}