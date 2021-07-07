package com.test.feulmgmt.pojos.orders;

import com.google.gson.annotations.SerializedName;

public class OrdersItem{

	@SerializedName("transactionToken")
	private String transactionToken;

	@SerializedName("quantity")
	private double quantity;

	@SerializedName("lattitude")
	private double lattitude;

	@SerializedName("userId")
	private int userId;

	@SerializedName("totalAmount")
	private double totalAmount;

	@SerializedName("unit")
	private String unit;

	@SerializedName("fuelType")
	private FuelType fuelType;

	@SerializedName("statusId")
	private int statusId;

	@SerializedName("id")
	private int id;

	@SerializedName("user")
	private User user;

	@SerializedName("fuelTypeId")
	private int fuelTypeId;

	@SerializedName("longitude")
	private double longitude;

	@SerializedName("status")
	private Status status;

	public String getTransactionToken(){
		return transactionToken;
	}

	public double getQuantity(){
		return quantity;
	}

	public double getLattitude(){
		return lattitude;
	}

	public int getUserId(){
		return userId;
	}

	public double getTotalAmount(){
		return totalAmount;
	}

	public String getUnit(){
		return unit;
	}

	public FuelType getFuelType(){
		return fuelType;
	}

	public int getStatusId(){
		return statusId;
	}

	public int getId(){
		return id;
	}

	public User getUser(){
		return user;
	}

	public int getFuelTypeId(){
		return fuelTypeId;
	}

	public double getLongitude(){
		return longitude;
	}

	public Status getStatus(){
		return status;
	}
}