package com.test.feulmgmt.pojos.feedback;

import com.google.gson.annotations.SerializedName;

public class FeedbackResponse{

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