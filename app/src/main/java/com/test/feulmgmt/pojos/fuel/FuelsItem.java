package com.test.feulmgmt.pojos.fuel;

import com.google.gson.annotations.SerializedName;

public class FuelsItem{

	@SerializedName("pricePerGallon")
	private double pricePerGallon;

	@SerializedName("name")
	private String name;

	@SerializedName("id")
	private int id;

	private boolean isSelected;

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean value){
		this.isSelected = value;
	}

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