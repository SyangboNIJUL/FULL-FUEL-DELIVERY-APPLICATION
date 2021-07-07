package com.test.feulmgmt.pojos.orders;

import com.google.gson.annotations.SerializedName;

public class OrderResponse{

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