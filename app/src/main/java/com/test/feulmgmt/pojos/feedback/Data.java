package com.test.feulmgmt.pojos.feedback;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class Data{

	@SerializedName("feedbacks")
	private List<FeedbacksItem> feedbacks;

	public List<FeedbacksItem> getFeedbacks(){
		return feedbacks;
	}
}