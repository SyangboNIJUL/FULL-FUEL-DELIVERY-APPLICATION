package com.test.feulmgmt.ui.pending;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.test.feulmgmt.MainActivity;
import com.test.feulmgmt.R;
import com.test.feulmgmt.pojos.orders.OrderResponse;
import com.test.feulmgmt.pojos.orders.OrdersItem;
import com.test.feulmgmt.utils.ApiUrls;
import com.test.feulmgmt.utils.FuelPrefs;

import java.util.ArrayList;
import java.util.List;

public class PendingOrdersFragment extends Fragment {
    private AppCompatTextView txtTitle;
    private AppCompatImageView imgBack, imgDrawer;
    private RecyclerView pendingLists;
    private PendingAdapter pendingAdapter;
    private FuelPrefs prefs;
    private ProgressBar progressBar;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_pending, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressBar = view.findViewById(R.id.progress_bar);
        txtTitle = view.findViewById(R.id.txtTitle);
        imgBack = view.findViewById(R.id.imgBack);
        prefs = new FuelPrefs();
        if (prefs.getUserData().getUserDetail().getRole().equalsIgnoreCase("user")) {
            txtTitle.setText("Pending");
            fetchAllOrdersUsers();
        } else {
            txtTitle.setText("Manage Bookings");
            imgBack.setVisibility(View.INVISIBLE);
            fetchAllOrdersAdmin();
        }

        imgDrawer = view.findViewById(R.id.imgDrawer);

        imgBack.setOnClickListener(view12 -> {
            ((MainActivity) getActivity()).onBackPressed();
        });

        imgDrawer.setOnClickListener(view1 -> ((MainActivity) getActivity()).showDrawer());

        pendingLists = view.findViewById(R.id.pendingLists);
        pendingLists.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));

    }

    private void fetchAllOrdersAdmin() {
        progressBar.setVisibility(View.VISIBLE);
        Ion.with(this).load(ApiUrls.ALL_ORDERS_ADMIN).
                setHeader("Authorization", "Bearer " + prefs.getUserData().getToken()).
                asJsonObject().
                setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        progressBar.setVisibility(View.GONE);
                        if (result != null) {
                            if (result.get("meta").getAsJsonObject().get("success").getAsBoolean()) {
                                OrderResponse orderResponse = new Gson().fromJson(result, OrderResponse.class);
                                setOrdersAdapter(orderResponse);
                            } else {
                                Toast.makeText(requireContext(), result.get("meta").getAsJsonObject().get("message").getAsString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    private void setOrdersAdapter(OrderResponse orderResponse) {
        List<OrdersItem> ordersItems = new ArrayList<>();
        for (OrdersItem item : orderResponse.getData().getOrders()) {
            if (item.getStatusId() == 1)
                ordersItems.add(item);
        }
        if (ordersItems.size() == 0)
            Toast.makeText(requireContext(), "No current bookings", Toast.LENGTH_SHORT).show();
        pendingAdapter = new PendingAdapter(ordersItems, new PendingAdapter.PendingOrderCallback() {

            @Override
            public void onStatusUpdate(int id, int status, int position) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("id", id + "");
                jsonObject.addProperty("statusId", status + "");
                Ion.with(requireContext()).load("PATCH",ApiUrls.UPDATE_ORDER_STATUS).
                        setHeader("Authorization", "Bearer " + prefs.getUserData().getToken()).
                        setJsonObjectBody(jsonObject).asJsonObject().
                        setCallback(new FutureCallback<JsonObject>() {
                            @Override
                            public void onCompleted(Exception e, JsonObject result) {
                                if (result != null && result.get("meta").getAsJsonObject().get("success").getAsBoolean()) {
                                    Toast.makeText(requireContext(),result.get("meta").getAsJsonObject().get("message").getAsString(),Toast.LENGTH_SHORT).show();
                                    fetchAllOrdersAdmin();
                                }else{
                                    Toast.makeText(requireContext(),result.get("meta").getAsJsonObject().get("message").getAsString(),Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }

            @Override
            public void onLocateUser(OrdersItem item) {
                Uri gmmIntentUri = Uri.parse("geo:"+item.getLattitude()+","+item.getLongitude()+"?q="+item.getLattitude()+","+item.getLongitude()+"("+item.getUser().getFullName()+")");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
                    startActivity(mapIntent);
                }
            }
        });
        pendingLists.setAdapter(pendingAdapter);
    }

    private void fetchAllOrdersUsers() {
        progressBar.setVisibility(View.VISIBLE);
        Ion.with(this).load(ApiUrls.ALL_ORDERS_USER).
                setHeader("Authorization", "Bearer " + prefs.getUserData().getToken()).
                asJsonObject().
                setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        progressBar.setVisibility(View.GONE);
                        if (result != null) {
                            if (result.get("meta").getAsJsonObject().get("success").getAsBoolean()) {
                                OrderResponse orderResponse = new Gson().fromJson(result, OrderResponse.class);
                                setOrdersAdapter(orderResponse);
                            } else {
                                Toast.makeText(requireContext(), result.get("meta").getAsJsonObject().get("message").getAsString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }
}