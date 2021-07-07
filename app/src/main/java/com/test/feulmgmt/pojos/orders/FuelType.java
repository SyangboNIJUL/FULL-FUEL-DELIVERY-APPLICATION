package com.test.feulmgmt.pojos.orders;

import com.google.gson.annotations.SerializedName;

public class FuelType{

	@SerializedName("pricePerGallon")
	private double pricePerGallon;

	@SerializedName("name")
	private String name;

	@SerializedName("id")
	private int id;

	public double getPricePerGallon(){
		return pricePerGallon;
	}

	public String getName(){
		return name;
	}

	public int getId(){
		return id;
	}
}