package com.test.feulmgmt.utils;

public class ApiUrls {

    public static String BASE_URL = "https://fueldelivery.tk/api/";
    public static String IMAGE_URL = "https://www.fueldelivery.tk/";

    public static final String UPDATE_USER = BASE_URL + "auth/edituser";
    public static String LOGIN = BASE_URL + "auth/login";
    public static String REGISTER = BASE_URL + "auth/register";
    public static final String FUEL = BASE_URL + "fuel";
    public static final String ALL_ORDERS_ADMIN = BASE_URL + "manage/ViewAllOrders";
    public static final String ALL_ORDERS_USER = BASE_URL + "orders/GetAllOrders";
    public static final String UPDATE_ORDER_STATUS = BASE_URL + "manage/UpdateOrderStatus";
    public static final String SUBMIT_ORDERS = BASE_URL + "orders/CreateOrder";
    public static final String USERS_LIST = BASE_URL + "manage/GetAllUsers";
    public static String USER_IMAGE = IMAGE_URL + "Users/"; //https://www.fueldelivery.tk/Users/
    public static String CREATE_TRANSACTION_TOKEN = BASE_URL + "payment/MakePayment";
    public static String GET_EPHIMERAL_KEY = BASE_URL + "payment/GetKey?stripeApiVersion=2020-08-27";
    public static String SUBMIT_FEEDBACK = BASE_URL + "feedback/NewFeedback";
    public static String FEEDBACK_LISTS = BASE_URL + "manage/GetAllFeedbacks";
    public static String EDIT_FUEL_ITEM = BASE_URL +"manage/UpdateFuelType";
    public static String DELETE_FUEL = BASE_URL +"manage/DeleteFuelType?id=";
    public static String CREATE_FUEL_TYPE = BASE_URL+"manage/CreateFuelType";
    public static String CHANGE_PASSWORD = BASE_URL+"/auth/ChangePassword";
    public static String DELETE_USER = BASE_URL+"/auth/ChangePassword";
    public static String SAVE_FCM = BASE_URL +"manage/DeleteUser?id=";
}
