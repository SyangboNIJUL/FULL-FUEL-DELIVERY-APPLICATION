package com.test.feulmgmt.pojos.orders;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class Data{

	@SerializedName(value ="orders",alternate = {"order"})
	private List<OrdersItem> orders;

	public List<OrdersItem> getOrders(){
		return orders;
	}
}