package com.test.feulmgmt.utils;

import com.google.android.gms.maps.model.LatLng;
import com.test.feulmgmt.pojos.fuel.FuelsItem;
import com.test.feulmgmt.pojos.orders.OrderResponse;

import java.util.List;

public class FuelCaches {
    private static OrderResponse orders;
    private static LatLng selectedLatLng;
    private static List<FuelsItem> fuelsList;
    private static float fuelLiters;
    private static boolean toOrder;

    public static void saveOrderResponse(OrderResponse orderResponse) {
        orders = orderResponse;
    }

    public static OrderResponse getOrders(){
        return orders;
    }

    public static void saveCurrentLocation(LatLng currentLatLng) {
        selectedLatLng = currentLatLng;
    }

    public static LatLng getSelectedLatLng() {
        return selectedLatLng;
    }

    public static void saveFuelResponse(List<FuelsItem> fuels) {
        fuelsList = fuels;
    }

    public static List<FuelsItem> getFuelsList() {
        return fuelsList;
    }

    public static void saveLitres(float litres) {
        fuelLiters = litres;
    }

    public static float getFuelLiters() {
        return fuelLiters;
    }

    public static void toOrderConfirm(boolean value) {
        toOrder = value;
    }

    public static boolean isToOrder() {
        return toOrder;
    }
}
