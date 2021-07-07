package com.test.feulmgmt.pojos.fuel;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class Data{

	@SerializedName("fuels")
	private List<FuelsItem> fuels;

	public List<FuelsItem> getFuels(){
		return fuels;
	}
}