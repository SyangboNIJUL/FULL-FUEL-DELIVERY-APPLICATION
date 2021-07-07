package com.test.feulmgmt.pojos.userLists;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class Data{

	@SerializedName("users")
	private List<UsersItem> users;

	public List<UsersItem> getUsers(){
		return users;
	}
}